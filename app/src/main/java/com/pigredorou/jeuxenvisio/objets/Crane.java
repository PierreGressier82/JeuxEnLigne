package com.pigredorou.jeuxenvisio.objets;

public class Crane {
    private int mId;
    private Personnage mPersonnage;

    public Crane(int id, Personnage personnage) {
        mId = id;
        mPersonnage = personnage;
    }

    public Crane(Personnage personnage) {
        mPersonnage = personnage;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Personnage getPersonnage() {
        return mPersonnage;
    }

    public void setPersonnage(Personnage personnage) {
        mPersonnage = personnage;
    }
}
