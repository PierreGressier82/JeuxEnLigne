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
    public static final String urlVote = MainActivity.url + "vote.php?partie=";
    public static final String urlReponse = MainActivity.url + "enregistreReponse.php?partie=";
    // Tableaux des resssources
    static final int[] tableIdRessourcesMots = {R.id.mot_1, R.id.mot_2, R.id.mot_3, R.id.mot_4, R.id.mot_5};
    static final int[] tableIdRessourcesChoixNumero = {R.id.choix_1, R.id.choix_2, R.id.choix_3, R.id.choix_4, R.id.choix_5};
    // Eléments graphiques
    private Button mBoutonMancheSuivante;
    private TextView mNbVoteJoueurs;
    private TextView mTitreChoixNumero;
    private TextView mTexteScore;
    private TextView mTexteTour;
    private LinearLayout mChoixNumero;
    private LinearLayout mblocValidationReponse;
    private ConstraintLayout mBlocCarteListeMots;
    private EditText mZoneSaisie;
    // Variables globales

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
        startRefreshAuto(urlJeu);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getTag().toString()) {
            case "bouton_valider":
                if (!mZoneSaisie.getText().toString().isEmpty()) {
                    desactiveBouton(mBoutonValider);
                    new MainActivity.TacheURLSansRetour().execute(urlReponse + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + mZoneSaisie.getText().toString());
                }
                break;
            case "choix_1":
            case "choix_2":
            case "choix_3":
            case "choix_4":
            case "choix_5":
                clickNumero(v.getId(), v.getTag().toString());
                break;
            case "bouton_reussite":
                reponseJoueurActif("action=reussite");
                // TODO : utilise table score +1 (id_joueur à 0), tour +1
                break;
            case "bouton_echec":
                reponseJoueurActif("action=echec");
                // TODO : ne change pas le score, mais tour +2
                break;
            case "bouton_passer":
                reponseJoueurActif("action=passer");
                // TODO : ne change pas le score, tour +1
                break;
        }
    }

    private void reponseJoueurActif(String action) {
        mblocValidationReponse.setVisibility(View.GONE);
        new MainActivity.TacheURLSansRetour().execute(urlMancheSuivante + mIdPartie + action);
    }

    private void clickNumero(int id, String tag) {
        resetFondNumero();

        TextView tv2 = findViewById(id);
        tv2.setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));

        String[] numero = tag.split("_");
        new MainActivity.TacheURLSansRetour().execute(urlVote + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + numero[1]);
    }

    private void resetFondNumero() {
        for (int value : tableIdRessourcesChoixNumero) {
            TextView tv = findViewById(value);
            // Enlève la couleur du fond
            tv.setBackgroundColor(0);
            // Désactive le bouton
            tv.setOnClickListener(null);
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
        // Libellés valeur jeu
        mTexteScore = findViewById(R.id.score);
        mTexteTour = findViewById(R.id.tour);
        // Zone de saisie
        mZoneSaisie = findViewById(R.id.zone_saisie);
        // Votes
        mNbVoteJoueurs = findViewById(R.id.nb_vote_joueurs);
        // Boutons
        mblocValidationReponse = findViewById(R.id.bloc_validation_reponse);
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonMancheSuivante = findViewById(R.id.bouton_manche_suivante);
        Button boutonEchec = findViewById(R.id.bouton_echec);
        Button boutonReussite = findViewById(R.id.bouton_reussite);
        Button boutonPasser = findViewById(R.id.bouton_passer);
        mBoutonValider.setOnClickListener(this);
        mBoutonMancheSuivante.setOnClickListener(this);
        boutonEchec.setOnClickListener(this);
        boutonReussite.setOnClickListener(this);
        boutonPasser.setOnClickListener(this);
    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);

        // Suis-je le joueur actif ?
        mJeSuisLeJoueurActif = suisJeActif(mPseudo, mListeJoueurs);

        // Mots
        mListeMots = parseNoeudsMots(doc, "Carte");
        afficheElements();

        if (mJeSuisLeJoueurActif && mNumeroMotChoisi != 0)
            afficheReponseChoisi();

        // Reponses
        mListeMotsReponses = parseNoeudsReponses(doc);
        afficheReponses();
        // Tous les joueurs ont données une réponse
        if (mListeMotsReponses.size() == mListeJoueurs.size() - 1) {
            afficheToutesLesReponses();

        }
    }

    private void afficheReponseChoisi() {
        resetFondNumero();
        TextView tv = findViewById(tableIdRessourcesChoixNumero[mNumeroMotChoisi - 1]);
        tv.setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));
    }

    private void afficheToutesLesReponses() {
        // Masque la zone de saisie
        mZoneSaisie.setVisibility(View.GONE);
        mZoneSaisie.setText("");
        mNbVoteJoueurs.setVisibility(View.GONE);
        mBoutonValider.setText(getResources().getString(R.string.devoiler_les_mots));

        boolean tousLesMotsSontValides = true;
        for (int i = 0; i < mListeMotsReponses.size(); i++) {
            if (mListeMotsReponses.get(i).getElimine() == -1) {
                tousLesMotsSontValides = false;
            }
            // TODO : gérer l'affichage des réponses en dynamique
        }

        if (tousLesMotsSontValides)
            desactiveBouton(mBoutonValider);
        else
            activeBouton(mBoutonValider);

        // Pour le joueur actif, affiche les mots restants et active l'enregistrement de sa réponse
        if (mJeSuisLeJoueurActif && tousLesMotsSontValides)
            mblocValidationReponse.setVisibility(View.VISIBLE);
    }

    private void afficheElements() {
        if (mJeSuisLeJoueurActif) {
            mBlocCarteListeMots.setVisibility(View.INVISIBLE);
            mZoneSaisie.setVisibility(View.GONE);
            mChoixNumero.setVisibility(View.VISIBLE);
            mTitreChoixNumero.setVisibility(View.VISIBLE);
            mblocValidationReponse.setVisibility(View.GONE);
            mBoutonValider.setVisibility(View.GONE);
        } else {
            mBlocCarteListeMots.setVisibility(View.VISIBLE);
            mZoneSaisie.setVisibility(View.VISIBLE);
            mChoixNumero.setVisibility(View.GONE);
            mTitreChoixNumero.setVisibility(View.GONE);
            mblocValidationReponse.setVisibility(View.GONE);
            mBoutonValider.setVisibility(View.VISIBLE);
            afficheMots(mListeMots);
        }

        mBoutonMancheSuivante.setVisibility(View.GONE);
        mNbVoteJoueurs.setVisibility(View.VISIBLE);
        mBoutonValider.setText(getResources().getString(R.string.valider));
        mZoneSaisie.setEnabled(true);
    }

    private void afficheReponses() {
        String texteNbVotes = "Nombre de réponses : " + mListeMotsReponses.size() + "/" + (mListeJoueurs.size() - 1);
        mNbVoteJoueurs.setText(texteNbVotes);

        for (int i = 0; i < mListeMotsReponses.size(); i++) {
            if (mListeMotsReponses.get(i).getIdJoueur() == mIdJoueur) {
                mZoneSaisie.setEnabled(false);
                mZoneSaisie.setText(mListeMotsReponses.get(i).getReponse());
                desactiveBouton(mBoutonValider);
                break;
            }
        }
    }

    private void afficheMots(ArrayList<Mot> listeMots) {
        for (int i = 0; i < listeMots.size(); i++) {
            TextView tv = findViewById(tableIdRessourcesMots[i]);
            tv.setText(listeMots.get(i).getMot());
            tv.setTag(String.valueOf(listeMots.get(i).getIdMot()));
            if (mNumeroMotChoisi == 0 || mNumeroMotChoisi - 1 == i)
                tv.setTextColor(getResources().getColor(R.color.noir));
            else
                tv.setTextColor(getResources().getColor(R.color.grisTransparent));
        }
    }
}