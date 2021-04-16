package com.pigredorou.jeuxenvisio.objets;

public class Reponse {
    private final int mIdJoueur;
    private final String mReponse;
    private int mElimine;

    public Reponse(int idJoueur, String reponse) {
        mIdJoueur = idJoueur;
        mReponse = reponse;
        mElimine = -1;
    }

    public Reponse(int idJoueur, String reponse, int elimine) {
        mIdJoueur = idJoueur;
        mReponse = reponse;
        mElimine = elimine;
    }

    public int getElimine() {
        return mElimine;
    }

    public void setElimine(int elimine) {
        mElimine = elimine;
    }

    public int getIdJoueur() {
        return mIdJoueur;
    }

    public String getReponse() {
        return mReponse;
    }
}
