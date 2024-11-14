package com.duo.superior.duo.modbus;
import static java.lang.Integer.parseInt;
import com.duo.superior.duo.Global;
import com.duo.superior.duo.modbus.asynchronous.Async;
import com.duo.superior.duo.modbus.asynchronous.Words;


public class CLP {

    public static boolean rodando;
    ModbusClient modbus;
    public static String[] alarmesRobo = new String[64];


    public CLP(String ip){
        try {
            this.modbus = new ModbusClient(ip, 502);
            this.modbus.setConnectionTimeout(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TESTE-ME
    public int lerWord(String endereco) throws Exception{
        return this.modbus.ReadHoldingRegisters(endWordDVP15MC(endereco),1)[0];
    }

    public boolean lerAlarmes() throws Exception{
        //MD147, primeiro alarme eixo -> Z

        String alarmeCLP = "MD147";

        int endW = parseInt(alarmeCLP.substring(alarmeCLP.indexOf("D")+1))*2;

        int word[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC("MW"+endW),(Global.numAlarmesCLP+Global.numAlamesDriver));
        boolean novoAlarme = false;

        for(int i=0; i<word.length;i++){
            if(word[i] != Global.al[i]){
                Global.al[i] = word[i];
                novoAlarme = true;
            }
        }
        //Lemos bit a bit, e lemos o arquivo de alarmes, retornamos a string com os textos de alarmes
        return novoAlarme;
    }

    public boolean[] lerSequencia(String endereco,int numero) throws Exception{

        int words = numero>=16?(int) Math.ceil(numero/16):1;
        boolean retorneMe[] = new boolean[numero];


        int word[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC(endereco),words);

        for(int j =0;j < words;j++) {

            for (int i = 0; i < 16; i++) {
                if (((word[j]>> i) & 1) == 1) {
                    retorneMe[i+16*j] = true;
                } else if(i+16*j==numero){
                        break;
                    }else{
                    retorneMe[i+16*j] = false;
                }
            }
        }

//        for(int i=0;i<numero;i++){
//            System.out.println(i+"|------->"+retorneMe[i]);
//        }

        return retorneMe;

    }
    //TESTE-ME
    public boolean lerBit(String endereco) throws Exception
    {
        final int ponto = endereco.indexOf('.');
        final int X = endereco.indexOf('X');

        int c = parseInt(endereco.substring(X+1,ponto));
        int a = (int) Math.floor(c/2);
        int b = parseInt(endereco.substring(ponto+1));

        int add = c%2==0?0:8;

        String endWord = "MW"+a;
        int word = this.lerWord(endWord);

        int returnMe = (word >> (b+add)) & 1;

        if(returnMe == 1) {
            return true;
        }else
                return false;
    }

    public void escreverFloat(String endereco, float valor) throws Exception{
       int dword = Float.floatToIntBits(valor);
       this.escreverInteiro(endereco,dword);
    }

    public float lerFloat(String endereco) throws Exception{

        int dword = this.lerInteiro(endereco);
//        int intBits = Float.floatToIntBits(dword);
//        String binary = Integer.toBinaryString(intBits);
        return Float.intBitsToFloat(dword);
    }
    //TESTE ME INTENSAMENTE
    public float lerFloat2(String endereco) throws Exception{

        int dword = this.lerInteiro(endereco);
        int sign = (dword >>31 &1) ==1?-1:1;
        int exponent = ((dword&(0xFF<<23)) >> 23)-127;
        int mantissa = dword&(0x7FFFFF);

        int numBitsMantissa = (int)(Math.log(mantissa)/Math.log(2));

        float retorneMe = 0;
        for(int i=0;i<numBitsMantissa;i++){
            if((mantissa>>(numBitsMantissa-i) & 1) ==1){
                retorneMe += Math.pow(2,-23+(numBitsMantissa-i));
            }
        }

        retorneMe = sign*(retorneMe+1)*((int) Math.pow(2,exponent));

        System.out.println(retorneMe);
        return retorneMe;
    }

    public static int wPDW(int p0, int p1){

        if(p0>0 && p1==0){
            return p0;
        }

        if(p0<0 && p1>0){
            int a = p1 << 16;
            int mask = 0b1111111111111111 << 16;
            return a|(mask^p0);
        }

        if(p1<0 && p0>0){
            int returnMe = (p1<<16)| p0;
            return returnMe;
        }

        if(p1<0 && p0<0){
            int a = p1 << 16;
            int mask = 0b1111111111111111 << 16;
            int returnMe = a|(mask^p0);
            return returnMe;
        }

        int a = p1 << 16;
        int mask = 0b1111111111111111 << 16;
        int returnMe = a|(p0);

        return returnMe;
    }

    public int[] lerInteiroSequencia(String endereco, int quantos) throws Exception{
        int endW = parseInt(endereco.substring(endereco.indexOf("D")+1))*2;
        int p[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC("MW"+endW),2*quantos);

        int[] returnMe = new int[quantos];

        for(int i =0; i< quantos;i++){
            if(p[2*i]>0 && p[1+2*i]==0){
                returnMe[i] = p[2*i];
                continue;
            }

            if(p[2*i]<0 && p[1+2*i]>0){
                int a = p[1+2*i] << 16;
                int mask = 0b1111111111111111 << 16;
                returnMe[i] =a|(mask^p[0]);
                continue;
            }

            if(p[1+2*i]<0 && p[2*i]>0){
                returnMe[i] = (p[1+2*i]<<16)| p[2*i];

                continue;
            }

            if(p[1+2*i]<0 && p[2*i]<0){
                int a = p[1+2*i] << 16;
                int mask = 0b1111111111111111 << 16;
                returnMe[i] = a|(mask^p[2*i]);
                continue;
            }

            int a = p[2*i+1] << 16;
            int mask = 0b1111111111111111 << 16;
            returnMe[i] = a|(p[2*i]);
            continue;


        }

        return returnMe;
    }
    public int lerInteiro(String endereco) throws Exception{
        int endW = parseInt(endereco.substring(endereco.indexOf("D")+1))*2;
        int p[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC("MW"+endW),2);


        if(p[0]>0 && p[1]==0){
            return p[0];
        }

        if(p[0]<0 && p[1]>0){
            int a = p[1] << 16;
            int mask = 0b1111111111111111 << 16;
            return a|(mask^p[0]);
        }

        if(p[1]<0 && p[0]>0){
            int returnMe = (p[1]<<16)| p[0];
            return returnMe;
        }

        if(p[1]<0 && p[0]<0){
            int a = p[1] << 16;
            int mask = 0b1111111111111111 << 16;
            int returnMe = a|(mask^p[0]);
            return returnMe;
        }

        int a = p[1] << 16;
        int mask = 0b1111111111111111 << 16;
        int returnMe = a|(p[0]);
//        System.out.println("Q->"+Integer.toBinaryString(-4564566));
//        System.out.println("1->"+Integer.toBinaryString(p[0]));
//        System.out.println("2->"+Integer.toBinaryString(p[1]));
//        System.out.println("R->"+Integer.toBinaryString(returnMe));

        return returnMe;
    }

    public int lerInteiro2(String endereco) throws Exception{

        int endW = parseInt(endereco.substring(endereco.indexOf("D")+1))*2;
        int p[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC("MW"+endW),2);
        boolean negativo = false; // verdadeiro se estamos lidando com palavras negativas


        int size1 = Integer.toBinaryString(p[0]).length();
        int size2 = Integer.toBinaryString(p[1]).length();


        if (p[1] == -1 && p[0]> 0) { ///TESTAR
            p[0]*= -1;
            endereco = Integer.toBinaryString(p[0]);
            endereco = endereco.substring(16);
            return -1 * parseInt(endereco, 2);

        } else if (p[1]== -1 && p[0]< 0) { ///TESTAR

            return p[0];

        } else if (p[1] == 0 && p[0]< 0) {
            endereco = Integer.toBinaryString(p[1]);
            endereco = endereco.substring(16);
            return parseInt(endereco, 2);
        }
        if(p[0]==0 && p[1]<0){
            negativo=true;
            p[1]=p[1]*(-1);
        }
        if (p[0] < 0 && p[1] < 0) {
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = true;
            p[0] *= -1;
            p[1] = (p[1] + 1) * (-1);
        }
        if (p[0] > 0 && p[1] < 0) {//////////////////////////////////////////REVER CONDIÇÃO TESTAR p1=8789 p2=-2
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = false;

            p[1] = (p[1] + 1) * (-1);
        }
        String var10000 = this.wordParaBinario16Bits(p[1]);
        String var2 = "";
        if (p[0] >= 0) {
            var2 = this.wordParaBinario16Bits(p[0]);
        } else if (p[0] < 0 || negativo) {
            var2 = Integer.toBinaryString(p[0]).substring(16);

        }


        String dWord = var10000 + var2;


        if (dWord.charAt(0) == '0' && negativo == false) {
            return parseInt(dWord, 2);
        } else {
            return -1 * parseInt(dWord, 2);

        }

    }
    //TESTE-ME
    public int lerDWord(String enderecoDelta) throws  Exception {

        int p[] = this.modbus.ReadHoldingRegisters(endWordDVP15MC(enderecoDelta),2);
        boolean negativo = false; // verdadeiro se estamos lidando com palavras negativas


        int size1 = Integer.toBinaryString(p[0]).length();
        int size2 = Integer.toBinaryString(p[1]).length();


        if (p[1] == -1 && p[0]> 0) { ///TESTAR
            p[0]*= -1;
            enderecoDelta = Integer.toBinaryString(p[0]);
            enderecoDelta = enderecoDelta.substring(16);
            return -1 * parseInt(enderecoDelta, 2);

        } else if (p[1]== -1 && p[0]< 0) { ///TESTAR

            return p[0];

        } else if (p[1] == 0 && p[0]< 0) {
            enderecoDelta = Integer.toBinaryString(p[1]);
            enderecoDelta = enderecoDelta.substring(16);
            return parseInt(enderecoDelta, 2);
        }
        if(p[0]==0 && p[1]<0){
            negativo=true;
            p[1]=p[1]*(-1);
        }
        if (p[0] < 0 && p[1] < 0) {
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = true;
            p[0] *= -1;
            p[1] = (p[1] + 1) * (-1);
        }
        if (p[0] > 0 && p[1] < 0) {//////////////////////////////////////////REVER CONDIÇÃO TESTAR p1=8789 p2=-2
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = false;

            p[1] = (p[1] + 1) * (-1);
        }
        String var10000 = this.wordParaBinario16Bits(p[1]);
        String var2 = "";
        if (p[0] >= 0) {
            var2 = this.wordParaBinario16Bits(p[0]);
        } else if (p[0] < 0 || negativo) {
            var2 = Integer.toBinaryString(p[0]).substring(16);

        }


        String dWord = var10000 + var2;


        if (dWord.charAt(0) == '0' && negativo == false) {
            return parseInt(dWord, 2);
        } else {
            return -1 * parseInt(dWord, 2);

        }

    }

    ///TESTE-ME
    public synchronized void escreverBit(String endereco, boolean valor) throws Exception {

        boolean current = this.lerBit(endereco);

        final int ponto = endereco.indexOf('.');
        final int X = endereco.indexOf('X');

        int c = parseInt(endereco.substring(X+1,ponto));
        int a = (int) Math.floor(c/2);
        int b = parseInt(endereco.substring(ponto+1));

        int add = c%2==0?0:8;

        String endWord = endereco.charAt(0) + "W"+a;
        int word = this.lerWord(endWord);

        int returnMe = (word >> (b+add)) & 1;

        if(returnMe == 1) {
            current = true;
        }else{
            current = false;
        }

        if(current == valor){
            return;
        }

        if(valor == true){
            int escrevaMe = word + (1<<(b+add));
            this.modbus.WriteSingleRegister(endWordDVP15MC(endWord),escrevaMe);
        }

        else if(valor ==false){
            int escrevaMe = word - (1<<(b+add));
            this.modbus.WriteSingleRegister(endWordDVP15MC(endWord),escrevaMe);
        }

    }

    ///TESTE-ME
    public void  escreverWord(String endereco, int valor) throws Exception {
        this.modbus.WriteSingleRegister(endWordDVP15MC(endereco),valor);
    }


    public static void sEscreverInteiro(String endereco, int dword){
        Words word = new Words(dword,"escreverDWord",endereco);
        word.execute();
    }
    public synchronized void escreverInteiro(String endereco, int dword) throws Exception {

        int numero = 2*parseInt(endereco.substring(endereco.indexOf('D')+1));
        String end1 = "MW" + numero;
        numero++;
        String end2 = "MW" + numero;

        int[] words = dwPara2W(dword);
        escreverWord(end1, words[0]);
        escreverWord(end2, words[1]);

    }

    public static void sEscreverFloat(String endereco, float valor){
        Words delta = new Words(valor, "escreverFloat",endereco);
        delta.execute();
    }
    ///TESTE-ME
    public static void sEscreverBit(String endereco, boolean valor){
        try{
            Async as = new Async(endereco,valor);
            as.execute();
        }catch(Exception e){
           e.printStackTrace();
        }
    }

    ///TESTE-ME
    public static void sEscreverWord(String endereco, int valor)throws Exception{
        Words delta = new Words(valor, "escreverWord",endereco);
        delta.execute();
    }


    ///TESTE-ME
    public static void sEscreverDWord(String endereco, int valor){
        Words delta = new Words(valor, "escreverDWord",endereco);
        delta.execute();
    }

    public void desconectar() {
try {
    this.modbus.Disconnect();
}catch (Exception e){
    e.printStackTrace();
}
    }

    public void conectar() throws Exception{
            this.modbus.Connect();

    }

    public boolean estaConectado() {
        return this.modbus.isConnected();
    }


    public static int[] dwPara2W(int dword) {

        int[] words = new int[2];
        String temp1;
        String temp2;
        boolean negativo = false; //Vê se temos uma dword negativa

        if (dword < 0) {
            dword *= -1;
            negativo = true;
        }

        temp1 = Integer.toBinaryString(dword);
        int n = temp1.length();
        if (n <= 16 && !negativo) {
            words[1] = 0;
            words[0] = dword;
            return words;
        }
        if (temp1.length() <= 15) {
            //Temos uma palavra só portanto podemos retornar
            words[0] = dword;
            words[1] = 0;

            if (negativo) {
                words[0] *= -1;
                words[1] = -1;
            }
            return words;

        } else if (temp1.length() == 16) {
            //Temos uma palavra só portanto podemos retornar
            words[0] = dword;
            words[1] = 0;

            if (negativo) {
                words[0] *= -1;
                words[1] = -1;
            }
            return words;

        } else
            temp2 = temp1.substring(n - 16, n);
        temp1 = temp1.substring(0, n - 16);

        words[0] = parseInt(temp2, 2);
        words[1] = parseInt(temp1, 2);
        if (negativo) {


            words[0] *= -1;
            words[1] = -1 * (words[1] + 1);
        }

        return words;

    }

    public static void sescreverDWord(String enderecoDelta, int dword){

        int p2 = Integer.valueOf(enderecoDelta.substring(1)) + 1;
        String end2 = enderecoDelta.charAt(0) + String.valueOf(p2);

        int[] words = dwPara2W(dword);
        sescreverWord(enderecoDelta, words[0]);
        sescreverWord(end2, words[1]);

    }

    public static int enderecoEstatico(String enderecoDelta) throws Exception {
        char r = enderecoDelta.charAt(0);
        int endNum;
        int d0;
        if (r == 'M') {
            endNum = Integer.valueOf(enderecoDelta.substring(1));
            if (endNum >= 0 && endNum <= 4095) {
                if (endNum <= 1535) {
                    d0 = (int) Long.parseLong("800", 16);
                    return d0 + endNum;
                } else {
                    endNum -= 1536;
                    d0 = (int) Long.parseLong("b000", 16);
                    return d0 + endNum;
                }
            } else {
                throw new Exception("Endereço fora do intervalo válido.");
            }
        } else if (r != 'D') {
            if (r == 'I') {
                endNum = (int) Long.parseLong("0400", 16);
                d0 = Integer.valueOf(enderecoDelta.substring(1));
                if (d0 >= 0 && d0 <= 7) {
                    return endNum + d0;
                } else {
                    throw new Exception("Endereço fora do intervalo válido.");
                }
            } else if (r == 'Q') {
                endNum = (int) Long.parseLong("0500", 16);
                d0 = Integer.valueOf(enderecoDelta.substring(1));
                if (d0 >= 0 && d0 <= 7) {
                    return endNum + d0;
                } else {
                    throw new Exception("Endereço fora do intervalo válido.");
                }
            } else {
                throw new Exception("Endereço Inválido!");
            }
        } else {
            endNum = Integer.valueOf(enderecoDelta.substring(1));
            if (endNum >= 0 && (endNum <= 6226 || endNum >= 6250) && (endNum <= 6476 || endNum >= 6500) && (endNum <= 6518 || endNum >= 7000) && endNum <= 45055) {
                if (endNum >= 0 && endNum <= 4095) {
                    d0 = (int) Long.parseLong("1000", 16);
                    return d0 + endNum;
                } else if (endNum >= 4096 && endNum <= 5999) {
                    endNum -= 4096;
                    d0 = (int) Long.parseLong("9000", 16);
                    return d0 + endNum;
                } else if (endNum >= 7000 && endNum <= 24575) {
                    endNum -= 7000;
                    d0 = (int) Long.parseLong("9b58", 16);
                    return d0 + endNum;
                } else if (endNum >= 6000 && endNum <= 6226) {
                    endNum -= 6000;
                    d0 = (int) Long.parseLong("9770", 16);
                    return d0 + endNum;
                } else if (endNum >= 6250 && endNum <= 6476) {
                    endNum -= 6250;
                    d0 = (int) Long.parseLong("986a", 16);
                    return d0 + endNum;
                } else if (endNum >= 6500 && endNum <= 6518) {
                    endNum -= 6500;
                    d0 = (int) Long.parseLong("9964", 16);
                    return d0 + endNum;
                } else if (endNum >= 24576 && endNum <= 28671) {
                    endNum -= 24576;
                    d0 = (int) Long.parseLong("e000", 16);
                    return d0 + endNum;
                } else if (endNum >= 28672 && endNum <= 45055) {
                    endNum -= 28672;
                    d0 = (int) Long.parseLong("2000", 16);
                    return d0 + endNum;
                } else {
                    return 0;
                }
            } else {
                throw new Exception("Endereço fora do intervalo válido.");
            }
        }
    }


    public int endWordDVP15MC(String end){
        char type = end.charAt(0);

        int zero[] = {0x8000,0xA000,0x0000};

        int a = parseInt(end.substring(2));

        int returnMe = 0;

        switch (type){
            case 'M':
                returnMe = zero[2]+a;
                break;
            case 'I':
                returnMe = zero[0] + a;
                break;
            case 'Q':
                returnMe = zero[1] +a;
                break;
        }
        return returnMe;
    }

    public int endBitDVP15MC(String end){
      char type = end.charAt(0);
      int returnMe = 0;

        int a = parseInt(end.substring(end.indexOf('X')+1,end.indexOf('.')));
        int b = parseInt(end.substring(end.indexOf('.')+1));

        switch(type){
          case 'M':

              returnMe = 0x10000000 + 8*a + b;
              break;
          case 'I':

              returnMe = 0x6000 + 8*a + b;

              break;
          case 'Q':

              returnMe = 0xA000 + 8*a + b;
              break;
        }

    return returnMe;
    }

    public static void main(String[] args){

        CLP clp = new CLP(Global.ip);
        int f = clp.endBitDVP15MC("MX131071.7");
        System.out.println(String.format("-----> 0x%08X", f));
        System.out.println("Teste");
    }

    public int converterEndereco(String enderecoDelta) throws Exception {
        if(!Global.DVP15MC){
            return DVP15MC(enderecoDelta);
        }
        return DVP10MC(enderecoDelta);
    }


    ///Converte o endereco de DVP10MC para um endereco
    ///Convencionado do DVP15MC
    public int DVP15MC(String enderecoDelta){
        return 0;
    }

    ///Converte um endereco do DVP15MC em modubus
    public int converter(String endereco) throws Exception{
        //Ver se o endereco é consistente
        if(endereco.length()>8 || endereco.charAt(0)!='%' || (endereco.charAt(1)!='M' || endereco.charAt(1)!='Q' || endereco.charAt(1)!='I')){
            throw new IllegalArgumentException("O endereço passado é inválido!");
        }

        //ver se é bit ou word
        if(endereco.charAt(2)=='W'){
            //word
            int word = parseInt(endereco.substring(3));
            return word;
        } else if(endereco.charAt(2)=='X'){

            //bit
            int posPonto = endereco.indexOf('.');
            int word = parseInt(endereco.substring(3,posPonto));
            int bit = parseInt(endereco.substring(posPonto+1));

            return 8*word+bit;
        }else
            throw new IllegalArgumentException("O endereço passado é inválido");

    }

    public int DVP10MC(String enderecoDelta) throws Exception {
        char r = enderecoDelta.charAt(0);
        int endNum;
        int d0;
        if (r == 'M') {
            endNum = Integer.valueOf(enderecoDelta.substring(1));
            if (endNum >= 0 && endNum <= 4095) {
                if (endNum <= 1535) {
                    d0 = (int) Long.parseLong("800", 16);
                    return d0 + endNum;
                }else {
                    endNum -= 1536;
                    d0 = (int) Long.parseLong("b000", 16);
                    return d0 + endNum;
                }
            }else {
                throw new Exception("Endereço fora do intervalo válido.");
            }
        }else if (r != 'D') {
            if (r == 'I') {
                endNum = (int) Long.parseLong("0400", 16);
                d0 = Integer.valueOf(enderecoDelta.substring(1));
                if (d0 >= 0 && d0 <= 7) {
                    return endNum + d0;
                } else {
                    throw new Exception("Endereço fora do intervalo válido.");
                }
            } else if (r == 'Q') {
                endNum = (int) Long.parseLong("0500", 16);
                d0 = Integer.valueOf(enderecoDelta.substring(1));
                if (d0 >= 0 && d0 <= 7) {
                    return endNum + d0;
                } else {
                    throw new Exception("Endereço fora do intervalo válido.");
                }
            } else {
                throw new Exception("Endereço Inválido!");
            }
        } else {
            endNum = Integer.valueOf(enderecoDelta.substring(1));
            if (endNum >= 0 && (endNum <= 6226 || endNum >= 6250) && (endNum <= 6476 || endNum >= 6500) && (endNum <= 6518 || endNum >= 7000) && endNum <= 45055) {
                if (endNum >= 0 && endNum <= 4095) {
                    d0 = (int) Long.parseLong("1000", 16);
                    return d0 + endNum;
                } else if (endNum >= 4096 && endNum <= 5999) {
                    endNum -= 4096;
                    d0 = (int) Long.parseLong("9000", 16);
                    return d0 + endNum;
                } else if (endNum >= 7000 && endNum <= 24575) {
                    endNum -= 7000;
                    d0 = (int) Long.parseLong("9b58", 16);
                    return d0 + endNum;
                } else if (endNum >= 6000 && endNum <= 6226) {
                    endNum -= 6000;
                    d0 = (int) Long.parseLong("9770", 16);
                    return d0 + endNum;
                } else if (endNum >= 6250 && endNum <= 6476) {
                    endNum -= 6250;
                    d0 = (int) Long.parseLong("986a", 16);
                    return d0 + endNum;
                } else if (endNum >= 6500 && endNum <= 6518) {
                    endNum -= 6500;
                    d0 = (int) Long.parseLong("9964", 16);
                    return d0 + endNum;
                } else if (endNum >= 24576 && endNum <= 28671) {
                    endNum -= 24576;
                    d0 = (int) Long.parseLong("e000", 16);
                    return d0 + endNum;
                } else if (endNum >= 28672 && endNum <= 45055) {
                    endNum -= 28672;
                    d0 = (int) Long.parseLong("2000", 16);
                    return d0 + endNum;
                } else {
                    return 0;
                }
            } else {
                throw new Exception("Endereço fora do intervalo válido.");
            }
        }
    }

    public String wordParaBinario16Bits(int word) throws Exception {
        int i;
        if (word < 0) {
            word *= -1;
            StringBuilder str = new StringBuilder("1");
            String bin = Integer.toBinaryString(word);

            for (i = 0; i < 15; ++i) {
                char cChar;
                try {
                    cChar = bin.charAt(i);
                }catch (Exception e){
                    cChar='0';
                }
                if (cChar== '0') {
                    str.append("1");
                } else {
                    str.append("0");
                }
            }

            return str.toString();
        } else if (word < 0) {
            return "0";
        } else {
            String bin = Integer.toBinaryString(word);
            if (bin.length() >= 16) {
                throw new Exception("Parâmetro inválido.");
            } else {
                StringBuilder str = new StringBuilder(bin);

                for (i = 0; i < 16 - bin.length(); ++i) {
                    str.insert(0, "0");
                }

                bin = str.toString();
                return bin;
            }
        }
    }

    public static String sWordParaBinario16Bits(int word) throws Exception {
        int i;
        if (word < 0) {
            word *= -1;
            StringBuilder str = new StringBuilder("1");
            String bin = Integer.toBinaryString(word);

            for (i = 0; i < 15; ++i) {

                if (bin.charAt(i) == '0') {
                    str.append("1");
                } else {
                    str.append("0");
                }
            }

            return str.toString();
        } else if (word < 0) {
            return "0";
        } else {
            String bin = Integer.toBinaryString(word);
            if (bin.length() >= 16) {
                throw new Exception("Parâmetro inválido.");
            } else {
                StringBuilder str = new StringBuilder(bin);

                for (i = 0; i < 16 - bin.length(); ++i) {
                    str.insert(0, "0");
                }

                bin = str.toString();
                return bin;
            }
        }
    }



    public static int slerWord(String endereco,ModbusClient modbus) throws Exception {
        return modbus.ReadHoldingRegisters(enderecoEstatico(endereco), 1)[0];
    }


    public static void sescreverWord(String endereco, int valor)  {
        ModbusClient local=null;
        try {
             local = new ModbusClient("192.168.0.3", 502);
            local.Connect();
            local.WriteSingleRegister(enderecoEstatico(endereco), valor);
            local.Disconnect();
        }catch (Exception e ){
            try{local.Disconnect();}catch (Exception ex){ex.printStackTrace();}
            e.printStackTrace();
        }

    }


    public String inversor16Bits(int word) {
        if (word < 0) {
            word *= -1;
        }
        StringBuilder str = new StringBuilder();
        String word1 = Integer.toBinaryString(word);
        int r = 0;
        for (int i = 0; i < 15; i++) {

            if (i >= (16 - word1.length())) {
                if (word1.charAt(i - r) == '1') {
                    str.append('0');
                    continue;
                } else
                    str.append('1');
                continue;
            } else
                str.append('1');
            r++;

        }

        return str.toString();
    }

    public int binarioParaWord16bits(String bin) {
        if (bin.charAt(0) != '1') {
            return parseInt(bin, 2);
        } else {
            StringBuilder str = new StringBuilder();
            int i = 0;
            if (i < bin.length()) {
                if (bin.charAt(i) == '1') {
                    str.append("0");
                    return parseInt(str.toString());
                } else {
                    str.append("1");
                    return parseInt(str.toString());
                }
            } else {
                return -1 * parseInt(bin, 2);
            }
        }
    }


    public static int sbinarioParaWord16bits(String bin) {
        if (bin.charAt(0) != '1') {
            return parseInt(bin, 2);
        } else {
            StringBuilder str = new StringBuilder();
            int i = 0;
            if (i < bin.length()) {
                if (bin.charAt(i) == '1') {
                    str.append("0");
                    return parseInt(str.toString());
                } else {
                    str.append("1");
                    return parseInt(str.toString());
                }
            } else {
                return -1 * parseInt(bin, 2);
            }
        }
    }


    public static void forceSetReset(String endereco, boolean valor){
            try {
                boolean goal;

                ModbusClient local = new ModbusClient("192.168.0.3", 502);
                local.Connect();

                while((goal=local.ReadDiscreteInputs((enderecoEstatico(endereco)),1)[0])!=valor) {
                    local.WriteSingleCoil((enderecoEstatico(endereco)), valor);
                }
                local.Disconnect();
            }catch (Exception e ){
                System.out.println("Sem conexão!!");

                e.printStackTrace();
            }
    }

    public static void sSetReset(String endereco){
        try {
            boolean goal;

            System.out.println("*******************SETRESET*********************");
            ModbusClient local = new ModbusClient("192.168.0.3", 502);
            local.Connect();

            local.WriteSingleCoil((enderecoEstatico(endereco)), true);
            //Thread.currentThread().wait(500);
            local.WriteSingleCoil((enderecoEstatico(endereco)), false);
            local.Disconnect();
        }catch (Exception e ){
            System.out.println("Sem conexão!!");

            e.printStackTrace();
        }
    }

    public static void setReset(String endereco, boolean valor){

        if (endereco.charAt(0) != 'M' && endereco.charAt(0) != 'Q' && endereco.charAt(0) != 'I') {
            try {
                if (endereco.charAt(0) != 'D') {
                    System.out.println("Erro de escrita.");
                    return;
                }
                ModbusClient local = new ModbusClient("192.168.0.3", 502);
                local.Connect();

                int word = slerWord(endereco.substring(0, endereco.indexOf(".")),local);
                String bin = sWordParaBinario16Bits(word);
                int bit = endereco.indexOf(46);
                bit = Integer.valueOf(endereco.substring(bit + 1));
                bit = 15 - bit;
                StringBuilder temp = new StringBuilder("");

                for (int i = 0; i < 16; ++i) {
                    if (i == bit) {
                        if (valor) {
                            temp.append("1");
                        } else {
                            temp.append("0");
                        }
                    } else {
                        temp.append(bin.charAt(i));
                    }
                }

                bin = temp.toString();
                word=sbinarioParaWord16bits(bin);
                String endereco2 = endereco.substring(0, endereco.indexOf(46));
                local.WriteSingleRegister(enderecoEstatico(endereco2),word);
                local.Disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
            //this.modbus.WriteSingleRegister(this.converterEndereco(endereco2), bit);
        }
        else {
            try {
                ModbusClient local = new ModbusClient("192.168.0.3", 502);
                local.Connect();
                local.WriteSingleCoil((enderecoEstatico(endereco)), valor);
                local.Disconnect();
            }catch (Exception e ){
                System.out.println("Sem conexão!!");
                e.printStackTrace();
            }
        }

    }

    public int[] palavrasAlarmes() throws Exception{
    //Lemos as 4 palavras
        int[] palavras = new int[4];

            palavras = this.modbus.ReadHoldingRegisters(this.converterEndereco("D6470"), 4);
            return palavras;

    }


    public boolean[] lerSequenciaBitsDVP10MC(String endereco, int num) throws Exception {
        if (endereco.charAt(0) != 'M' && endereco.charAt(0) != 'Q' && endereco.charAt(0) != 'I' && endereco.charAt(0)!='D') {
            throw new Exception("Este método funciona somenta para endereços dos tipos: M,I,Q.");
        }else if(endereco.charAt(0)=='D'){
            int palavra = lerWordDVP10MC(endereco);
            String palabra = wordParaBinario16Bits(palavra);
            boolean listaBits[] = new boolean[16];

            for(int i=0;i<16;i++){
                listaBits[i]=palabra.charAt(i)=='1'? true:false;
            }

            return listaBits;
        }
        else {
            if (!this.modbus.isConnected()) {
                this.modbus.Connect();
            }

            return this.modbus.ReadDiscreteInputs(this.converterEndereco(endereco), num);
        }
    }

    public boolean lerBitDVP10MC(String endereco) throws Exception {
        if(Global.DVP15MC) {
            return false;
        }else
        {
            if (endereco.charAt(0) != 'M' && endereco.charAt(0) != 'Q' && endereco.charAt(0) != 'I') {
                if (endereco.charAt(0) == 'D') {
                    int bit = endereco.indexOf(46);
                    if (bit == -1) {
                        throw new Exception("Uma WORD foi especificada e não um bit.");
                    } else {
                        String sub = endereco.substring(0, bit);
                        int word = this.lerWordDVP10MC(sub);
                        bit = Integer.valueOf(endereco.substring(bit + 1));
                        if (bit >= 0 && bit <= 15) {
                            String bin = this.wordParaBinario16Bits(word).substring(15 - bit, 16 - bit);
                            return bin.equals("1");
                        } else {
                            throw new Exception("O bit especificado está fora do intervalo válido.");
                        }
                    }
                } else {
                    return false;
                }
            } else {


                return this.modbus.ReadDiscreteInputs(this.converterEndereco(endereco), 1)[0];
            }
        }
    }

    public void escreverDWordDVP10MC(String enderecoDelta, int dword) throws Exception {

        int p2 = Integer.valueOf(enderecoDelta.substring(1)) + 1;
        String end2 = enderecoDelta.charAt(0) + String.valueOf(p2);

        int[] words = dwPara2W(dword);
        escreverWordDVP10MC(enderecoDelta, words[0]);
        escreverWordDVP10MC(end2, words[1]);

    }

    public synchronized void escreverBitDVP10MC(String endereco, boolean valor) throws Exception {
        if (endereco.charAt(0) != 'M' && endereco.charAt(0) != 'Q' && endereco.charAt(0) != 'I') {
            if (endereco.charAt(0) != 'D') {
                throw new Exception("Erro de escrita.");
            }

            int word = this.lerWordDVP10MC(endereco.substring(0, endereco.indexOf(".")));
            String bin = this.wordParaBinario16Bits(word);
            int bit = endereco.indexOf(46);
            bit = Integer.valueOf(endereco.substring(bit + 1));
            bit = 15 - bit;
            StringBuilder temp = new StringBuilder("");

            for (int i = 0; i < 16; ++i) {
                if (i == bit) {
                    if (valor) {
                        temp.append("1");
                    } else {
                        temp.append("0");
                    }
                } else {
                    temp.append(bin.charAt(i));
                }
            }

            bin = temp.toString();
            word = this.binarioParaWord16bits(bin);
            String endereco2 = endereco.substring(0, endereco.indexOf(46));
            this.modbus.WriteSingleRegister(this.converterEndereco(endereco2),word);
        } else {


            this.modbus.WriteSingleCoil(this.converterEndereco(endereco), valor);
        }

    }

    public void escreverWordDVP10MC(String endereco, int valor) throws Exception {
        this.modbus.WriteSingleRegister(this.converterEndereco(endereco), valor);
    }

    public void DMOVDVP10MC(String enderecoPara, String enderecoDe) throws Exception {

        int p2 = Integer.valueOf(enderecoPara.substring(1)) + 1;
        String end2 = enderecoPara.charAt(0) + String.valueOf(p2);

        int dword = this.lerDWordDVP10MC(enderecoDe);
        int[] words = dwPara2W(dword);
        escreverWordDVP10MC(enderecoPara, words[0]);
        escreverWordDVP10MC(end2, words[1]);
    }

    public int lerDWordDVP10MC(String enderecoDelta) throws  Exception {

        int p[] = this.modbus.ReadHoldingRegisters(this.converterEndereco(enderecoDelta), 2);
        boolean negativo = false; // verdadeiro se estamos lidando com palavras negativas


        int size1 = Integer.toBinaryString(p[0]).length();
        int size2 = Integer.toBinaryString(p[1]).length();


        if (p[1] == -1 && p[0]> 0) { ///TESTAR
            p[0]*= -1;
            enderecoDelta = Integer.toBinaryString(p[0]);
            enderecoDelta = enderecoDelta.substring(16);
            return -1 * parseInt(enderecoDelta, 2);

        } else if (p[1]== -1 && p[0]< 0) { ///TESTAR

            return p[0];

        } else if (p[1] == 0 && p[0]< 0) {
            enderecoDelta = Integer.toBinaryString(p[1]);
            enderecoDelta = enderecoDelta.substring(16);
            return parseInt(enderecoDelta, 2);
        }
        if(p[0]==0 && p[1]<0){
            negativo=true;
            p[1]=p[1]*(-1);
        }
        if (p[0] < 0 && p[1] < 0) {
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = true;
            p[0] *= -1;
            p[1] = (p[1] + 1) * (-1);
        }
        if (p[0] > 0 && p[1] < 0) {//////////////////////////////////////////REVER CONDIÇÃO TESTAR p1=8789 p2=-2
            if (p[1] == -1) {
                p[1] = 0;
            }
            negativo = false;

            p[1] = (p[1] + 1) * (-1);
        }
        String var10000 = this.wordParaBinario16Bits(p[1]);
        String var2 = "";
        if (p[0] >= 0) {
            var2 = this.wordParaBinario16Bits(p[0]);
        } else if (p[0] < 0 || negativo) {
            var2 = Integer.toBinaryString(p[0]).substring(16);

        }


        String dWord = var10000 + var2;


        if (dWord.charAt(0) == '0' && negativo == false) {
            return parseInt(dWord, 2);
        } else {
            return -1 * parseInt(dWord, 2);

        }

    }

    public int[] lerSequenciaWordsDVP10MC(String endereco, int numero) throws Exception {
        //Lê até 100 palavras
        return this.modbus.ReadHoldingRegisters(this.converterEndereco(endereco), numero);
    }

    public int lerWordDVP10MC(String endereco) throws Exception {
        return this.modbus.ReadHoldingRegisters(this.converterEndereco(endereco), 1)[0];
    }

    public void escreverSequenciaBitsDVP10MC(String endereco, boolean[] valores) throws Exception {
        if (endereco.charAt(0) != 'M' && endereco.charAt(0) != 'Q' && endereco.charAt(0) != 'I') {
            if (endereco.charAt(0) != 'D') {
                throw new Exception("Erro de escrita.");

            } else {
                this.modbus.WriteMultipleCoils(this.converterEndereco(endereco), valores);
            }

        }
    }
}