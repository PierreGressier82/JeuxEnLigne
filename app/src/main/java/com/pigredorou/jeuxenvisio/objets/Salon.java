package com.pigredorou.jeuxenvisio.objets;

public class Salon {
    private int id;
    private String nom;
    private int numeroMission;

    public Salon(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Salon(int id, String nom, int numeroMission) {
        this.id = id;
        this.nom = nom;
        this.numeroMission = numeroMission;
    }

    public int getNumeroMission() {
        return numeroMission;
    }

    public void setNumeroMission(int numeroMission) {
        this.numeroMission = numeroMission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
