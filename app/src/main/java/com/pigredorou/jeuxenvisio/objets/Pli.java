package com.pigredorou.jeuxenvisio.objets;

public class Pli {
    private String mNomJoueur;
    private Carte mCarte;
    private String mCommunication;

    public Pli(String nomJoueur, Carte carte, String communication) {
        mNomJoueur = nomJoueur;
        mCarte = carte;
        mCommunication = communication;
    }

    public String getNomJoueur() {
        return mNomJoueur;
    }

    public String getCommunication() {
        return mCommunication;
    }

    public String getJoueur() {
        return mNomJoueur;
    }

    public Carte getCarte() {
        return mCarte;
    }

    public void setCarte(Carte carte) {
        mCarte = carte;
    }
}
