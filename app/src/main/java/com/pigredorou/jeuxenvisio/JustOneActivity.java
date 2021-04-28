package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.pigredorou.jeuxenvisio.objets.Mot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class JustOneActivity extends JeuEnVisioActivity {

    // URLs des actions en base
    public static final String urlJeu = MainActivity.url + "justOne.php?partie=";
    public static final String urlMancheSuivante = MainActivity.url + "justOneMancheSuivante.php?partie=";
    public static final String urlVote = MainActivity.url + "vote.php?partie=";
    public static final String urlReponse = MainActivity.url + "enregistreReponse.php?partie=";
    public static final String urlDevoile = MainActivity.url + "justeOneDevoile.php?partie=";
    // Tableaux des resssources
    static final int[] tableIdRessourcesMots = {R.id.mot_1, R.id.mot_2, R.id.mot_3, R.id.mot_4, R.id.mot_5};
    static final int[] tableIdRessourcesChoixNumero = {R.id.choix_1, R.id.choix_2, R.id.choix_3, R.id.choix_4, R.id.choix_5};
    static final int[] tableIdRessourcesReponses = {R.id.reponse_1, R.id.reponse_2, R.id.reponse_3, R.id.reponse_4, R.id.reponse_5, R.id.reponse_6, R.id.reponse_7, R.id.reponse_8, R.id.reponse_9, R.id.reponse_10, R.id.reponse_11, R.id.reponse_12};
    // Eléments graphiques
    private Button mBoutonMancheSuivante;
    private TextView mNbVoteJoueurs;
    private TextView mTitreChoixNumero;
    private TextView mTexteScore;
    private TextView mTexteTour;
    private LinearLayout mChoixNumero;
    private LinearLayout mblocValidationReponse;
    private ScrollView mBlocReponses;
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
                if (mBoutonValider.getText().toString().equals(getResources().getString(R.string.devoiler_les_mots))) {
                    new MainActivity.TacheURLSansRetour().execute(urlDevoile + mIdPartie);
                } else if (!mZoneSaisie.getText().toString().isEmpty()) {
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
                reponseJoueurActif("&action=reussite");
                break;
            case "bouton_echec":
                reponseJoueurActif("&action=echec");
                // TODO : gérer le cas de la dernière manche => score -1 !
                break;
            case "bouton_passer":
                reponseJoueurActif("&action=passer");
                break;
            default:
                if (v.getTag().toString().startsWith("reponse_")) {
                    clicReponse(v.getId());
                }
        }

    }

    private void clicReponse(int id) {
        TextView tv = findViewById(id);
        tv.setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));
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
        // Réponses
        mBlocReponses = findViewById(R.id.bloc_reponses);
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

        // TODO : lire le score et le numéro de la manche
        afficheScoreEtManche(doc);

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

    private void afficheScoreEtManche(Document doc) {
        Node noeudJustOne = getNoeudUnique(doc, "JustOne");
        mMonScore = 0;

        int manche = 0;
        int maxManche = 0;

        for (int i = 0; i < noeudJustOne.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud JustOne
            if (noeudJustOne.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudJustOne.getAttributes().item(i).getNodeName()) {
                case "manche":
                    manche = Integer.parseInt(noeudJustOne.getAttributes().item(i).getNodeValue());
                    break;
                case "maxManche":
                    maxManche = Integer.parseInt(noeudJustOne.getAttributes().item(i).getNodeValue());
                    break;
                case "score":
                    mMonScore = Integer.parseInt(noeudJustOne.getAttributes().item(i).getNodeValue());
                    break;
            }
        }

        String texte = getResources().getString(R.string.score) + " : " + mMonScore;
        mTexteScore.setText(texte);
        texte = getResources().getString(R.string.tour) + " " + manche + "/" + maxManche;
        mTexteTour.setText(texte);
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
        if (!mJeSuisLeJoueurActif)
            mBlocReponses.setVisibility(View.VISIBLE);

        // Construction graphique dynamique
        //mBlocReponses.removeAllViewsInLayout();
        //TableLayout tl = new TableLayout(this);
        //TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 0);
        //tl.setLayoutParams(params);
        //mBlocReponses.addView(tl);

        boolean tousLesMotsSontValides = true;
        for (int i = 0; i < mListeMotsReponses.size(); i++) {
            if (mListeMotsReponses.get(i).getElimine() == -1) {
                tousLesMotsSontValides = false;
            }
            String reponse = (i + 1) + ".  " + mListeMotsReponses.get(i).getReponse();
            //TableRow tr = new TableRow(this);
            //if(i%2==0) {
            //    TableRow.LayoutParams paramsTR = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
            //    tr.setLayoutParams(params);
            //    tl.addView(tr);
            //}
            //TextView tv = new TextView(this);
            //TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
            //paramsTV.setMargins(20, 0, 20, 10);
            //tv.setLayoutParams(paramsTV);
            //tv.setText(reponse);
            //tv.setTextColor(getResources().getColor(R.color.noir));
            //tv.setTextSize(30, Dimension.SP);
            //tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //tv.setTag("reponse_"+mListeMotsReponses.get(i).getIdJoueur());
            //tr.addView(tv);

            TextView tv = findViewById(tableIdRessourcesReponses[i]);
            tv.setVisibility(View.VISIBLE);
            tv.setText(reponse);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setOnClickListener(this);
            tv.setTag("reponse_" + mListeMotsReponses.get(i).getIdJoueur());
        }

        for (int j = mListeMotsReponses.size(); j < tableIdRessourcesReponses.length; j++) {
            if (mListeMotsReponses.size() % 2 != 0 && j == mListeMotsReponses.size())
                findViewById(tableIdRessourcesReponses[j]).setVisibility(View.INVISIBLE);
            else
                findViewById(tableIdRessourcesReponses[j]).setVisibility(View.GONE);
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

        mBlocReponses.setVisibility(View.GONE);
        mBoutonMancheSuivante.setVisibility(View.GONE);
        mNbVoteJoueurs.setVisibility(View.VISIBLE);
        mBoutonValider.setText(getResources().getString(R.string.valider));
        mZoneSaisie.setEnabled(true);
    }

    private void afficheReponses() {
        String texteNbVotes = "Nombre de réponses : " + mListeMotsReponses.size() + "/" + (mListeJoueurs.size() - 1);
        mNbVoteJoueurs.setText(texteNbVotes);
        activeBouton(mBoutonValider);

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