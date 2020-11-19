package com.pigredorou.jeuxenvisio.objets;

public class Jeu {
    String nom;
    int idRessource;
    int numMission;

    public Jeu(String nom, int idRessource) {
        this.nom = nom;
        this.idRessource = idRessource;
    }

    public Jeu(String nom, int idRessource, int numMission) {
        this.nom = nom;
        this.idRessource = idRessource;
        this.numMission = numMission;
    }

    public int getNumMission() {
        return numMission;
    }

    public void setNumMission(int numMission) {
        this.numMission = numMission;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdRessource() {
        return idRessource;
    }

    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
    }
}
