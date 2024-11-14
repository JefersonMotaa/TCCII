package com.duo.superior.duo;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.duo.com.duo.R;

public class EditarActivity extends duoActivity {

    Button btnReferencia, btnReceitas, btnEdtVoltar;

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_editar);
        //Chama a tela de referência
        btnReferencia = (Button) findViewById(R.id.btnReferencia);
        btnReferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trocarTela(ReferenciaActivity.class);
            }
        });

        //Chama a tela de Receitas
        btnReceitas = (Button) findViewById(R.id.btnReceitas);
        btnReceitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trocarTela(ReceitaActivity.class);
            }
        });

        //Botão voltar
        btnEdtVoltar = (Button) findViewById(R.id.btnEdtVoltar);
        btnEdtVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
    }



    @Override
    public void atualizarCLP() throws Exception {

    }

    @Override
    public void atualizarUI() {

    }
}