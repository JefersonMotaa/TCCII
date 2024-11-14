package com.duo.superior.duo;

import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duo.com.duo.R;
import com.duo.superior.duo.conectividade.Receita;

import java.util.List;

public class AcompanhamentoActivity extends duoActivity {

    //Esse é o LinearLayout no qual todos os outros estão incluidos
    LinearLayout llayout;
    int[] e,d;
    List<Button> bE, bD;
    int linhas, colunas;
    int[] lMemorias;
    boolean atualizar = false;
    int numLayouts = 0;

    @Override
    public void inicializarUI() {

        setContentView(R.layout.activity_acompanhamento);
        llayout = (LinearLayout) findViewById(R.id.llayout);

        //Precisamos saber quantos frisos desenhar
        Receita rec = sql.pegarReceita(Global.receita);
        numLayouts = rec.vale;

        //Fixamos 5 linhas e calculamos as colunas
        linhas = 5;
        colunas = (int) Math.ceil(1.0*numLayouts/linhas);

        this.e = new int[linhas*colunas];
        this.d = new int[linhas*colunas];
        lMemorias = new int[6];

        desenharVales();
        }

        public void desenharVales(){
            llayout.removeAllViews();
            if(numLayouts > 80){
                Toast.makeText(getBaseContext(),"O número de vales está muito alto. Nessa tela o máximo é 80", Toast.LENGTH_SHORT).show();
                return;
            }
            //Estabelecemos o número de linhas e colunas
            int contador = 0;

            for(int i = 0; i< linhas; i++){
                //Este Layout contem a linha
                LinearLayout lLinha = new LinearLayout(getBaseContext());
                lLinha.setOrientation(LinearLayout.HORIZONTAL);
                for(int j = 0; j < colunas; j++){
                    //Desenhamos o elemento unitário com o número
                    //do vale, e as indicações esquerda, direita

                    LinearLayout container = new LinearLayout(getBaseContext());
                    container.setOrientation(LinearLayout.HORIZONTAL);
                    //Esse container vai conter os 3 elementos
                    //o número do vale e um LinearLayout com os
                    //2 botões

                    //Esse container vai organizar os botões E e D
                    LinearLayout coluna = new LinearLayout(getBaseContext());
                    coluna.setOrientation(LinearLayout.VERTICAL);

                    //Criamos os 3 botões
                    Button vale = new Button(getBaseContext());
                    int valeC = (this.d[contador] & this.e[contador]) == 1 ?  R.color.green :R.color.white;
                    vale.setBackgroundColor(getColor(valeC));

                    LinearLayout.LayoutParams btnVale = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    btnVale.weight = 1;
                    vale.setLayoutParams(btnVale);
                    vale.setStateListAnimator(null);


                    Button d = new Button(getBaseContext());
                    int dC = this.d[contador] == 1? R.color.green :R.color.cinzaDireita;
                    d.setBackgroundColor(getColor(dC));

                    Button e = new Button(getBaseContext());
                    int eC = this.e[contador] == 1? R.color.green :R.color.cinzaDireita;
                    e.setBackgroundColor(getColor(eC));

                    vale.setText(String.valueOf(contador));
                    d.setText("D");
                    e.setText("E");
                    contador++;

                    //Adicionamos os botões nos respectivos layouts
                    container.addView(vale);
                    coluna.setLayoutParams(btnVale);
                    coluna.addView(d);
                    coluna.addView(e);
                    container.addView(coluna);
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(getColor(R.color.gray1)); //white background
                    border.setStroke(1, 0xFF000000); //black border with full opacity
                    container.setBackground(border);

                    //Configuração do tamanho de cada container
                    LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT);
                    containerParam.weight = 1;

                    lLinha.addView(container, containerParam);
                }
                LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0);
                lParam.weight = 1;
                lLinha.setLayoutParams(lParam);
                llayout.addView(lLinha);
            }
        }

    @Override
    public void atualizarCLP() throws Exception {
        int memorias[] = clp.lerInteiroSequencia("MD300",6);

        for(int j =0; j < memorias.length; j++){
            if(memorias[j]  != lMemorias[j]){
               lMemorias[j] = memorias[j];
                atualizar = true;
            }
        }

        if(atualizar){
            for(int k = 0; k < 3; k++){
                for(int bit = 0; bit < 16; bit++){
                    this.e[(k*16)+bit] = (memorias[k] >> bit) & 1;
                    this.d[(k*16)+bit] = (memorias[k+3] >> bit) & 1;
                }
            }
        }
    }

    @Override
    public void atualizarUI() {
        if(!atualizar)
            return;
        desenharVales();
        atualizar = false;
    }
}

