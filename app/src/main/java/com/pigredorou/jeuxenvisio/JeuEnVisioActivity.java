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

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Mot;
import com.pigredorou.jeuxenvisio.objets.Objet;
import com.pigredorou.jeuxenvisio.objets.Reponse;
import com.pigredorou.jeuxenvisio.objets.Situation;
import com.pigredorou.jeuxenvisio.objets.TopTen;
import com.pigredorou.jeuxenvisio.objets.Vote;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class JeuEnVisioActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables globales
    String mPseudo;
    int mIdJoueur;
    int mIdPartie;
    int mMonScore;
    int mMethodeSelection;
    boolean mAdmin;
    boolean mJeSuisLeJoueurActif = false;
    int mNumeroMotChoisi;
    ArrayList<Joueur> mListeJoueurs;
    ArrayList<Mot> mListeMots;
    ArrayList<Situation> mListeSituations;
    ArrayList<Objet> mListeObjets;
    ArrayList<Reponse> mListeMotsReponses;
    ArrayList<Vote> mListeVotes;
    // Variables statiques
    final static String urlMAJ = MainActivity.url + "majJeu.php?partie=";
    // Elements graphique
    Button mBoutonValider;
    // Refresh auto
    Thread t;
    boolean mMajTerminee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Ecran de chargement
        findViewById(R.id.chargement).setVisibility(View.VISIBLE);

        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        mIdJoueur = intent.getIntExtra(MainActivity.VALEUR_ID_JOUEUR, 0);
        mMethodeSelection = getIntent().getIntExtra(MainActivity.VALEUR_METHODE_SELECTION, MainActivity.mSelectionDragAndDrop);

        // Affiche le contexte de l'entête
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);

        // Bouton retour
        chargeBoutonRetour();

        // Refresh info jeu
        mMajTerminee = true;
    }

    void chargeBoutonRetour() {
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        // Stop refresh auto
        stopRefreshAuto();
        super.onPause();
    }

    @Override
    public void onStop() {
        stopRefreshAuto();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopRefreshAuto();
        super.onDestroy();
    }

    void stopRefreshAuto() {
        if (t != null)
            t.interrupt();
    }

    @Override
    public void onClick(View v) {
        if (v != null && v.getTag() != null) {
            switch (v.getTag().toString()) {
                case "bouton_retour":
                    stopRefreshAuto();
                    finish();
                    break;

                case "bouton_valider":
                    //startRefreshAuto(urlJeu); -> A reprendre dans la classe fille
                    //desactiveBouton(mBoutonValider);
                    //new MainActivity.TacheURLSansRetour().execute(urlMAJ + mIdPartie + "&joueur=" + mIdJoueur);
                    break;
            }
        }
    }

    void debug(String message) {
        Log.d("PGR", message);
    }

    void activeBouton(Button bouton) {
        bouton.setTextColor(getResources().getColor(R.color.blanc));
        bouton.setOnClickListener(this);
    }

    void desactiveBouton(Button bouton) {
        bouton.setTextColor(getResources().getColor(R.color.noir));
        bouton.setOnClickListener(null);
    }

    void startRefreshAuto(final String urlJeu) {
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
                                        new TacheGetInfoJeu().execute(urlJeu + mIdPartie + "&joueur=" + mPseudo);
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

    public static Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
    }

    static ArrayList<Joueur> parseNoeudsJoueur(Document doc) {
        Node NoeudJoueurs = getNoeudUnique(doc, "Joueurs");

        String pseudo = "";
        String equipe = "";
        int admin = 0;
        int nv = 0;
        int id = 0;
        int score = 0;
        int actif = 0;
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        for (int i = 0; i < NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudJoueurs.getChildNodes().item(i);
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Joueur", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                if (noeudCarte.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "id":
                        id = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "pseudo":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin":
                        admin = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "equipe":
                        equipe = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "actif":
                        actif = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "new":
                        nv = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "score":
                        score = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(id, pseudo, equipe, score, admin, nv, actif);
            listeJoueurs.add(joueur);
            equipe = "";
        }

        return listeJoueurs;
    }

    TopTen parseNoeudTapis(Document doc) {
        Node noeudTapis = getNoeudUnique(doc, "Tapis");

        int licorne = 0;
        int caca = 0;
        int manche = 0;
        int numero = 0;
        int nbCartes = 0;

        for (int i = 0; i < noeudTapis.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud tapis
            Log.d("PGR-XML-Tapis", noeudTapis.getAttributes().item(i).getNodeName() + "_" + noeudTapis.getAttributes().item(i).getNodeValue());
            if (noeudTapis.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudTapis.getAttributes().item(i).getNodeName()) {
                case "licorne":
                    licorne = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "caca":
                    caca = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "manche":
                    manche = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "numero":
                    numero = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "nbCartes":
                    nbCartes = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
            }
        }
        return new TopTen(licorne, caca, manche, numero, nbCartes);
    }

    ArrayList<Mot> parseNoeudsMots(Document doc, String nomNoeud) {
        Node noeudMots = getNoeudUnique(doc, nomNoeud);
        mNumeroMotChoisi = 0;

        int idMot = 0;
        String mot = "";
        int lettre = 0;
        boolean elimine = false;
        ArrayList<Mot> listeMots = new ArrayList<>();

        for (int i = 0; i < noeudMots.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud parent
            if ("numero".equals(noeudMots.getAttributes().item(i).getNodeName()) && !noeudMots.getAttributes().item(i).getNodeValue().isEmpty())
                mNumeroMotChoisi = Integer.parseInt(noeudMots.getAttributes().item(i).getNodeValue());
        }

        if (noeudMots != null)
            for (int i = 0; i < noeudMots.getChildNodes().getLength(); i++) { // Parcours tous les mots
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
                        case "elimine":
                            if (Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue()) == 1)
                                elimine = true;
                            break;
                    }
                }
                Mot monMot = new Mot(idMot, mot, lettre, elimine);
                listeMots.add(monMot);
            }

        return listeMots;
    }

    ArrayList<Situation> parseNoeudsSituations(Document doc) {
        Node noeudSituations = getNoeudUnique(doc, "Situations");
        mNumeroMotChoisi = 0;

        int idMot = 0;
        String situation = "";
        int idCouleur = 0;
        int actif = 0;
        ArrayList<Situation> ListeSituations = new ArrayList<>();

        if (noeudSituations != null)
            for (int i = 0; i < noeudSituations.getChildNodes().getLength(); i++) { // Parcours toutes les situations
                Node noeudMot = noeudSituations.getChildNodes().item(i);
                Log.d("PGR-XML-Situation", noeudMot.getNodeName());
                for (int j = 0; j < noeudMot.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud situation
                    Log.d("PGR-XML-Situation", noeudMot.getAttributes().item(j).getNodeName() + "_" + noeudMot.getAttributes().item(j).getNodeValue());
                    switch (noeudMot.getAttributes().item(j).getNodeName()) {
                        case "id_mot":
                            idMot = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                        case "situation":
                            situation = noeudMot.getAttributes().item(j).getNodeValue();
                            break;
                        case "id_couleur":
                            idCouleur = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                        case "actif":
                            actif = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Situation maSituation = new Situation(idMot, situation, idCouleur, actif);
                ListeSituations.add(maSituation);
            }

        return ListeSituations;
    }

    ArrayList<Objet> parseNoeudsObjets(Document doc) {
        Node noeudObjets = getNoeudUnique(doc, "Objets");

        int id = 0;
        int actif = 0;
        ArrayList<Objet> ListeObjets = new ArrayList<>();

        if (noeudObjets != null)
            for (int i = 0; i < noeudObjets.getChildNodes().getLength(); i++) { // Parcours toutes les situations
                Node noeudMot = noeudObjets.getChildNodes().item(i);
                Log.d("PGR-XML-Objet", noeudMot.getNodeName());
                for (int j = 0; j < noeudMot.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud situation
                    Log.d("PGR-XML-Objet", noeudMot.getAttributes().item(j).getNodeName() + "_" + noeudMot.getAttributes().item(j).getNodeValue());
                    switch (noeudMot.getAttributes().item(j).getNodeName()) {
                        case "id_objet":
                            id = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                        case "actif":
                            actif = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Objet monObjet = new Objet(id, actif);
                ListeObjets.add(monObjet);
            }

        return ListeObjets;
    }

    ArrayList<Reponse> parseNoeudsReponses(Document doc) {
        Node noeudReponses = getNoeudUnique(doc, "Reponses");

        int idJoueur = 0;
        String mot = "";
        int elimine = 0;
        ArrayList<Reponse> listeReponses = new ArrayList<>();

        if (noeudReponses != null)
            for (int i = 0; i < noeudReponses.getChildNodes().getLength(); i++) { // Parcours toutes les réponses
                Node noeudMot = noeudReponses.getChildNodes().item(i);
                Log.d("PGR-XML-Reponse", noeudMot.getNodeName());
                for (int j = 0; j < noeudMot.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud mot
                    Log.d("PGR-XML-Reponse", noeudMot.getAttributes().item(j).getNodeName() + "_" + noeudMot.getAttributes().item(j).getNodeValue());
                    switch (noeudMot.getAttributes().item(j).getNodeName()) {
                        case "id_joueur":
                            idJoueur = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                        case "mot":
                            mot = noeudMot.getAttributes().item(j).getNodeValue();
                            break;
                        case "elimine":
                            elimine = Integer.parseInt(noeudMot.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Reponse reponse = new Reponse(idJoueur, mot, elimine);
                listeReponses.add(reponse);
            }

        return listeReponses;
    }

    ArrayList<Vote> parseNoeudsVotes(Document doc) {
        Node noeudVotes = getNoeudUnique(doc, "Votes");

        ArrayList<Vote> listeVotes = new ArrayList<>();

        if (noeudVotes != null)
            for (int i = 0; i < noeudVotes.getChildNodes().getLength(); i++) { // Parcours toutes les votes
                Node noeudVote = noeudVotes.getChildNodes().item(i);
                Log.d("PGR-XML-Vote", noeudVote.getNodeName());
                int id_joueur = 0;
                int id_mot = 0;
                int lettre = -1;
                for (int j = 0; j < noeudVote.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud vote
                    Log.d("PGR-XML-Vote", noeudVote.getAttributes().item(j).getNodeName() + "_" + noeudVote.getAttributes().item(j).getNodeValue());
                    if (noeudVote.getAttributes().item(j).getNodeValue().isEmpty())
                        continue;
                    switch (noeudVote.getAttributes().item(j).getNodeName()) {
                        case "id_joueur":
                            id_joueur = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                            break;
                        case "id_mot":
                            id_mot = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                            break;
                        case "lettre":
                            lettre = Integer.parseInt(noeudVote.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Vote vote = new Vote(id_joueur, id_mot, lettre);
                listeVotes.add(vote);
            }

        return listeVotes;
    }

    ArrayList<Carte> parseNoeudsCarte(Document doc, String nomNoeud) {
        Node NoeudCartes = getNoeudUnique(doc, nomNoeud);

        String couleur = "";
        int valeur = 0;
        boolean cartePosee = false;
        ArrayList<Carte> listeCartes = new ArrayList<>();

        for (int i = 0; i < NoeudCartes.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudCartes.getChildNodes().item(i);
            Log.d("PGR-XML-Carte", noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Carte", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "couleur":
                        couleur = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "valeur":
                        valeur = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "pose":
                        if (!noeudCarte.getAttributes().item(j).getNodeValue().isEmpty()) {
                            int ordrePose = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                            cartePosee = ordrePose != 0;
                        } else
                            cartePosee = false;
                        break;
                }
            }
            Carte carte = new Carte(couleur, valeur, cartePosee);
            listeCartes.add(carte);
        }

        return listeCartes;
    }

    static boolean suisJeAdmin(String pseudo, ArrayList<Joueur> listeJoueurs) {
        boolean suisAdmin = false;

        for (int i = 0; i < listeJoueurs.size(); i++) {
            // Suis-je admin ?
            if (pseudo.equals(listeJoueurs.get(i).getNomJoueur()) && listeJoueurs.get(i).getAdmin() == 1) {
                suisAdmin = true;
                break;
            }
        }
        return suisAdmin;
    }

    static boolean suisJeActif(String pseudo, ArrayList<Joueur> listeJoueurs) {
        boolean suisActif = false;

        for (int i = 0; i < listeJoueurs.size(); i++) {
            // Suis-je admin ?
            if (pseudo.equals(listeJoueurs.get(i).getNomJoueur()) && listeJoueurs.get(i).getActif() == 1) {
                suisActif = true;
                break;
            }
        }
        return suisActif;
    }

    void parseXML(Document doc) {
        // A surcharger dans les classes

        // Masque l'écran de chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);
        // Liste des joueurs de la partie
        mListeJoueurs = parseNoeudsJoueur(doc);
        mAdmin = suisJeAdmin(mPseudo, mListeJoueurs);

        // Liste des mots du jeu
        //mListeMots = parseNoeudsMots(doc);

        // Liste des votes
        //mListeVotes = parseNoeudsVotes(doc);
    }

    void afficheMonScore(ArrayList<Joueur> listeJoueurs) {
        String textScore;

        mMonScore = getScoreJoueurId(listeJoueurs);
        textScore = getResources().getString(R.string.score_quipe) + " : " + mMonScore;
        TextView tv = findViewById(R.id.score);
        tv.setText(textScore);
    }

    int getScoreJoueurId(ArrayList<Joueur> listeJoueurs) {
        int score = 0;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            if (listeJoueurs.get(i).getId() == mIdJoueur) {
                score = listeJoueurs.get(i).getScore();
                break;
            }
        }

        return score;
    }

    int getIdJoueurFromPseudo(ArrayList<Joueur> listeJoueurs) {
        int id = 0;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            if (listeJoueurs.get(i).getNomJoueur().equals(mPseudo)) {
                id = listeJoueurs.get(i).getId();
                break;
            }
        }

        return id;
    }

    /**
     * Classe qui permet de récupérer en base toutes les informations du jeu Majority
     * -> Retourne l'ensemble des informations à afficher au joueur sous forme XML
     */
    class TacheGetInfoJeu extends AsyncTask<String, Void, Document> {

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
            }
            super.onPostExecute(doc);
        }
    }
}