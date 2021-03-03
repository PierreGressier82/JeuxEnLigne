package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JustOneActivity extends JeuEnVisioActivity {

    // URLs des actions en base
    public static final String urlJeu = MainActivity.url + "JustOne.php?partie=";
    // Tableaux des resssources
    static final int[] tableauIdRessourcesMots = {R.id.mot_1, R.id.mot_2, R.id.mot_3, R.id.mot_4, R.id.mot_5};
    // Eléments graphiques
    Button mBoutonMancheSuivante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_just_one);

        super.onCreate(savedInstanceState);

        // Chargement des élements de Just One
        ChargeJustOne();

        // TODO : retirer le gone du chargement au profit du retour de XML
        findViewById(R.id.chargement).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        //startRefreshAuto(urlJeu);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getTag().toString()) {
            case "bouton_manche_suivante":
                desactiveBouton(mBoutonMancheSuivante);
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitTopTen + mIdPartie + "&manche=suivante");
                break;
        }
    }

    void ChargeJustOne() {
        // Carte mots
        // Boutons
        mBoutonMancheSuivante = findViewById(R.id.bouton_manche_suivante);
        mBoutonMancheSuivante.setOnClickListener(this);
    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);
    }

}