package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pigredorou.jeuxenvisio.objets.Joueur;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static com.pigredorou.jeuxenvisio.outils.outilsXML.parseNoeudsJoueur;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.suisJeAdmin;

public class TimelineActivity extends JeuEnVisionActivity implements View.OnClickListener {

    // Variables globales
    private String mPseudo;
    private int mIdJoueur;
    private int mIdPartie;
    private int mMonScore;
    private boolean mAdmin;
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "timeline.php?partie=";
    private final static String urlMAJ = MainActivity.url + "majority.php?partie=";
    // Elements graphique
    private Button mBoutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Affiche la vue
        setContentView(R.layout.activity_timeline);

        // TODO : ajouter les éléments de timeline
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "bouton_retour":
                stopRefreshAuto();
                finish();
                break;

            case "bouton_valider":
                startRefreshAuto(urlJeu);
                desactiveBouton(mBoutonValider);
                new MainActivity.TacheURLSansRetour().execute(urlMAJ + mIdPartie + "&joueur=" + mIdJoueur);
                break;
        }

    }

    public void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        // Joueurs
        ArrayList<Joueur> listeJoueurs = parseNoeudsJoueur(doc);
        mAdmin = suisJeAdmin(mPseudo, listeJoueurs);
        mIdJoueur = getIdJoueurFromPseudo(listeJoueurs);
        afficheScore(listeJoueurs);

    }
}