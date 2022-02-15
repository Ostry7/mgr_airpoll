package jdbc;

import java.sql.*;
import java.util.Map;

public class DatabaseAgent {

    private final static String openWeatherStatement =
            "INSERT INTO airai.open_weather (wind_speed, wind_direction, weather, date, id_station) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private final static String openWeatherPollutions =
            "INSERT INTO airai.open_weather_pollutions (no2, no, o3, so2, pm2_5, pm10, nh3, id_station, co)" +
                    "VALUES (?,?,?,?,?,?,?,?,?)";

    public void insertOpenWeatherData(Map<String, String> hourData) {
        Statement stmt;
        Connection c = connectDB();
        try { stmt = c.createStatement();
            PreparedStatement st = c.prepareStatement(openWeatherStatement);
            st.setString(1, hourData.get("wind_speed"));
            st.setString(2, hourData.get("wind_direction"));
            st.setString(3, hourData.get("weather"));
            st.setString(4, hourData.get("time"));
            st.setInt(5, Integer.parseInt(hourData.get("station")));
            stmt.executeUpdate(String.valueOf(st));
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }
    }



    public void insertOpenWeatherPollutionDate(Map<String, String> currentData) {
        Statement stmt;
        Connection c = connectDB();
        try { stmt = c.createStatement();
            PreparedStatement st = c.prepareStatement(openWeatherPollutions);
            st.setString(1, currentData.get("no2"));
            st.setString(2, currentData.get("no"));
            st.setString(3, currentData.get("o3"));
            st.setString(4, currentData.get("so2"));
            st.setString(5, currentData.get("pm2_5"));
            st.setString(6, currentData.get("pm10"));
            st.setString(7, currentData.get("nh3"));
            st.setInt(8,Integer.parseInt(currentData.get("station")));
            st.setString(9, currentData.get("co"));
            stmt.executeUpdate(String.valueOf(st));
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println("[ERROR]" +  e.getClass().getName()+": "+ e.getMessage());
        }
    }


    public Connection connectDB() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/magisterka",
                            "postgres", "admin");
            c.setAutoCommit(false);
            return c;
        } catch (Exception e) {
            System.out.println("[ERROR] Failed connection to psqlDB");
            return c;
        }
    }
}
