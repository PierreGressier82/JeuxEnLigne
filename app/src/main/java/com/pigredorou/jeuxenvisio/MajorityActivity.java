package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pigredorou.jeuxenvisio.objets.Mot;
import com.pigredorou.jeuxenvisio.objets.Vote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class MajorityActivity extends JeuEnVisioActivity {

    // Variables globales
    private int mIdMot;
    private int mValeurVote;
    private int mNbMotsTrouves;
    private int mNbJoueursMajoritaires;
    private boolean mSuisElimine;
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "majority.php?partie=";
    private final static String urlVote = MainActivity.url + "vote.php?partie=";
    private final static String urlSuivant = MainActivity.url + "initMajority.php?partie=";
    private final static int VOTE_NON_REALISE = -1;
    private final static int VOTE_CARTE_MAJORITE = 0;
    private final static int VOTE_CARTE_A = 1;
    private final static int VOTE_CARTE_B = 2;
    private final static int VOTE_CARTE_C = 3;
    private final static int[] tableIdRessourcesMots = {R.id.mot_principal, R.id.mot_1, R.id.mot_2, R.id.mot_3};
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

        // Refresh info jeu
        startRefreshAuto(urlJeu);

        mSuisElimine = false;
        // TODO : en fin de partie afficher le tableau des scores
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
                if (mValeurVote > 0) {
                    desactiveBouton(mBoutonValider);
                    new MainActivity.TacheURLSansRetour().execute(urlVote + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + mIdMot);
                }
                startRefreshAuto(urlJeu);
                break;

            case "bouton_suivant":
                if (mBoutonMancheTourSuivant.getText().toString().equals(getResources().getString(R.string.tour_suivant))) {
                    // TODO : attributer les points si ex-equos avec le vote majoritaire
                    new MainActivity.TacheURLSansRetour().execute(urlSuivant + mIdPartie + "&tour=oui");
                } else if (mBoutonMancheTourSuivant.getText().toString().equals(getResources().getString(R.string.manche_suivante))) {
                    // TODO : attributer les points aux joueurs majoritaires
                    new MainActivity.TacheURLSansRetour().execute(urlSuivant + mIdPartie + "&manche=oui");
                }
                break;
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

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);
        afficheScore(mListeJoueurs);

        // Bouton tour ou manche suivant que pour les joueurs admin
        if (mAdmin)
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.GONE);

        // Mots
        ArrayList<Mot> listeMots = parseNoeudsMots(doc);
        afficheMots(listeMots);

        // Votes
        ArrayList<Vote> listeVotes = parseNoeudsVotes(doc);
        afficheVotes(listeVotes);
    }

    private void afficheVotes(ArrayList<Vote> listeVotes) {
        int nbVoteAttendu = listeVotes.size();
        Integer[] nbVoteParCarte = {0, 0, 0, 0};
        mSuisElimine = true;
        mValeurVote = -1;
        int nbVote = 0;

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

            // TODO : si vote majoritaire == carte étoiles -> Attribuer les points -> Est-ce via le php tour/manche suivante ?

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
        } else
            mBoutonValider.setText(getResources().getString(R.string.valider));

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
                if (i > VOTE_CARTE_MAJORITE) // On ne prend pas le résultat du vote "carte étoiles"
                    mNbJoueursMajoritaires += valeurVoteMax; // Si 2 votes sont majoritaires, il faut les cumuler.
                findViewById(tableIdRessourcesCarteVote[i]).setAlpha(1);
                findViewById(tableIdRessourcesTexteVote[i]).setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));
            } else {
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

    private ArrayList<Mot> parseNoeudsMots(Document doc) {
        Node noeudMots = getNoeudUnique(doc, "Mots");

        int idMot = 0;
        String mot = "";
        int lettre = 0;
        ArrayList<Mot> listeMots = new ArrayList<>();

        if (!noeudMots.getAttributes().item(0).getNodeValue().isEmpty())
            mNbMotsTrouves = Integer.parseInt(noeudMots.getAttributes().item(0).getNodeValue());
        for (int i = 0; i < noeudMots.getChildNodes().getLength(); i++) { // Parcours toutes les mots
            Node noeudMot = noeudMots.getChildNodes().item(i);
            Log.d("PGR-XML-Mot", noeudMot.getNodeName());
            for (int j = 0; j < noeudMot.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud mot
                Log.d("PGR-XML-Mot", noeudMot.getAttributes().item(j).getNodeName() + "_" + noeudMot.getAttributes().item(j).getNodeValue());
                switch (noeudMot.getAttributes().item(j).getNodeName()) {
                    case "id_mot":
                        idMot = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                        break;
                    case "mot":
                        mot = noeudMot.getAttributes().item(j).getNodeValue();
                        break;
                    case "lettre":
                        lettre = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Mot monMot = new Mot(idMot, mot, lettre);
            listeMots.add(monMot);
        }

        return listeMots;
    }

    private void selectionneCarteVoteParLettre(int lettre) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        findViewById(tableIdRessourcesCarteVote[lettre]).setBackgroundResource(R.drawable.fond_carte_vert);
    }
}