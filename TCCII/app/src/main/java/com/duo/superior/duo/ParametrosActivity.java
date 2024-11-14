package com.duo.superior.duo;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duo.com.duo.R;
import com.duo.superior.duo.modbus.CLP;

public class ParametrosActivity extends duoActivity {

    boolean first;
    Button btnRegistrar, btnVoltar;
    EditText edtParamPunho, edtParamFaisca, edtParamDelaySaidaMag;
    private int comprimentoPunho, tempoDeteccaoFaisca, delaySaidaMagazine;

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_parametros);

        edtParamPunho = (EditText) findViewById(R.id.edtParamPunho);
        edtParamFaisca = (EditText) findViewById(R.id.edtParamFaisca);
        edtParamDelaySaidaMag = (EditText) findViewById(R.id.edtParamDelaySaidaMag);
        btnRegistrar = (Button) findViewById(R.id.btnParamRegistrar);

        btnVoltar = (Button) findViewById(R.id.btnParamVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void atualizarCLP() throws Exception {
            comprimentoPunho = clp.lerInteiro("MD146");
            tempoDeteccaoFaisca = clp.lerInteiro("MD177");
            delaySaidaMagazine = clp.lerInteiro("MD180");
     }

    public void Registrar(){
        try {
            CLP.sEscreverDWord("MD146", Integer.valueOf(edtParamPunho.getText().toString()));
            CLP.sEscreverDWord("MD177", Integer.valueOf(edtParamFaisca.getText().toString()));
            CLP.sEscreverDWord("MD180", Integer.valueOf(edtParamDelaySaidaMag.getText().toString()));
            Toast.makeText(getApplicationContext(), "Informações registradas!", Toast.LENGTH_SHORT).show();
        }catch (NumberFormatException ex){
            Toast.makeText(getApplicationContext(), "Falha no envio das informações. \nNumero em formato incorreto", Toast.LENGTH_SHORT).show();
        }
        }

    @Override
    public void atualizarUI() {
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Registrando informações", Toast.LENGTH_SHORT).show();
                Registrar();
            }
        });
        if (!first) {
            edtParamPunho.setText(String.valueOf(comprimentoPunho));
            edtParamFaisca.setText(String.valueOf(tempoDeteccaoFaisca));
            edtParamDelaySaidaMag.setText(String.valueOf(delaySaidaMagazine));
            first = true;
        }
    }
}