package com.duo.superior.duo;
import static com.duo.superior.duo.Global.numAlamesDriver;
import static com.duo.superior.duo.Global.numAlarmesCLP;
import static com.duo.superior.duo.Global.parar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duo.com.duo.R;
import com.duo.superior.duo.conectividade.DxMySQL;
import com.duo.superior.duo.conectividade.Eletrodo;
import com.duo.superior.duo.conectividade.Receita;
import com.duo.superior.duo.modbus.asynchronous.Words;
import com.duo.superior.duo.conectividade.DxSQLite;
import com.duo.superior.duo.modbus.CLP;
import com.duo.superior.duo.modbus.InterfaceCLP;
import com.duo.superior.duo.modbus.ModbusClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public abstract class duoActivity extends AppCompatActivity {

    TextView txtCoord;
    Thread consciencia;
    Handler handler;
    Timer timer;
    CLP clp;
    Class<?> returnCls;

    private int scanTime=10;
    public TextView scrollLoop;
    public ImageView imageErro;
    public Map<String,Integer> preferences;
    public DxSQLite sql;
    String msg;
    public boolean conectado, naoConectou, carregarReceita, carregarEletrodo, first;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        scanTime=Global.scanTime;
        msg = "";

        handler = new Handler();
        sql = new DxSQLite(this, Global.dbVersion);
        System.out.println("------------------DB-------------------");
        System.out.println(sql.pegarNumEletrodosTurnoAtual());
        Global.novoAlarme = true;

        preferences = sql.lerPreferencias();
        Global.ip = "192.168.1." + preferences.get("IP");
        inicializarUI();
        inicializarVars();
        timer = new Timer("horizontalScrollViewTimer");
        timer.scheduleAtFixedRate(criarTT(), 0, 10);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custombar, null);
        ImageView mImage = (ImageView) mCustomView.findViewById(R.id.imgLogo);
        mImage.setImageResource(R.drawable.duot);
        imageErro = (ImageView) mCustomView.findViewById(R.id.imgErro);

        Button parar = (Button) mCustomView.findViewById(R.id.btnActionBarCoord);
        parar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Global.parar = true;
                    parar.setBackgroundColor(Color.RED);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    parar.setBackgroundColor(getColor(R.color.vermelhoClaro));
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    parar.setBackgroundColor(getColor(R.color.vermelhoClaro));
                    Global.parar = true;
                }
                return false;
            }
        });

        Button btnClear = (Button) mCustomView.findViewById(R.id.btnActionBarReset);
        btnClear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    CLP.sEscreverBit("MX3.1", true);
                    btnClear.setBackgroundColor(Color.YELLOW);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    btnClear.setBackgroundColor(getColor(R.color.amareloClaro));
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    CLP.sEscreverBit("MX3.1",false);
                    btnClear.setBackgroundColor(getColor(R.color.amareloClaro));

                }
                return false;
            }
        });

        scrollLoop = mCustomView.findViewById(R.id.scrollLoop);
        scrollLoop.setText(Global.alarmes);
        scrollLoop.setTextColor(getResources().getColor(R.color.green));
        scrollLoop.setSelected(true);

        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }


    public TimerTask criarTT(){
        TimerTask Tt =   new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                boolean developer = true;
                try {
                    clp = new CLP(Global.ip);
                    consciencia = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(Global.timeOut);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollLoop.setText("Não foi possível conectar! Verifique a Rede");
                                                scrollLoop.setTextColor(getResources().getColor(R.color.amareloBotao));
                                                scrollLoop.setSelected(true);
                                                naoConectou = true;
                                            }
                                        });
                                    }
                                });
                            }
                            catch(Exception e){
                                 //e.printStackTrace();
                            }
                        }
                    });

                    //consciencia.start();
                    clp.conectar();
                    //consciencia.interrupt();
                    msg="Conectado";
//                    DxMySQL.contarPassos(clp, sql);
                    conectado = true;
//Comando de parada
                    if (Global.parar){
                        clp.escreverBit("MX3.4", true);
                    }
//Reconhecimento de parada
                    if(clp.lerBit("MX6.6")){
                        clp.escreverBit("MX3.4", false);

                        clp.escreverBit("MX6.6", false);
                        Global.parar=false;
                    }
                    if(!Global.novoAlarme)
                        Global.novoAlarme = clp.lerAlarmes();
                    atualizarCLP();

                    if(!first){
                       carregarReceita = true;
                       carregarEletrodo = true;
                    }

                    if(carregarEletrodo){
                        Eletrodo el = sql.pegarRecEletrodo(0);

                        Words word = new Words(el);
                        word.execute();
                        carregarEletrodo = false;
                    }

                    if(carregarReceita){
                        Global.receita = preferences.get("Receita");
                        Receita rec = sql.pegarReceita(Global.receita);
                        Words word = new Words(rec);
                        word.execute();
                        carregarReceita = false;
                    }

                    if(!first){
                        first = true;
                    }
                    if(Global.receita%2 == 0){
                        clp.escreverBit("MX6.5", true );
                    }else{
                        clp.escreverBit("MX6.5", false);
                    }
            }

                catch (Exception e){
                e.printStackTrace();
                msg = "Problema na Conexão.";
                conectado = false;
            }finally{
                    clp.desconectar();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(Global.novoAlarme || naoConectou&&conectado || !first){
                            Global.alarmes = lerAssetAlarmes();
                            scrollLoop.setText(Global.alarmes);
                            scrollLoop.setTextColor(getResources().getColor(R.color.green));
                            scrollLoop.setSelected(true);
                            Global.novoAlarme = false;
                            naoConectou = false;
                        }
                        atualizarUI();
                    }
                });
            }
        };

        return Tt;
    }

    public void trocarTela(Class<?> cls){
        timer.cancel();
        Global.returnCls = getClass();
        Intent it = new Intent(getBaseContext(), cls);
        startActivity(it);
    }

    public void setarScanTime(int scanTime){
        this.scanTime=scanTime;
    }

    public void setClasseRetorno(Class<?> classe){
        this.returnCls=classe;
    }

    @Override
    public void onBackPressed(){

        timer.cancel();
        if(Global.returnCls!=null && this.returnCls==null){
            Intent it = new Intent(getBaseContext(), Global.returnCls);
            startActivity(it);
        }else {
            Intent it = new Intent(getBaseContext(), this.returnCls);
            startActivity(it);
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();

    }

    public void inicializarVars(){
        Global.editarEletrodo=false;
        Global.developer=true;
        Global.statusReceita=true;
    }

    public String lerAssetAlarmes(){
        try {
            //Variaveis Globais

            StringBuilder lista = new StringBuilder();

            //Ler Alarmes CLP
            boolean alCLP[] = new boolean[16*numAlarmesCLP];
            for(int i = 0; i< numAlarmesCLP; i++){
                for(int j=0; j<16;j++){
                    if(((Global.al[Global.numAlamesDriver+i]>>j) & 1) == 1){
                        alCLP[i*16+j] = true;
                    }
                }
            }

            InputStream is = getAssets().open("alarmes.txt");
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            for(int n=0;n<alCLP.length;n++) {
                String linha = bf.readLine();
                if (alCLP[n] == true)
                    lista.append("   " + linha + " ");
            }

            bf.close();

            InterfaceCLP.listaAlarmes="Sem conexão.";
            InterfaceCLP.novaListaAlarmes=InterfaceCLP.listaAlarmes;

            String[] eixos = {"Z", "X", "C", "G"};

            is = getAssets().open("drivers.txt");
            bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String linha;
            int qntFalta = numAlamesDriver;

            int[] alDrive = new int[4];

            for(int i=0; i<4;i++){
                alDrive[i] = CLP.wPDW(Global.al[2*i], Global.al[2*i+1]);
            }

            for(int n = 0; n< 4; n++){
                if(alDrive[n] == 0){
                    qntFalta--;
                }
            }

            for(int k=1; k<100;k++)
            {
                linha = bf.readLine();
                for(int n = 0; n<alDrive.length; n++){
                    if(alDrive[n]==k){
                        lista.append("   ALARME "+Integer.toHexString(k)+" NO EIXO "+eixos[n]+" -> "+linha);
                        qntFalta--;
                    }
                }

                if(qntFalta == 0){
                    break;
                }
            }

            return lista.toString();
            //Ler Alarmes Drive
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public abstract void inicializarUI();
    public abstract void atualizarCLP() throws Exception;
    public abstract void atualizarUI();


}
