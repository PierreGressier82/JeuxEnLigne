package com.pigredorou.jeuxenvisio.objets;

public class Mot {
    private final int idMot;
    private final String mot;
    private final int nbPoint;
    private final int lettre;
    private final boolean elimine;

    public Mot(int idMot, String mot, int lettre, boolean elimine) {
        this.idMot = idMot;
        this.mot = mot;
        this.nbPoint = 1;
        this.lettre = lettre;
        this.elimine = elimine;
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

    public boolean isElimine() {
        return elimine;
    }
}
