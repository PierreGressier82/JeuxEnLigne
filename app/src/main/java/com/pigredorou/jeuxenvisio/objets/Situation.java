package com.pigredorou.jeuxenvisio.objets;

public class Situation {
    private int mIdMot;
    private String mSituation;
    private int mIdCouleur;
    private int mActif;

    public Situation(int idMot, String situation, int idCouleur, int actif) {
        mIdMot = idMot;
        mSituation = situation;
        mIdCouleur = idCouleur;
        mActif = actif;
    }

    public int getIdMot() {
        return mIdMot;
    }

    public String getSituation() {
        return mSituation;
    }

    public int getIdCouleur() {
        return mIdCouleur;
    }

    public int getActif() {
        return mActif;
    }
}
