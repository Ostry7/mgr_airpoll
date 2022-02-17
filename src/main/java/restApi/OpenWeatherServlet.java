package restApi;

import com.rabbitmq.client.Connection;
import org.json.JSONArray;
import org.json.JSONObject;
import rabbit.MQAgent;
import utility.FileReader;
import utility.UnixTimer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class OpenWeatherServlet {
    private final static String apikey = FileReader.getOpenWeatherApikey();
    private final static String q1 = "http://history.openweathermap.org/data/2.5/history/city?lat=";
    private final static String q2 = "&lon=";
    private final static String q3 = "&dt=";
    private final static String q4 = "&appid=";
    private final static String q5 = "&start=";
    private final static String q6 = "&end=";
    private final static String q7 = "&units=metric";


    public OpenWeatherServlet() {
        System.out.println("[OpenWeather] START");
        try {
            ArrayList<String> arr = utility.FileReader.readOpenWeather();
            Iterator<String> iter = arr.iterator();
            do {
                ArrayList<String> arr2 = new ArrayList<>();
                arr2.add(iter.next());
                arr2.add(iter.next());
                saveData(Objects.requireNonNull(openWeatherGet(arr2)), iter.next());
            } while (iter.hasNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[OpenWeather] END");
    }

    private static JSONObject openWeatherGet(ArrayList<String> arr) {
        try {
            URL url = new URL(q1 + arr.get(0) + q2 + arr.get(1) + q5 + "1622569761" + q6 + "1635792561" + q7 + q4 + apikey);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return new JSONObject(String.valueOf(content));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, String> getData(JSONObject hourData, String id) {
        Map<String, String> map = new HashMap<>();
        if (hourData.has("temp")) {
            map.put("temp", String.valueOf(hourData.get("temp")));
            map.put("humidity", String.valueOf(hourData.get("humidity")));
            map.put("pressure", String.valueOf(hourData.get("pressure")));
            map.put("station", id);


        }
        else {}

        return map;

    }


    private static void saveData(JSONObject openWeatherResponse, String id) {
        Connection c = MQAgent.connectRabbitMQ();
        assert c != null;
        JSONArray array = openWeatherResponse.getJSONArray("list");
        for (int i = 0; i < array.length(); i++) {
            JSONObject row = array.getJSONObject(i);
            JSONObject obj = row.getJSONObject("main");
            Map<String, String> array2 = getData(obj, id);
            MQAgent.sendData(c, "ow", array2, id);
            MQAgent.receiveData(c, "ow");
        }
        /*
        MQAgent.sendData(c, "ow", getData(openWeatherResponse.getJSONObject("message"), id));
        MQAgent.receiveData(c, "ow");
        Iterator<Object> iter = openWeatherResponse.getJSONArray("hourly").iterator();
        do {
            MQAgent.sendData(c, "ow", getData((JSONObject) iter.next(), id));
            MQAgent.receiveData(c, "ow");
        } while (iter.hasNext());*/
    }
}
