package com.duo.superior.duo.conectividade;


import static com.duo.superior.duo.Global.atualizandoRemoto;
import static com.duo.superior.duo.conectividade.Tempo.TurnoAtual;

import android.content.ContentValues;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.duo.superior.duo.Global;
import com.duo.superior.duo.modbus.CLP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DxMySQL {

    public static void puxar(){

    }

    public static void empurrar(){

    }

   public static void contarPassos(CLP clp, DxSQLite sql) throws Exception{
        //Passo 13 começa a queimar
        // Passo 15 terminou 100%
       int step = clp.lerInteiro("MD155");
       System.out.println(step);
       if(step > 13 && !Global.gatilho) {
           SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
           Global.previousTimeStamp = df.format(new Date());
           Global.gatilho = true;
       }
       if(step <13 && Global.gatilho)
       {
           salvarEletrodoLocal(clp, sql);
           Global.gatilho = false;
       }

       // Puxar linha ainda não enviada da tabela local
       Map<String, String> linhaLocal = sql.lerEletrodosLocal();

       if(linhaLocal.size()>0){
           System.out.println("Novos dados para enviar");
           System.out.println(linhaLocal);
           // Enviar a linha local para o servidor
           System.out.println("Enviando...");
           Thread internet = new Thread(){
               @Override
               public void run(){
                   try{
                       if(enviarEletrodoLocal(linhaLocal)){
                           System.out.println("Enviado com sucesso.");
                           sql.updateEletrodosLocal(linhaLocal.get("Id"));
                           atualizandoRemoto = false;
                       }else{
                           //Enviamos um GET para atualizar o servidor
                           Atualizar();
                       }

                   }catch (Exception e){
                       e.printStackTrace();
                   }
               }
           };

           if(!atualizandoRemoto){
               internet.start();
           }
       }else{

       }
   }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean Atualizar() throws Exception{
        URL url = new URL("http://adm.duo.com.br:8080");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);

        String body = "{" +
                "\"command\": \"" + 1 + "\"," +
                "\"raw\": " + Global.numSerie  +
                "}";

        OutputStream os = con.getOutputStream();
        byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input);
        os.close();

        if(con.getResponseCode() != 200){
            return false;
        }
        BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder("");

        String line;
        while ((line = bf.readLine()) != null){
            response.append(line);
        }

        bf.close();
        String result = response.toString();

        if(result.contains("INSERIDO!") || result.contains("Está duplicado")){
            return true;
        }
        else{
            System.out.println(result);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean enviarEletrodoLocal(Map<String, String> linhaLocal) throws Exception{
        URL url = new URL("http://adm.duo.com.br:8080");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = ndf.format(df.parse(linhaLocal.get("Timestamp")));

        String body = "{" +
                "\"command\": \"" + 0+ "\"," +
                "\"ts\": \"" + timestamp + "\"," +
                "\"tempo_trabalhado\": " + linhaLocal.get("Tempo_Trabalhado")  + "," +
                "\"direcao\": " + linhaLocal.get("Direcao")  + "," +
                "\"vale\": " + linhaLocal.get("Vale")  + "," +
                "\"especial\": " + linhaLocal.get("Especial")  + "," +
                "\"receita\": " + linhaLocal.get("Receita")  + "," +
                "\"turno\": " + linhaLocal.get("Turno")  + "," +
                "\"parou\": " + linhaLocal.get("Parou")  + "," +
                "\"num_serie\": " + linhaLocal.get("Num_Serie")  + "," +
                "\"simulacao\": " + linhaLocal.get("Simulacao")  +
                "}";

        OutputStream os = con.getOutputStream();
        byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input);
        os.close();

        if(con.getResponseCode() != 200){
            return false;
        }
        BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder("");

        String line;
        while ((line = bf.readLine()) != null){
            response.append(line);
        }

        bf.close();
        String result = response.toString();

        if(result.contains("INSERIDO!") || result.contains("Está duplicado")){
            return true;
        }
        else{
            System.out.println(result);
            return false;
        }
    }

    public static void salvarEletrodoLocal(CLP clp, DxSQLite sql) throws Exception {

        ContentValues cv = new ContentValues();
        boolean frisoDireito = clp.lerBit("MX5.1");
        boolean frisoEsquerdo = clp.lerBit("MX5.2");

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timeStamp = df.format(new Date());

        Date d1 = df.parse(Global.previousTimeStamp);
        Date d2 = df.parse(timeStamp);

        long tempoTrabalhado = ( d2.getTime() - d1.getTime() ) / 1000 ; // em segundos
        int vale = clp.lerInteiro("MD152");
        int direcao = frisoDireito ? 0 : 1;
        int especial = 0;
        int receita = Global.receita;
        int turno = TurnoAtual();
        int parou = 0;
        int numSerie = Global.numSerie; // colocar endereço onde o numero de série pode ser lido
        int simulacao = 0;


        cv.put("Timestamp", timeStamp);
        cv.put("Tempo_Trabalhado", tempoTrabalhado);
        cv.put("Vale", vale);
        cv.put("Direcao", direcao);
        cv.put("Especial", especial);
        cv.put("Receita", receita);
        cv.put("Turno", turno);
        cv.put("Parou", parou);
        cv.put("Num_Serie", numSerie);
        cv.put("Simulacao", simulacao);
        cv.put("Enviado", 0);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String date = sd.format(new Date()).toString();
        cv.put("Date", date);

        sql.inserirEletrodosLocal(cv);

    }
}
