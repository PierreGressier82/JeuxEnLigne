package com.pigredorou.jeuxenvisio.objets;

import java.util.ArrayList;

public class Salon {
    private int mId;
    private final String mNom;
    private int mIdPartie;
    private int mNumMission;
    private ArrayList<Joueur> mListeJoueurs;
    private ArrayList<Jeu> mListeJeux;

    public Salon(int id, String nom, int idPartie, int numMission) {
        mId = id;
        mNom = nom;
        mIdPartie = idPartie;
        mNumMission = numMission;
    }

    public Salon(int id, String nom, ArrayList<Joueur> listeJoueurs, ArrayList<Jeu> listeJeux) {
        mId = id;
        mNom = nom;
        mListeJoueurs = listeJoueurs;
        mListeJeux = listeJeux;
    }

    public int getNumMission() {
        return mNumMission;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getNom() {
        return mNom;
    }

    public int getIdPartie() {
        return mIdPartie;
    }

    public ArrayList<Joueur> getListeJoueurs() {
        return mListeJoueurs;
    }

    public ArrayList<Jeu> getListeJeux() {
        return mListeJeux;
    }
}
