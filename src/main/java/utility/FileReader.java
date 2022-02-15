package utility;

import java.io.*;
import java.util.*;

public class FileReader {
    private final static String ow_path = "src/main/resources/station_id/ow";
    private final static String owpoll_path = "src/main/resources/station_id/owpoll";



    public static ArrayList<String> readOpenWeather() throws IOException {
        Scanner scanner = new Scanner(new File(ow_path));
        ArrayList<String> arr = new ArrayList<>();
        do {
            String s = scanner.next();
            String[] sa = s.split(",");
            arr.add(sa[0]);
            arr.add(sa[1]);
            arr.add(sa[2]);
        } while (scanner.hasNext());
        return arr;
    }
    public static ArrayList<String> readOpenWeatherPollution() throws IOException {
        Scanner scanner = new Scanner(new File(owpoll_path));
        ArrayList<String> arr = new ArrayList<>();
        do {
            String s = scanner.next();
            String[] sa = s.split(",");
            arr.add(sa[0]);
            arr.add(sa[1]);
            arr.add(sa[2]);
        } while (scanner.hasNext());
        return arr;
    }
    public static String getOpenWeatherApikey() {
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/apikey/ow"));
            return scanner.next();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
