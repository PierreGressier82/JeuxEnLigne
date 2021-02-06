package com.pigredorou.jeuxenvisio.objets;

public class Carte {
    private String mCouleur;
    private int mValeur;

    public Carte(String couleur, int valeur) {
        this.mValeur = valeur;
        this.mCouleur = couleur;
    }

    public Carte(int valeur) {
        this.mValeur = valeur;
    }

    public int getValeur() {
        return this.mValeur;
    }

    public String getCouleur() {
        return this.mCouleur;
    }

}
