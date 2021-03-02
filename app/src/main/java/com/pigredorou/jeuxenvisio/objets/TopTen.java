package com.pigredorou.jeuxenvisio.objets;

public class TopTen {
    private final int nbLicorne;
    private final int nbCaca;
    private final int manche;
    private final int numeroCarte;
    private final int nbCartesSurTapis;

    public TopTen(int nbLicorne, int nbCaca, int manche, int numeroCarte, int nbCartesSurTapis) {
        this.nbLicorne = nbLicorne;
        this.nbCaca = nbCaca;
        this.manche = manche;
        this.numeroCarte = numeroCarte;
        this.nbCartesSurTapis = nbCartesSurTapis;
    }

    public int getNbLicorne() {
        return nbLicorne;
    }

    public int getNbCaca() {
        return nbCaca;
    }

    public int getManche() {
        return manche;
    }

    public int getNumeroCarte() {
        return numeroCarte;
    }

    public int getNbCartesSurTapis() {
        return nbCartesSurTapis;
    }
}
