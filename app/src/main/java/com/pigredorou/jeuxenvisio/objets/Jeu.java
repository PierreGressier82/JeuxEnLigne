package com.pigredorou.jeuxenvisio.objets;

public class Jeu {
    int mId;
    String mNom;
    int mNumMission;

    public Jeu(int mId, String nom, int numMission) {
        this.mNom = nom;
        this.mId = mId;
        this.mNumMission = numMission;
    }

    public int getNumMission() {
        return mNumMission;
    }

    public void setNumMission(int numMission) {
        this.mNumMission = numMission;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        this.mNom = nom;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }
}
