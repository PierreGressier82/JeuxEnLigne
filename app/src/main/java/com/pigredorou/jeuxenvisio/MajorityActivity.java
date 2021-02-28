package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Mot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.pigredorou.jeuxenvisio.outils.outilsXML.getNoeudUnique;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.parseNoeudsJoueur;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.suisJeAdmin;

public class MajorityActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables globales
    private String mPseudo;
    private int mIdJoueur;
    private int mIdPartie;
    private int mIdMot;
    private int mMethodeSelection;
    private int mValeurVote;
    private int mMonScore;
    private int mNbMotsTrouves;
    private int mNbVote;
    private int mNbVoteAttendu;
    private boolean mAdmin;
    // Variables statiques
    private final static int VOTE_CARTE_A = 1;
    private final static int VOTE_CARTE_B = 2;
    private final static int VOTE_CARTE_C = 3;
    private final static int VOTE_CARTE_MAJORITE = 4;
    private final static String urlMajority = MainActivity.url + "majority.php?partie=";
    private final static String urlVote = MainActivity.url + "vote.php?partie=";
    private static final int[] tableIdRessourcesMots = {R.id.mot_principal, R.id.mot_1, R.id.mot_2, R.id.mot_3};
    private static final int[] tableIdRessourcesCarteVote = {R.id.carte_vote_majority, R.id.carte_vote_a, R.id.carte_vote_b, R.id.carte_vote_c};
    private static final int[] tableIdRessourcesTexteVote = {R.id.resultat_vote_majority, R.id.resultat_vote_a, R.id.resultat_vote_b, R.id.resultat_vote_c};
    // Elements graphique
    private Button mBoutonValider;
    private Button mBoutonMancheTourSuivant;
    private ImageView mCarteVoteA;
    private ImageView mCarteVoteB;
    private ImageView mCarteVoteC;
    private ImageView mCarteVoteMajority;
    private TextView mResultatVoteA;
    private TextView mResultatVoteB;
    private TextView mResultatVoteC;
    private TextView mResultatVoteMajority;
    // Refresh auto
    private Thread t;
    private boolean mMajTerminee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_majority);

        // Chargement
        //findViewById(R.id.chargement).setVisibility(View.GONE);

        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        int idSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        mMethodeSelection = intent.getIntExtra(MainActivity.VALEUR_METHODE_SELECTION, MainActivity.mSelectionDragAndDrop);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);

        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);

        // Cartes pour le vote
        chargeCartesVote();
        // Boutons
        chargeBoutons();
        desactiveBouton(mBoutonValider);
        desactiveBouton(mBoutonMancheTourSuivant);

        // Refresh info jeu
        mMajTerminee = true;
        startRefreshAuto();

        // TODO : en fin de partie afficher le tableau des scores
    }

    private void chargeBoutons() {
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonMancheTourSuivant = findViewById(R.id.bouton_tour_manchant_suivante);
        mValeurVote = 0;
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
    protected void onPause() {
        // Stop refresh auto
        stopRefreshAuto();
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopRefreshAuto();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopRefreshAuto();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        startRefreshAuto();
        super.onResume();
    }

    private void stopRefreshAuto() {
        t.interrupt();
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
                startRefreshAuto();
                if (mValeurVote > 0) {
                    desactiveBouton(mBoutonValider);
                    new MainActivity.TacheURLSansRetour().execute(urlVote + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + mIdMot);
                }
                break;

            case "bouton_suivant":
                // TODO : Gestion tour ou manche suivante
                break;
        }

    }

    private void activeBouton(Button bouton) {
        bouton.setTextColor(getResources().getColor(R.color.blanc));
        bouton.setOnClickListener(this);
    }

    private void desactiveBouton(Button bouton) {
        bouton.setTextColor(getResources().getColor(R.color.noir));
        bouton.setOnClickListener(null);
    }

    private void selectionneCarteVote(String tag) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        switch (tag) {
            case "carte_vote_a":
                mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_A;
                mIdMot = Integer.parseInt(findViewById(tableIdRessourcesMots[1]).getTag().toString());
                break;
            case "carte_vote_b":
                mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_B;
                mIdMot = Integer.parseInt(findViewById(tableIdRessourcesMots[2]).getTag().toString());
                break;
            case "carte_vote_c":
                mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_C;
                mIdMot = Integer.parseInt(findViewById(tableIdRessourcesMots[3]).getTag().toString());
                break;
            case "carte_vote_majority":
                mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_vert);
                mValeurVote = VOTE_CARTE_MAJORITE;
                mIdMot = 0;
                break;
        }
    }

    private void startRefreshAuto() {
        if (t == null || !t.isAlive()) {
            t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (mMajTerminee) {
                                        mMajTerminee = false;
                                        // Mise à jour complète
                                        new TacheGetInfoMajority().execute(urlMajority + mIdPartie);
                                    }
                                }
                            });
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };

            t.start();
        }
    }


    private void parseXML(Document doc) {

        Element element = doc.getDocumentElement();
        element.normalize();

        // Joueurs
        ArrayList<Joueur> listeJoueurs = parseNoeudsJoueur(doc);
        mAdmin = suisJeAdmin(mPseudo, listeJoueurs);
        mIdJoueur = getIdJoueurFromPseudo(listeJoueurs);
        afficheScore(listeJoueurs);

        // Bouton tour ou manche suivant que pour les joueurs admin
        if (mAdmin)
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.bouton_tour_manchant_suivante).setVisibility(View.GONE);

        // Mots
        ArrayList<Mot> listeMots = parseNoeudsMots(doc);
        afficheMots(listeMots);

        // Votes
        Integer[] listeVotes = parseNoeudsVotes(doc);
        afficheVotes(listeVotes);
    }

    private void afficheScore(ArrayList<Joueur> listeJoueurs) {
        String textScore;

        mMonScore = getSCoreJoueurId(listeJoueurs);
        textScore = getResources().getString(R.string.score_quipe) + " : " + mMonScore;
        TextView tv = findViewById(R.id.score);
        tv.setText(textScore);
    }

    private int getSCoreJoueurId(ArrayList<Joueur> listeJoueurs) {
        int score = 0;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            if (listeJoueurs.get(i).getId() == mIdJoueur) {
                score = listeJoueurs.get(i).getScore();
                break;
            }
        }

        return score;
    }

    private int getIdJoueurFromPseudo(ArrayList<Joueur> listeJoueurs) {
        int id = 0;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            if (listeJoueurs.get(i).getNomJoueur().equals(mPseudo)) {
                id = listeJoueurs.get(i).getId();
                break;
            }
        }

        return id;
    }

    private void afficheVotes(Integer[] listeVotes) {
        TextView tv = findViewById(R.id.nb_vote_joueurs);
        String texteVote = mNbVote + " vote(s) sur " + mNbVoteAttendu;
        tv.setText(texteVote);

        // Affichage des résultats du vote
        if (mNbVote == mNbVoteAttendu) {
            afficheResultatsVote(listeVotes);
            activeBouton(mBoutonMancheTourSuivant);
        } else {
            activeCarteVote(listeVotes);
            masqueResultats();
        }
    }

    private void activeCarteVote(Integer[] listeVotes) {
        // Détermine les cartes ayant le vote max
        for (int i = 0; i < listeVotes.length; i++) {
            findViewById(tableIdRessourcesCarteVote[i]).setAlpha(1);
            findViewById(tableIdRessourcesCarteVote[i]).setBackgroundResource(R.drawable.fond_carte_blanc);
            findViewById(tableIdRessourcesCarteVote[i]).setOnClickListener(this);
        }
    }

    private void masqueResultats() {
        mResultatVoteA.setVisibility(View.GONE);
        mResultatVoteB.setVisibility(View.GONE);
        mResultatVoteC.setVisibility(View.GONE);
        mResultatVoteMajority.setVisibility(View.GONE);
    }

    private void afficheResultatsVote(Integer[] listeVotes) {
        determineVoteMajoritaire(listeVotes);
        String texte = listeVotes[0] + " votes";
        mResultatVoteMajority.setText(texte);
        texte = listeVotes[1] + " votes";
        mResultatVoteA.setText(texte);
        texte = listeVotes[2] + " votes";
        mResultatVoteB.setText(texte);
        texte = listeVotes[3] + " votes";
        mResultatVoteC.setText(texte);

        // TODO : afficher joueur éliminé
        mResultatVoteA.setVisibility(View.VISIBLE);
        mResultatVoteB.setVisibility(View.VISIBLE);
        mResultatVoteC.setVisibility(View.VISIBLE);
        mResultatVoteMajority.setVisibility(View.VISIBLE);
    }

    private void determineVoteMajoritaire(Integer[] listeVotes) {
        int voteMax = 0;
        Integer[] listeVotesMajoritaire = {0, 0, 0, 0};
        // Détermine le vote max
        for (Integer listeVote : listeVotes) {
            if (listeVote > voteMax)
                voteMax = listeVote;
        }

        // Détermine les cartes ayant le vote max
        for (int i = 0; i < listeVotes.length; i++) {
            if (listeVotes[i] == voteMax) {
                findViewById(tableIdRessourcesCarteVote[i]).setAlpha(1);
                findViewById(tableIdRessourcesTexteVote[i]).setBackgroundColor(getResources().getColor(R.color.vert_clair_transparent));
            } else {
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

    private Integer[] parseNoeudsVotes(Document doc) {
        Node noeudVotes = getNoeudUnique(doc, "Votes");

        int id_joueur = 0;
        int id_mot = 0;
        int lettre = 0;
        Integer[] listeVotes = {0, 0, 0, 0};
        mNbVote = 0;
        mNbVoteAttendu = 0;

        for (int i = 0; i < noeudVotes.getChildNodes().getLength(); i++) { // Parcours toutes les votes
            Node noeudVote = noeudVotes.getChildNodes().item(i);
            Log.d("PGR-XML-Vote", noeudVote.getNodeName());
            for (int j = 0; j < noeudVote.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud vote
                Log.d("PGR-XML-Vote", noeudVote.getAttributes().item(j).getNodeName() + "_" + noeudVote.getAttributes().item(j).getNodeValue());
                switch (noeudVote.getAttributes().item(j).getNodeName()) {
                    case "id_joueur":
                        id_joueur = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                        mNbVoteAttendu++;
                        break;
                    case "id_mot":
                        id_mot = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                        break;
                    case "lettre":
                        if (noeudVote.getAttributes().item(j).getNodeValue().isEmpty())
                            lettre = -1;
                        else {
                            mNbVote++;
                            lettre = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                            listeVotes[lettre]++;
                            if (mIdJoueur == id_joueur) {
                                desactiveBouton(mBoutonValider);
                                desactiveCartesVote();
                                selectionneCarteVoteParLettre(lettre);
                            }
                        }
                        break;
                }
            }
        }

        return listeVotes;
    }

    private void selectionneCarteVoteParLettre(int lettre) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        findViewById(tableIdRessourcesCarteVote[lettre]).setBackgroundResource(R.drawable.fond_carte_vert);
    }

    /**
     * Classe qui permet de récupérer en base toutes les informations du jeu Majority
     * -> Retourne l'ensemble des informations à afficher au joueur sous forme XML
     */
    private class TacheGetInfoMajority extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... strings) {
            URL url;
            Document doc = null;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                // Lecture du flux
                InputStream is = url.openStream();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(is);
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            mMajTerminee = true;
            if (doc != null) {
                parseXML(doc);
                findViewById(R.id.chargement).setVisibility(View.GONE);
            }
            super.onPostExecute(doc);
        }
    }

}