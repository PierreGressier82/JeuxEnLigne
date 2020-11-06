package com.pigredorou.jeuxenvisio;

public class Pli {
    private String mNomJoueur;
    private Carte mCarte;

    public Pli(String joueur, Carte carte) {
        mNomJoueur = joueur;
        mCarte = carte;
    }

    public String getJoueur() {
        return mNomJoueur;
    }

    public void setJoueur(String joueur) {
        mNomJoueur = joueur;
    }

    public Carte getCarte() {
        return mCarte;
    }

    public void setCarte(Carte carte) {
        mCarte = carte;
    }
}
