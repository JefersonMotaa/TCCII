package com.duo.superior.duo.conectividade;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Tempo {

    public  static int TurnoAtual() {
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);


        if(hour<7) {
            return 1;
        }
        else if(hour <=15 && hour>=7) {
            return 2;
        }

        else if(hour >15) {return 3;}
        else
            return 0;
    }

    Tempo(){

    }

    public static int Year(){
        try {
         //   String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

            return Calendar.getInstance().get(Calendar.YEAR);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }


    }

    public static int CLPDate(){
        try {
            String currentDate = new SimpleDateFormat("MMdd", Locale.getDefault()).format(new Date());

            return (1+Calendar.getInstance().get(Calendar.MONTH))*100+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }


    }

    public static int CLPHour(){
        String currentTime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());

        return Integer.valueOf(currentTime);
    }

}
