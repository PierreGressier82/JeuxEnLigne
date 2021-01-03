package com.pigredorou.jeuxenvisio.objets;

public class Personnage {
    private String mNom;
    private String mContexte;
    private int mNiveau;

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

    public int getNiveau() {
        return mNiveau;
    }

    public void setNiveau(int niveau) {
        mNiveau = niveau;
    }
}
