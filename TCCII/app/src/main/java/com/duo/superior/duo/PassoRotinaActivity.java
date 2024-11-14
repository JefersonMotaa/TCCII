package com.duo.superior.duo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.duo.com.duo.R;

public class PassoRotinaActivity extends duoActivity {

    TextView tvRotinaOp, tvRotinaMag, tvRotinaTroca ;
    int passoRotinaOperacao, passoRotinaMagazine, passoRotinaTrocaEletrodo;
    String passoOp, passoMag, passoTroca;
    Button btnAcompVoltar;

    @Override
    public void inicializarUI() {
        setContentView(R.layout.activity_passo_rotina);
        tvRotinaOp = (TextView) findViewById(R.id.tvRotinaOp);
        tvRotinaMag = (TextView) findViewById(R.id.tvRotinaMag);
        tvRotinaTroca = (TextView) findViewById(R.id.tvRotinaTroca);
        btnAcompVoltar = (Button) findViewById(R.id.btnAcompVoltar);
        btnAcompVoltar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
    }

    @Override
    public void atualizarCLP() throws Exception {
        passoRotinaOperacao = clp.lerInteiro("MD155");
        passoRotinaMagazine = clp.lerInteiro("MD156");
        passoRotinaTrocaEletrodo = clp.lerInteiro("MD157");
    }

    @Override
    public void atualizarUI() {
        //Exibe os textos dos passos da rotina de operação
        switch (passoRotinaOperacao){
            case 0: passoOp = " 00 - Parado";
                break;
            case 1: passoOp = " 01 - Aguarda a troca de eletrodo";
                break;
            case 2: passoOp = " 02 - Escreve posição inicial do eixo X";
                break;
            case 3: passoOp = " 03 - Move eixo X para a posição inicial";
                break;
            case 4: passoOp = " 04 - Escreve posição inicial para os eixos Z e C";
                break;
            case 5: passoOp = " 05 - Move para a posição inicial os eixos Z e C";
                break;
            case 6: passoOp = " 06 - Escreve posição de aproximação da camisa para o eixo X";
                break;
            case 7: passoOp = " 07 - Move o eixo X para a posição de aproximação da camisa";
                break;
            case 8: passoOp = " 08 - Escreve a posição de aproximação do friso para o eixo X";
                break;
            case 9: passoOp = " 09 - Move lentamente o eixo X para a posição de início de chapisco";
                break;
            case 10: passoOp = " 10 - Liga a maquina de solda";
                break;
            case 11: passoOp = " 11 - Move o eixo Z para a posição de abertura de arco";
                break;
            case 12: passoOp = " 12 - Aguarda a abertura de arco";
                break;
            case 13: passoOp = " 13 - Inicia interpolação";
                break;
            case 14: passoOp = " 14 - Chapiscando";
                break;
            case 15: passoOp = " 15 - Atualiza status dos frisos chapsicados";
                break;
            case 16: passoOp = " 16 - Recuando eixo X, desliga máquina de solda, desliga bomba d'água";
                break;
        }

        //Exibe os textos dos passos da rotina do magazine
        switch (passoRotinaMagazine){
            case 0: passoMag = " 00 - Parado";
                break;
            case 1: passoMag = " 01 - Verifica se há eletrodo na saída do magazine ";
                break;
            case 2: passoMag = " 02 - Verifica contagem de ciclo para controle do bico de saída";
                break;
            case 3: passoMag = " 03 - Aciona o index do magazine e aguarda o sinal do sensor de avanço\n do magazine";
                break;
            case 4: passoMag = " 04 - Falha no acionamento do index, aciona a catraca para corrigir a\n indexação";
                break;
            case 5: passoMag = " 05 - Aciona o index do magazine e aguarda o sinal do sensor de avanço\n do magzine";
                break;
            case 6: passoMag = " 06 - Desaciona o cilindro posicionador e aguarda desacionar o sensor \nde posicionador avançado";
                break;
            case 7: passoMag = " 07 - Recua o cilindro da catraca";
                break;
            case 8: passoMag = " 08 - Aciona o cilindro posicionador e aguarda o acionar sinal do sensor\n de posicionador avançado";
                break;
            case 9: passoMag = " 09 - Desaciona o cilindro de index e aguarda desacionar o sensor de \nindex avançado";
                break;
            case 10: passoMag = " 10 - Avança o cilindro da catraca";
                break;
            case 11: passoMag = " 11 - Aciona o cilindro de index do magazine e aguarda acionar o sensor\n de index avançado";
                break;
            case 12: passoMag = " 12 - Recua o cilindro posicionador do magazine e aguarda desacionar o \nsensor do posicionador avançado";
                break;
            case 13: passoMag = " 13 - Verifica se tem eletrodo na saida do magazine e aguarda desacionar \no cilindro de saida de eletrodo";
                break;
        }

        //Exibe os textos dos passos da rotina de troca de eletrodos
        switch (passoRotinaTrocaEletrodo){
            case 0: passoTroca = " 00 - Parado";
                break;
            case 1: passoTroca = " 01 - Move o eixo X para a posição de descarte de eletrodo";
                break;
            case 2: passoTroca = " 02 - Move o eixo C para o ângulo 0";
                break;
            case 3: passoTroca = " 03 - Move o eixo Z para a posição inicial";
                break;
            case 4: passoTroca = " 04 - Move o eixo C para o ângulo de descarte";
                break;
            case 5: passoTroca = " 05 - Move o eixo Z para a posição de descarte";
                break;
            case 6: passoTroca = " 06 - Abre a garra e expulsa o eletrodo";
                break;
            case 7: passoTroca = " 07 - Move o eixo Z para a posição pós descarte";
                break;
            case 8: passoTroca = " 08 - Move o eixo X para a posição de pega de eletrodo";
                break;
            case 9: passoTroca = " 09 - Move o eixo Z para a posição de pega de eletrodo";
                break;
            case 10: passoTroca = " 10 - Aguarda a liberação de pega de eletrodo";
                break;
            case 11: passoTroca = " 11 - Fecha garra";
                break;
            case 12: passoTroca = " 12 - Move o eixo Z para a posição inicial";
                break;
            case 13: passoTroca = " 13 - Move o eixo C para o ângulo 0";
                break;
            case 14: passoTroca = " 14 - Troca de eletrodo finalizada";
                break;
        }

        tvRotinaOp.setText(passoOp);
        tvRotinaMag.setText(passoMag);
        tvRotinaTroca.setText(passoTroca);
    }
}