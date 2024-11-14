package com.duo.superior.duo.conectividade;

public class Eletrodo {
    public int id;
    public int comprimento;
    public int tempo;
    public int haste;
    public int compensacao;
    public int peso;
    public int utilizacao;
    public int angulo;
    public int utilizacaoFlancoIni;
    public int tempoFlancoIni;
    public int compensacaoFlancoIni;

    @Override
    public String toString() {
        return "Eletrodo{" +
                "id=" + id +
                ", comprimento=" + comprimento +
                ", tempo=" + tempo +
                ", haste=" + haste +
                ", compensacao=" + compensacao +
                ", peso=" + peso +
                ", utilizacao=" + utilizacao +
                ", angulo=" + angulo +
                ", utilizacaoFlancoIni=" + utilizacaoFlancoIni +
                ", tempoFlancoIni=" + tempoFlancoIni +
                ", compensacaoFlancoIni=" + compensacaoFlancoIni +
                '}';
    }
}
