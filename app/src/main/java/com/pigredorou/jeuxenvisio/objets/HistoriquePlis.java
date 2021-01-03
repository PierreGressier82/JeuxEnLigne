package com.pigredorou.jeuxenvisio.objets;

import java.util.ArrayList;

public class HistoriquePlis {
    private ArrayList<Pli> mPlis;
    private int mScore;
    private String mJoueurQuiRemportePli;

    public HistoriquePlis(ArrayList<Pli> plis, int score, String joueurQuiRemportePli) {
        this.mPlis = plis;
        this.mScore = score;
        this.mJoueurQuiRemportePli = joueurQuiRemportePli;
    }

    public HistoriquePlis(ArrayList<Pli> plis) {
        this.mPlis = plis;
    }

    public ArrayList<Pli> getPlis() {
        return mPlis;
    }

    public void setPlis(ArrayList<Pli> plis) {
        this.mPlis = plis;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public String getJoueurQuiRemportePli() {
        return mJoueurQuiRemportePli;
    }

    public void setJoueurQuiRemportePli(String joueurQuiRemportePli) {
        this.mJoueurQuiRemportePli = joueurQuiRemportePli;
    }
}
