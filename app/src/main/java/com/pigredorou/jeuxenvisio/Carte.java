package com.pigredorou.jeuxenvisio;

public class Carte {
    private String mCouleur;
    private int mValeur;

    public Carte() {
        super();
    }

    public Carte(String couleur, int valeur) {
        super();
        this.mValeur=valeur;
        this.mCouleur=couleur;
    }

    public void setValeur(int valeur) {
        this.mValeur=valeur;
    }

    public void setCouleur(String couleur) {
        this.mCouleur=couleur;
    }

    public int getValeur() {
        return this.mValeur;
    }

    public String getCouleur() {
        return this.mCouleur;
    }
}
