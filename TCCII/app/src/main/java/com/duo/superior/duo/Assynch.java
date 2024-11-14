package com.duo.superior.duo;
import android.os.AsyncTask;
import com.duo.superior.duo.modbus.CLP;

public class Assynch extends AsyncTask<Void, Void,Void> {

    private Exception exception;
    private boolean valor;
    private String endereco;
    private boolean force;
    private boolean setReset;


    protected void onPostExecute() {

    }

    public Assynch(String endereco, boolean valor,CLP clp){
        this.endereco=endereco;
        this.valor=valor;
        this.setReset=setReset;
        this.force=false;
    }

    public Assynch(String endereco, boolean valor,boolean force, boolean setReset){
        this.endereco=endereco;
        this.valor=valor;
        this.setReset=setReset;
        this.force=false;
    }

    public Assynch(String endereco,boolean valor,boolean force){
        this.valor=valor;
        this.endereco=endereco;
        this.force = force;
    }
    public Assynch(String endereco,boolean valor){
        this.valor=valor;
        this.endereco=endereco;
        this.force=false;
    }
    @Override
    protected Void doInBackground(Void... voids) {

              //CLP.setReset(endereco, valor);
                CLP clp;
                clp = new CLP(Global.ip);
                try{
                    clp.conectar();
                    clp.escreverBitDVP10MC(endereco,valor);

                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    clp.desconectar();
                }

                //CLP.forceSetReset(endereco, valor);


        return null;
    }
}