package com.pigredorou.jeuxenvisio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pigredorou.jeuxenvisio.objets.Crane;
import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Personnage;
import com.pigredorou.jeuxenvisio.objets.TourDeJeuCrane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class FiestaDeLosMuertosActivity extends AppCompatActivity implements View.OnClickListener {

    // URLs des actions en base
    private static final String urlFiesta = MainActivity.url + "fiesta.php?partie=";
    private static final String urlInitJeu = MainActivity.url + "initFiesta.php?partie=";
    private static final String urlValidMot = MainActivity.url + "validMotFiesta.php?partie=";
    private static final String urlDeduction = MainActivity.url + "deductionFiesta.php?partie=";
    // Elements graphiques
    private ImageView mImageCrane;
    private TextView mContextePersonnage;
    // Refresh auto
    private Thread t;
    // Variables globales
    private static int[] mListeIdPersonnage = {R.id.personnage1, R.id.personnage2, R.id.personnage3, R.id.personnage4, R.id.personnage5, R.id.personnage6, R.id.personnage7, R.id.personnage8};
    private static int[] mListeIdPersoArdoise = {R.id.nom_ardoise_1, R.id.nom_ardoise_2, R.id.nom_ardoise_3, R.id.nom_ardoise_4, R.id.nom_ardoise_5, R.id.nom_ardoise_6, R.id.nom_ardoise_7, R.id.nom_ardoise_8};
    private static int[] mListeIdMot = {R.id.mot_ardoise_1, R.id.mot_ardoise_2, R.id.mot_ardoise_3, R.id.mot_ardoise_4, R.id.mot_ardoise_5, R.id.mot_ardoise_6, R.id.mot_ardoise_7, R.id.mot_ardoise_8};
    private static int[] mListeIdCranesResultat = {R.id.crane1, R.id.crane2, R.id.crane3, R.id.crane4, R.id.crane5, R.id.crane6, R.id.crane7, R.id.crane8};
    private static int[] mListeIdImageCranesResultat = {R.id.image_crane1, R.id.image_crane2, R.id.image_crane3, R.id.image_crane4, R.id.image_crane5, R.id.image_crane6, R.id.image_crane7, R.id.image_crane8};
    private static int[] mListeIdNomPersoResultat = {R.id.nom_personnage1, R.id.nom_personnage2, R.id.nom_personnage3, R.id.nom_personnage4, R.id.nom_personnage5, R.id.nom_personnage6, R.id.nom_personnage7, R.id.nom_personnage8};
    private static int[] mListeIdContextePersoResultat = {R.id.contexte_personnage1, R.id.contexte_personnage2, R.id.contexte_personnage3, R.id.contexte_personnage4, R.id.contexte_personnage5, R.id.contexte_personnage6, R.id.contexte_personnage7, R.id.contexte_personnage8};
    private static int[] mListeIdZoneSaisieResultat = {R.id.zone_saisie1, R.id.zone_saisie2, R.id.zone_saisie3, R.id.zone_saisie4, R.id.zone_saisie5, R.id.zone_saisie6, R.id.zone_saisie7, R.id.zone_saisie8};
    private static int[] mListeImageCranesOuvert = {R.drawable.fiesta_crane_ouvert, R.drawable.fiesta_crane_ouvert_1, R.drawable.fiesta_crane_ouvert_2, R.drawable.fiesta_crane_ouvert_3, R.drawable.fiesta_crane_ouvert_4, R.drawable.fiesta_crane_ouvert_5, R.drawable.fiesta_crane_ouvert_6, R.drawable.fiesta_crane_ouvert_7};
    private static String[] mListePhasesJeu = {"Mots", "Déduction", "Apaiser les morts"};
    private String mPseudo;
    private String mMot;
    private String mPersonnageSelectionne;
    private TextView mNomPersonnage;
    private EditText mZoneSaisie;
    private Button mBoutonValider;
    private Button mBoutonInitialiser;
    private ArrayList<Joueur> mListeJoueurs;
    private ArrayList<Personnage> mListePersonnages;
    private ArrayList<Crane> mListeCranes;
    private ArrayList<TourDeJeuCrane> mListeTourDeJeu;
    private int[] mNbBonnesReponsesDeduction;
    private int mIdPartie;
    private int mTourDeJeu;
    private int mNbJoueurValides;
    private int mIdPerso;
    private int mNbJoueurPhaseDeduction;
    private int mPhaseEnCours;
    private boolean mMotValide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_fiesta_de_los_muertos);

        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);

        // Entête
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

        // Crane
        mImageCrane = findViewById(R.id.image_crane);
        mNomPersonnage = findViewById(R.id.nom_personnage);
        mContextePersonnage = findViewById(R.id.contexte_personnage);
        mZoneSaisie = findViewById(R.id.zone_saisie);
        mZoneSaisie.setInputType(InputType.TYPE_CLASS_TEXT);
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonValider.setOnClickListener(this);
        mBoutonInitialiser = findViewById(R.id.bouton_initialiser);
        mBoutonInitialiser.setOnClickListener(this);

        // Ardoise
        for (int value : mListeIdPersoArdoise) {
            TextView tv = findViewById(value);
            tv.setOnClickListener(this);
        }
        for (int value : mListeIdPersonnage) {
            TextView tv = findViewById(value);
            tv.setOnClickListener(this);
        }

        // Refresh auto
        startRefreshAuto();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_retour:
                finish();
                break;
            case R.id.bouton_valider:
                if (mMotValide) {
                    new MainActivity.TacheURLSansRetour().execute(urlDeduction + mIdPartie + "&joueur=" + mPseudo + "&reponse=" + getAssociatioCranePersonnage());
                    desactiveArdoise();
                }
                else {
                    new MainActivity.TacheURLSansRetour().execute(urlValidMot + mIdPartie + "&joueur=" + mPseudo + "&mot=" + mZoneSaisie.getText().toString() + "&tourDeJeu=" + (mTourDeJeu + 1));
                    mZoneSaisie.setText("");
                }
                activeBoutonValider(false);
                break;

            case R.id.bouton_initialiser:
                new MainActivity.TacheURLSansRetour().execute(urlInitJeu + mIdPartie);
                break;

            case R.id.personnage1:
            case R.id.personnage2:
            case R.id.personnage3:
            case R.id.personnage4:
            case R.id.personnage5:
            case R.id.personnage6:
            case R.id.personnage7:
            case R.id.personnage8:
                for (int value : mListeIdPersonnage) {
                    TextView tv = findViewById(value);
                    tv.setTextColor(getResources().getColor(R.color.couleurFondFiestaMuertos));
                }
                TextView tv = findViewById(v.getId());
                tv.setTextColor(getResources().getColor(R.color.rouge));
                mPersonnageSelectionne = tv.getTag().toString();
                for (int i = 0; i < mListePersonnages.size(); i++) {
                    if (mListePersonnages.get(i).getNom().equals(mPersonnageSelectionne))
                        mIdPerso = mListePersonnages.get(i).getId();
                }
                break;

            case R.id.nom_ardoise_1:
            case R.id.nom_ardoise_2:
            case R.id.nom_ardoise_3:
            case R.id.nom_ardoise_4:
            case R.id.nom_ardoise_5:
            case R.id.nom_ardoise_6:
            case R.id.nom_ardoise_7:
            case R.id.nom_ardoise_8:
                tv = findViewById(v.getId());
                tv.setText(mPersonnageSelectionne);
                tv.setTag(String.valueOf(mIdPerso));
                if (mTourDeJeu == 4)
                    activeBoutonValider(aiJeToutesLesReponsesPhaseDeduction());
                break;
        }
    }

    private void desactiveArdoise() {
        for (int value : mListeIdPersoArdoise) {
            TextView tv = findViewById(value);
            tv.setOnClickListener(null);
        }
    }

    private String getAssociatioCranePersonnage() {
        StringBuilder retour = new StringBuilder();
        for (int index = 0; index < mListeIdPersoArdoise.length; index++) {
            TextView tv = findViewById(mListeIdPersoArdoise[index]);

            if (!tv.getText().toString().isEmpty()) {
                int idPerso = Integer.parseInt(tv.getTag().toString());
                int idCrane = mListeCranes.get(index).getId();

                if (!retour.toString().isEmpty())
                    retour.append("_");
                retour.append(idCrane).append("-").append(idPerso);
            }
        }

        Log.d("PGR-Assoc", retour.toString());

        return retour.toString();
    }

    private boolean aiJeToutesLesReponsesPhaseDeduction() {
        boolean aiJeToutesLesReponses = true;
        // Vérifie si tous les cranes ont un personnage pour autoriser l'enregistrement des choix
        for (int i = 0; i < mListeJoueurs.size(); i++) {
            TextView tv = findViewById(mListeIdPersoArdoise[i]);
            if (tv.getText().toString().isEmpty())
                aiJeToutesLesReponses = false;
        }

        return aiJeToutesLesReponses;
    }

    private void activeBoutonValider(boolean active) {
        mZoneSaisie.setFocusable(active);
        mBoutonValider.setClickable(active);

        if (active)
            mBoutonValider.setTextColor(getResources().getColor(R.color.blanc));
        else
            mBoutonValider.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
                                    // Mise à jour complète
                                    new TacheGetInfoFiesta().execute(urlFiesta + mIdPartie + "&joueur=" + mPseudo);
                                }
                            });
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };

            t.start();
            debug();
        }
    }

    private void stopRefreshAuto() {
        t.interrupt();
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

    private void debug() {
        Log.d("PGR", "start refresh");
    }

    private Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
    }

    private void parseXML(Document doc) {

        Element element = doc.getDocumentElement();
        element.normalize();

        // Joueurs
        mListeJoueurs = parseNoeudsJoueur(doc);

        // Personnages
        mListePersonnages = parseNoeudsPersonnage(doc);
        affichePersonnages();

        // Cranes
        mListeCranes = parseNoeudsCranes(doc);

        // Tour de jeu
        mListeTourDeJeu = parseNoeudsTourDeJeu(doc);
        activeLignesAvecMot();
        afficheMots();

        // Mon Crane
        Crane monCrane = parseNoeudsMonCrane(doc);

        // Déductions
        parseNoeudsDeduction(doc);

        // Affichage
        affichePhaseDeJeu();
        afficheCraneEtArdoise(monCrane);
        if (mTourDeJeu < 3)
            RAZArdoise();
    }

    private void affichePhaseDeJeu() {
        // Gestion des phases
        if (mNbJoueurPhaseDeduction == mListeJoueurs.size()) // Tous les joueurs ont répondu à la deduction
            mPhaseEnCours = 3;
        else if (mNbJoueurPhaseDeduction > 0) // Phase déduction en cours
            mPhaseEnCours = 2;
        else if (mTourDeJeu >= 4 && mNbJoueurValides == mListeJoueurs.size())
            mPhaseEnCours = 2;
        else
            mPhaseEnCours = 1;

        // Activation bouton valider
        boolean activerBoutonValider;
        activerBoutonValider = !mMotValide || (mPhaseEnCours == 2 && aiJeToutesLesReponsesPhaseDeduction() && mTourDeJeu == 4);
        activeBoutonValider(activerBoutonValider);

        /* Affichage de l'état de la phase de jeu */
        TextView tv = findViewById(R.id.validation_joueurs);
        String textePhaseJeu = mListePhasesJeu[mPhaseEnCours - 1];
        switch (mPhaseEnCours) {
            case 1:
                textePhaseJeu += "  -  Joueurs : " + mNbJoueurValides + "/" + mListeJoueurs.size();
                break;
            case 2:
                textePhaseJeu += " - Joueurs : " + mNbJoueurPhaseDeduction + "/" + mListeJoueurs.size();
                break;
            case 3:
                int nbMortsApaise = 0;
                for (int value : mNbBonnesReponsesDeduction) {
                    if (value >= mListeJoueurs.size() - 1)
                        nbMortsApaise++;
                }
                textePhaseJeu += " : " + nbMortsApaise + " sur " + mListeCranes.size() + " apaisés";
                break;
        }
        tv.setText(textePhaseJeu);

    }

    private void RAZArdoise() {
        for (int value : mListeIdPersoArdoise) {
            TextView tv = findViewById(value);
            tv.setText("");
            tv.setTag("");
        }

        for (int value : mListeIdMot) {
            TextView tv = findViewById(value);
            tv.setText("");
            tv.setTag("");
        }
    }

    private void parseNoeudsDeduction(Document doc) {
        mNbBonnesReponsesDeduction = new int[mListeJoueurs.size()];
        Node NoeudTourDeJeu = getNoeudUnique(doc, "Deduction");

        int idCrane = 0;
        int idPerso = 0;
        mNbJoueurPhaseDeduction = 0;

        for (int i = 0; i < NoeudTourDeJeu.getChildNodes().getLength(); i++) { // Parcours tous les joueurs
            Node noeudJoueur = NoeudTourDeJeu.getChildNodes().item(i);
            Log.d("PGR-XML-Deduction", noeudJoueur.getNodeName());
            String pseudoDeduction = noeudJoueur.getAttributes().item(0).getNodeValue();
            mNbJoueurPhaseDeduction++;
            if (pseudoDeduction.equals(mPseudo))
                mTourDeJeu = 5;
            for (int j = 0; j < noeudJoueur.getChildNodes().getLength(); j++) { // Parcours tous les cranes
                Node noeudCrane = noeudJoueur.getChildNodes().item(j);
                Log.d("PGR-XML-Deduction", noeudCrane.getNodeName());
                for (int k = 0; k < noeudCrane.getAttributes().getLength(); k++) { // Parcours tous les attributs du noeud Crane
                    Log.d("PGR-XML-TourDeJeu", noeudCrane.getAttributes().item(k).getNodeName() + "_" + noeudCrane.getAttributes().item(k).getNodeValue());
                    switch (noeudCrane.getAttributes().item(k).getNodeName()) {
                        case "idCrane":
                            idCrane = Integer.parseInt(noeudCrane.getAttributes().item(k).getNodeValue());
                            break;
                        case "idPerso":
                            idPerso = Integer.parseInt(noeudCrane.getAttributes().item(k).getNodeValue());
                            break;
                    }
                }
                if (getCraneFromId(idCrane).getPersonnage().getId() == idPerso)
                    mNbBonnesReponsesDeduction[j]++;
            }
        }
    }

    private void afficheMots() {
        for (int i = 0; i < mListeIdMot.length; i++) {
            TextView tv = findViewById(mListeIdMot[i]);
            if (i < mListeCranes.size())
                tv.setText(mListeTourDeJeu.get(i + (3 * mListeJoueurs.size())).getMot());
            else
                tv.setText("");
        }

    }

    private void affichePersonnages() {
        for (int i = 0; i < mListeIdPersonnage.length; i++) {
            TextView tv = findViewById(mListeIdPersonnage[i]);
            String textePerso = mListePersonnages.get(i).getNom();
            if (!mListePersonnages.get(i).getContexte().isEmpty())
                textePerso += "\n (" + mListePersonnages.get(i).getContexte() + ")";
            tv.setText(textePerso);
            tv.setTag(mListePersonnages.get(i).getNom());
        }
    }

    private void activeLignesAvecMot() {
        // Rend clickable que les lignes ayant un mot
        for (int value = 0; value < mListeIdPersoArdoise.length; value++) {
            TextView tv = findViewById(mListeIdPersoArdoise[value]);
            if (value < mListeJoueurs.size())
                tv.setOnClickListener(this);
            else
                tv.setOnClickListener(null);
        }
    }

    private ArrayList<TourDeJeuCrane> parseNoeudsTourDeJeu(Document doc) {
        Node NoeudTourDeJeu = getNoeudUnique(doc, "TourDeJeu");

        int idCrane = 0;
        String mot = "";
        String pseudo = "";
        ArrayList<TourDeJeuCrane> listeTourDeJeu = new ArrayList<>();
        mNbJoueurValides = 0;

        for (int i = 0; i < NoeudTourDeJeu.getChildNodes().getLength(); i++) { // Parcours tous les tours
            Node noeudTour = NoeudTourDeJeu.getChildNodes().item(i);
            Log.d("PGR-XML-TourDeJeu", noeudTour.getNodeName());
            int numeroTour = Integer.parseInt(noeudTour.getAttributes().item(0).getNodeValue());
            for (int j = 0; j < noeudTour.getChildNodes().getLength(); j++) { // Parcours tous les mots
                Node noeudMot = noeudTour.getChildNodes().item(j);
                Log.d("PGR-XML-TourDeJeu", noeudMot.getNodeName());
                for (int k = 0; k < noeudMot.getAttributes().getLength(); k++) { // Parcours tous les attributs du noeud Mot
                    Log.d("PGR-XML-TourDeJeu", noeudMot.getAttributes().item(k).getNodeName() + "_" + noeudMot.getAttributes().item(k).getNodeValue());
                    switch (noeudMot.getAttributes().item(k).getNodeName()) {
                        case "mot":
                            mot = noeudMot.getAttributes().item(k).getNodeValue();
                            break;
                        case "idCrane":
                            idCrane = Integer.parseInt(noeudMot.getAttributes().item(k).getNodeValue());
                            break;
                        case "joueur":
                            pseudo = noeudMot.getAttributes().item(k).getNodeValue();
                            break;
                    }
                }
                if (!mot.isEmpty())
                    mNbJoueurValides++;
                TourDeJeuCrane tourDeJeuCrane = new TourDeJeuCrane(numeroTour, mot, new Crane(idCrane), pseudo);
                listeTourDeJeu.add(tourDeJeuCrane);
            }
            if (mNbJoueurValides == mListeJoueurs.size() && numeroTour < 4)
                mNbJoueurValides = 0;
        }

        return listeTourDeJeu;
    }

    private ArrayList<Crane> parseNoeudsCranes(Document doc) {
        Node NoeudPersonnages = getNoeudUnique(doc, "Cranes");

        int idCrane = 0;
        int idPerso = 0;
        ArrayList<Crane> listeCranes = new ArrayList<>();

        for (int i = 0; i < NoeudPersonnages.getChildNodes().getLength(); i++) { // Parcours tous les cranes
            Node noeudCrane = NoeudPersonnages.getChildNodes().item(i);
            Log.d("PGR-XML-Crane", noeudCrane.getNodeName());
            for (int j = 0; j < noeudCrane.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud Crane
                Log.d("PGR-XML-Crane", noeudCrane.getAttributes().item(j).getNodeName() + "_" + noeudCrane.getAttributes().item(j).getNodeValue());
                switch (noeudCrane.getAttributes().item(j).getNodeName()) {
                    case "id":
                        idCrane = Integer.parseInt(noeudCrane.getAttributes().item(j).getNodeValue());
                        break;
                    case "idPerso":
                        idPerso = Integer.parseInt(noeudCrane.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Crane crane = new Crane(idCrane, getPerso(idPerso));
            listeCranes.add(crane);
        }

        return listeCranes;
    }

    /**
     * Fonction qui permet de récupèrer un personnage à partir de son id
     *
     * @param idPerso : identifiant du personnage
     * @return : la structure complète du personnage
     */
    private Personnage getPerso(int idPerso) {
        Personnage perso = null;
        for (int i = 0; i < mListePersonnages.size(); i++) {
            if (mListePersonnages.get(i).getId() == idPerso)
                perso = mListePersonnages.get(i);
        }

        return perso;
    }

    /**
     * Fonction qui permet de récupérer le crane dont l'id est passé en paramètre
     *
     * @param idCrane : id du crane à récupérer
     * @return : la structure crane complète
     */
    private Crane getCraneFromId(int idCrane) {
        Crane crane = null;
        for (int i = 0; i < mListeCranes.size(); i++) {
            if (mListeCranes.get(i).getId() == idCrane)
                crane = mListeCranes.get(i);
        }

        return crane;
    }

    private ArrayList<Personnage> parseNoeudsPersonnage(Document doc) {
        Node NoeudPersonnages = getNoeudUnique(doc, "Personnages");

        int idPerso = 0;
        int niveau = 0;
        String nom = "";
        String contexte = "";
        ArrayList<Personnage> listePersonnages = new ArrayList<>();

        for (int i = 0; i < NoeudPersonnages.getChildNodes().getLength(); i++) { // Parcours tous les personnnages
            Node noeudPerso = NoeudPersonnages.getChildNodes().item(i);
            Log.d("PGR-XML-Personnage", noeudPerso.getNodeName());
            for (int j = 0; j < noeudPerso.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud Personnage
                Log.d("PGR-XML-Personnage", noeudPerso.getAttributes().item(j).getNodeName() + "_" + noeudPerso.getAttributes().item(j).getNodeValue());
                switch (noeudPerso.getAttributes().item(j).getNodeName()) {
                    case "id":
                        idPerso = Integer.parseInt(noeudPerso.getAttributes().item(j).getNodeValue());
                        break;
                    case "nom":
                        nom = noeudPerso.getAttributes().item(j).getNodeValue();
                        break;
                    case "contexte":
                        contexte = noeudPerso.getAttributes().item(j).getNodeValue();
                        break;
                    case "niveau":
                        niveau = Integer.parseInt(noeudPerso.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Personnage personnage = new Personnage(idPerso, nom, contexte, niveau);
            listePersonnages.add(personnage);
        }

        return listePersonnages;
    }

    private ArrayList<Joueur> parseNoeudsJoueur(Document doc) {
        Node NoeudJoueurs = getNoeudUnique(doc, "Joueurs");

        String pseudo = "";
        String equipe = "";
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        for (int i = 0; i < NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            int admin = 0;
            int scoreEquipe = 0;
            Node noeudCarte = NoeudJoueurs.getChildNodes().item(i);
            Log.d("PGR-XML-Joueur", noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Joueur", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "pseudo":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin":
                        admin = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(pseudo, equipe, scoreEquipe, admin);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    private Crane parseNoeudsMonCrane(Document doc) {
        Node noeudMonCrane = getNoeudUnique(doc, "MonCrane");

        int idPerso = 0;
        Crane monCrane = null;

        for (int j = 0; j < noeudMonCrane.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud MonCrane
            Log.d("PGR-XML-Crane", noeudMonCrane.getAttributes().item(j).getNodeName() + "_" + noeudMonCrane.getAttributes().item(j).getNodeValue());
            switch (noeudMonCrane.getAttributes().item(j).getNodeName()) {
                case "tourDeJeu":
                    mTourDeJeu = Integer.parseInt(noeudMonCrane.getAttributes().item(j).getNodeValue());
                    break;
                case "motValide":
                    mMotValide = noeudMonCrane.getAttributes().item(j).getNodeValue().equals("oui");
                    break;
                case "idPerso":
                    idPerso = Integer.parseInt(noeudMonCrane.getAttributes().item(j).getNodeValue());
                    break;
                case "mot":
                    mMot = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
            }
            monCrane = new Crane(getPerso(idPerso));
        }

        return monCrane;
    }

    private void afficheCraneEtArdoise(Crane monCrane) {
        String contexte = monCrane.getPersonnage().getContexte();

        ConstraintLayout crane = findViewById(R.id.crane);
        LinearLayout ardoise = findViewById(R.id.ardoise);
        LinearLayout personnages = findViewById(R.id.personnages);
        LinearLayout clavier = findViewById(R.id.clavier);
        HorizontalScrollView cranesReponses = findViewById(R.id.liste_cranes_reponses);
        switch (mPhaseEnCours) {
            case 1: // Saisie des mots
                crane.setVisibility(View.VISIBLE);
                clavier.setVisibility(View.VISIBLE);
                ardoise.setVisibility(View.GONE);
                personnages.setVisibility(View.GONE);
                cranesReponses.setVisibility(View.GONE);
                if (!contexte.isEmpty())
                    contexte = "(" + contexte + ")";

                if (!mMotValide)
                    mTourDeJeu--;

                // On n'affiche le personnage que si le joueur n'a pas encore écrit de mot au premier tour
                if (!mMotValide && mTourDeJeu == 0) {
                    mNomPersonnage.setText(monCrane.getPersonnage().getNom());
                    mContextePersonnage.setText(contexte);
                    mNomPersonnage.setVisibility(View.VISIBLE);
                    mContextePersonnage.setVisibility(View.VISIBLE);
                    mZoneSaisie.setHint(R.string.ecrit_un_mot);
                    mImageCrane.setImageResource(R.drawable.fiesta_crane_ouvert);
                } else {
                    mZoneSaisie.setHint(mMot);
                    mNomPersonnage.setVisibility(View.INVISIBLE);
                    mContextePersonnage.setVisibility(View.INVISIBLE);
                }
                afficheCrane();
                mBoutonValider.setVisibility(View.VISIBLE);
                if (jeSuisAdmin())
                    mBoutonInitialiser.setVisibility(View.VISIBLE);
                else
                    mBoutonInitialiser.setVisibility(View.GONE);
                break;
            case 2: // Deduction
                crane.setVisibility(View.GONE);
                clavier.setVisibility(View.GONE);
                ardoise.setVisibility(View.VISIBLE);
                personnages.setVisibility(View.VISIBLE);
                cranesReponses.setVisibility(View.GONE);
                mBoutonValider.setVisibility(View.VISIBLE);
                mBoutonInitialiser.setVisibility(View.GONE);
                break;
            case 3: // Affichage des resultats
                crane.setVisibility(View.GONE);
                clavier.setVisibility(View.GONE);
                ardoise.setVisibility(View.VISIBLE);
                personnages.setVisibility(View.GONE);
                cranesReponses.setVisibility(View.VISIBLE);
                mBoutonValider.setVisibility(View.GONE);
                if (jeSuisAdmin())
                    mBoutonInitialiser.setVisibility(View.VISIBLE);
                else
                    mBoutonInitialiser.setVisibility(View.GONE);
                afficheResultats();
                break;
        }
    }

    private boolean jeSuisAdmin() {
        boolean reponse = false;
        for (int i = 0; i < mListeJoueurs.size(); i++) {
            if (mListeJoueurs.get(i).getNomJoueur().equals(mPseudo))
                reponse = mListeJoueurs.get(i).getAdmin() == 1;
        }
        return reponse;
    }

    private void afficheResultats() {
        for (int i = 0; i < mListeIdCranesResultat.length; i++) {
            ConstraintLayout crane = findViewById(mListeIdCranesResultat[i]);
            ImageView iv = findViewById(mListeIdImageCranesResultat[i]);
            TextView tvPerso = findViewById(mListeIdNomPersoResultat[i]);
            TextView tvContexte = findViewById(mListeIdContextePersoResultat[i]);
            TextView tvSaisie = findViewById(mListeIdZoneSaisieResultat[i]);
            if (i < mListeJoueurs.size()) {
                crane.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
                tvPerso.setVisibility(View.VISIBLE);
                tvContexte.setVisibility(View.VISIBLE);
                tvSaisie.setVisibility(View.VISIBLE);
                if (mNbBonnesReponsesDeduction[i] >= mListeJoueurs.size() - 1) {
                    iv.setImageResource(R.drawable.fiesta_crane_ferme);
                    tvPerso.setText("");
                    tvContexte.setText("");
                    tvSaisie.setText(mListeCranes.get(i).getPersonnage().getNom());
                } else {
                    iv.setImageResource(mListeImageCranesOuvert[mNbBonnesReponsesDeduction[i]]);
                    tvPerso.setText(mListeCranes.get(i).getPersonnage().getNom());
                    tvContexte.setText(mListeCranes.get(i).getPersonnage().getContexte());
                    tvSaisie.setText("");
                }

            } else {
                iv.setVisibility(View.GONE);
                crane.setVisibility(View.GONE);
                tvPerso.setVisibility(View.GONE);
                tvContexte.setVisibility(View.GONE);
                tvSaisie.setVisibility(View.GONE);
            }
        }
    }

    private void afficheCrane() {
        switch (mTourDeJeu) {
            case 1:
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ferme_1);
                break;
            case 2:
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ferme_2);
                break;
            case 3:
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ferme_3);
                break;
            case 4:
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ferme_4);
                break;
            default:
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ouvert);
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TacheGetInfoFiesta extends AsyncTask<String, Void, Document> {

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
            if (doc != null)
                parseXML(doc);
            super.onPostExecute(doc);
        }
    }

}
