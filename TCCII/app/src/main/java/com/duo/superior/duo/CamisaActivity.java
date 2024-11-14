package com.duo.superior.duo;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duo.superior.duo.modbus.CLP;
import com.duo.com.duo.R;

import com.duo.superior.duo.modbus.InterfaceCLP;

public class CamisaActivity extends duoActivity {
    private TextView scrollLoop;
    ImageView imageInicioVale, imageFlangeEsq, imageFlangeDir, imageVale0;
    Button btnRegistrar, btnLimpar, btnCamisaVoltar;
    EditText edtValeInicial;

    int valeAtual;
    boolean flangeEsqAtivo, flangeDirAtivo, inicioVale0Ativo,once;

    void escreve() {


    }

    void ler() {

    }

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_camisa);
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

        once = false;
        imageFlangeDir = (ImageView) findViewById(R.id.imageCamisaFlangeDir);
        imageFlangeEsq = (ImageView) findViewById(R.id.imageCamisaFlangeEsq);
        imageInicioVale = (ImageView) findViewById(R.id.imageCamisaInicioVale);
        imageVale0 = (ImageView) findViewById(R.id.imageCamisaVale0);
        btnRegistrar = (Button) findViewById(R.id.btnCamisaRegistrar);

        edtValeInicial = (EditText) findViewById(R.id.edtCamisaValeInicial);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escreve();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLP.sEscreverInteiro("MD153", Integer.valueOf(edtValeInicial.getText().toString()));
            }
        });

/*        btnRegistrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int value = Integer.valueOf(edtValeInicial.getText().toString());
                CLP.sEscreverInteiro("MD153'", value);
                return false;
            }
        });
*/
        btnLimpar = (Button) findViewById(R.id.btnCamisaLimpar);
        btnLimpar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    CLP.sEscreverBit("MX6.1",true);
                }
                return false;
            }
        });

        imageFlangeDir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    CLP.sEscreverBit("MX5.0",true);
                }
                return false;
            }
        });

        imageFlangeEsq.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    CLP.sEscreverBit("MX5.0",false);
                }
                return false;
            }
        });

        imageInicioVale.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InterfaceCLP.btnMomentaneo("D6153.2");

                return false;
            }
        });

        imageVale0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                inicioVale0Ativo = !inicioVale0Ativo;
                if (inicioVale0Ativo) {
                    imageVale0.setImageResource(R.drawable.led_on);
                } else {
                    imageVale0.setImageResource(R.drawable.led_off);
                }
                escreve();
                return false;
            }
        });

        btnCamisaVoltar = (Button) findViewById(R.id.btnCamisaVoltar);
        btnCamisaVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

        ler();
        String svaleAtual = String.valueOf(valeAtual);
        System.out.println(svaleAtual);
        edtValeInicial.setText(svaleAtual);
    }



    public void atualizarCLP() throws Exception {

        if(!once){
            valeAtual = clp.lerInteiro("MD154");
            once = true;
        }
        flangeEsqAtivo=!clp.lerBit("MX5.0");
        flangeDirAtivo=clp.lerBit("MX5.0");


//            inicioValeAtivo=clp.lerBitDVP10MC("D6153.2");
//            flangeEsqAtivo=clp.lerBitDVP10MC("D6153.5");
//            flangeDirAtivo=clp.lerBitDVP10MC("D6153.6");
    }

    @Override
    public void atualizarUI() {
        try {

            if(!once){
                edtValeInicial.setText(String.valueOf(valeAtual));
                once=true;
            }

//            InterfaceCLP.blink(imageInicioVale,inicioValeAtivo);
//            InterfaceCLP.blink(imageFlangeEsq,flangeEsqAtivo);
//            InterfaceCLP.blink(imageFlangeDir,flangeDirAtivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flangeEsqAtivo) {
            imageFlangeEsq.setImageResource(R.drawable.led_on);
        }else{
            imageFlangeEsq.setImageResource(R.drawable.led_off);
        }
        if (flangeDirAtivo) {
            imageFlangeDir.setImageResource(R.drawable.led_on);
        }else{
            imageFlangeDir.setImageResource(R.drawable.led_off);
        }
    }
}

