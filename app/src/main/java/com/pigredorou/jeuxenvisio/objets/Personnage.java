package com.pigredorou.jeuxenvisio.objets;

public class Personnage {
    private int id;
    private String mNom;
    private String mContexte;
    private int mNiveau;

    public Personnage(int id, String nom, String contexte, int niveau) {
        this.id = id;
        mNom = nom;
        mContexte = contexte;
        mNiveau = niveau;
    }

    public String getNom() {
        return mNom;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
