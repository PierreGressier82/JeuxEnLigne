package com.pigredorou.jeuxenvisio.objets;

public class Tour {
    String mMot;
    Crane mCrane;
    String mPseudo;

    public Tour(String mot, Crane crane, String pseudo) {
        mMot = mot;
        mCrane = crane;
        mPseudo = pseudo;
    }

    public String getMot() {
        return mMot;
    }

    public void setMot(String mot) {
        mMot = mot;
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
