package com.duo.superior.duo.modbus.asynchronous;

import android.os.AsyncTask;
import com.duo.superior.duo.Global;
import com.duo.superior.duo.conectividade.Eletrodo;
import com.duo.superior.duo.conectividade.Receita;
import com.duo.superior.duo.modbus.CLP;

public class Words extends AsyncTask<Void, Void,Void> {

    private Exception exception;
    private int valor;
    private float fValor;
    boolean valorBit;
    private String endereco;
    private int comando;
    private CLP clp;
    private Eletrodo el;
    private Receita rec;

    public void carregarEletrodo(CLP clp) throws Exception{

        clp.escreverInteiro("MD138", el.comprimento);
        clp.escreverInteiro("MD139", el.tempo);
        clp.escreverInteiro("MD140", el.haste);
        clp.escreverInteiro("MD141", el.utilizacao);
        clp.escreverInteiro("MD142", el.angulo);
        clp.escreverInteiro("MD143", el.utilizacaoFlancoIni);
        clp.escreverInteiro("MD144", el.tempoFlancoIni);
        clp.escreverInteiro("MD178", el.peso);
    }

    public void carregarReceita(CLP clp) throws Exception{

        clp.escreverFloat("MD126",rec.primeiroFriso);
        clp.escreverFloat("MD127",rec.passo);
        clp.escreverFloat("MD128",rec.base);

        clp.escreverInteiro("MD129", rec.angulo);
        clp.escreverInteiro("MD131", rec.comprimento);
        clp.escreverInteiro("MD132", rec.ci);
        clp.escreverInteiro("MD133", rec.cf);

        clp.escreverInteiro("MD134", rec.vale);
        //foi invertido o md136 pelo md137 porque o valor estava chegando errado no clp

        clp.escreverInteiro("MD135", rec.valeEspecial);
        clp.escreverInteiro("MD137", rec.valeZero);
        clp.escreverInteiro("MD136", rec.profundidade);
        clp.escreverInteiro("MD145", rec.altura);


    }
    //Sintaxe Delta(escreverDWORD,endereco,valor)
    //Sintaxe Delta(escreverWord,endereco,valor)
    //Sintaxe Delta(escreverBit, endereco,valor)
    public Words(float valor,String ... args){

        endereco=args[1];

        switch (args[0]){
            case "escreverFloat":
                this.comando = 1;
                this.fValor=valor;
                break;
        }

    }

    public Words(int valor,String ... args){

        endereco=args[1];

        switch (args[0]){
            case "escreverDWord":
                this.comando=2;
                this.valor=valor;
                break;
            case "escreverFloat":
                this.comando=1;
                this.valor=valor;
                break;
            case "escreverBit":
                this.comando=0;
                valorBit=Boolean.valueOf(args[2]);
                break;
        }

    }

    public Words(Eletrodo el){
                this.el = el;
                this.comando = 4;
    }

    public Words(Receita rec){
        this.rec = rec;
        this.comando = 3;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        CLP clp = new CLP(Global.ip);
        try{

            clp.conectar();
            switch (comando){
                case 0:
                    clp.escreverBit(endereco,valorBit);
                    break;
                case 1:
                    clp.escreverFloat(endereco,fValor);
                    break;
                case 2:
                    clp.escreverInteiro(endereco,valor);
                    break;

                case 3:
                    carregarReceita(clp);
                    break;
                case 4:
                    carregarEletrodo(clp);
                    break;
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            clp.desconectar();
        }

        return null;
    }
}