package com.pigredorou.jeuxenvisio.objets;

public class Joueur {

    private int mId;
    private String mNomJoueur;
    private String mNomEquipe;
    private int mScoreEquipe;
    private int mAdmin;
    private int mNew;
    private int mActif;

    public Joueur(int id, String nomJoueur, int admin, int aNew, int actif) {
        mId = id;
        mNomJoueur = nomJoueur;
        mAdmin = admin;
        mNew = aNew;
        mActif = actif;
    }

    public Joueur(String nomJoueur, String nomEquipe, int scoreEquipe, int admin) {
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScoreEquipe = scoreEquipe;
        mAdmin = admin;
    }

    public Joueur(int id, String nomJoueur, int admin, int actif) {
        mId = id;
        mNomJoueur = nomJoueur;
        mAdmin = admin;
        mActif = actif;
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

    public int getActif() {
        return mActif;
    }

    public int getId() {
        return mId;
    }
}
