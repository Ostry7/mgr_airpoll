package utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnixTimer {

    public static int getUnixTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
    public static int convertToUnixTime(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date dt = sdf.parse(timestamp);
            return (int) (dt.getTime() / 1000);
        } catch (ParseException e) {
            return 0;
        }
    }
}