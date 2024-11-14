package com.duo.superior.duo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.duo.superior.duo.conectividade.Receita;
import com.duo.superior.duo.modbus.CLP;
import com.duo.superior.duo.modbus.asynchronous.Words;

import com.duo.com.duo.R;

public class ReceitaActivity extends duoActivity {

    //Variável indicativa de modo edição
    boolean editMode;
    int receitaNr;
    public static Receita rec;

    EditText edtFriso, edtGF, edtAltura,edtPasso,edtBase,edtComprimento,edtCI,edtCF,edtProfundidade,edtValeZero;
    Button btnRecMenu,btnRecEdit,btnSalvar,btnEletrodo,btnCarregar, btnRecVoltar;
    EditText edtNrRecSalvar, edtNrRecCarregar, edtValeEspecial, edtVales, senha;
    ImageView imgInicioFriso;
    View tela_login;
    boolean statusInicioFriso, auxCarregar;

    public int pegarNumeroInteiro(EditText text){

        String isNumber = text.getText().toString();

        if(isNumber.length()==0){
            return 0;
        }else
            return (int) Math.floor(Float.valueOf(isNumber));
    }

    public float pegarNumero(EditText text){

        String isNumber = text.getText().toString();

        if(isNumber.length()==0){
            return 0f;
        }else
            return Float.valueOf(isNumber);
    }



    @Override
    public void inicializarUI() {
        Global.developer = true;
        setContentView(R.layout.activity_receita);
        btnSalvar = (Button) findViewById(R.id.btnReceitaSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //escreveReceita();
                editMode=!editMode;
                Global.editarReceita=editMode;
                int saveNumber;
                try{
                    saveNumber=Integer.valueOf(edtNrRecSalvar.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ReceitaActivity.this, "Você deve selecionar aonde salvar a receita!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int num = sql.contarLinhas("select * from Receitas where id="+saveNumber);

                if(num>=0){
                    rec = new Receita();
                    rec.id = Integer.valueOf(edtNrRecSalvar.getText().toString());
                    rec.comprimento = pegarNumeroInteiro(edtComprimento);
                    rec.altura = pegarNumeroInteiro(edtAltura);
                    rec.base = pegarNumero(edtBase);
                    rec.cf = pegarNumeroInteiro(edtCF);
                    rec.ci = pegarNumeroInteiro(edtCI);
                    rec.passo = pegarNumero(edtPasso);
                    rec.primeiroFriso = pegarNumero(edtFriso);
                    rec.angulo = pegarNumeroInteiro(edtGF);
                    rec.profundidade= pegarNumeroInteiro(edtProfundidade);
                    rec.valeZero= pegarNumeroInteiro(edtValeZero);
                    rec.inicioFriso = statusInicioFriso?1:0;
                    rec.vale = pegarNumeroInteiro(edtVales);
                    rec.valeEspecial = pegarNumeroInteiro(edtValeEspecial);
                    sql.updateReceita(rec);
                    Toast.makeText(getApplicationContext(),"Receita salva!",Toast.LENGTH_LONG).show();

                    Words word = new Words(rec);
                    word.execute();
                    Global.receita = rec.id;

                    editMode=false;
                    habilitaCampos(false);
                }
            }
        });

        btnRecVoltar = (Button) findViewById(R.id.btnRecVoltar);
        btnRecVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

        btnEletrodo = (Button) findViewById(R.id.btnRecEletrodo);
        btnEletrodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.returnCls=ReceitaActivity.class;
                trocarTela(Eletrodos.class);
            }
        });

        edtFriso = (EditText) findViewById(R.id.edtFriso);
        edtAltura = (EditText) findViewById(R.id.edtAltura);
        edtGF = (EditText) findViewById(R.id.edtGF);
        edtCI = (EditText) findViewById(R.id.edtCI);
        edtCF = (EditText) findViewById(R.id.edtCF);
        edtPasso = (EditText) findViewById(R.id.edtPasso);
        edtBase = (EditText) findViewById(R.id.edtBase);
        edtComprimento = (EditText) findViewById(R.id.edtComprimento);
        edtNrRecSalvar = (EditText) findViewById(R.id.edtReceitaNr);
        edtNrRecCarregar = (EditText) findViewById(R.id.edtReceitaNr2);
        edtValeEspecial = (EditText) findViewById(R.id.edtValeEspecial);
        edtVales = (EditText) findViewById(R.id.edtVales);

        editMode = false;

        edtProfundidade =(EditText) findViewById(R.id.edtValeZero);
        edtValeZero =(EditText) findViewById(R.id.edtProf);


        //lerReceita();
        //Chamada de função para desabilitar campos para edição
        habilitaCampos(false);
        btnRecEdit = (Button) findViewById(R.id.btnRecEdit);
        btnRecEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode = !editMode;
                Global.editarReceita=editMode;
                habilitaCampos(editMode);
                if (editMode){
                    btnRecEdit.setText("EDIÇÃO");
                }else{
                    btnRecEdit.setText("EDITAR");
                }
            }
        });
        btnRecMenu = (Button) findViewById(R.id.btnRecMenu);
        btnRecMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                trocarTela(MainActivity.class);
                return false;
            }
        });

        btnCarregar = (Button) findViewById(R.id.btnReceitaCarregar);
        btnCarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auxCarregar=true;
                carregarReceitaL();

            }
        });

        imgInicioFriso = (ImageView) findViewById(R.id.imgReceitaFriso);
        imgInicioFriso.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN && editMode){
                    statusInicioFriso = !statusInicioFriso;
                }
                return false;
            }
        });

        edtNrRecCarregar.setText(String.valueOf(Global.receita));
        carregarReceitaL();

        habilitaCampos(Global.editarReceita);
        if(Global.editarReceita) {
            editMode=true;
            edtVales.setText(String.valueOf(Global.vale));
            edtValeEspecial.setText(String.valueOf(Global.valeEspecial));
            edtFriso.setText(String.valueOf(Global.primeiroFriso));
            edtAltura.setText(String.valueOf(Global.altura));
            edtBase.setText(String.valueOf(Global.base));
            edtCF.setText(String.valueOf(Global.cf));
            edtCI.setText(String.valueOf(Global.ci));
            edtProfundidade.setText(String.valueOf(Global.Fprofundidade));
            edtValeZero.setText(String.valueOf(Global.valeZero));
            edtComprimento.setText(String.valueOf(Global.comprimento));
            edtPasso.setText(String.valueOf(Global.passo));
            edtGF.setText(String.valueOf(Global.gf));
        }
    }

    @Override
    public void atualizarCLP() {

    }

    @Override
    public void atualizarUI() {
        if(!editMode){
            btnSalvar.setEnabled(false);
            btnCarregar.setEnabled(true);
        }else{
            btnSalvar.setEnabled(true);
            btnCarregar.setEnabled(false);
        }
    }

    void habilitaCampos(boolean habilita){
        edtNrRecCarregar.setEnabled(!habilita);
        edtNrRecCarregar.setBackgroundColor(!habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtNrRecCarregar.setTextColor(!habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtNrRecSalvar.setEnabled(habilita);
        edtNrRecSalvar.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtNrRecSalvar.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtFriso.setEnabled(habilita);
        edtFriso.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtFriso.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtAltura.setEnabled(habilita);
        edtAltura.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtAltura.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtGF.setEnabled(habilita);
        edtGF.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtGF.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtCF.setEnabled(habilita);
        edtCF.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtCF.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtCI.setEnabled(habilita);
        edtCI.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtCI.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtPasso.setEnabled(habilita);
        edtPasso.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtPasso.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtComprimento.setEnabled(habilita);
        edtComprimento.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtComprimento.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtBase.setEnabled(habilita);
        edtBase.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtBase.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtVales.setEnabled(habilita);
        edtVales.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtVales.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtValeEspecial.setEnabled(habilita);
        edtValeEspecial.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtValeEspecial.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtProfundidade.setEnabled(habilita);
        edtProfundidade.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtProfundidade.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtValeZero.setEnabled(habilita);
        edtValeZero.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtValeZero.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));



        edtVales.setEnabled(habilita);
        edtVales.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtVales.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));

        edtValeEspecial.setEnabled(habilita);
        edtValeEspecial.setBackgroundColor(habilita?getResources().getColor(R.color.green):getResources().getColor(R.color.white));
        edtValeEspecial.setTextColor(habilita?getResources().getColor(R.color.white):getResources().getColor(R.color.colorPrimary));
    }


    public void escreveReceita(){

        Assynch set = new Assynch("M2102",true);
        set.execute();

        set = new Assynch("M2103",true);
        set.execute();
    }


    public  void onBackPressed(){
        trocarTela(MainActivity.class);
    }
    public  void carregarReceitaL(){

        AlertDialog.Builder dialogContagemEletrodo = new AlertDialog.Builder(this).setTitle("Atenção")
                .setMessage("Deseja reiniciar a contagem de eletrodo?").setCancelable(false);
        dialogContagemEletrodo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CLP.sEscreverInteiro("MD176", 0);
                Toast.makeText(getApplicationContext(), "Receita carregada com sucesso!", Toast.LENGTH_SHORT).show();
                CLP.sEscreverBit("MX5.6", true);
            }
        });

        dialogContagemEletrodo.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Receita carregada com sucesso!", Toast.LENGTH_SHORT).show();
                CLP.sEscreverBit("MX5.6", true);
            }
        });

        AlertDialog.Builder dialogEsqueceFrisosChapiscados = new AlertDialog.Builder(this).setTitle("Atenção")
                .setMessage("Deseja esquecer os frisos que já foram chapiscados?").setCancelable(false);
        dialogEsqueceFrisosChapiscados.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CLP.sEscreverBit("MX7.6", true);
                dialogContagemEletrodo.show();
            }
        });

        dialogEsqueceFrisosChapiscados.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogContagemEletrodo.show();
            }
        });

        int numeroReceita= Integer.valueOf(edtNrRecCarregar.getText().toString());
        Global.receita = numeroReceita;
        sql.atualizarValorPreferencia("Preferencias", "Receita", numeroReceita);

        int numLinhas =0 ;
        try{
            numLinhas = sql.contarLinhas("select * from Receitas where id="+numeroReceita);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(numLinhas>0){
            Receita rec = sql.pegarReceita(numeroReceita);
            receitaNr = numeroReceita;
            edtVales.setText(String.valueOf(rec.vale));
            edtValeEspecial.setText(String.valueOf(rec.valeEspecial));
            edtFriso.setText(String.valueOf(rec.primeiroFriso));
            edtAltura.setText(String.valueOf(rec.altura));
            edtBase.setText(String.valueOf(rec.base));
            edtCF.setText(String.valueOf(rec.cf));
            edtCI.setText(String.valueOf(rec.ci));
            edtProfundidade.setText(String.valueOf(rec.profundidade));
            edtValeZero.setText(String.valueOf(rec.valeZero));
            edtComprimento.setText(String.valueOf(rec.comprimento));
            edtGF.setText(String.valueOf(rec.angulo));
            edtPasso.setText(String.valueOf(rec.passo));
            statusInicioFriso=rec.inicioFriso==1?true:false;
            imgInicioFriso.setImageResource(statusInicioFriso?R.drawable.led_on:R.drawable.led_off);
            edtNrRecSalvar.setText(String.valueOf(receitaNr));
            if (auxCarregar){
                dialogEsqueceFrisosChapiscados.show();
                auxCarregar=false;
            }
            escreveReceita();
            CLP.sEscreverBit("MX4.1", true);
        }else{
            CLP.sEscreverBit("MX4.1", false);
            Toast.makeText(getApplicationContext(),"Receita não existe!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void trocarTela(Class<?> cls){
        try {
            Global.comprimento = pegarNumero(edtComprimento);
            Global.altura = pegarNumero(edtAltura);
            Global.base = pegarNumero(edtBase);
            Global.cf = pegarNumero(edtCF);
            Global.ci = pegarNumero(edtCI);
            Global.passo = pegarNumero(edtPasso);
            Global.primeiroFriso = pegarNumero(edtFriso);
            Global.angulo = pegarNumero(edtGF);
            Global.Fprofundidade = pegarNumero(edtProfundidade);
            Global.valeZero = pegarNumero(edtValeZero);
            Global.gf = pegarNumero(edtGF);
            Global.vale = pegarNumeroInteiro(edtVales);
            Global.valeEspecial = pegarNumeroInteiro(edtValeEspecial);
        }catch (Exception e){
            e.printStackTrace();
        }
        // rec.inicioFriso = statusInicioFriso;
        super.trocarTela(cls);
    }
}
 