package jdbc;

import java.sql.*;
import java.util.logging.Logger;

public class GetData {

    private final String url = "jdbc:postgresql://localhost:5432/magisterka";
    private final String user = "postgres";
    private final String password = "admin";

    public GetData() {

        getActorCount();
    }


    public int getActorCount() {
        String SQL = "select * from airai.open_weather_pollutions where id = 2001";
        int count = 0;

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("select * from airai.open_weather_pollutions ");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                System.out.print(rs.getInt(1));
                System.out.print(": ");
                System.out.println(rs.getString(2));
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }


        return count;
    }
}
