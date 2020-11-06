package com.pigredorou.jeuxenvisio;

public class Carte {
    private String mCouleur;
    private int mValeur;

    public Carte() {
        super();
    }

    Carte(String couleur, int valeur) {
        this.mValeur=valeur;
        this.mCouleur=couleur;
    }

    public void setValeur(int valeur) {
        this.mValeur=valeur;
    }

    public void setCouleur(String couleur) {
        this.mCouleur=couleur;
    }

    int getValeur() {
        return this.mValeur;
    }

    String getCouleur() {
        return this.mCouleur;
    }
}
