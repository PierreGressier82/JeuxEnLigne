package com.pigredorou.jeuxenvisio.objets;

public class Salon {
    private int mId;
    private final String mNom;
    private final int mIdPartie;
    private final int mNumMission;

    public Salon(int id, String nom, int idPartie, int numMission) {
        mId = id;
        mNom = nom;
        mIdPartie = idPartie;
        mNumMission = numMission;
    }

    public int getNumMission() {
        return mNumMission;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getNom() {
        return mNom;
    }

    public int getIdPartie() {
        return mIdPartie;
    }
}
