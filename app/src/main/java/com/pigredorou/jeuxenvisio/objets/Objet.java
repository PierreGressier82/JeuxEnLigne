package com.pigredorou.jeuxenvisio.objets;

public class Objet {
    private int mId;
    private int mActif;

    public Objet(int id, int actif) {
        mId = id;
        mActif = actif;
    }

    public int getId() {
        return mId;
    }

    public int getActif() {
        return mActif;
    }
}
