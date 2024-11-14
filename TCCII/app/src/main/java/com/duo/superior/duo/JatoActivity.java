package com.duo.superior.duo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.duo.superior.duo.modbus.CLP;
import com.duo.com.duo.R;
import java.util.Timer;
import java.util.TimerTask;

public class JatoActivity extends duoActivity {

    TextView tvStatusJato;
    ImageView  imgStatusJato, imgStatusFluxo;
    Button btnJatoAtiva, btnJatoDesativa, btnJatoTeste, btnJatoVoltar;
    boolean jatoAtivo, habilitaJato, fluxo, first;

    Timer timer;

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_jato);

        imgStatusJato = (ImageView) findViewById(R.id.imgStatusJato);
        tvStatusJato = (TextView) findViewById(R.id.tvStatusJato);

        btnJatoAtiva = (Button) findViewById(R.id.btnJatoAtiva);
        btnJatoAtiva.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    CLP.sEscreverBit("MX5.3" , true);
                    btnJatoAtiva.setBackgroundColor(Color.GREEN);
                    btnJatoDesativa.setBackgroundColor(Color.GRAY);
                }
                return false;
            }
        });

        btnJatoDesativa = (Button) findViewById(R.id.btnJatoDesativa);
        btnJatoDesativa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    CLP.sEscreverBit("MX5.3" , false);
                    btnJatoAtiva.setBackgroundColor(Color.GRAY);
                    btnJatoDesativa.setBackgroundColor(Color.RED);
                }
                return false;
            }
        });

        btnJatoTeste = (Button) findViewById(R.id.btnJatoTeste);
        btnJatoTeste.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    CLP.sEscreverBit("MX5.4",true);
                    btnJatoTeste.setBackgroundColor(Color.YELLOW);}
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    CLP.sEscreverBit("MX5.4",false);
                    btnJatoTeste.setBackgroundColor(Color.GRAY);}
                if(motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    CLP.sEscreverBit("MX5.4",false);
                    btnJatoTeste.setBackgroundColor(Color.GRAY);}
                return false;
            }
        });

        btnJatoVoltar = (Button) findViewById(R.id.btnJatoVoltar);
        btnJatoVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

    }

    @Override
    public void atualizarCLP() throws Exception{
        jatoAtivo = clp.lerSequencia("QX0",3)[2];
        habilitaJato = clp.lerBit("MX5.3");
    }

    @Override
    public void atualizarUI() {
        try{
            imgStatusJato.setImageResource(jatoAtivo ? R.drawable.led_verde2 : R.drawable.led_cinza);
            tvStatusJato.setText(jatoAtivo ? "Jato\nLigado" : "Jato\nDesligado");
            imgStatusFluxo.setImageResource(fluxo ? R.drawable.led_verde2 : R.drawable.led_cinza);


        }catch(Exception e){
            e.printStackTrace();
        }
        if (!first){
            btnJatoAtiva.setBackgroundColor(habilitaJato ? Color.GREEN : Color.GRAY);
            btnJatoDesativa.setBackgroundColor(habilitaJato ? Color.GRAY : Color.RED);
            first=true;
        }
    }
}
