package com.pigredorou.jeuxenvisio.objets;

public class Personnage {
    String mNom;
    String mContexte;
    int mNiveau;

    public Personnage(String nom, String contexte) {
        mNom = nom;
        mContexte = contexte;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        mNom = nom;
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
}
