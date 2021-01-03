package com.pigredorou.jeuxenvisio.objets;

public class TourDeJeuCrane {
    private int mTourDeJeu;
    private String mMot;
    private Crane mCrane;
    private String mPseudo;

    public TourDeJeuCrane(int tourDeJeu, String mot, Crane crane, String pseudo) {
        mTourDeJeu = tourDeJeu;
        mMot = mot;
        mCrane = crane;
        mPseudo = pseudo;
    }

    public Crane getCrane() {
        return mCrane;
    }

    public void setCrane(Crane crane) {
        mCrane = crane;
    }

    public String getPseudo() {
        return mPseudo;
    }

    public void setPseudo(String pseudo) {
        mPseudo = pseudo;
    }
}
