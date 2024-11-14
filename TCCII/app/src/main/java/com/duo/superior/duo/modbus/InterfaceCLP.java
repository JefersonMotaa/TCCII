package com.duo.superior.duo.modbus;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.view.View;
import android.widget.TextView;

import com.duo.superior.duo.Assynch;

public class InterfaceCLP {

    public static String listaAlarmes,novaListaAlarmes;
    public static boolean semInternet=true;

    public static void lerAlarmes(CLP clp) throws  Exception{

        int[] palavras = clp.palavrasAlarmes();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 4; i++) {

            if (palavras[i] == 0) {
                continue;
            }
            String pAlarme = Integer.toBinaryString(palavras[i]);
            for (int j = 0; j < pAlarme.length(); j++) {

                //Lemos bit a bit.
                if (pAlarme.charAt(pAlarme.length() - j - 1) == '1') {
                    if (sb.length() > 1) {
                        sb.append(" ");
                    }


                    sb.append(CLP.alarmesRobo[16 * i + j]);

                }
            }
        }

        //Aumentar o número de caracteres para preencher a tela
        while (sb.length() < 80) {
            sb.append(" ");
            sb.append(sb.toString());
        }
      listaAlarmes=sb.toString();
    }

    public static void atualizarAlarmes(TextView scrollLoop){
        try {
            if (!novaListaAlarmes.contentEquals(listaAlarmes)) {
                scrollLoop.setSelected(false);
                scrollLoop.setText(listaAlarmes);
                novaListaAlarmes = listaAlarmes;
                scrollLoop.setSelected(true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void blink(View view,boolean status){
        view.setBackgroundColor(status?0xFF00FF00:0xFF808080);
    }


    public static void btnMomentaneo(String memoria){
        Assynch set = new Assynch(memoria,true);
        set.execute();

        set = new Assynch(memoria,false);
        set.execute();
    }
    public static boolean estaCarregando(Context context){
        boolean isCharging;
        IntentFilter  ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent batteryStatus = context.registerReceiver(null,ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        return isCharging;
    }


    public static String getCurrentSsid(Context context) {
        String ssid = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();

        ssid = wifiInfo.getSupplicantState().name();
//        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
//            ssid = wifiInfo.getSupplicantState().name();
//        }
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            return null;
//        }
//
//        if (networkInfo.isConnected()) {
//            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
//                ssid = connectionInfo.getSSID();
//            }
//        }
//
        return ssid;
    }

   public static String formatarNumXDigitos(int num, int x){

        if(num<0||x<0){
            return "0";
        }

        StringBuilder sb=new StringBuilder();
        String number=String.valueOf(num);

        while(x-number.length()!=sb.length()){
            sb.append("0");
        }

        sb.append(number);
        return sb.toString();
    }
}
