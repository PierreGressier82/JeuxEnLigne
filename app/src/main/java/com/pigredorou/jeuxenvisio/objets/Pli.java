package com.pigredorou.jeuxenvisio.objets;

public class Pli {
    private Carte mCarte;
    private final String mNomJoueur;
    private final String mCommunication;

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
