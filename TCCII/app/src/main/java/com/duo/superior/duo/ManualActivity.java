package com.duo.superior.duo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.duo.superior.duo.modbus.CLP;
import com.duo.com.duo.R;
public class ManualActivity extends duoActivity {

    Button btnManGarra, btnManExpulsa, btnManJato, btnManTroca,btnManVoltar;
    boolean garra, jato,expulsaEletrodo, trocaEletrodo, first;


    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_manual);

        //ActionBar customizado
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custombar, null);

        //Configura a ActionBar
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        ImageView mImage = (ImageView) mCustomView.findViewById(R.id.imgLogo);
        mImage.setImageResource(R.drawable.duot);

        btnManVoltar = (Button) findViewById(R.id.btnManVoltar);
        btnManVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

        //Troca eletrodo
        btnManTroca = (Button) findViewById(R.id.btnManTroca);
        btnManTroca.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!trocaEletrodo) {
                            CLP.sEscreverBit("MX3.3", true);
                        }else{
                            Global.parar=true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX3.3", false);
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX3.3",false);
                        break;
                }
                return true;
            }
        });

        //BOTÃO GARRA
        btnManGarra = (Button) findViewById(R.id.btnManGarra);
        btnManGarra.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!garra) {
                            CLP.sEscreverBit("MX3.5", true);
                            garra=true;
                            break;}
                        else if (garra){
                            CLP.sEscreverBit("MX3.5", false);
                            garra=false;
                        };
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX3.5",false);
                        break;
                }
                return false;
            }
        });

        // BOTÃO TESTE JATO DÁGUA
        btnManJato = (Button) findViewById(R.id.btnManJato);
        btnManJato.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (!jato) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        CLP.sEscreverBit("MX3.7", true);
                        jato = true;
                    }
                } else if(jato){
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        CLP.sEscreverBit("MX3.7", false);
                        jato=false;
                    }
                }
                return false;
            }
        });


        // BOTÃO EXPULSA ELETRODO
        btnManExpulsa = (Button) findViewById(R.id.btnManExpulsa);
        btnManExpulsa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!expulsaEletrodo) {
                            CLP.sEscreverBit("MX3.6", true);
                            expulsaEletrodo=true;
                            break;
                        }
                        if(expulsaEletrodo){
                            CLP.sEscreverBit("MX3.6", false);
                            expulsaEletrodo=false;
                            break;
                        }

                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX3.6",true);
                        break;
                }
                return false;
            }
        });

    }

    public void atualizarCLP()throws Exception{
        if(!first){
            garra=clp.lerBit("MX7.2");
            expulsaEletrodo=clp.lerBit("MX7.3");
            jato=clp.lerBit("MX7.4");
            first=true;
        }
        trocaEletrodo=clp.lerBit("MX7.5");
    }

    @Override
    public void atualizarUI() {
        if(first){
            btnManGarra.setBackgroundColor(garra?Color.GREEN:Color.GRAY);
            btnManExpulsa.setBackgroundColor(expulsaEletrodo?Color.GREEN:Color.GRAY);
            btnManJato.setBackgroundColor(jato?Color.GREEN:Color.GRAY);
            btnManTroca.setBackgroundColor(trocaEletrodo?Color.GREEN:Color.GRAY);
        }
    }
}