package com.pigredorou.jeuxenvisio.objets;

public class Salon {
    private final String mNom;
    private final int mIdPartie;
    private int mId;

    public Salon(int id, String nom, int idPartie) {
        mId = id;
        mNom = nom;
        mIdPartie = idPartie;
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
