package com.pigredorou.jeuxenvisio.objets;

public class Joueur {

    private String mNomJoueur;
    private String mNomSalon;
    private String mNomEquipe;
    private int mAdmin;

    public Joueur(String nomJoueur) {
        mNomJoueur = nomJoueur;
    }

    public Joueur(String nomJoueur, String nomEquipe, int admin) {
        this.mNomJoueur=nomJoueur;
        this.mNomEquipe=nomEquipe;
        this.mAdmin=admin;
    }

    public Joueur(String nomJoueur, int admin) {
        mNomJoueur = nomJoueur;
        mAdmin = admin;
    }

    public String getNomEquipe() {
        return mNomEquipe;
    }

    public void setNomEquipe(String nomEquipe) {
        mNomEquipe = nomEquipe;
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
