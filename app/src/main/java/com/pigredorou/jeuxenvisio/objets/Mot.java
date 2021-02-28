package com.pigredorou.jeuxenvisio.objets;

public class Mot {
    private int idMot;
    private String mot;
    private int nbPoint;
    private int lettre;

    public Mot(int idMot, String mot, int lettre) {
        this.idMot = idMot;
        this.mot = mot;
        this.nbPoint = 1;
        this.lettre = lettre;
    }

    public int getIdMot() {
        return idMot;
    }

    public String getMot() {
        return mot;
    }

    public int getNbPoint() {
        return nbPoint;
    }

    public int getLettre() {
        return lettre;
    }
}
