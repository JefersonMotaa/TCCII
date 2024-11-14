package com.duo.superior.duo;

import android.os.AsyncTask;
import com.duo.superior.duo.modbus.CLP;

public class Delta extends AsyncTask<Void, Void,Void> {

    private Exception exception;
    private int valor;
    boolean valorBit;
    private String endereco;
    private int comando;

    //Sintaxe Delta(escreverDWORD,endereco,valor)
    //Sintaxe Delta(escreverWord,endereco,valor)
    //Sintaxe Delta(escreverBit, endereco,valor)
    public Delta(int valor,String ... args){

        endereco=args[1];

        switch (args[0]){
            case "escreverDWord":
                this.comando=2;
                this.valor=valor;
                break;
            case "escreverWord":
                this.comando=1;
                this.valor=valor;
                break;
            case "escreverBit":
                this.comando=0;
                valorBit=Boolean.valueOf(args[2]);
                break;
        }

    }
    @Override
    protected Void doInBackground(Void... voids) {

        CLP clp;
        clp = new CLP(Global.ip);
        try{

            switch (comando){
                case 0:
                    clp.escreverBitDVP10MC(endereco,valorBit);
                    break;
                case 1:
                    clp.escreverWordDVP10MC(endereco,valor);
                    break;
                case 2:
                    clp.escreverDWordDVP10MC(endereco,valor);
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