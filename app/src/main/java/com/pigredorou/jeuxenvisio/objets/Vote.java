package com.pigredorou.jeuxenvisio.objets;

public class Vote {
    private int mIdJoueur;
    private int mIdMot;
    private int mLettre;

    public Vote(int idJoueur, int idMot, int lettre) {
        mIdJoueur = idJoueur;
        mIdMot = idMot;
        mLettre = lettre;
    }

    public int getIdJoueur() {
        return mIdJoueur;
    }

    public int getIdMot() {
        return mIdMot;
    }

    public int getLettre() {
        return mLettre;
    }
}
