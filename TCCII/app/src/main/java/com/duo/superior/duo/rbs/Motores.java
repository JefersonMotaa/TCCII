package com.duo.superior.duo.rbs;

import com.duo.superior.duo.Assynch;

public class Motores {

    public static void fazerHome(int eixo){
        Assynch set;
        switch (eixo){
            case 0:
                try{
                    set = new Assynch("M30",true,true,true);
                    set.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 1:
                try{
                    set = new Assynch("M31",true,true,true);
                    set.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
                    set = new Assynch("M32",true,true,true);
                    set.execute();

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
