package com.duo.superior.duo;

import static com.duo.superior.duo.Global.parar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.duo.superior.duo.modbus.CLP;
import com.duo.com.duo.R;
import com.duo.superior.duo.rbs.Operacao;


public class OperacaoActivity extends duoActivity{

        public TextView txtFlanco, tvPosAtualZ, tvPosAtualX, tvPosAtualC, tvPosAtualG, tvProgressChapisco, tvProgressFrisos, tvOpPassoOp, tvOpEletConsumido, tvOpPesoEletrodo, edtValeAtual, tvOpIntervalo, tvOpFrisoE, tvOpFrisoD, tvOpInicia, tvOpPara, tvOpReceita;
        ImageButton imageOpInicia, imageOpPara, imageOpInter;
        ImageView imageD, imageE, imageZAtivo, imageXAtivo, imageCAtivo, imageGAtivo;
        Button btnOscilador, btnVale, btnJato, btnOpVoltar, btnOpSimuEletrodo, btnOpSimuFaisca, btnOpMagazine, btnOpReceita, btnVelocidades, btnOpAcompanhamento;
        ProgressBar progressBarChapisco, progressBarFrisos;
        AlertDialog.Builder diagExecutandoHomeAuto, diagHomeAutoDone;
        AlertDialog diagExecHomeAut;
        boolean frisoDireito, frisoEsquerdo, statusOperacao, intervalo, homeZDone,homeXDone, homeCDone, homeGdone, homeAutoDone,
                simulacaoEletrodo, simulacaoFaisca, desativaJog, aguardaHomeAuto, executandoHome, chapiscando, once,
                homeMotoresOk,first, first2;
        int posX, posZ, valeAtual, statusPassoOperacao,qntEletConsumido;
        float posC, posG, progressoChapisco, progressoFrisos, pesoEletrodo;
        String passoOp;
        Operacao operacao;

    @Override
    protected void onStart() {
        CLP.sEscreverBit("MX3.2",false);
        super.onStart();
    }
        @Override
        public void inicializarUI() {

            setContentView(R.layout.activity_operacao);
            once = false;
            operacao=new Operacao();
            setClasseRetorno(MainActivity.class);

            tvOpReceita = (TextView) this.findViewById(R.id.tvOpReceita);
            tvOpReceita.setText(String.valueOf(Global.receita));

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            chapiscando = false;

            tvProgressChapisco  = (TextView) findViewById(R.id.tvProgressChapisco);
            tvProgressFrisos    = (TextView) findViewById(R.id.tvProgressoFrisos);
            txtFlanco           = (TextView) findViewById(R.id.txtFlanco);

            tvOpIntervalo       = (TextView) findViewById(R.id.tvOpIntervalo);
            tvOpFrisoE          = (TextView) findViewById(R.id.tvOpFrisoE);
            tvOpFrisoD          = (TextView) findViewById(R.id.tvOpFrisoD);
            tvOpInicia          = (TextView) findViewById(R.id.tvOpInicia);
            tvOpPara            = (TextView) findViewById(R.id.tvOpPara);
            tvOpPassoOp         = (TextView) findViewById(R.id.tvOpPassoOp);
            tvOpEletConsumido   = (TextView) findViewById(R.id.tvOpEletConsumido);
            tvOpPesoEletrodo    = (TextView) findViewById(R.id.tvOpPesoEletrodo);

            btnOscilador = (Button) findViewById(R.id.btnOpOscilador);
            btnOscilador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trocarTela(OsciladorActivity.class);
                }
            });


            btnVelocidades = (Button) findViewById(R.id.btnOpVelocidades2);
            btnVelocidades.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trocarTela(VelOp.class);
                }
            });

            btnVale = (Button) findViewById(R.id.btnOperacaoVale);
            btnVale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trocarTela(CamisaActivity.class);
                }
            });

            btnJato = (Button) findViewById(R.id.btnOpJato);
            btnJato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trocarTela(JatoActivity.class);
                }
            });

            btnOpAcompanhamento = (Button) findViewById(R.id.btnOpAcompanhamento);
            btnOpAcompanhamento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trocarTela(AcompanhamentoActivity.class);
                }
            });

            //Cria diálogo questionando se deseja executar o home automático
            AlertDialog.Builder diagHomeAuto = new AlertDialog.Builder(this).setMessage("Não foi feito o home dos motores.\nVolte para a tela de referência e faça o home dos motores").setTitle("ATENÇÃO!");;
            diagHomeAuto.setCancelable(false);

            diagHomeAuto.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            //Cria diálogo informando que o home automático está sendo executado
            diagExecutandoHomeAuto = new AlertDialog.Builder(this);
            diagExecutandoHomeAuto.setTitle("Aguarde...").setMessage("Executando home automático").setCancelable(true);
            diagExecutandoHomeAuto.setNegativeButton("Parar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    parar=true;
                }
            });
            diagExecHomeAut = diagExecutandoHomeAuto.create();

            //Cria diálogo informando que o home automático foi concluido e se deseja iniciar operação
            diagHomeAutoDone = new AlertDialog.Builder(this);
            diagHomeAutoDone.setTitle("Home automático concluído").setMessage("Deseja iniciar a operação?");
            diagHomeAutoDone.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CLP.sEscreverBit("MX4.6", true);
                }
            });
            diagHomeAutoDone.setNegativeButton("Não", null);


            imageOpInicia = (ImageButton) findViewById(R.id.imageOpInicia);
            imageOpInicia.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //if(first2){
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        /*if (!homeMotoresOk) {
                            diagHomeAuto.show();
                        } else {*/
                            CLP.sEscreverBit("MX4.6", true);
                        //+}

                    //}

                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            CLP.sEscreverBit("MX4.6", false);
                        }
                        if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                            CLP.sEscreverBit("MX4.6", false);
                        }

                    }
                    return false;
                }
            });

            imageOpInter = (ImageButton) findViewById(R.id.imageOpInter);
            imageOpInter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (intervalo) {
                            intervalo = false;
                        } else if (!intervalo) {
                            intervalo = true;
                        }
                    }
                    return false;
                }
                });

            imageOpPara = (ImageButton) findViewById(R.id.imageOpPara);
            imageOpPara.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        aguardaHomeAuto=false;
                        parar=true;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        parar=true;
                    }
                    return false;
                }
            });
            edtValeAtual = (TextView) findViewById(R.id.edtOperacaoVale);
            edtValeAtual.setText(String.valueOf(valeAtual));

            btnOpSimuEletrodo = (Button) findViewById(R.id.btnOpSimuEletrodo);
            btnOpSimuEletrodo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (simulacaoEletrodo) {
                            simulacaoEletrodo = false;
                        }else if(!simulacaoEletrodo) {
                            simulacaoEletrodo = true;
                        }
                    }
                    return false;
                }
            });



            btnOpSimuFaisca = (Button) findViewById(R.id.btnOpSimuFaisca);
            btnOpSimuFaisca.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (simulacaoFaisca) {
                            simulacaoFaisca = false;
                        }else if (!simulacaoFaisca) {
                            simulacaoFaisca = true;
                        }
                    }
                    return false;
                }
            });

            btnOpVoltar = (Button) findViewById(R.id.btnOpVoltar);
            btnOpVoltar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onBackPressed();
                    return false;
                }
            });

            btnOpReceita = (Button) findViewById(R.id.btnOpReceita);
            btnOpReceita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trocarTela(ReceitaActivity.class);
                }
            });

            btnOpMagazine = (Button) findViewById(R.id.btnOpMagazine);
            btnOpMagazine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trocarTela(MagazineActivity.class);
                }
            });


            progressBarChapisco = (ProgressBar) findViewById(R.id.progressBarChapisco);
            progressBarFrisos = (ProgressBar) findViewById(R.id.progressBarFrisos);

            tvPosAtualZ = (TextView) findViewById(R.id.tvOpPosZ);
            tvPosAtualX = (TextView) findViewById(R.id.tvOpPosX);
            tvPosAtualC = (TextView) findViewById(R.id.tvOpPosC);
            tvPosAtualG = (TextView) findViewById(R.id.tvOpPosG);
            imageZAtivo = (ImageView) findViewById(R.id.imageZAtivo);
            imageXAtivo = (ImageView) findViewById(R.id.imageXAtivo);
            imageCAtivo = (ImageView) findViewById(R.id.imageCAtivo);
            imageGAtivo = (ImageView) findViewById(R.id.imageGAtivo);

        }

    @Override
    protected void onStop() {
            aguardaHomeAuto=false;
        super.onStop();
    }

    @Override
        public void atualizarUI() {

        tvPosAtualZ.setText(String.valueOf(posZ));
        tvPosAtualX.setText(String.valueOf(posX));
        tvPosAtualC.setText(String.format("%.0f", posC));
        tvPosAtualG.setText(String.format("%.0f", posG));
        tvOpEletConsumido.setText(String.format("Eletrodos consumidos: %d", qntEletConsumido));
        tvOpPesoEletrodo.setText(String.format("Peso total consumido: %.2f Kg.", pesoEletrodo));

        edtValeAtual.setText(String.valueOf(valeAtual));

        //Status friso direito
        imageD = (ImageView) findViewById(R.id.imageOperacaoD);
        imageD.setImageResource(frisoEsquerdo ? R.drawable.led_verde2 : R.drawable.led_cinza);

        //Status friso esquerdo
        imageE = (ImageView) findViewById(R.id.imageOperacaoE);
        imageE.setImageResource(frisoDireito ? R.drawable.led_verde2 : R.drawable.led_cinza);

        //Status botão inicia
        imageOpInicia.setBackgroundColor(statusOperacao ? Color.GREEN : Color.GRAY);

        //Status botão para
        imageOpPara.setBackgroundColor(statusOperacao ? Color.GRAY : Color.RED);

        //ProgressBar chapisco
        progressBarChapisco.setProgress((int) progressoChapisco);

        //TextView progresso de chapisco
        tvProgressChapisco.setText(String.format("Progresso de queima do eletrodo" +
                ": %.2f%%", progressoChapisco));

        //ProgressBar frisos
        progressBarFrisos.setProgress((int)progressoFrisos);

        //TextView progresso dos frisos
        tvProgressFrisos.setText(String.format("Progresso dos frisos: %.2f%%", progressoFrisos));

        //Exibe os textos dos passos da rotina de operação
        switch (statusPassoOperacao) {
            case 0:
                passoOp = " 00 - Parado";
                break;
            case 1:
                passoOp = " 01 - Aguarda a troca de eletrodo";
                break;
            case 2:
                passoOp = " 02 - Escreve posição segura eixo X";
                break;
            case 3:
                passoOp = " 03 - Move eixo X";
                break;
            case 4:
                passoOp = " 04 - Escreve posição inicial para os eixos Z e C";
                break;
            case 5:
                passoOp = " 05 - Move para a posição inicial os eixos Z e C";
                break;
            case 6:
                passoOp = " 06 - Escreve posição de aproximação da camisa para o eixo X";
                break;
            case 7:
                passoOp = " 07 - Move o eixo X para a posição de aproximação da camisa";
                break;
            case 8:
                passoOp = " 08 - Escreve a posição de aproximação do friso para o eixo X";
                break;
            case 9:
                passoOp = " 09 - Move lentamente o eixo X para o fundo do friso";
                break;
            case 10:
                passoOp = " 10 - Liga a maquina de solda";
                break;
            case 11:
                passoOp = " 11 - Move o eixo Z para a posição de abertura de arco";
                break;
            case 12:
                passoOp = " 12 - Aguarda a abertura de arco";
                break;
            case 13:
                passoOp = " 13 - Inicia interpolação";
                break;
            case 14:
                passoOp = " 14 - Chapiscando";
                break;
            case 15:
                passoOp = " 15 - Atualiza status dos frisos chapsicados";
                break;
            case 16:
                passoOp = " 16 - Recuando eixo X";
                break;
            case 17:
                passoOp = " 17 - Move o eixo Z para a posição inicial";
                break;
        }
        tvOpPassoOp.setText(passoOp);

        if (executandoHome) {
            if (!first) {
                diagExecHomeAut.show();
                first = true;
            }
        } else {
            diagExecHomeAut.dismiss();
        }

        if (!executandoHome) {
            first = false;
        }

        if (homeAutoDone) {
            if (!Global.firstHome) {
                diagHomeAutoDone.show();
                Global.firstHome = true;
            }
        }

        if (!homeAutoDone) {
            Global.firstHome = false;
        }

        if (!homeMotoresOk) {
            first = false;
        }
        imageZAtivo.setImageResource(homeZDone ? R.drawable.led_verde2 : R.drawable.led_off);
        imageXAtivo.setImageResource(homeXDone ? R.drawable.led_verde2 : R.drawable.led_off);
        imageCAtivo.setImageResource(homeCDone ? R.drawable.led_verde2 : R.drawable.led_off);
        imageGAtivo.setImageResource(homeGdone ? R.drawable.led_verde2 : R.drawable.led_off);

        if (!simulacaoEletrodo) {
            btnOpSimuEletrodo.setBackgroundColor(Color.GRAY);
        } else {
            btnOpSimuEletrodo.setBackgroundColor(getResources().getColor(R.color.azulClaro));
        }

        if(!simulacaoFaisca){
            btnOpSimuFaisca.setBackgroundColor(Color.GRAY);
        }else{
            btnOpSimuFaisca.setBackgroundColor(getResources().getColor(R.color.azulClaro));
        }

        if (!intervalo) {
            imageOpInter.setBackgroundColor(Color.GRAY);
        }else{
            imageOpInter.setBackgroundColor(getResources().getColor(R.color.azulClaro));
        }
        }

        @Override
        public void atualizarCLP() throws Exception{
            if (!desativaJog){
                clp.escreverBit("MX0.0",false);
                desativaJog=true;
            }

            long inicio = System.currentTimeMillis();
            posZ                = clp.lerInteiro("MD100");
            posX                = clp.lerInteiro("MD101");
            posC                = clp.lerFloat("MD102");
            posG                = clp.lerFloat("MD103");
            valeAtual           = clp.lerInteiro("MD154");
            frisoDireito        = clp.lerBit("MX5.1");
            frisoEsquerdo       = clp.lerBit("MX5.2");
            if (first2) {
                clp.escreverBit("MX5.7", simulacaoEletrodo);
                clp.escreverBit("MX6.0", simulacaoFaisca);
                clp.escreverBit("MX4.7", intervalo);
            }

            statusOperacao      = clp.lerBit("MX5.5");
            progressoChapisco   = clp.lerFloat("MD174");
            progressoFrisos     = clp.lerFloat("MD175");
            statusPassoOperacao = clp.lerInteiro("MD155");
            homeMotoresOk       = clp.lerBit("MX6.7");
            executandoHome      = clp.lerBit("MX7.0");
            homeZDone           = clp.lerBit("MX1.5");
            homeXDone           = clp.lerBit("MX1.6");
            homeCDone           = clp.lerBit("MX1.7");
            homeGdone           = clp.lerBit("MX1.7");
            qntEletConsumido    = clp.lerInteiro("MD176");
            pesoEletrodo        = clp.lerFloat("MD179");


            if (!first2) {
                simulacaoEletrodo = clp.lerBit("MX5.7");
                simulacaoFaisca = clp.lerBit("MX6.0");
                intervalo = clp.lerBit("MX4.7");
                //homeAutoDone = clp.lerBit("MX7.1");
                first2 = true;
            }

            long fim = System.currentTimeMillis() - inicio;

            System.out.println("Haloo");
            System.out.println(fim);
        }

}




