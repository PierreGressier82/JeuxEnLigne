package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.pigredorou.jeuxenvisio.objets.Mot;
import com.pigredorou.jeuxenvisio.objets.Vote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;

public class MajorityActivity extends JeuEnVisioActivity {

    // Variables globales
    private int mIdMot;
    private int mValeurVote;
    private int mNbMotsTrouves;
    private int mNbJoueursMajoritaires;
    private boolean mSuisElimine;
    private final boolean[] mListeVoteMajoritaire = {false, false, false, false};
    // Sablier
    private CountDownTimer mCompteurARebours;
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "majority.php?partie=";
    private final static String urlVote = MainActivity.url + "vote.php?partie=";
    private final static String urlSuivant = MainActivity.url + "initMajority.php?partie=";
    private final static String urlMAJScore = MainActivity.url + "majScore.php?partie=";
    private final static int VOTE_NON_REALISE = -1;
    private final static int VOTE_CARTE_MAJORITE = 0;
    private final static int VOTE_CARTE_A = 1;
    private final static int VOTE_CARTE_B = 2;
    private final static int VOTE_CARTE_C = 3;
    private final int TEMPS_SABLIER = 30;
    private final static int[] tableIdRessourcesMots = {R.id.mot_principal, R.id.mot_1, R.id.mot_2, R.id.mot_3};
    private final static int[] tableIdRessourcesLigneScore = {R.id.score_ligne_1, R.id.score_ligne_2, R.id.score_ligne_3, R.id.score_ligne_4, R.id.score_ligne_5, R.id.score_ligne_6, R.id.score_ligne_7, R.id.score_ligne_8, R.id.score_ligne_9, R.id.score_ligne_10, R.id.score_ligne_11};
    private final static int[] tableIdRessourcesPseudoScore = {R.id.score_pseudo_1, R.id.score_pseudo_2, R.id.score_pseudo_3, R.id.score_pseudo_4, R.id.score_pseudo_5, R.id.score_pseudo_6, R.id.score_pseudo_7, R.id.score_pseudo_8, R.id.score_pseudo_9, R.id.score_pseudo_10, R.id.score_pseudo_11};
    private final static int[] tableIdRessourcesScore = {R.id.score_1, R.id.score_2, R.id.score_3, R.id.score_4, R.id.score_5, R.id.score_6, R.id.score_7, R.id.score_8, R.id.score_9, R.id.score_10, R.id.score_11};
    private final static int[] tableIdRessourcesCarteVote = {R.id.carte_vote_majority, R.id.carte_vote_a, R.id.carte_vote_b, R.id.carte_vote_c};
    private final static int[] tableIdRessourcesTexteVote = {R.id.resultat_vote_majority, R.id.resultat_vote_a, R.id.resultat_vote_b, R.id.resultat_vote_c};
    // Elements graphique
    private Button mBoutonMancheTourSuivant;
    private ImageView mCarteVoteA;
    private ImageView mCarteVoteB;
    private ImageView mCarteVoteC;
    private ImageView mCarteVoteMajority;
    private TextView mResultatVoteA;
    private TextView mResultatVoteB;
    private TextView mResultatVoteC;
    private TextView mResultatVoteMajority;
    private TextView mTempsRestant;
    private ProgressBar mSablier;
    private LinearLayout mMajority;
    private LinearLayout mClassement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_majority);

        super.onCreate(savedInstanceState);

        // Cartes pour le vote
        chargeCartesVote();
        // Boutons
        chargeBoutons();
        desactiveBouton(mBoutonValider);
        desactiveBouton(mBoutonMancheTourSuivant);

        // Eléments du jeu
        mMajority = findViewById(R.id.majority);
        mClassement = findViewById(R.id.classement);
        afficheJeu();

        // Sablier
        mTempsRestant = findViewById(R.id.temps_restant);
        mSablier = findViewById(R.id.sablier);

        // Refresh info jeu
        startRefreshAuto(urlJeu);

        mSuisElimine = false;
    }

    private void startChrono() {
        // On lance le sablier s'il n'est pas déjà lancé
        if (mCompteurARebours == null) {
            //mCompteurARebours.cancel();
            mSablier.setVisibility(View.VISIBLE);
            mTempsRestant.setVisibility(View.VISIBLE);
            mSablier.setIndeterminate(false);
            mSablier.setMax(TEMPS_SABLIER);
            mSablier.setProgress(TEMPS_SABLIER);

            mCompteurARebours = new CountDownTimer(TEMPS_SABLIER * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    String texte = String.valueOf(millisUntilFinished / 1000);
                    mTempsRestant.setText(texte);
                    mSablier.setProgress((int) (millisUntilFinished / 1000));
                }

                public void onFinish() {
                    mTempsRestant.setText(getResources().getString(R.string.temps_ecoule));
                }
            }.start();
        }
    }

    private void chargeBoutons() {
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonMancheTourSuivant = findViewById(R.id.bouton_tour_manchant_suivante);
    }

    private void chargeCartesVote() {
        mCarteVoteA = findViewById(R.id.carte_vote_a);
        mCarteVoteB = findViewById(R.id.carte_vote_b);
        mCarteVoteC = findViewById(R.id.carte_vote_c);
        mCarteVoteMajority = findViewById(R.id.carte_vote_majority);
        mCarteVoteA.setOnClickListener(this);
        mCarteVoteB.setOnClickListener(this);
        mCarteVoteC.setOnClickListener(this);
        mCarteVoteMajority.setOnClickListener(this);
        mResultatVoteA = findViewById(R.id.resultat_vote_a);
        mResultatVoteB = findViewById(R.id.resultat_vote_b);
        mResultatVoteC = findViewById(R.id.resultat_vote_c);
        mResultatVoteMajority = findViewById(R.id.resultat_vote_majority);
    }

    private void desactiveCartesVote() {
        mCarteVoteA.setOnClickListener(null);
        mCarteVoteB.setOnClickListener(null);
        mCarteVoteC.setOnClickListener(null);
        mCarteVoteMajority.setOnClickListener(null);
    }

    @Override
    protected void onResume() {
        startRefreshAuto(urlJeu);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "bouton_retour":
                stopRefreshAuto();
                finish();
                break;
            case "carte_vote_a":
            case "carte_vote_b":
            case "carte_vote_c":
            case "carte_vote_majority":
                stopRefreshAuto();
                selectionneCarteVote(v.getTag().toString());
                activeBouton(mBoutonValider);
                break;

            case "bouton_valider":
                desactiveBouton(mBoutonValider);
                new MainActivity.TacheURLSansRetour().execute(urlVote + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + mIdMot);
                // Arrête le sablier
                mCompteurARebours.cancel();
                startRefreshAuto(urlJeu);
                break;

            case "bouton_suivant":
                if (mBoutonMancheTourSuivant.getText().toString().equals(getResources().getString(R.string.tour_suivant))) {
                    attribuePoints(false);
                    new MainActivity.TacheURLSansRetour().execute(urlSuivant + mIdPartie + "&tour=oui");
                } else if (mBoutonMancheTourSuivant.getText().toString().equals(getResources().getString(R.string.manche_suivante))) {
                    attribuePoints(true);
                    new MainActivity.TacheURLSansRetour().execute(urlSuivant + mIdPartie + "&manche=oui");
                }
                desactiveBouton(mBoutonMancheTourSuivant);
                break;
        }

    }

    private void attribuePoints(boolean finDeManche) {
        // Récupère les joueurs qui ont fait un vote majoritaire (carte étoiles)
        for (int i = 0; i < mListeVotes.size(); i++) {
            // Est-ce que le vote est majoritaire ?
            int lettre = mListeVotes.get(i).getLettre();
            int nbPoints = 0;
            // Attribue les points aux joueurs avec le vote sur une lettre majoritaire
            if (mListeVoteMajoritaire[lettre]) {
                if (lettre == VOTE_CARTE_MAJORITE) {
                    nbPoints = mNbMotsTrouves;
                } else if (finDeManche) {
                    nbPoints = mNbMotsTrouves + 1;
                }

                if (nbPoints > 0) {
                    int idJoueur = mListeVotes.get(i).getIdJoueur();
                    new MainActivity.TacheURLSansRetour().execute(urlMAJScore + mIdPartie + "&joueur=" + idJoueur + "&score=" + nbPoints);
                }
            }
        }
    }

    private void selectionneCarteVote(String tag) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        switch (tag) {
            case "carte_vote_majority":
                mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_MAJORITE;
                break;
            case "carte_vote_a":
                mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_A;
                break;
            case "carte_vote_b":
                mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_B;
                break;
            case "carte_vote_c":
                mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_C;
                break;
        }
        mIdMot = Integer.parseInt(findViewById(tableIdRessourcesMots[mValeurVote]).getTag().toString());
    }

    void afficheClassement() {
        mMajority.setVisibility(View.GONE);

        Collections.sort(mListeJoueurs);

        for (int i = 0; i < tableIdRessourcesLigneScore.length; i++) {
            if (i < mListeJoueurs.size()) {
                TableRow tr = findViewById(tableIdRessourcesLigneScore[i]);
                if (mListeJoueurs.get(i).getNomJoueur().equals(mPseudo))
                    tr.setBackgroundResource(R.drawable.tour_blanc_fond_vert_transparent);
                else
                    tr.setBackgroundResource(R.drawable.tour_blanc);
                tr.setVisibility(View.VISIBLE);
                TextView tv = findViewById(tableIdRessourcesPseudoScore[i]);
                tv.setText(mListeJoueurs.get(i).getNomJoueur());
                tv = findViewById(tableIdRessourcesScore[i]);
                tv.setText(String.valueOf(mListeJoueurs.get(i).getScore()));
            } else
                findViewById(tableIdRessourcesLigneScore[i]).setVisibility(View.GONE);
        }

        mClassement.setVisibility(View.VISIBLE);

    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);
        afficheMonScore(mListeJoueurs);
        // Bouton tour ou manche suivant que pour les joueurs admin
        if (mAdmin)
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.GONE);

        // Spécifique Majority
        parseNoeudMajority(doc);

        // Mots
        mListeMots = parseNoeudsMots(doc);
        afficheMots(mListeMots);

        // Liste des votes
        mListeVotes = parseNoeudsVotes(doc);
        afficheVotes(mListeVotes);

        verifieSiVictoire();
    }

    private void parseNoeudMajority(Document doc) {
        Node noeudMajority = getNoeudUnique(doc, "Majority");

        //int manche=0;

        for (int i = 0; i < noeudMajority.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud majority
            Log.d("PGR-XML-Majority", noeudMajority.getAttributes().item(i).getNodeName() + "_" + noeudMajority.getAttributes().item(i).getNodeValue());
            if (noeudMajority.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudMajority.getAttributes().item(i).getNodeName()) {
                case "manche":
                    //manche = Integer.parseInt(noeudMajority.getAttributes().item(i).getNodeValue());
                    break;
                case "nbMots":
                    mNbMotsTrouves = Integer.parseInt(noeudMajority.getAttributes().item(i).getNodeValue());
                    break;
            }
        }
    }

    private void verifieSiVictoire() {
        boolean unJoueurAGagne = false;
        for (int i = 0; i < mListeJoueurs.size(); i++) {
            int NB_POINTS_VICTOIRE = 20;
            if (mListeJoueurs.get(i).getScore() >= NB_POINTS_VICTOIRE) {
                unJoueurAGagne = true;
                break;
            }
        }

        if (unJoueurAGagne) {
            TextView tv = findViewById(R.id.nb_vote_joueurs);
            tv.setText(R.string.partie_termine);
            if (mCompteurARebours != null)
                mCompteurARebours.cancel();
            afficheClassement();
        } else
            afficheJeu();

    }

    private void afficheJeu() {
        mMajority.setVisibility(View.VISIBLE);
        mClassement.setVisibility(View.GONE);
    }

    private void afficheVotes(ArrayList<Vote> listeVotes) {
        int nbVoteAttendu = listeVotes.size();
        Integer[] nbVoteParCarte = {0, 0, 0, 0};
        mSuisElimine = true;
        mValeurVote = VOTE_NON_REALISE;
        int nbVote = 0;
        boolean startChrono = true;

        // Reset l'affichage des votes
        activeToutesLesCartesVotes();
        masqueResultats();

        for (int i = 0; i < listeVotes.size(); i++) {
            // Le vote a déjà était enregistré
            if (listeVotes.get(i).getLettre() != VOTE_NON_REALISE) {
                nbVoteParCarte[listeVotes.get(i).getLettre()]++;
                nbVote++;
                // Si j'ai déjà voté pour ce tour, désactive le bouton
                if (mIdJoueur == listeVotes.get(i).getIdJoueur()) {
                    desactiveBouton(mBoutonValider);
                    startChrono = false;
                    mCompteurARebours = null;
                    desactiveCartesVote();
                    mValeurVote = listeVotes.get(i).getLettre();
                    selectionneCarteVoteParLettre(mValeurVote);
                }
            }
            // Si je suis présent dans la liste des votes, je suis encore en jeu
            if (mIdJoueur == listeVotes.get(i).getIdJoueur())
                mSuisElimine = false;
        }

        // Mise à jour du nombre de vote
        TextView tv = findViewById(R.id.nb_vote_joueurs);
        String texteVote = nbVote + " vote(s) sur " + nbVoteAttendu;
        tv.setText(texteVote);

        // Affichage des résultats du vote
        if (nbVote == nbVoteAttendu) {
            afficheResultatsVote(nbVoteParCarte);

            if (mNbJoueursMajoritaires > 2) // Si au moins 3 joueurs sont encore en jeu -> Tour suivant
                mBoutonMancheTourSuivant.setText(getResources().getString(R.string.tour_suivant));
            else // Manche suivante
                mBoutonMancheTourSuivant.setText(getResources().getString(R.string.manche_suivante));
            activeBouton(mBoutonMancheTourSuivant);
        }

        // Si je suis éliminé, désactive le vote
        if (mSuisElimine) {
            desactiveBouton(mBoutonValider);
            desactiveCartesVote();
            mBoutonValider.setText(getResources().getString(R.string.elimine));
            mBoutonValider.setTextColor(getResources().getColor(R.color.rouge));
        } else {
            mBoutonValider.setText(getResources().getString(R.string.valider));
            if (startChrono)
                startChrono();
        }
    }

    private void activeToutesLesCartesVotes() {
        for (int value : tableIdRessourcesCarteVote) {
            findViewById(value).setAlpha(1);
            findViewById(value).setBackgroundResource(R.drawable.fond_carte_blanc);
            findViewById(value).setOnClickListener(this);
        }
    }

    private void masqueResultats() {
        mResultatVoteA.setVisibility(View.GONE);
        mResultatVoteB.setVisibility(View.GONE);
        mResultatVoteC.setVisibility(View.GONE);
        mResultatVoteMajority.setVisibility(View.GONE);
    }

    private void afficheResultatsVote(Integer[] nbVotesParCarte) {
        determineVoteMajoritaire(nbVotesParCarte);

        mResultatVoteMajority.setText(texteVote(nbVotesParCarte[VOTE_CARTE_MAJORITE]));
        mResultatVoteA.setText(texteVote(nbVotesParCarte[VOTE_CARTE_A]));
        mResultatVoteB.setText(texteVote(nbVotesParCarte[VOTE_CARTE_B]));
        mResultatVoteC.setText(texteVote(nbVotesParCarte[VOTE_CARTE_C]));

        mResultatVoteMajority.setVisibility(View.VISIBLE);
        mResultatVoteA.setVisibility(View.VISIBLE);
        mResultatVoteB.setVisibility(View.VISIBLE);
        mResultatVoteC.setVisibility(View.VISIBLE);
    }

    private String texteVote(Integer listeVote) {
        String texte;
        if (listeVote < 2)
            texte = listeVote + " vote";
        else
            texte = listeVote + " votes";

        return texte;
    }

    private void determineVoteMajoritaire(Integer[] nbVotesParCarte) {
        int valeurVoteMax = 0;
        mNbJoueursMajoritaires = 0;
        // Détermine le vote max
        for (Integer listeVote : nbVotesParCarte) {
            if (listeVote > valeurVoteMax)
                valeurVoteMax = listeVote;
        }

        // Détermine les cartes ayant le vote max
        for (int i = 0; i < nbVotesParCarte.length; i++) {
            if (nbVotesParCarte[i] == valeurVoteMax) {
                mListeVoteMajoritaire[i] = true;
                if (i > VOTE_CARTE_MAJORITE) // On ne prend pas le résultat du vote "carte étoiles"
                    mNbJoueursMajoritaires += valeurVoteMax; // Si 2 votes sont majoritaires, il faut les cumuler.
                findViewById(tableIdRessourcesCarteVote[i]).setAlpha(1);
                findViewById(tableIdRessourcesTexteVote[i]).setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));
            } else {
                mListeVoteMajoritaire[i] = false;
                // Si j'ai fait un choix non majoritaire -> Je suis éliminé
                if (mValeurVote == i)
                    mSuisElimine = true;
                findViewById(tableIdRessourcesCarteVote[i]).setAlpha((float) 0.5);
                findViewById(tableIdRessourcesTexteVote[i]).setBackgroundColor(getResources().getColor(R.color.noir_transparent));
            }
        }
    }

    private void afficheMots(ArrayList<Mot> listeMots) {
        TextView tv = findViewById(R.id.nb_mot_trouves);
        tv.setText(String.valueOf(mNbMotsTrouves));
        for (int i = 0; i < listeMots.size(); i++) {
            tv = findViewById(tableIdRessourcesMots[i]);
            tv.setText(listeMots.get(i).getMot());
            tv.setTag(String.valueOf(listeMots.get(i).getIdMot()));
        }
    }

    private void selectionneCarteVoteParLettre(int lettre) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        findViewById(tableIdRessourcesCarteVote[lettre]).setBackgroundResource(R.drawable.fond_carte_vert);
    }
}