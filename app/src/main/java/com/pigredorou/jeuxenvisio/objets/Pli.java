package com.pigredorou.jeuxenvisio.objets;

public class Pli {
    private String mNomJoueur;
    private Carte mCarte;
    private String mCommunication;

    public Pli(String joueur, Carte carte) {
        mNomJoueur = joueur;
        mCarte = carte;
    }

    public Pli(String nomJoueur, Carte carte, String communication) {
        mNomJoueur = nomJoueur;
        mCarte = carte;
        mCommunication = communication;
    }

    public String getNomJoueur() {
        return mNomJoueur;
    }

    public void setNomJoueur(String nomJoueur) {
        mNomJoueur = nomJoueur;
    }

    public String getCommunication() {
        return mCommunication;
    }

    public void setCommunication(String communication) {
        mCommunication = communication;
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
