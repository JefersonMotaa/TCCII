package com.duo.superior.duo;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.duo.superior.duo.modbus.CLP;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import com.duo.com.duo.R;

public class ReferenciaActivity extends duoActivity {

    boolean bHomeZOk, bHomeXOk, bHomeCOk, bHomeGOk, bStatusZ, bStatusX, bStatusC, bStatusG, bSensorZ, bSensorX, bSensorC, bSensorG, statusManivela, bHomingZ, bHomingX,
            bHomingC, bHomingG, blink;

    int posZ,posX, referenciaZ, referenciaX;
    int dumping = 0;
    float posC, posG;

    TextView  tvPosAtualZ, tvPosAtualX, tvPosAtualC, tvPosAtualG,txtHomeZ, txtHomeX, txtHomeC, txtHomeG,txtZ, txtX, txtC, txtG,tvStatusManivela,
              txtUZ,txtUX, txtUC,tvReferenciaZ, tvReferenciaX;

    ImageView imgStatusZ, imgStatusX, imgStatusC, imgStatusG,imgRefZ, imgRefX, imgRefC, imgRefG,imgHomeZ, imgHomeX, imgHomeC, imgHomeG;

    Button btnAtivaManivela, btnRReceita, btnRManual, btnRPosicoes, btnRMenu, btnRVoltar, btnZerarZ, btnZerarX, btnRefMagazine;

    ImageView arm, guide;

    public void inicializarUI() {
        setContentView(R.layout.activity_referencia);
        //ActionBar customizado
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custombar, null);
        ImageView mImage = (ImageView) mCustomView.findViewById(R.id.imgLogo);
        mImage.setImageResource(R.drawable.duot);
        imageErro = (ImageView) mCustomView.findViewById(R.id.imgErro);

        //CLP.sEscreverBit("MX0.0", false);
        arm = (ImageView) findViewById(R.id.arm);
        tvStatusManivela = (TextView) findViewById(R.id.tvStatusManivela);


        tvPosAtualZ = (TextView) findViewById(R.id.tvPosAtualZ);
        tvPosAtualX = (TextView) findViewById(R.id.tvPosAtualX);
        tvPosAtualC = (TextView) findViewById(R.id.tvPosAtualC);
        tvPosAtualG = (TextView) findViewById(R.id.tvPosAtualG);

        txtZ = (TextView) findViewById(R.id.textView);
        txtX = (TextView) findViewById(R.id.textView1);
        txtC = (TextView) findViewById(R.id.textView2);
        txtG = (TextView) findViewById(R.id.textView200);

        txtUZ = (TextView) findViewById(R.id.textView65);
        txtUX = (TextView) findViewById(R.id.textView66);
        txtUC = (TextView) findViewById(R.id.textView67);

        txtHomeZ = (TextView) findViewById(R.id.txtHomeZ);
        txtHomeX = (TextView) findViewById(R.id.txtHomeX);
        txtHomeC = (TextView) findViewById(R.id.txtHomeC);
        txtHomeG = (TextView) findViewById(R.id.txtHomeG);

        tvReferenciaZ = (TextView) findViewById(R.id.tvReferenciaZ);
        tvReferenciaX = (TextView) findViewById(R.id.tvReferenciaX);

        imgRefZ = (ImageView) findViewById(R.id.imgStatusRefZ);
        imgRefX = (ImageView) findViewById(R.id.imgStatusRefX);
        imgRefC = (ImageView) findViewById(R.id.imgStatusRefC);
        imgRefG = (ImageView) findViewById(R.id.imgStatusRefG);

        imgStatusZ = (ImageView) findViewById(R.id.imgStatusZAtivo);
        imgStatusX = (ImageView) findViewById(R.id.imgStatusXAtivo);
        imgStatusC = (ImageView) findViewById(R.id.imgStatusCAtivo);
        imgStatusG = (ImageView) findViewById(R.id.imgStatusGAtivo);

        btnZerarZ = (Button) findViewById(R.id.btnOpZerarZ);
        btnZerarZ.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX4.5",true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX4.5",false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX4.5",false);
                        break;
                }
                return false;
            }
        });

        btnZerarX = (Button) findViewById(R.id.btnOpZerarX);
        btnZerarX.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX4.4",true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX4.4",false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX4.4",false);
                        break;
                }
                return false;
            }
        });

        btnRMenu= (Button) findViewById(R.id.btnRefMenu);
        btnRMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocarTela(MainActivity.class);
            }
        });

        btnRManual = (Button) findViewById(R.id.btnRManual);
        btnRManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocarTela(ManualActivity.class);
            }
        });

        btnRReceita = (Button) findViewById(R.id.btnRReceita);
        btnRReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocarTela(ReceitaActivity.class);
            }
        });


        btnRPosicoes = (Button) findViewById(R.id.btnRPosicoes);
        btnRPosicoes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                exibirDialogSenha();
                return false;
            }
        });

        btnAtivaManivela = (Button) findViewById(R.id.btnAtivaManivela);
        btnAtivaManivela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trocarTela(VelRef.class);
            }
        });

        btnRVoltar = (Button) findViewById(R.id.btnRefVoltar);
        btnRVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });

        imgHomeZ = (ImageView) findViewById(R.id.imgStatusZHoming);
        imgHomeZ.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX1.1",true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX1.1",false);
                        bHomingZ = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX1.1",false);
                        break;
                }
                return false;
            }
        });

        imgHomeX = (ImageView) findViewById(R.id.imgStatusXHoming);
        imgHomeX.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX1.2",true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX1.2",false);
                        bHomingX = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX1.2",false);
                        break;
                }
                return false;
            }
        });

        imgHomeC = (ImageView) findViewById(R.id.imgStatusCHoming);
        imgHomeC.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX1.3",true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX1.3",false);
                        bHomingC = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX1.3",false);
                        break;
                }
                return false;
            }
        });

    imgHomeG = (ImageView) findViewById(R.id.imgStatusGHoming);
        imgHomeG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        CLP.sEscreverBit("MX1.4", true);
                        break;
                    case MotionEvent.ACTION_UP:
                        CLP.sEscreverBit("MX1.4", false);
                        bHomingG  = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        CLP.sEscreverBit("MX1.4", false);
                        break;
                }
                return false;
            }
    });
}

    @Override
    protected void onStart() {
        CLP.sEscreverBit("MX3.2", true);
        super.onStart();
    }

    public void atualizarCLP() throws Exception {

        boolean enderecos[] = clp.lerSequencia("MW0", 32);

        statusManivela =  enderecos[0];

        bHomeZOk = enderecos[13];
        bHomeXOk = enderecos[14];
        bHomeCOk = enderecos[15];
        bHomeGOk = enderecos[16];

        bSensorZ = enderecos[17];
        bSensorX = enderecos[18];
        bSensorC = enderecos[19];
        bSensorG = enderecos[20];

        bStatusZ = enderecos[21];
        bStatusX = enderecos[22];
        bStatusC = enderecos[23];
        bStatusG = enderecos[24];

        posZ        = clp.lerInteiro("MD100");
        posX        = clp.lerInteiro("MD101");
        posC        = clp.lerFloat("MD102");
        posG        = clp.lerFloat("MD103");

        referenciaZ = clp.lerInteiro("MD158");
        referenciaX = clp.lerInteiro("MD159");

    }

    @Override
    public void onBackPressed(){
        timer.cancel();
        CLP.sEscreverBit("MX3.2",false);
        Intent it = new Intent(getBaseContext(), MainActivity.class);
        startActivity(it);
    }
    private void exibirDialogSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Digite a senha");

        // Inflar o layout personalizado para o AlertDialog
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_senha, null);
        builder.setView(view);

        final EditText edtSenha = view.findViewById(R.id.edtSenha);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String senhaDigitada = edtSenha.getText().toString();
                // Verificar a senha aqui
                if (senhaCorreta(senhaDigitada)) {
                    // Senha correta, chame a próxima tela
                    abrirProximaTela();
                } else {
                    // Senha incorreta, exiba uma mensagem ou tome a ação apropriada
                    Toast.makeText(ReferenciaActivity.this, "Senha incorreta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private boolean senhaCorreta(String senha) {
        // Lógica para verificar se a senha está correta
        // Substitua por sua própria lógica de verificação
        return "2017dc".equals(senha);
    }

    private void abrirProximaTela() {
        trocarTela(PosicoesActivity.class);
    }
    public void atualizarUI() {

        imgRefZ.setImageResource(bSensorZ?R.drawable.btn_verde:R.drawable.led_off);
        imgRefX.setImageResource(bSensorX?R.drawable.btn_verde:R.drawable.led_off);
        imgRefC.setImageResource(bSensorC?R.drawable.btn_verde:R.drawable.led_off);
        imgRefG.setImageResource(bSensorG?R.drawable.btn_verde:R.drawable.led_off);

        imgStatusZ.setImageResource(bStatusZ?R.drawable.btn_verde:R.drawable.led_off);
        imgStatusX.setImageResource(bStatusX?R.drawable.btn_verde:R.drawable.led_off);
        imgStatusC.setImageResource(bStatusC?R.drawable.btn_verde:R.drawable.led_off);
        imgStatusG.setImageResource(bStatusG?R.drawable.btn_verde:R.drawable.led_off);

        tvPosAtualZ.setText(String.valueOf(posZ));
        tvPosAtualX.setText(String.valueOf(posX));
        tvPosAtualC.setText(String.format("%.2f",posC));
        tvPosAtualG.setText(String.format("%.2f",posG));

        tvReferenciaZ.setText(String.valueOf(referenciaZ));
        tvReferenciaX.setText(String.valueOf(referenciaX));


        if(bHomingZ){
            imgHomeZ.setBackgroundColor(getResources().getColor(blink?R.color.green:R.color.vermelho));
            txtHomeZ.setText("Home");
        }

        if(bHomingX){
            imgHomeX.setBackgroundColor(getResources().getColor(blink?R.color.green:R.color.vermelho));
            txtHomeX.setText("Home");
        }

        if(bHomingC){
            imgHomeC.setBackgroundColor(getResources().getColor(blink?R.color.green:R.color.vermelho));
            txtHomeC.setText("Home");
        }

        if(bHomingG){
            imgHomeG.setBackgroundColor(getResources().getColor(blink?R.color.green:R.color.vermelho));
            txtHomeG.setText("Home");
        }

        if(bHomeZOk){bHomingZ=false;txtHomeZ.setText("Home \n OK");imgHomeZ.setImageResource(R.drawable.btn_verde);}
        if(bHomeXOk){bHomingX=false;txtHomeX.setText("Home \n OK");imgHomeX.setImageResource(R.drawable.btn_verde);}
        if(bHomeCOk){bHomingC=false;txtHomeC.setText("Home \n OK");imgHomeC.setImageResource(R.drawable.btn_verde);}
        if(bHomeGOk){bHomingG=false;txtHomeG.setText("Home \n OK");imgHomeG.setImageResource(R.drawable.btn_verde);}

        if(dumping>=20){

            dumping = 0;
            blink =! blink;
        }else{
            dumping++;
        }


    }
}