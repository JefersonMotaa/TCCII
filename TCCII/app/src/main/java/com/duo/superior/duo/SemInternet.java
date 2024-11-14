//package com.example.lia9atb.duo;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.widget.TextView;
//import java.util.Timer;
//import java.util.TimerTask;
//
//
//public class SemInternet extends AppCompatActivity {
//    final Handler handler = new Handler();
//    private static String msg;
//    public String ssid;
//    TextView txtViewMsg;
//    IntentFilter ifilter;
//    Intent batteryStatus;
//    Timer timer;
//    @Override
//    protected void onPause(){
//        super.onPause();
//
//
//    }
//    @Override
//    protected void onStop(){
//        super.onStop();
//
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//         ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        ssid=InterfaceCLP.getCurrentSsid(SemInternet.this);
//        setContentView(R.layout.activity_sem_internet);
//        setFinishOnTouchOutside(false);
//        final Context context = getApplicationContext();
//        txtViewMsg=(TextView) findViewById(R.id.textView169);
//        boolean isCharging2=true;
//        timer = new Timer("horizontalScrollViewTimer");
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                if(InterfaceCLP.getCurrentSsid(SemInternet.this).equals("COMPLETED") && InterfaceCLP.estaCarregando(SemInternet.this)){
//                    trocarTela(PrincipalActivity.class);
//                }
//
//
//            }
//        }, 500, 2000);
//
////        Intent itServico = new Intent(this,ServicoComunicacao.class);
////        stopService(itServico);
////
////        Thread threadEthernet = new Thread(new Runnable() {
////
////            @Override
////            public void run() {
////                boolean end=false;
////                try  {
////                //    this.wait(1000);
////                        CLP clp = new CLP("192.168.1.3");
////                        clp.conectar();
////                        clp.desconectar();
////                        end=true;
//////                        Intent  itServico = new Intent(SemInternet.this,ServicoComunicacao.class);
//////                        startService(itServico);
////                         Intent itServico = new Intent(SemInternet.this, PrincipalActivity.class);
////                        startActivity(itServico);
////                } catch (Exception e) {
////                    System.out.println("Desconectado");
////                    if(!end){
////                        this.run();
////                    }
////                }
////            }
////        });
//
////        Thread threadUSB = new Thread(new Runnable() {
////
//////            @Override
////            public void run() {
////                boolean end=false;
////                try  {
////                    batteryStatus = context.registerReceiver(null, ifilter);
////                    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
////                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
////                            status == BatteryManager.BATTERY_STATUS_FULL;
////
////                    if(isCharging){
////                        Intent itServico = new Intent(SemInternet.this, PrincipalActivity.class);
////                        startActivity(itServico);
////                    }else
////                   handler.postDelayed(this,5000);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//
////        batteryStatus = context.registerReceiver(null, ifilter);
////        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
////        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
////                status == BatteryManager.BATTERY_STATUS_FULL;
////
////        if(!isCharging){
////            txtViewMsg.setText("Ou o robô ou o tablet está desconectado.");
////            threadUSB.start();
////        }else if(isCharging){
//            txtViewMsg.setText("Robô desligado!");
////        threadEthernet.start();}
//
//
//    }
//    public void trocarTela(Class<?> cls){
//        timer.cancel();
//        Intent it=new Intent(SemInternet.this,cls);
//        startActivity(it);
//    }
//
//    @Override
//    public void onBackPressed(){
//
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // If we've received a touch notification that the user has touched
//        // outside the app, finish the activity.
//        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
//            System.out.println("Robô desligado.");
//            return true;
//        }
//
//        // Delegate everything else to Activity.
//        return super.onTouchEvent(event);
//    }
//}