package com.duo.superior.duo.conectividade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Pair;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DxSQLite extends SQLiteOpenHelper {

    String tableName = "LOG";
    String colData = "Data";
    String colTurno ="Turno";
    String colEletrodo ="Eletrodos";
    String colOperador = "Operador";
    String nomeRobo = "NomeRobo";
    String colMinutos="Minutos";
    String colEnviado="Enviado";

    String memoryTableName="Preferencias";
    String operatorTableName="Operadores";

    public DxSQLite(@Nullable Context context, int version) {
        super(context,"RBS",null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE "+tableName+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +nomeRobo+ " TEXT, " + colEletrodo + " INT," +colMinutos+" INT,"+colData + " DATE," + colTurno+" INT,"+colOperador+" TEXT,"+colEnviado+ " INT)";
        sqLiteDatabase.execSQL(createTableStatement);


        createTableStatement = "CREATE TABLE "+operatorTableName+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, Operador VARCHAR(255) NOT NULL)";
        sqLiteDatabase.execSQL(createTableStatement);


        createTableStatement = "CREATE TABLE "+memoryTableName+" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "                                                 Receita INT, Vale INT, IntervaloLub INT, " +
                "                                                 VelocidadeOsc INT, DistanciaOsc INT, " +
                                                                    "IP INT,Porta INT)";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement="INSERT INTO "+memoryTableName+" (Receita, Vale,IntervaloLub,VelocidadeOsc,DistanciaOsc, IP, Porta) values (1,0,40,50,50,2, 502)";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement="CREATE TABLE Receitas (id auto_increment primary key ," +
                                "comprimento int, primeiro_friso float, passo float, altura int," +
                                "base float,ci int,cf int,angulo int, profundidade int, vale_zero int," +
                                "vale_especial int, vale int, vale_inicial int, angulo_eletrodo float," +
                                "percentagem_queima float, inicio_friso int)";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement = "CREATE TABLE Receita_Eletrodo (id auto_increment primary key , comprimento int," +
                                "haste int, compensacao int, peso int, utilizacao int, angulo int," +
                                "utilizacao_flanco int, tempo_flanco_inicial int, compensacao_flanco_inicial int,tempo int)";
        sqLiteDatabase.execSQL(createTableStatement);


        createTableStatement="INSERT INTO "+operatorTableName+" (Operador) values ('Operador numero 1')";
        sqLiteDatabase.execSQL(createTableStatement);

        createTableStatement = "CREATE TABLE Eletrodos (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "Timestamp datetime, Tempo_Trabalhado int, Vale int, Direcao int, Especial int, Receita int," +
                "Turno int, Parou int, Num_Serie int, Simulacao int, Enviado boolean, Date date)";
        sqLiteDatabase.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i==1 && i1 == 2){
            String query;
            query = "Create Table Dados (id INTEGER primary key autoincrement, dado int)";
            sqLiteDatabase.execSQL(query);

            query = "Insert into Dados (id, dado) values (1,-1)";
            sqLiteDatabase.execSQL(query);
        }
    }

    public int pegarNumeroSerie(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Dados where id = 1", null);

        int numSerie;

        if(cursor.moveToFirst()) {
            do {
                cursor.getInt(0); // id
                numSerie = cursor.getInt(1);

            } while (cursor.moveToNext());

            return numSerie;
        }else
            return -1;
    }


    public void alterarNumeroSerie(int novoNumSerie){
        SQLiteDatabase db = getWritableDatabase();
        String query ="Update Dados set dado = "+ novoNumSerie+" where id = 1";
        db.execSQL(query);
    }

    public boolean deleteAll(){
        try{
            String getQuery="DELETE FROM "+tableName;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(getQuery);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public String pegarMinutosTurnoAtual(){
        String query = "Select "+colMinutos+" from "+tableName+ " WHERE "+colTurno+"="+Tempo.TurnoAtual()+" and date("+colData+")="+ "date(\""+java.time.LocalDate.now().toString()+"\")";
        SQLiteDatabase db = getReadableDatabase();
        String number;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                number = cursor.getString(0);
            } while (cursor.moveToNext());

            return number;
        }else
            return "0";
    }

    public String pegarNumEletrodosTurnoAtual(){
        String query = "Select " + colEletrodo+" from "+tableName+ " WHERE "+colTurno+"="+Tempo.TurnoAtual()+" and date("+colData+")="+ "date(\""+java.time.LocalDate.now().toString()+"\")";
        SQLiteDatabase db = getReadableDatabase();
        String number;
       Cursor cursor = db.rawQuery(query,null);
       if(cursor.moveToFirst()) {
           do {
               number = cursor.getString(0);
           } while (cursor.moveToNext());

           return number;
       }else
           return "0";
    }


    public int tamanhoCursor(Cursor cursor){
        int i=0;
        while(cursor.moveToNext()){
           i++;
        }
        return i;
    }


    public String mostrarBanco(){
        SQLiteDatabase db = getReadableDatabase();

        String query ="Select * from " + tableName;
        Cursor cursor = db.rawQuery(query,null);
        StringBuilder sb = new StringBuilder("|");
        if(cursor.moveToFirst()){
            do{
               sb.append(cursor.getString(1));
               sb.append("|");
                sb.append(cursor.getString(2));
                sb.append("|");
                sb.append(cursor.getString(3));
                sb.append("|");
                sb.append(cursor.getString(4));
                sb.append("|");
                sb.append(cursor.getString(5));
                sb.append("|");
                sb.append(cursor.getString(6));
                sb.append("|");
                sb.append(cursor.getString(7));
                sb.append("|");

            }while (cursor.moveToNext());
        }
        return sb.toString();
    }


    public ArrayList<Pair> listFromTable(String tableName, int columnNumber){

        ArrayList<Pair> myList = new ArrayList<Pair>();
        SQLiteDatabase db = getReadableDatabase();

        String query ="Select * from " + tableName;
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                Pair<Integer, String> P = new Pair<Integer, String>
                        (cursor.getInt(0), cursor.getString(columnNumber));
                myList.add(P);
            }while (cursor.moveToNext());
        }
        return myList;
    }

    public boolean addOperator(String operatorName){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Operador", operatorName);
        long result = db.insert("Operadores",null, cv);

        if(result!=-1){
            return true;
        }else
            return false;
    }

    public boolean deleteOperator(int operatorId){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "rowid=" + operatorId;

        long result = db.delete("Operadores", whereClause, null);

        if(result!=-1){
            return true;
        }else
            return false;
    }

    public boolean editOperator(int operatorId, String newOperatorName){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String whereClause = "rowid=" + operatorId;

        cv.put("Operador", newOperatorName);
        long result = db.update("Operadores", cv, whereClause, null);

        if(result!=-1){
            return true;
        }else
            return false;
    }


    public void atualizarValorPreferencia(String table,String field, int value){
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE "+table+" set "+field+"="+value;
            db.execSQL(query);
    }

    public void inserirReceita(Receita receita){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("id",receita.id);
        cv.put("vale_especial",receita.valeEspecial);
        cv.put("vale",receita.vale);
        cv.put("inicio_friso",receita.inicioFriso);
        cv.put("vale_inicial",receita.valeInicial);
        cv.put("comprimento",receita.comprimento);
        cv.put("primeiro_friso",receita.primeiroFriso);
        cv.put("passo",receita.passo);
        cv.put("altura",receita.altura);

        cv.put("base",receita.base);
        cv.put("ci",receita.ci);
        cv.put("cf",receita.cf);
        cv.put("angulo",receita.angulo);
        cv.put("profundidade", receita.profundidade);

        cv.put("angulo_eletrodo", receita.anguloEletrodo);
        cv.put("percentagem_queima", receita.percentagemQueima);
        cv.put("vale_zero", receita.valeZero);


        long a = db.insert("Receitas",null, cv);
    }

    public void inserirRecEletrodos(Eletrodo eletrodo){

        String query = "insert into Receita_Eletrodo (id, comprimento, haste, compensacao, peso, utilizacao, angulo," +
                "utilizacao_flanco, tempo_flanco_inicial, compensacao_flanco_inicial, tempo)" +
                 String.format("values (%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d)",eletrodo.id,eletrodo.comprimento,
                                eletrodo.haste,eletrodo.compensacao, eletrodo.peso, eletrodo.utilizacao, eletrodo.angulo,eletrodo.utilizacaoFlancoIni, eletrodo.tempoFlancoIni,
                                eletrodo.compensacaoFlancoIni,eletrodo.tempo);
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(query);
        db.close();
    }

    public int contarLinhas(String query){
        SQLiteDatabase db = getReadableDatabase();

       Cursor cursor = db.rawQuery(query,null);

       int numLines = 0;

       if(cursor.moveToFirst()){
           do{
            numLines++;
           }while(cursor.moveToNext());
       }

       return numLines;
    }

    public boolean updateRecEletrodo(Eletrodo eletrodo){

        int num = contarLinhas("Select * from Receita_Eletrodo where id="+eletrodo.id);

        if(num ==0){
            //inserir
            inserirRecEletrodos(eletrodo);
            return true;
        }
        String query = String.format("Update Receita_Eletrodo set comprimento = %d," +
                        "tempo = %d, haste= %d, compensacao= %d, peso= %d, utilizacao= %d, angulo= %d, " +
                        "utilizacao_flanco = %d, tempo_flanco_inicial= %d, compensacao_flanco_inicial = %d",
                        eletrodo.comprimento, eletrodo.tempo, eletrodo.haste, eletrodo.compensacao, eletrodo.peso, eletrodo.utilizacao,
                        eletrodo.angulo, eletrodo.utilizacaoFlancoIni, eletrodo.tempoFlancoIni, eletrodo.compensacaoFlancoIni);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);

        return true;
    }

    public boolean updateReceita(Receita receita){

        int num = contarLinhas("Select * from Receitas where id="+receita.id);

        if(num ==0){
            //inserir
            inserirReceita(receita);
            return true;
        }
                String query = String.format("Update Receitas set vale_especial=%d, vale = %d, inicio_friso = %d, vale_inicial = %d, comprimento = %d," +
                        "primeiro_friso = %f,passo = %f, altura = %d, base = %f, ci = %d, cf = %d, angulo = %d, profundidade = %d," +
                        "angulo_eletrodo = %d, percentagem_queima = %d, vale_zero = %d where id=%d",
                receita.valeEspecial,receita.vale,receita.inicioFriso, receita.valeInicial,
                receita.comprimento, receita.primeiroFriso, receita.passo, receita.altura, receita.base,
                receita.ci,receita.cf, receita.angulo, receita.profundidade, receita.anguloEletrodo, receita.percentagemQueima,
                receita.valeZero,receita.id);

        System.out.println(query);


        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("vale_especial",receita.valeEspecial);
        cv.put("vale",receita.vale);
        cv.put("inicio_friso",receita.inicioFriso);
        cv.put("vale_inicial",receita.valeInicial);
        cv.put("comprimento",receita.comprimento);
        cv.put("primeiro_friso",receita.primeiroFriso);
        cv.put("passo",receita.passo);
        cv.put("altura",receita.altura);

        cv.put("base",receita.base);
        cv.put("ci",receita.ci);
        cv.put("cf",receita.cf);
        cv.put("angulo",receita.angulo);
        cv.put("profundidade", receita.profundidade);

        cv.put("angulo_eletrodo", receita.anguloEletrodo);
        cv.put("percentagem_queima", receita.percentagemQueima);
        cv.put("vale_zero", receita.valeZero);

        db.update("Receitas", cv, "id=?",new String[]{String.valueOf(receita.id)});

        return true;
    }

    public Eletrodo pegarRecEletrodo(int id){

        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from Receita_Eletrodo where id=" + id;
        Cursor cursor = db.rawQuery(query,null);

        Eletrodo rec = new Eletrodo();
        if (cursor.moveToFirst()){

            do{
                rec.id = cursor.getInt(0);
                rec.comprimento = cursor.getInt(1);
                rec.haste = cursor.getInt(2);
                rec.compensacao = cursor.getInt(3);
                rec.peso = cursor.getInt(4);
                rec.utilizacao = cursor.getInt(5);
                rec.angulo = cursor.getInt(6);
                rec.utilizacaoFlancoIni = cursor.getInt(7);
                rec.tempoFlancoIni = cursor.getInt(8);
                rec.compensacaoFlancoIni = cursor.getInt(9);
                rec.tempo = cursor.getInt(10);
            }while(cursor.moveToNext());
        }

        return rec;
    }

    public Receita pegarReceita(int id){

        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from Receitas where id=" + id;
        Cursor cursor = db.rawQuery(query,null);

        Receita rec = new Receita();
        if (cursor.moveToFirst()){

            do{

                rec.id = cursor.getInt(0);
                rec.comprimento = cursor.getInt(1);
                rec.primeiroFriso = cursor.getFloat(2);
                rec.passo = cursor.getFloat(3);
                rec.altura = cursor.getInt(4);
                rec.base = cursor.getFloat(5);
                rec.ci = cursor.getInt(6);
                rec.cf = cursor.getInt(7);
                rec.angulo = cursor.getInt(8);
                rec.profundidade = cursor.getInt(9);
                rec.valeZero = cursor.getInt(10);
                rec.valeEspecial = cursor.getInt(11);
                rec.vale = cursor.getInt(12);
                rec.valeInicial = cursor.getInt(13);
                rec.anguloEletrodo = cursor.getInt(14);
                rec.percentagemQueima = cursor.getInt(15);
                rec.inicioFriso = cursor.getInt(16);

            }while(cursor.moveToNext());
        }

        return rec;
    }

    public Map<String,Integer> lerPreferencias(){

        Map<String,Integer> map = new HashMap<String,Integer>();
        String query = "SELECT * FROM " + memoryTableName;
        SQLiteDatabase db = getReadableDatabase();
        String number;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                map.put("Receita", cursor.getInt(1));
                map.put("Vale", cursor.getInt(2));
                map.put("IntervaloLub", cursor.getInt(3));
                map.put("VelocidadeOsc", cursor.getInt(4));
                map.put("DistanciaOsc", cursor.getInt(5));
                map.put("IP", cursor.getInt(6));
                map.put("Porta", cursor.getInt(7));
            } while (cursor.moveToNext());

            return map;
        }else
            return map;
    }

    public void inserirEletrodosLocal(ContentValues cv){
        SQLiteDatabase db = getWritableDatabase();
        db.insert("Eletrodos",null, cv);
    }


    public Map<String, String> lerEletrodosLocal(){

        Map<String, String> map = new HashMap<String, String>();
        String query = "SELECT * FROM Eletrodos WHERE Enviado = 0";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()) {
            do {
                map.put("Id", cursor.getString(0));
                map.put("Timestamp", cursor.getString(1));
                map.put("Tempo_Trabalhado", cursor.getString(2));
                map.put("Vale", cursor.getString(3));
                map.put("Direcao", cursor.getString(4));
                map.put("Especial", cursor.getString(5));
                map.put("Receita", cursor.getString(6));
                map.put("Turno", cursor.getString(7));
                map.put("Parou", cursor.getString(8));
                map.put("Num_Serie", cursor.getString(9));
                map.put("Simulacao", cursor.getString(10));
                map.put("Enviado", cursor.getString(11));
            } while (cursor.moveToNext());

            return map;
        }else
            return map;
    }


    public void updateEletrodosLocal(String id){
        String query = "UPDATE Eletrodos SET Enviado = \"1\" WHERE id = " + id;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }





}
