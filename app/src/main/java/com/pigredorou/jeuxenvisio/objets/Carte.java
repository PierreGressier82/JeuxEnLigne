package com.pigredorou.jeuxenvisio.objets;

public class Carte {
    private String mCouleur;
    private int mValeur;
    private String mNom;

    public Carte(String couleur, int valeur) {
        this.mValeur=valeur;
        this.mCouleur=couleur;
    }

    public Carte(String couleur, int valeur, String nom) {
        mCouleur = couleur;
        mValeur = valeur;
        mNom = nom;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        mNom = nom;
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
