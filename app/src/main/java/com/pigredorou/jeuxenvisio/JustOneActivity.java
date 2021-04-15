package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.pigredorou.jeuxenvisio.objets.Mot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class JustOneActivity extends JeuEnVisioActivity {

    // URLs des actions en base
    public static final String urlJeu = MainActivity.url + "justOne.php?partie=";
    public static final String urlMancheSuivante = MainActivity.url + "justOneMancheSuivante.php?partie=";
    // Tableaux des resssources
    static final int[] tableIdRessourcesMots = {R.id.mot_1, R.id.mot_2, R.id.mot_3, R.id.mot_4, R.id.mot_5};
    static final int[] tableIdRessourcesChoixNumero = {R.id.choix_1, R.id.choix_2, R.id.choix_3, R.id.choix_4, R.id.choix_5};
    // Eléments graphiques
    private Button mBoutonMancheSuivante;
    private TextView mNbVoteJoueurs;
    private TextView mTitreChoixNumero;
    private LinearLayout mChoixNumero;
    private ConstraintLayout mBlocCarteListeMots;
    private EditText mZoneSaisie;
    // Variables globales
    private boolean mSuisActif = false;
    private ArrayList<Mot> mListeMotsReponses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_just_one);

        super.onCreate(savedInstanceState);

        // Chargement des élements de Just One
        ChargeJustOne();

        // Refresh info jeu
        startRefreshAuto(urlJeu);
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
            case "bouton_valider":
                // TODO : enregistrer le mot
                break;
            case "bouton_manche_suivante":
                desactiveBouton(mBoutonMancheSuivante);
                new MainActivity.TacheURLSansRetour().execute(urlMancheSuivante + mIdPartie);
                break;
        }
    }

    void ChargeJustOne() {
        // Carte mots
        mBlocCarteListeMots = findViewById(R.id.bloc_carte_liste_mots);
        // Choix numéro
        mTitreChoixNumero = findViewById(R.id.choix_numero_texte);
        mChoixNumero = findViewById(R.id.choix_numero_chiffres);
        for (int value : tableIdRessourcesChoixNumero) {
            findViewById(value).setOnClickListener(this);
        }
        // Zone de saisie
        mZoneSaisie = findViewById(R.id.zone_saisie);
        // Votes
        mNbVoteJoueurs = findViewById(R.id.nb_vote_joueurs);
        // Boutons
        mBoutonMancheSuivante = findViewById(R.id.bouton_manche_suivante);
        mBoutonMancheSuivante.setOnClickListener(this);
    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);

        // Suis-je le joueur actif ?
        mSuisActif = suisJeActif(mPseudo, mListeJoueurs);

        // Mots
        mListeMots = parseNoeudsMots(doc, "Carte");
        afficheElements();

        if (mNumeroMotChoisi != 0) {
            activeBouton(mBoutonValider);
            masqueMotNonChoisi();
        } else
            desactiveBouton(mBoutonValider);

        // Reponses
        mListeMotsReponses = parseNoeudsMots(doc, "Reponses");
        afficheNbReponses();
    }

    private void afficheElements() {
        if (mSuisActif) {
            mBlocCarteListeMots.setVisibility(View.INVISIBLE);
            mZoneSaisie.setVisibility(View.GONE);
            mChoixNumero.setVisibility(View.VISIBLE);
            mTitreChoixNumero.setVisibility(View.VISIBLE);
        } else {
            mBlocCarteListeMots.setVisibility(View.VISIBLE);
            mZoneSaisie.setVisibility(View.VISIBLE);
            mChoixNumero.setVisibility(View.GONE);
            mTitreChoixNumero.setVisibility(View.GONE);
            afficheMots(mListeMots);
        }

        if (mAdmin)
            mBoutonMancheSuivante.setVisibility(View.VISIBLE);
        else
            mBoutonMancheSuivante.setVisibility(View.GONE);
    }

    private void afficheNbReponses() {
        String texteNbVotes = "Nombre de réponses : " + mListeMotsReponses.size() + "/" + String.valueOf(mListeJoueurs.size() - 1);
        mNbVoteJoueurs.setText(texteNbVotes);

        if ((mListeJoueurs.size() - 1) == mListeMotsReponses.size())
            activeBouton(mBoutonMancheSuivante);
        else
            desactiveBouton(mBoutonMancheSuivante);
    }

    private void masqueMotNonChoisi() {
        // TODO
    }

    private void afficheMots(ArrayList<Mot> listeMots) {
        for (int i = 0; i < listeMots.size(); i++) {
            TextView tv = findViewById(tableIdRessourcesMots[i]);
            tv.setText(listeMots.get(i).getMot());
            tv.setTag(String.valueOf(listeMots.get(i).getIdMot()));
        }
    }

}