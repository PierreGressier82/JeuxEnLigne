package com.pigredorou.jeuxenvisio.objets;

public class Joueur {

    private int mId;
    private String mNomJoueur;
    private String mNomEquipe;
    private int mScoreEquipe;
    private int mAdmin;
    private int mNew;

    public Joueur(String nomJoueur, String nomEquipe, int scoreEquipe, int admin) {
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScoreEquipe = scoreEquipe;
        mAdmin = admin;
    }

    public Joueur(int id, String nomJoueur, int admin, int aNew) {
        mId = id;
        mNomJoueur = nomJoueur;
        mAdmin = admin;
        mNew = aNew;
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

    public int getNew() {
        return mNew;
    }
}
