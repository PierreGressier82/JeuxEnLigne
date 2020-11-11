package com.pigredorou.jeuxenvisio.objets;

public class Tache {
    private String mPseudoJoueur;
    private Carte mCarte;
    private String mOption="";
    private boolean mRealise=false;

    public Tache() {
        super();
    }

    public Tache(String pseudoJoueur, Carte carte) {
        mPseudoJoueur = pseudoJoueur;
        mCarte = carte;
    }

    public Tache(String pseudoJoueur, Carte carte, String option) {
        mPseudoJoueur = pseudoJoueur;
        mCarte = carte;
        mOption = option;
    }

    public Tache(String joueur, Carte carte, String option, boolean realise) {
        mPseudoJoueur = joueur;
        mCarte = carte;
        mOption = option;
        mRealise = realise;
    }

    public String getJoueur() {
        return mPseudoJoueur;
    }

    public void setJoueur(String joueur) {
        mPseudoJoueur = joueur;
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

    public void setOption(String option) {
        mOption = option;
    }

    public boolean isRealise() {
        return mRealise;
    }

    public void setRealise(boolean realise) {
        mRealise = realise;
    }
}
