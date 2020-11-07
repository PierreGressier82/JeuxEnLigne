package com.pigredorou.jeuxenvisio.objets;

public class Joueur {

    private String mNomJoueur;
    private String mNomSalon;
    private int mAdmin;

    public Joueur(String nomJoueur, String nomSalon, int admin) {
        this.mNomJoueur=nomJoueur;
        this.mNomSalon=nomSalon;
        this.mAdmin=admin;
    }

    public Joueur(String nomJoueur, int admin) {
        mNomJoueur = nomJoueur;
        mAdmin = admin;
    }

    public Joueur() {
    }

    public String getNomJoueur() {
        return mNomJoueur;
    }

    public String getNomSalon() {
        return mNomSalon;
    }

    public int getAdmin() {
        return mAdmin;
    }

    public void setNomJoueur(String nomJoueur) {
        mNomJoueur = nomJoueur;
    }

    public void setNomSalon(String nomSalon) {
        mNomSalon = nomSalon;
    }

    public void setAdmin(int admin) {
        mAdmin = admin;
    }
}
