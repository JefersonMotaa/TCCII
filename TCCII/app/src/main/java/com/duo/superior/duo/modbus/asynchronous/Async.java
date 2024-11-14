package com.duo.superior.duo.modbus.asynchronous;

import android.os.AsyncTask;
import com.duo.superior.duo.Global;
import com.duo.superior.duo.modbus.CLP;

public class Async extends AsyncTask<Void, Void,Void> {

    private Exception exception;
    private boolean valor;
    private String endereco;

    public Async(String endereco, boolean valor){
        this.endereco=endereco;
        this.valor=valor;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        CLP clp;
        clp = new CLP(Global.ip);
        try{
            clp.conectar();
            clp.escreverBit(endereco,valor);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            clp.desconectar();
        }

        return null;
    }
}
