package com.pigredorou.jeuxenvisio.objets;

public class Jeu {
    private int mId; // Id du jeu en BDD
    private final String mNom;

    public Jeu(int id, String nom) {
        mId = id;
        mNom = nom;
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
