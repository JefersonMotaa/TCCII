package com.duo.superior.duo;

public class Global {
    public static boolean parar=false;
    public static String ip = "192.168.0.2";
    public static boolean DVP15MC=false;
    public static int numAlarmesCLP = 4;
    public static int numAlamesDriver = 8;
    public static boolean novoAlarme;
    public static String alarmes;
    public static int[] al = new int[numAlamesDriver+numAlarmesCLP];
    public static int timeOut;
    public static int ternoEscolhido;
    public static int receita;
    public static int scanTime;
    public static boolean statusReceita;
    public static boolean first;
    public static boolean developer;
    public static boolean statusHome;
    public static boolean firstHome;
    public static boolean editarEletrodo;
    public static boolean editarReceita;
    public static boolean editarPreAjuste;


    //Dados da camisa da moenda
    public static int numero; //Número da receita
    public static float comprimento;
    public static float primeiroFriso;
    public static float passo;
    public static float altura;
    public static float base;
    public static float ci;
    public static float cf;
    public static float angulo;
    public static float Fprofundidade;
    public static float valeZero;
    public static float percentagemQueima;
    public static boolean inicioFriso;
    public static int valeEspecial; //Quantidade total de vales especiais
    public static int vale; //Quantidade total de vales
    public static int valeInicial;
    public static float anguloEletrodo;
    public static float gf;
    public static float per;
    //public float percentagemQueima;
    public static int seno;
    public static int cosseno;
    public static  int tempoMinQueima;

    public static boolean gatilho;
    public static boolean atualizandoRemoto;
    public static Class<?> returnCls;

    public static String previousTimeStamp;
    public static int numSerie = 18;
    public static int dbVersion = 2;
}
