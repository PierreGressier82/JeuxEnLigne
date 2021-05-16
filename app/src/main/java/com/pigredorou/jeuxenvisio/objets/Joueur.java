package com.pigredorou.jeuxenvisio.objets;

public class Joueur implements Comparable<Joueur> {

    private int mId;
    private final String mNomJoueur;
    private final String mNomEquipe;
    private final int mAdmin;
    private int mScore;
    private int mNew;
    private int mActif;

    public Joueur(int id, String nomJoueur, int score, int admin, int aNew, int actif) {
        mId = id;
        mNomJoueur = nomJoueur;
        mScore = score;
        mAdmin = admin;
        mNew = aNew;
        mActif = actif;
        mNomEquipe = "";
    }

    public Joueur(String nomJoueur, String nomEquipe, int score, int admin) {
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScore = score;
        mAdmin = admin;
    }

    public Joueur(int id, String nomJoueur, String nomEquipe, int score, int admin, int aNew, int actif) {
        mId = id;
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScore = score;
        mAdmin = admin;
        mNew = aNew;
        mActif = actif;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
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

    @Override
    public int compareTo(Joueur joueur) {
        return (joueur.mScore - this.mScore);
    }
}
