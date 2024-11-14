package com.duo.superior.duo.rbs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.duo.com.duo.R;
import com.duo.superior.duo.Assynch;
import com.duo.superior.duo.modbus.CLP;

public class Operacao {


    ///Todos:
    //[]Não temos uma verificação do óleo.

    private boolean basico,homeX,homeZ,homeC, emergencia, oleo, simOleo, ativoX,ativoZ,ativoC,calculo,receita,operando;
    private  int erro;

    private CLP clp;
    public Operacao(){

    }


    //Pega todas as memórias relevantes para a operacao.
    public void get(CLP clp) throws  Exception{

        basico=clp.lerBitDVP10MC("M1000");
        operando=clp.lerBitDVP10MC("M1001");
        homeZ=clp.lerBitDVP10MC("D6254.0");
        homeX=clp.lerBitDVP10MC("D6254.2");
        homeC=clp.lerBitDVP10MC("D6254.3");
        emergencia=clp.lerBitDVP10MC("D6470.2");
        simOleo=clp.lerBitDVP10MC("D6153.8");
        ativoZ=clp.lerBitDVP10MC("M1028");
        ativoX=clp.lerBitDVP10MC("M1030");
        ativoC=clp.lerBitDVP10MC("M1031");
        receita= clp.lerBitDVP10MC("D6254.6");//RECEITA OK
        calculo = clp.lerBitDVP10MC("D6254.7");//Calculo ok
        oleo=false;

    }





    public void corrigirErro(){

    }

    public boolean podeOperar(){

        return homeC && homeX && homeZ && receita && calculo;
    }
    public void msgOperar(Context context){
        if(!homeOK()){

            int nEixo=0;

            if(!homeC){nEixo=2;}
            else if(!homeX){nEixo=1;}
            else if(!homeZ){nEixo=0;}

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            String[] eixo = {"Z","X","C"};
            dialog.setTitle("O motor "+eixo[nEixo]+" não foi referenciado.");
            dialog.setMessage("Deseja fazer home?");

            dialog.setCancelable(true);
            dialog.setIcon(R.drawable.erro);

            dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            final int finalNEixo = nEixo;
            dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                Motores.fazerHome(finalNEixo);

                }
            });

            dialog.create();
            dialog.show();

        }

        else if(!receita || !calculo){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            String[] eixo = {"Z","X","C"};
            dialog.setTitle("Nenhuma receita foi carregada.");
            dialog.setMessage("Por favor, carregue e tente novamente.");

            dialog.setCancelable(true);
            dialog.setIcon(R.drawable.erro);

            dialog.setPositiveButton("OK",null);


        }
    }

    public boolean podeIniciar(){
        return motoresOK() && !emergencia && !basico && (simOleo || oleo) && homeOK();
    }
    public void msgIniciar(Context context){
        if(emergencia){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            String[] eixo = {"Z","X","C"};
            dialog.setTitle("O botão de emergência está ativado.");
            dialog.setMessage("Por favor, desative-o e tente novamente.");

            dialog.setCancelable(true);
            dialog.setIcon(R.drawable.erro);

            dialog.setPositiveButton("OK",null);
        }

        else if(basico){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            String[] eixo = {"Z","X","C"};
            dialog.setTitle("Você está no modo básico.");
            dialog.setMessage("Por favor, entre em operação e tente novamente.");

            dialog.setCancelable(true);
            dialog.setIcon(R.drawable.erro);

            dialog.setPositiveButton("OK",null);
        }
    }
    public boolean homeOK(){
        return homeZ && homeX && homeC;
    }
    public boolean motoresOK(){
        return ativoX && ativoZ && ativoC;
    }

    public boolean isBasico() {
        return basico;
    }
    public void setBasico(boolean basico) {
        this.basico = basico;
    }

    public boolean isHomeX() {
        return homeX;
    }

    public void setHomeX(boolean homeX) {
        this.homeX = homeX;
    }

    public void fazerHome(int eixo){
        Assynch set;
        switch (eixo){
            case 0:
                try{
                    set = new Assynch("M30",true);
                    set.execute();

                    Thread.currentThread().wait(50);
                    set = new Assynch("M30",false);
                    set.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    public boolean isHomeZ() {
        return homeZ;
    }

    public void setHomeZ(boolean homeZ) {
        this.homeZ = homeZ;
    }

    public boolean isHomeC() {
        return homeC;
    }

    public void setHomeC(boolean homeC) {
        this.homeC = homeC;
    }

    public boolean isEmergencia() {
        return emergencia;
    }

    public void setEmergencia(boolean emergencia) {
        this.emergencia = emergencia;
    }

    public boolean isOleo() {
        return oleo;
    }

    public void setOleo(boolean oleo) {
        this.oleo = oleo;
    }

    public boolean isSimOleo() {
        return simOleo;
    }

    public void setSimOleo(boolean simOleo) {
        this.simOleo = simOleo;
    }

    public boolean isAtivoX() {
        return ativoX;
    }

    public void setAtivoX(boolean ativoX) {
        this.ativoX = ativoX;
    }

    public boolean isAtivoZ() {
        return ativoZ;
    }

    public void setAtivoZ(boolean ativoZ) {
        this.ativoZ = ativoZ;
    }

    public boolean isAtivoC() {
        return ativoC;
    }

    public void setAtivoC(boolean ativoC) {
        this.ativoC = ativoC;
    }

    public boolean isCalculo() {
        return calculo;
    }

    public void setCalculo(boolean calculo) {
        this.calculo = calculo;
    }

    public boolean isReceita() {
        return receita;
    }

    public void setReceita(boolean receita) {
        this.receita = receita;
    }


}
