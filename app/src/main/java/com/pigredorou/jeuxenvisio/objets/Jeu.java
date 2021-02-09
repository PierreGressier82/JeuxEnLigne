package com.pigredorou.jeuxenvisio.objets;

public class Jeu {
    private int mId; // Id du jeu en BDD
    private int mIdPartie; // Id de la partie de ce jeu en BDD
    private String mNom;
    private int mNumMission;

    public Jeu(int id, int idPartie, String nom, int numMission) {
        mId = id;
        mIdPartie = idPartie;
        mNom = nom;
        mNumMission = numMission;
    }

    public Jeu(int id, String nom) {
        mId = id;
        mNom = nom;
    }

    public int getIdPartie() {
        return mIdPartie;
    }

    public int getNumMission() {
        return mNumMission;
    }

    public String getNom() {
        return mNom;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }
}
