package com.pigredorou.jeuxenvisio.objets;

public class Jeu {
    private int mId; // Id du jeu en BDD
    private final String mNom;
    private final int actif;

    public Jeu(int id, String nom, int actif) {
        mId = id;
        mNom = nom;
        this.actif = actif;
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

    public int getActif() {
        return actif;
    }
}
