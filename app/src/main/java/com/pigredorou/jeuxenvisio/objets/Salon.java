package com.pigredorou.jeuxenvisio.objets;

public class Salon {
    private int id;
    private String nom;

    public Salon(int id, String nom) {
        this.id = id;
        this.nom = nom;
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
}
