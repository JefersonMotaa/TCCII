package com.duo.superior.duo.conectividade;

public class Receita {
    public int id; //Número da receita
    public int valeEspecial; //Quantidade total de vales especiais
    public int vale; //Quantidade total de vales
    public int inicioFriso;
    public int valeInicial;
    public int comprimento;
    public float primeiroFriso;
    public float passo;
    public int altura;
    public float base;
    public int ci;
    public int cf;
    public int angulo;
    public int profundidade;
    public int anguloEletrodo;
    public int percentagemQueima;
    public int valeZero;



    @Override
    public String toString() {
        return "Receita{" +
                "id=" + id +
                ", valeEspecial=" + valeEspecial +
                ", vale=" + vale +
                ", inicioFriso=" + inicioFriso +
                ", valeInicial=" + valeInicial +
                ", comprimento=" + comprimento +
                ", primeiroFriso=" + primeiroFriso +
                ", passo=" + passo +
                ", altura=" + altura +
                ", base=" + base +
                ", ci=" + ci +
                ", cf=" + cf +
                ", angulo=" + angulo +
                ", profundidade=" + profundidade +
                ", anguloEletrodo=" + anguloEletrodo +
                ", percentagemQueima=" + percentagemQueima +
                ", valeZero=" + valeZero +
                '}';
    }
}
