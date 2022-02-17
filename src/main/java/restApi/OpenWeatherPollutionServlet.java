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

/**
 * Created by kkarp.
 */
public class OpenWeatherPollutionServlet {

    private final static String apikey = FileReader.getOpenWeatherApikey();
    private final static String q1 = "http://api.openweathermap.org/data/2.5/air_pollution/history?lat=";
    private final static String q2 = "&lon=";
    private final static String q3 = "&dt=";
    private final static String q4 = "&appid=";
    private final static String q5 = "&start=";
    private final static String q6 = "&end=";
//https://www.unixtimestamp.com/ okreslic zakres pobieranych danych

    public OpenWeatherPollutionServlet() {
        System.out.println("OpenWeather Data Download");
        System.out.println("[OpenWeatherPollution] START");
        try {
            ArrayList<String> arrpoll = utility.FileReader.readOpenWeatherPollution();
            Iterator<String> iter = arrpoll.iterator();
            do {
                ArrayList<String> arrPoll2 = new ArrayList<>();
                arrPoll2.add(iter.next());
                arrPoll2.add(iter.next());
                saveData(Objects.requireNonNull(openWeatherPollutionGet(arrPoll2)), iter.next());
            } while (iter.hasNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[OpenWeatherPollution] END");
    }

    private static JSONObject openWeatherPollutionGet(ArrayList<String> arrPollution) {
        try {
            URL url = new URL(q1 + arrPollution.get(0) + q2 + arrPollution.get(1) + q5 + "1622569761" + q6 + "1635792561" + q4 + apikey);
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

    private static Map<String, String> getPollutionData(JSONObject currentData, String id) {
        Map<String, String> map = new HashMap<>();
        map.put("no2", String.valueOf(currentData.get("no2")));
        map.put("no", String.valueOf(currentData.get("no")));
        map.put("o3", String.valueOf(currentData.get("o3")));
        map.put("so2", String.valueOf(currentData.get("so2")));
        map.put("pm2_5", String.valueOf(currentData.get("pm2_5")));
        map.put("pm10", String.valueOf(currentData.get("pm10")));
        map.put("nh3", String.valueOf(currentData.get("nh3")));
        map.put("co", String.valueOf(currentData.get("co")));
        map.put("station", id);
        return map;
    }

    private static void saveData(JSONObject currentData, String id) {
        Connection c = MQAgent.connectRabbitMQ();
        assert c != null;
        JSONArray array = currentData.getJSONArray("list");
        for (int i = 0; i < array.length(); i++) {
            JSONObject row = array.getJSONObject(i);
                JSONObject obj = row.getJSONObject("components");
                Map<String, String> array2 =  getPollutionData(obj,id);
                MQAgent.sendData(c, "owpoll",array2, id);
                MQAgent.receiveData(c, "owpoll");
                }
        }
}


