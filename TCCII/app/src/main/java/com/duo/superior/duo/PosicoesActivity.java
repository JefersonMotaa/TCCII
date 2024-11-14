package com.duo.superior.duo;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.duo.superior.duo.modbus.CLP;
import com.duo.com.duo.R;

public class PosicoesActivity extends duoActivity{

    Button[] reg, btnJog;
    Button btnRoloEntrada, btnRoloSaida;
    TextView[] label, pos;
    EditText[] edtTexts;
    LinearLayout[] lLayouts;
    LinearLayout focusedLayout;
    ScrollView scrollViewRoloEntrada;
    String  posRoloEntrada[] = {"MD104", "MD105", "MD110", "MD114", "MD106", "MD107", "MD111", "MD112", "MD108", "MD109", "MD113"},
            posRoloSaida[]  = {"MD162", "MD163", "MD168", "MD172", "MD164", "MD165", "MD169", "MD170", "MD166", "MD167", "MD171"};
    boolean first, firstUI, escrever, escrito, saida, bMostrar;

    int selected = 0;

    float posicoes[];

    String eixosNome[] = {"Z", "X", "C", "G"};
    String unidades[] = {"mm", "mm", "º", "º"};
    String memJog[] = {"MX0.1","MX0.2","MX0.3","MX0.4","MX0.5","MX0.6","MX0.7","MX1.0"};

    int[] jogLabels = {R.id.btnPZ, R.id.btnPX, R.id.btnPC, R.id.btnPG,R.id.btnPPlus, R.id.btnPMinus};

    String[] posLidas;

    int[] txtLabels = {R.id.txtP1, R.id.txtP2,R.id.txtP3, R.id.txtP4, R.id.txtP5, R.id.txtP6,
            R.id.txtP7, R.id.txtP8, R.id.txtP9, R.id.txtP10, R.id.txtP11};

    int[] posLabels = {R.id.tvPPosZ, R.id.tvPPosX,R.id.tvPPosC, R.id.tvPPosG};

        String labels[] = {" Inicio Z"  ," Descarte Z"  ," 'Descarte' C"  ," Ângulo descarte G" ," Pós Descarte Z"  ," Pega Z"  ," Pega C"  ," Pega G"  ," C1"      ," C2"      , " G 90"      };
    String memPos[];
    int[] edtAxis = {0,0,2,3,0,0,2,3,2,2,3};

    int[] edtLabels = {R.id.edtP1, R.id.edtP2, R.id.edtP3, R.id.edtP4, R.id.edtP5, R.id.edtP6,
            R.id.edtP7, R.id.edtP8, R.id.edtP9, R.id.edtP10,R.id.edtP11};

    int[] regLabels = {R.id.btnP1,R.id.btnP2, R.id.btnP3,R.id.btnP4,R.id.btnP5,R.id.btnP6, R.id.btnP7,
            R.id.btnP8,R.id.btnP9, R.id.btnP10, R.id.btnP11};

    int[] llabels = {R.id.llP1,R.id.llP2, R.id.llP3,R.id.llP4,R.id.llP5,R.id.llP6, R.id.llP7,
            R.id.llP8,R.id.llP9, R.id.llP10, R.id.llP11};

    float[] posEixos;

    float valores[];

    int eixos[];

    @Override
    public void inicializarUI() {
        posLidas = new String[20];
        memPos = posRoloEntrada;
        saida = false;
        setContentView(R.layout.activity_posicoes);
        label = new TextView[labels.length];
        pos = new TextView[4];
        reg = new Button[label.length];
        edtTexts = new EditText[labels.length];
        lLayouts = new LinearLayout[llabels.length];

        btnJog = new Button[6];

        posEixos = new float[4];
        posicoes = new float[labels.length];

        for(int i=0; i<6;i++){
            if(i>=4){
                btnJog[i] = (Button) findViewById(jogLabels[i]);
                final int j = i;

                btnJog[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                btnJog[j].setTextColor(getResources().getColor(R.color.white));
                                pos[selected].setBackgroundColor(getResources().getColor(R.color.green));
                                CLP.sEscreverBit(j==4?memJog[selected*2]:memJog[selected*2+1],true);
                                break;
                            case MotionEvent.ACTION_UP:
                                btnJog[j].setTextColor(getResources().getColor(R.color.colorPrimary));
                                pos[selected].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                CLP.sEscreverBit(j==4?memJog[selected*2]:memJog[selected*2+1],false);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                btnJog[j].setTextColor(getResources().getColor(R.color.colorPrimary));
                                CLP.sEscreverBit(j==4?memJog[selected*2]:memJog[selected*2+1],false);
                                break;
                        }
                        return false;
                    }
                });
                continue;
            }else{
                final int j = i;
                btnJog[i] = (Button) findViewById(jogLabels[i]);
                btnJog[i].setBackgroundColor(getResources().getColor(selected==i?R.color.green:R.color.gray));
                btnJog[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        btnJog[selected].setBackgroundColor(getResources().getColor(R.color.gray));
                        btnJog[j].setBackgroundColor(getResources().getColor(R.color.green));
                        selected = j;
                        return false;
                    }
                });
            }
        }

        for(int i=0; i<labels.length;i++){
            label[i] = (TextView) findViewById(txtLabels[i]);
            label[i].setText(labels[i]+":");
            lLayouts[i] = (LinearLayout) findViewById(llabels[i]);
        }

        for(int i=0;i<4;i++){
            pos[i] = (TextView) findViewById(posLabels[i]);
        }

        for(int i=0;i<labels.length;i++){
            edtTexts[i] = (EditText) findViewById(edtLabels[i]);
            edtTexts[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            edtTexts[i].setTextColor(getResources().getColor(R.color.white));
            edtTexts[i].setText(String.format("%.2f", posicoes[i]));

            final int j = i;

            edtTexts[i].setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View view, boolean focused) {
                    if(focused){
                        label[j].setBackgroundColor(getResources().getColor(R.color.green));
                        edtTexts[j].setBackgroundColor(getResources().getColor(R.color.green));
                        edtTexts[j].setTextColor(getResources().getColor(R.color.white));
                        lLayouts[j].setBackgroundColor(getResources().getColor(R.color.green));
                    }else{
                        label[j].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        edtTexts[j].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        edtTexts[j].setTextColor(getResources().getColor(R.color.white));
                        lLayouts[j].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
            });

            reg[i] = (Button) findViewById(regLabels[i]);
            reg[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        int axis = edtAxis[j];
                        edtTexts[j].setText(axis>=2?String.format("%.2f",posEixos[axis]):String.format("%d",(int) posEixos[axis]));
                    }
                    return false;
                }
            });
        }

        Button submit = (Button) findViewById(R.id.btnSubmit);
        submit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getApplicationContext(),"Copiando dados para o CLP...",Toast.LENGTH_SHORT).show();

                    escrever = true;
                }
                return false;
            }
        });

        btnRoloEntrada = (Button) findViewById(R.id.btnPosRoloEntrada);
        btnRoloEntrada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btnRoloEntrada.setBackgroundColor(getResources().getColor(R.color.green));
                    btnRoloSaida.setBackgroundColor(getResources().getColor(R.color.gray));
                    memPos=posRoloEntrada;
                    first=false;
                }
                return false;
            }
        });

        btnRoloSaida = (Button) findViewById(R.id.btnPosRoloSaida);
        btnRoloSaida.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btnRoloSaida.setBackgroundColor(getResources().getColor(R.color.green));
                    btnRoloEntrada.setBackgroundColor(getResources().getColor(R.color.gray));
                    memPos=posRoloSaida;
                    first=false;
                }
                return false;
            }
        });


        btnRoloEntrada.setBackgroundColor(getResources().getColor(R.color.green));
        btnRoloSaida.setBackgroundColor(getResources().getColor(R.color.gray));



    }


    @Override
    public void atualizarCLP() throws Exception {

        if(!first){
            //lerPosicoes();
            ler();
            clp.escreverBit("MX0.0",true);
            first=true;
            firstUI=false;
        }

        if(escrever){
            escreverPosicoes();
            escrito = true;
            escrever = false;
        }
        posEixos[0] = clp.lerInteiro("MD100");
        posEixos[1] = clp.lerInteiro("MD101");
        posEixos[2] = clp.lerFloat("MD102");
        posEixos[3] = clp.lerFloat("MD103");

    }


    @Override
    public void atualizarUI() {

        if(!firstUI){
            mostrar();
            firstUI=true;
        }

        if(escrito){
            Toast.makeText(this,"Escrito com sucesso!",Toast.LENGTH_SHORT).show();
            escrito = false;
        }

        for(int i=0;i<4;i++){
            pos[i] = (TextView) findViewById(posLabels[i]);
            if (i<2) {
                pos[i].setText(String.format(" %s ->  %d %s", eixosNome[i], (int)posEixos[i], unidades[i]));
            }else{
                pos[i].setText(String.format(" %s ->  %.2f %s", eixosNome[i], posEixos[i], unidades[i]));
            }
        }


    }

    public void mostrar(){
        for(int i=0; i<labels.length;i++){
            if(i<2 || i>3 && i<6) {
                edtTexts[i].setText(posLidas[i]);
                continue;
            }
            edtTexts[i].setText(posLidas[i]);

        }
    }

    public void ler() throws Exception{
        for(int i=0; i<labels.length;i++){
            if(i<2 || i>3 && i<6) {
                posLidas[i] = String.format("%d", clp.lerInteiro(memPos[i]));
                continue;
            }
            posLidas[i] = String.format("%.2f",clp.lerFloat(memPos[i]));
        }

        first = false;
    }



    public void lerPosicoes()throws Exception{
        if(saida == false){
            memPos = posRoloEntrada;
        }else{
            memPos = posRoloSaida;
        }

        for(int i=0; i<labels.length;i++){
            if(i<2 || i>3 && i<6) {
                edtTexts[i].setText(String.format("%d", clp.lerInteiro(memPos[i])));
                continue;
            }
            edtTexts[i].setText(String.format("%.2f",clp.lerFloat(memPos[i])));

        }
    }

    public void escreverPosicoes() throws Exception{

        for(int i=0; i<labels.length;i++){

            if(i<2 || i>3 && i<6){
                clp.escreverInteiro(memPos[i],(int) Float.parseFloat(edtTexts[i].getText().toString()));
                continue;
            }
            clp.escreverFloat(memPos[i],Float.parseFloat(edtTexts[i].getText().toString().replace(',','.')));
        }
    }

}