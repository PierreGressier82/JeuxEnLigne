package com.pigredorou.jeuxenvisio.objets;

public class Joueur {

    private String mNomJoueur;
    private String mNomSalon;
    private String mNomEquipe;
    private int mScoreEquipe;
    private int mAdmin;

    public Joueur(String nomJoueur, String nomEquipe, int scoreEquipe, int admin) {
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScoreEquipe = scoreEquipe;
        mAdmin = admin;
    }

    public Joueur(String nomJoueur, String nomEquipe, int admin) {
        this.mNomJoueur=nomJoueur;
        this.mNomEquipe=nomEquipe;
        this.mAdmin=admin;
    }

    public Joueur(String nomJoueur, int admin) {
        mNomJoueur = nomJoueur;
        mAdmin = admin;
    }

    public int getScoreEquipe() {
        return mScoreEquipe;
    }

    public void setScoreEquipe(int scoreEquipe) {
        mScoreEquipe = scoreEquipe;
    }

    public String getNomEquipe() {
        return mNomEquipe;
    }

    public String getNomJoueur() {
        return mNomJoueur;
    }

    public int getAdmin() {
        return mAdmin;
    }
}
