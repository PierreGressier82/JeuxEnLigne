package com.pigredorou.jeuxenvisio.objets;

public class Crane {
    private int mId;
    private String mPersonnage;
    private String mContexte;
    private int mNiveau;
    private String mMot1;
    private String mMot2;
    private String mMot3;
    private String mMot4;

    public Crane(int id, String personnage, String contexte, int niveau) {
        mId = id;
        mPersonnage = personnage;
        mContexte = contexte;
        mNiveau = niveau;
    }

    public Crane(String personnage, String contexte) {
        mPersonnage = personnage;
        mContexte = contexte;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getPersonnage() {
        return mPersonnage;
    }

    public void setPersonnage(String personnage) {
        mPersonnage = personnage;
    }

    public String getContexte() {
        return mContexte;
    }

    public void setContexte(String contexte) {
        mContexte = contexte;
    }

    public int getNiveau() {
        return mNiveau;
    }

    public void setNiveau(int niveau) {
        mNiveau = niveau;
    }

    public String getMot1() {
        return mMot1;
    }

    public void setMot1(String mot1) {
        mMot1 = mot1;
    }

    public String getMot2() {
        return mMot2;
    }

    public void setMot2(String mot2) {
        mMot2 = mot2;
    }

    public String getMot3() {
        return mMot3;
    }

    public void setMot3(String mot3) {
        mMot3 = mot3;
    }

    public String getMot4() {
        return mMot4;
    }

    public void setMot4(String mot4) {
        mMot4 = mot4;
    }
}
