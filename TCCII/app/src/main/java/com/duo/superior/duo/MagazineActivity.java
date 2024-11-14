package com.duo.superior.duo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duo.com.duo.R;
import com.duo.superior.duo.modbus.CLP;

public class MagazineActivity extends duoActivity {
    Button btnMagVoltar, btnMagLiga, btnMagDesliga, btnMagReabastece;
    boolean auxBtnMagLiga, auxBtnMagReabastece, statusMagazine, first;
    ImageView imgStatusMag;
    TextView tvStatusMag;

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_magazine);
        tvStatusMag = (TextView) findViewById(R.id.tvStatusMag);
        imgStatusMag = (ImageView) findViewById(R.id.imgStatusMag);
        btnMagLiga = (Button) findViewById(R.id.btnMagLiga);
        btnMagLiga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auxBtnMagLiga = true;
            }
        });
        btnMagDesliga = (Button) findViewById(R.id.btnMagDesliga);
        btnMagDesliga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auxBtnMagLiga = false;
            }
        });
        btnMagReabastece = (Button) findViewById(R.id.btnMagReabastece);
        btnMagReabastece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!auxBtnMagReabastece){
                    CLP.sEscreverBit("MX6.4", true);
                }else{
                    CLP.sEscreverBit("MX6.4", false);
                }
            }
        });

        btnMagVoltar = (Button) findViewById(R.id.btnMagVoltar);
        btnMagVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void atualizarCLP() throws Exception{
        statusMagazine = clp.lerBit("MX4.2");
        if(!first){
            auxBtnMagLiga = clp.lerBit("MX4.0");
            first = true;
        }
        if(first){
            clp.escreverBit("MX4.0", auxBtnMagLiga);
        }
        auxBtnMagReabastece=clp.lerBit("MX6.4");
    }

    @Override
    public void atualizarUI() {
        imgStatusMag.setImageResource(statusMagazine? R.drawable.led_verde2 : R.drawable.led_cinza);
        tvStatusMag.setText(statusMagazine?"Ligado":"Desligado");
        btnMagReabastece.setBackgroundColor(auxBtnMagReabastece ? getResources().getColor(R.color.azulClaro) : Color.GRAY);
        btnMagLiga.setBackgroundColor(auxBtnMagLiga?Color.GREEN:Color.GRAY);
        btnMagDesliga.setBackgroundColor(auxBtnMagLiga?Color.GRAY:Color.RED);
        }
    }
