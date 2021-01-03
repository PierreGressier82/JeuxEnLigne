package com.pigredorou.jeuxenvisio.objets;

public class Tache {
    private String mPseudoJoueur;
    private Carte mCarte;
    private String mOption;
    private boolean mRealise;

    public Tache(String joueur, Carte carte, String option, boolean realise) {
        mPseudoJoueur = joueur;
        mCarte = carte;
        mOption = option;
        mRealise = realise;
    }

    public String getJoueur() {
        return mPseudoJoueur;
    }

    public Carte getCarte() {
        return mCarte;
    }

    public void setCarte(Carte carte) {
        mCarte = carte;
    }

    public String getOption() {
        return mOption;
    }

    public boolean isRealise() {
        return mRealise;
    }
}
