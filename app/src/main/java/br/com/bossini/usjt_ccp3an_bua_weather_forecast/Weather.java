package br.com.bossini.usjt_ccp3an_bua_weather_forecast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;


    public Weather (long dt, double minTemp, double maxTemp,
                    double humidity, String description,
                    String iconName){
        this.dayOfWeek = convert (dt);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        this.minTemp = nf.format(minTemp) + "\u00B0C";
        this.maxTemp = nf.format(maxTemp) + "\u00B0C";
        NumberFormat pf = NumberFormat.getPercentInstance();
        this.humidity = pf.format(humidity / 100);
        this.description = description;
        this.iconURL =
                "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    public String convert (long dt){
        Calendar agora = Calendar.getInstance();
        agora.setTimeInMillis(dt * 1000);
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEEE HH:mm");
        return sdf.format(agora.getTime());
    }

}
