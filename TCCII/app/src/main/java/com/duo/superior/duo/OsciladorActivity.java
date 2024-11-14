package com.duo.superior.duo;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duo.com.duo.R;
import com.duo.superior.duo.conectividade.DxSQLite;
import com.duo.superior.duo.modbus.CLP;

import java.util.Timer;

public class  OsciladorActivity extends duoActivity {

    private  int frequenciaOscilador, distancia;
    public TextView textData, scrollLoop, txtOscVel,txtOscDist;
    SeekBar seekBarVel, seekBarDist;
    ImageView imageOsciladorAtiva, imageOsciladorDesativa;
    EditText editFrequenciaOscilador, editDistanciaOscilacao;
    Button btnOsciladorRegistrar, btnOscVoltar;
    boolean statusOscilador, first,firstCLP;
    Timer timer;
    boolean oscAtivo;

    public void atualizarCLP() throws Exception{
        statusOscilador = clp.lerBit("MX6.3");
        distancia = clp.lerInteiro("MD160");
        frequenciaOscilador=clp.lerInteiro("MD161");

    }

    @Override
    public void atualizarUI() {
        imageOsciladorAtiva.setImageResource(statusOscilador ? R.drawable.led_on : R.drawable.led_off);
        imageOsciladorDesativa.setImageResource(statusOscilador ? R.drawable.led_off : R.drawable.led_on);
        if (!first) {
            editDistanciaOscilacao.setText(String.valueOf(distancia));
            editFrequenciaOscilador.setText(String.valueOf(frequenciaOscilador));
            first=true;
        }
    }

    public void registrar(){
        CLP.sEscreverInteiro("MD160", Integer.valueOf(editDistanciaOscilacao.getText().toString()));
        CLP.sEscreverInteiro("MD161", Integer.valueOf(editFrequenciaOscilador.getText().toString()));
    }


    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_oscilador);

        imageOsciladorAtiva     = (ImageView) findViewById(R.id.imageOsciladorAtiva);
        imageOsciladorDesativa  = (ImageView) findViewById(R.id.imageOsciladorDesativa);
        editFrequenciaOscilador = (EditText) findViewById(R.id.edtFrequenciaOscilacao);
        editDistanciaOscilacao  = (EditText) findViewById(R.id.edtDistanciaOscilacao);
        btnOsciladorRegistrar   = (Button) findViewById(R.id.btnOscReg);

        editDistanciaOscilacao.setText(String.valueOf(distancia));
        editFrequenciaOscilador.setText(String.valueOf(frequenciaOscilador));


/*        seekBarDist = (SeekBar) findViewById(R.id.seekBarDist);
        txtOscDist = (TextView) findViewById(R.id.txtOscDist);

 seekBarVel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                velocidade=i;
                txtOscVel.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
seekBarDist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i=distancia;
                txtOscDist.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
   */

        btnOscVoltar = (Button) findViewById(R.id.btnOscVoltar);
        btnOscVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

        imageOsciladorAtiva.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    CLP.sEscreverBit("MX6.3", true);
                }
                return false;
            }
        });

        imageOsciladorDesativa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    CLP.sEscreverBit("MX6.3", false);
                }
                return false;
            }
        });

        btnOsciladorRegistrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        registrar();
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        Toast.makeText(OsciladorActivity.this, "Registrado!", Toast.LENGTH_SHORT).show();

                        break;
                    case MotionEvent.ACTION_UP:
                        Toast.makeText(OsciladorActivity.this, "Registrado!", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}
