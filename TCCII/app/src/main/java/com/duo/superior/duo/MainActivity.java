package com.duo.superior.duo;

import static com.duo.superior.duo.Global.scanTime;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.duo.com.duo.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends duoActivity {
    Button btnOperacao, btnManutencao, btnMainEditar;

        @Override
        public void inicializarUI() {
            setContentView(R.layout.activity_main);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            System.out.println(Global.receita);
            scanTime=100;
            Global.timeOut = 6000;
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
            //Logotipo da DUO
            ImageView mImage = (ImageView) mCustomView.findViewById(R.id.imgLogo);
            mImage.setImageResource(R.drawable.duot);
            //Botão Clear
            Button btnActionBarReset = (Button) mCustomView.findViewById(R.id.btnActionBarReset);
            btnActionBarReset.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    }else{
                    }
                    return false;
                }
            });

            //Chama a tela de operação
            btnOperacao = (Button) findViewById(R.id.btnOperacao);
            btnOperacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trocarTela(OperacaoActivity.class);
                }
            });

            //Chama a tela de Manutenção
            btnManutencao = (Button) findViewById(R.id.btnManutencao);
            btnManutencao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trocarTela(Configuracoes.class);
                }
            });

            //Botão editar
            btnMainEditar = (Button) findViewById(R.id.btnMainEditar);
            btnMainEditar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    trocarTela(EditarActivity.class);
                    return false;
                }
            });
            setupRobo();
        }
        public int pegarNumSerie(){
            return -1;
        }
        @Override
        public void atualizarUI() {
        }

        public void atualizarCLP()throws Exception{
        }

        public void setupRobo(){
            try {
                InputStream is = getAssets().open("robo.txt");

                BufferedReader bf = new BufferedReader(new InputStreamReader(is));

                String nomeRobo;

                if((nomeRobo=bf.readLine())!=null){
                    System.out.println(nomeRobo);
                }
                else {
                    Toast.makeText(this,"Conectividade desconfigurada.",Toast.LENGTH_LONG);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

