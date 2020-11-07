package com.pigredorou.jeuxenvisio.objets;

public class Carte {
    private String mCouleur;
    private int mValeur;
    private String mOption;

    public Carte() {
        super();
    }

    public Carte(String couleur, int valeur) {
        this.mValeur=valeur;
        this.mCouleur=couleur;
    }

    public Carte(String couleur, int valeur, String option) {
        mCouleur = couleur;
        mValeur = valeur;
        mOption = option;
    }

    public void setValeur(int valeur) {
        this.mValeur=valeur;
    }

    public void setCouleur(String couleur) {
        this.mCouleur=couleur;
    }

    public void setOption(String option) {
        mOption = option;
    }

    public int getValeur() {
        return this.mValeur;
    }

    public String getCouleur() {
        return this.mCouleur;
    }

    public String getOption() {
        return mOption;
    }
}
