package com.pigredorou.jeuxenvisio.objets;

public class Crane {
    private int mId;
    private int mTourDeJeu;
    private String mPersonnage;
    private String mContexte;
    private int mNiveau;
    private String mMot;

    public Crane(int tourDeJeu, String personnage, String contexte, String mot) {
        mTourDeJeu = tourDeJeu;
        mPersonnage = personnage;
        mContexte = contexte;
        mMot = mot;
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

    public String getMot() {
        return mMot;
    }

    public void setMot(String mot) {
        mMot = mot;
    }

    public int getTourDeJeu() {
        return mTourDeJeu;
    }

    public void setTourDeJeu(int tourDeJeu) {
        mTourDeJeu = tourDeJeu;
    }
}
