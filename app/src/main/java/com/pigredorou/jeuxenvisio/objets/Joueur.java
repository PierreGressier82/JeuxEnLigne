package com.pigredorou.jeuxenvisio.objets;

public class Joueur implements Comparable<Joueur> {

    private int mId;
    private String mNomJoueur;
    private String mNomEquipe;
    private int mScore;
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

    public Joueur(String nomJoueur, String nomEquipe, int score, int admin) {
        mNomJoueur = nomJoueur;
        mNomEquipe = nomEquipe;
        mScore = score;
        mAdmin = admin;
    }

    public Joueur(int id, String nomJoueur, int score, int admin, int aNew, int actif) {
        mId = id;
        mNomJoueur = nomJoueur;
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
