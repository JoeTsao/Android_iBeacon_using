package mustcsie.test.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GETdate {
    public static String getDateTime(){
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
//System.out.println(strDate);
        return strDate;
    }
}
