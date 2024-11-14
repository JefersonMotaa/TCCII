package com.duo.superior.duo;
import com.duo.superior.duo.conectividade.Eletrodo;
import com.duo.superior.duo.modbus.asynchronous.Words;
import com.duo.com.duo.R;
import com.duo.superior.duo.modbus.InterfaceCLP;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;

public class Eletrodos extends duoActivity {
    public static EditText edtEletrodoAngulo,edtEletrodoComp,edtEletrodoTempo,edtEletrodoHaste,edtEletrodoUtil,edtEletrodoPeso, edtUtilizacaoFlancoIni,edtEletrodoTempoIni;
    Button btnCalc,btnVoltar,btnRegistro,btnEditar;
    TextView scrollLoop;
    Timer timer;

    boolean editar;

    @Override
    public void onBackPressed() {
        trocarTela(Global.returnCls);
    }

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_eletrodo);
        edtEletrodoAngulo = (EditText) findViewById(R.id.edtEletrodoAngulo);
        edtEletrodoComp = (EditText) findViewById(R.id.edtEletrodoComp);

        edtEletrodoTempo = (EditText) findViewById(R.id.edtEletrodoTempo);
        edtEletrodoHaste = (EditText) findViewById(R.id.edtEletrodoHaste);
        edtEletrodoPeso = (EditText) findViewById(R.id.edtEletrodoPeso);
        edtEletrodoUtil = (EditText) findViewById(R.id.edtEletrodoUtil);
        edtUtilizacaoFlancoIni = (EditText) findViewById(R.id.edtEletrodoFlancoIni);
        edtEletrodoTempoIni = (EditText) findViewById(R.id.edtEletrodoTempoIni);

        //btnCalc = (Button) findViewById(R.id.btnEletrodoCalc);
        btnEditar = (Button) findViewById(R.id.btnEditar);

        edtEletrodoAngulo.setFilters(new InputFilter[]{ new FiltroMinMax(1,90)});
        edtEletrodoComp.setFilters(new InputFilter[]{ new FiltroMinMax(0,100000)});
        edtEletrodoHaste.setFilters(new InputFilter[]{ new FiltroMinMax(1,10000)});
        edtEletrodoPeso.setFilters(new InputFilter[]{ new FiltroMinMax(1,500)});
        edtEletrodoUtil.setFilters(new InputFilter[]{ new FiltroMinMax(1,100)});
        edtUtilizacaoFlancoIni.setFilters(new InputFilter[]{ new FiltroMinMax(1,100)});

        toggleFields(false);


        //ActionBar customizado
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custombar,null);
        //Configura a ActionBar
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        editar = false;
        scrollLoop=mCustomView.findViewById(R.id.scrollLoop);
        scrollLoop.setText(InterfaceCLP.listaAlarmes!=null? InterfaceCLP.listaAlarmes:"Robô Desligado");
        scrollLoop.setSelected(true);

        btnEditar.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editar=true;
                Global.editarEletrodo=editar;
                toggleFields(true);
                return false;
            }
        });

        btnRegistro = (Button) findViewById(R.id.btnEletrodoRegistro);
        btnRegistro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(editar) {
                        try {

                            btnRegistro.setBackgroundColor(getResources().getColor(R.color.green));
                            Toast.makeText(Eletrodos.this, "Registrando...", Toast.LENGTH_SHORT).show();
                            escreve();
                            editar=false;
                            Global.editarEletrodo=editar;

                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                    }else
                        Toast.makeText(getBaseContext(),"A edição não está habilitada.",Toast.LENGTH_SHORT).show();
                }

                if(motionEvent.getAction()==MotionEvent.ACTION_UP){


                    btnRegistro.setBackgroundColor(getResources().getColor(R.color.amareloBotao));

                }
                return false;
            }
        });

        btnVoltar = (Button) findViewById(R.id.btnEletrodoVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        if(!Global.editarEletrodo) {
            ler();
        }
        if(Global.editarEletrodo) {
            editar=true;
            toggleFields(true);
            edtEletrodoAngulo.setText(String.valueOf(EletrodoReceita.angulo));
            edtEletrodoComp.setText(String.valueOf(EletrodoReceita.comprimento/100));
            edtEletrodoHaste.setText(String.valueOf(EletrodoReceita.haste/100));
            edtEletrodoPeso.setText(String.valueOf(EletrodoReceita.peso));
            edtEletrodoTempo.setText(String.valueOf(EletrodoReceita.tempo));
            edtEletrodoTempoIni.setText(String.valueOf(EletrodoReceita.tempoFlancoIni));
            edtEletrodoUtil.setText(String.valueOf(EletrodoReceita.utilizacao));
            edtUtilizacaoFlancoIni.setText(String.valueOf(EletrodoReceita.utilizacaoFlancoIni));
        }
    }

    public int pegarNumeroInteiro(EditText text){

        String isNumber = text.getText().toString();

        if(isNumber.length()==0){
            return 0;
        }else
            return Integer.valueOf(isNumber);
    }

    public float pegarNumero(EditText text){

        String isNumber = text.getText().toString();

        if(isNumber.length()==0){
            return 0f;
        }else
            return Float.valueOf(isNumber);
    }

    void escreve(){
try {

    Eletrodo el = new Eletrodo();
    el.angulo = pegarNumeroInteiro(edtEletrodoAngulo);
    el.comprimento = pegarNumeroInteiro(edtEletrodoComp);
    el.tempo = pegarNumeroInteiro(edtEletrodoTempo);
    el.haste = pegarNumeroInteiro(edtEletrodoHaste);
    el.peso = pegarNumeroInteiro(edtEletrodoPeso);
    el.utilizacaoFlancoIni = pegarNumeroInteiro(edtUtilizacaoFlancoIni);
    el.tempoFlancoIni = pegarNumeroInteiro(edtEletrodoTempoIni);
    el.utilizacao = pegarNumeroInteiro(edtEletrodoUtil);
    el.id = 0;

    sql.updateRecEletrodo(el);
    toggleFields(false);

    //Usamos outra thread para escrever a receita
    Words escrever = new Words(el);
    escrever.execute();

}catch (Exception e){
   e.printStackTrace();
    Toast.makeText(this,"Erro ao salvar.",Toast.LENGTH_SHORT);
}

    }

    void ler(){

        Eletrodo el = sql.pegarRecEletrodo(0);
        edtEletrodoAngulo.setText(String.valueOf(el.angulo));
        edtEletrodoComp.setText(String.valueOf(el.comprimento));
        edtEletrodoTempo.setText(String.valueOf(el.tempo));
        edtEletrodoHaste.setText(String.valueOf(el.haste));
        edtEletrodoPeso.setText(String.valueOf(el.peso));
        edtUtilizacaoFlancoIni.setText(String.valueOf(el.utilizacaoFlancoIni));
        edtEletrodoTempoIni.setText(String.valueOf(el.tempoFlancoIni));
        edtEletrodoUtil.setText(String.valueOf(el.utilizacao));

    }

    public void toggleFields(boolean status){

        edtEletrodoAngulo.setEnabled(status);
        edtEletrodoAngulo.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoAngulo.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoComp.setEnabled(status);
        edtEletrodoComp.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoComp.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoTempo.setEnabled(status);
        edtEletrodoTempo.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoTempo.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoHaste.setEnabled(status);
        edtEletrodoHaste.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoHaste.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoPeso.setEnabled(status);
        edtEletrodoPeso.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoPeso.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoUtil.setEnabled(status);
        edtEletrodoUtil.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoUtil.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtUtilizacaoFlancoIni.setEnabled(status);
        edtUtilizacaoFlancoIni.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtUtilizacaoFlancoIni.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtEletrodoTempoIni.setEnabled(status);
        edtEletrodoTempoIni.setBackgroundColor(status?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtEletrodoTempoIni.setTextColor(status?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));
    }

    public void atualizarCLP(){
    }

    @Override
    public void atualizarUI() {
        btnEditar.setBackgroundColor(editar?0xFF00FF00:0xFF808080);
        if(!editar) {
            ler();
        }
    }

    public void salvarNoDB(Eletrodo el){


    }

}
