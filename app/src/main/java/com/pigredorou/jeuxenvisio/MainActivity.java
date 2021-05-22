package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Jeu;
import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Salon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.pigredorou.jeuxenvisio.JeuEnVisioActivity.getNoeudUnique;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 1.02 : Version finale The Crew
     * 1.10 : Ajout du choix d'un jeu (seul jeu dispo : The Crew)
     * 1.11 : Gestion de plusieurs jeux en // avec la notion de partie
     * 1.12 : The Crew : amélioration ergonomie - Belote : début implémentation
     * 1.13 : The Crew : correction bug double clic + ajout double clic pour sélection tache
     * 1.14 : Implémentation échange jeu ou 1 carte + Belote : affichage carte atout à choisir
     * 1.15 : Correction double clic
     * 1.16 : Correction distribution si nombre de carte pas équitable + ajout de plus de salons + correction double clic + The Crew : implémentation Zone de silence
     * 2.00 : The Crew - Implementation XML + refresh complet toutes les secondes
     * 2.01 : The Crew - Correction affichage tâches réalisées + blocage communication avant le pli autorisé
     * 2.02 : Belote : Implémentation XML + Gestion du chois de l'atout (1er et 2ème tour)
     * 2.03 : Belote : Ajout de l'affichage de l'historique des plis + score + joueur qui remporte le pli
     * 2.04 : Belote : Ajout de l'affichage des scores
     * 2.05 : The Crew : Annulation dernière carte posée sur double clic (uniquement par le joueur qui l'a posé)
     * 2.06 : Belote : Gestion tour suivant (changement premier joueur + maj score)
     * 2.07 : Belote : Correction bug refresh histo + couleur choix 2ème tour + The Crew : début implémentation jeton détresse
     * 2.08 : Belote : Annulation dernière carte posée sur double clic (uniquement par le joueur qui l'a posé) + seul le joueur qui remporte le pli peut jouer le pli suivant
     * 2.09 : Belote : Gestion de la "coupe" pour les distributions et de la "goutière" pour la fin de distribution (on ne mélange pas les cartes entre les manches)
     * 2.10 : Fiesta de los muertos : début implémentation
     * 2.11 : Fiesta de los muertos : implémentation lecture XML + saisie mot (4x)
     * 2.12 : Fiesta de los muertos : implémentation v1 terminée
     * 2.13 : Fiesta de los muertos : ajout initialisation du jeu + Main : option admin affichées selon le jeu
     * 2.14 : Fiesta de los muertos : amélioration de l'affichage des résultats
     * 2.15 : Fiesta de los muertos : ajout du nom des joueurs précédent et suivants pour la première phase
     * 2.16 : Fiesta de los muertos : Correction bugs sur l'affichage des résultats
     * 2.17 : Fiesta de los muertos : Correction couleur nom perso deduction (si plusieurs parties de suite) + Manchots barjot : test de drag&drop pour The Crew
     * 2.18 : Fiesta de los muertos : Phase déduction, on grise les personnages déjà placés + scroll sur ardoise pour petits écrans
     * 2.19 : The Crew : Gestion de la distribution des tâches 1 à 1 (Ajout sans suppression)
     * 2.20 : Fiesta de los muertos : Correction affichage des mots à déduire et de la couleur des personnages sélectionnés
     * 2.21 : Fiesta de los muertos : Ajout d'un sablier + correction couleurs des noms de perso en phase 2 (déduction)
     * 2.22 : TopTen : Ajout du jeu
     * 2.23 : TopTen : Correction bouton manche suivante désativé à tort + nombre caca en gras
     * 3.0.0 : The Crew : Drag & drop pour jouer les cartes + Gambit 7 + Ajout préférence + Refonte page accueil + Détection mise à jour application
     * 3.0.16 : Affichage des joueurs qui n'ont pas encore installé l'application
     * 3.0.17 : Correctif Fiesta (bug affichage du nb de joueur et des résultats) + bug joueur admin + init activité Majority à vide
     * 3.0.18 : Correctif numero mission pour The Crew
     * 3.0.19 : Ajout image de fond pour accueil, Crew et Fiesta
     * 3.0.20 : Préférences : implémentation suppression pseudo et relance auto de l'application
     * 3.0.21 : The Crew implementation drag&drop cartes + annulation et tâches via sélection + touch
     * 3.0.22 : Creation Majority + Timeline, début mise en page de Majority
     * 3.0.23 : Amélioration chargement et gestion des pertes de connection
     * 3.0.24 : Ajout des salons et des joueurs de chaque dans les préférences
     * 3.0.25 : Implémentation majority (parse XML)
     * 3.0.26 : Implémentation classe de jeu générique
     * 3.0.27 : Migration topTen via la classe JeuEnVisioActivity
     * 3.0.28 : Migration majority la classe JeuEnVisioActivity + implémentation résultats vote + tour/manche suivant
     * 3.0.29 : Début mise en forme Just One
     * 3.0.30 : Améliorations mise en page (accueil, timeline et just one)
     * 3.1.00 : Majority terminée (v1)
     * 3.1.01 : Timeline -> Mise en page
     * 3.1.02 : Migration vers classe générique de tous les jeux (The Crew + Fiesta + Roi) + Suppression classe outils
     * 3.1.03 : Correctif à chaud
     * 3.1.04 : Correctif Majority + début implémentation timeline
     * 3.1.05 : Majority ajout du classement en fin de partie
     * 3.1.06 : Majority ajout sélection via mots + bouton partie suivante + Gestion des ex-aequos dans le classement
     * 3.1.07 : Corrections bugs : TheCrew double mise à jour auto - TopTen dévoiler la carte
     * 3.1.08 : Début implémentation administration
     * 3.1.09 : Correctif The Crew + implémentation administration
     * 3.1.10 : Implémentation JustOne en cours
     * 3.2.00 : JustOne V1
     * 3.2.01 : Fiesta de los muertos - Ajout nom perso en phase réponses + gestion aléatoire des joueurs précédents et suivants
     * 3.3.00 : Whaaat v1
     * 3.3.01 : Fiesta de los muertos - Correction nom perso en phase réponses + Whaaat - Ajout du joueur actif
     */
    // Variables statiques
    private static final String mNumVersion = "3.3.01";
    public static final String url = "http://julie.et.pierre.free.fr/Salon/";
    public static final String urlGetVersion = url + "getVersion.php";
    public static final String urlGetJoueurs = url + "getJoueurs.php";
    public static final String urlValideJoueur = url + "valideJoueur.php?joueur=";
    public static final String urlDistribueCartes = url + "distribueCartes.php?partie=";
    public static final String urlAnnulCarte = url + "annulCarte.php?partie=";
    public static final String urlInitFiesta = url + "initFiesta.php?partie=";
    public static final String urlInitTopTen = url + "initTopTen.php?partie=";
    public static final String urlInitMajority = url + "initMajority.php?partie=";
    public static final String urlInitJustOne = url + "initJustOne.php?partie=";
    public static final String urlInitWhaaat = url + "initWhaaat.php?partie=";
    public static final String urlNewJoueur = url + "newJoueur.php?joueur=";
    public static final String urlMajAdmin = url + "majAdminJoueur.php?joueur=";
    public static final String KEY_PREFERENCES = "MesPreferences";
    public static final String VALEUR_PSEUDO = "Pseudo";
    public static final String VALEUR_ADMIN = "Admin";
    public static final String VALEUR_ID_SALON = "idSalon";
    public static final String VALEUR_ID_PARTIE = "idPartie";
    public static final String VALEUR_ID_JOUEUR = "idJoueur";
    public static final String VALEUR_NOM_SALON = "NomSalon";
    public static final String VALEUR_METHODE_SELECTION = "MethodeSelection";
    private static final String urlGetSalonsFromJeux = url + "getSalonsFromJeu.php?joueur=";
    private static final String urlGetJeux = url + "getJeux.php?joueur=";
    private static final String urlRAZDistribution = url + "RAZDistribution.php?partie=";
    private static final String urlDistribueTaches = url + "distribueTaches.php?partie=";
    private static final String urlMAJNumMission = url + "majNumeroMission.php?partie=";
    private static final String urlEchangeCarte = url + "echangeCarte.php?partie=";
    private static final String urlVerifInit = url + "verifInitJeux.php?partie=";
    private static final int[] tableIdLigneSalon = {R.id.ligne_salon1, R.id.ligne_salon2, R.id.ligne_salon3, R.id.ligne_salon4, R.id.ligne_salon5, R.id.ligne_salon6, R.id.ligne_salon7, R.id.ligne_salon8};
    private static final int[] tableIdImageSalon = {R.id.image_salon1, R.id.image_salon2, R.id.image_salon3, R.id.image_salon4, R.id.image_salon5, R.id.image_salon6, R.id.image_salon7, R.id.image_salon8};
    private static final int[] tableIdNomSalon = {R.id.salon_text_1, R.id.salon_text_2, R.id.salon_text_3, R.id.salon_text_4, R.id.salon_text_5, R.id.salon_text_6, R.id.salon_text_7, R.id.salon_text_8};
    private static final int[] tableIdImageJeux = {R.id.jeu_1, R.id.jeu_2, R.id.jeu_3, R.id.jeu_4, R.id.jeu_5, R.id.jeu_6, R.id.jeu_7, R.id.jeu_8, R.id.jeu_9, R.id.jeu_10};
    private static final int[] tableIdResourceImageJeux = {0, R.drawable.the_crew, R.drawable.fiesta_de_los_muertos, R.drawable.le_roi_des_nains, R.drawable.gambit7, R.drawable.belote, R.drawable.top_ten, R.drawable.majority, R.drawable.timeline, R.drawable.just_one, R.drawable.whaaat};
    public static final int THE_CREW_ACTIVITY_REQUEST_CODE = 11;
    public static final int FIESTA_MUERTOS_ACTIVITY_REQUEST_CODE = 12;
    public static final int ROI_NAINS_ACTIVITY_REQUEST_CODE = 13;
    public static final int MANCHOTS_BARJOTS_ACTIVITY_REQUEST_CODE = 14;
    public static final int BELOTE_ACTIVITY_REQUEST_CODE = 15;
    public static final int TOPTEN_ACTIVITY_REQUEST_CODE = 16;
    public static final int MAJORITY_ACTIVITY_REQUEST_CODE = 17;
    public static final int TIMELINE_ACTIVITY_REQUEST_CODE = 18;
    public static final int JUSTONE_ACTIVITY_REQUEST_CODE = 19;
    public static final int WHAAAT_ACTIVITY_REQUEST_CODE = 20;
    public static final int ADMINISTRATION_REQUEST_CODE = 50;
    public static final int mSelectionDoucleClic = 1;
    public static final int mSelectionDragAndDrop = 2;
    //public static final int mSelectionDragAndDropLongTouch = 3;
    private static final int mIdTheCrew = 1;
    private static final int mIdFiestaDeLosMuertos = 2;
    private static final int mIdLeRoiDesNains = 3;
    private static final int mIdGambit7 = 4;
    private static final int mIdBelote = 5;
    private static final int mIdTopTen = 6;
    private static final int mIdMajority = 7;
    private static final int mIdTimeLine = 8;
    private static final int mIdJustOne = 9;
    private static final int mIdWhaaat = 10;

    // Chargement de l'application
    private Thread t;
    private int mNbChargement;
    private int mEtapeChargement;
    private final int mNbTentativeMax = 15;
    private boolean mChargementOK = false;
    //private String mMessageErreur;
    private TextView mTexteChargement;
    private TextView mTexteNouvelleVersion;
    private String mNumeroVersionDispo;
    private String mUrlNewVersion;

    // Variables globales - contexte
    private int mIdSalon; // Id du salon (en BDD)
    private int mIdPartie; // Id de la partie (en BDD)
    private int mIdJeu; // Id du jeu (en BDD)
    private int mIndexSalon; // Index du salon (numéro de la liste)
    private int mIdJoueur; // Index du salon (numéro de la liste)
    private int mAdmin; // Suis-je admin ?
    private String mPseudo;
    private boolean mSalonChoisi = false;
    private boolean mJeuChoisi = false;
    // Administration
    private Button mBoutonRAZ;
    private Button mBoutonDistribueCartes;
    private Button mBoutonDistribueTache;
    private Button mBoutonOptionTacheAjout;
    private boolean mOptionAjout;
    private TextView mNbTaches;
    private TableLayout mOptionTaches;
    private LinearLayout mLigneNbTaches;
    private Button mBoutonOptionTache1;
    private Button mBoutonOptionTache2;
    private Button mBoutonOptionTache3;
    private Button mBoutonOptionTache4;
    private Button mBoutonOptionTache5;
    private Button mBoutonOptionTache6;
    private Button mBoutonOptionTache7;
    private Button mBoutonOptionTache8;
    private Button mBoutonOptionTache9;
    private Button mBoutonOptionTache10;
    // Mission
    private LinearLayout mLigneNumMission;
    private TextView mNumMission;
    private Button mBoutonMissionSuivante;
    // Autres options
    private Button mBoutonEchangeCarte;
    private Button mBoutonEchangeJeu;
    // Administration
    private Button mBoutonAdministration;

    // Listes
    private SharedPreferences mPreferences;
    private ArrayList<Salon> mListeSalons;
    private ArrayList<Joueur> mListeJoueurs;
    private ArrayList<Jeu> mListeJeux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Charge le layout
        setContentView(R.layout.activity_main);

        // Affiche le message de chargement
        findViewById(R.id.chargement).setVisibility(View.VISIBLE);
        mTexteChargement = findViewById(R.id.texteChargement);

        // On recupère les préférences du joueur
        mPreferences = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
        mIdSalon = mPreferences.getInt(VALEUR_ID_SALON, 1);
        mIdJoueur = mPreferences.getInt(VALEUR_ID_JOUEUR, 0);
        mAdmin = mPreferences.getInt(VALEUR_ADMIN, 0);
        mPseudo = mPreferences.getString(VALEUR_PSEUDO, "");
        //String nomSalon = mPreferences.getString(VALEUR_NOM_SALON, "");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Entete
        TextView version1 = findViewById(R.id.version);
        String version = version1.getText() + " " + mNumVersion;
        version1.setText(version);
        // Bouton quitter
        ImageView boutonQuitter = findViewById(R.id.bouton_quitter);
        boutonQuitter.setOnClickListener(this);
        // Pseudo
        TextView tvPseudo = findViewById(R.id.pseudo);
        tvPseudo.setText(mPseudo);
        tvPseudo.setOnClickListener(this);

        // Nouvelle version
        mTexteNouvelleVersion = findViewById(R.id.newVersion);
        mTexteNouvelleVersion.setOnClickListener(this);

        // Liste des joueurs du salon
        mNbChargement = 0;
        mEtapeChargement = 0;
        startChargementBDD();

        // Charge les élements des jeux
        chargementJeux();

        // Masque les salons
        findViewById(R.id.bloc_salons).setVisibility(View.GONE);

        // Bouton valider
        Button boutonValider = findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(this);

        // Options
        chargementOptions();
    }

    private void postChargementBDD() {
        if (mPseudo.isEmpty()) {
            findViewById(R.id.bloc_joueurs).setVisibility(View.VISIBLE);
            findViewById(R.id.bloc_salons).setVisibility(View.GONE);
            findViewById(R.id.tableau_jeux).setVisibility(View.GONE);
        } else {
            if (mIdJoueur == 0)
                mIdJoueur = getIdJoueurFromPseudo(mListeJoueurs);
            findViewById(R.id.bloc_joueurs).setVisibility(View.GONE);
        }
        findViewById(R.id.chargement).setVisibility(View.GONE);
        t.interrupt();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        // if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chargementJeux() {
        for (int idImageJeux : tableIdImageJeux) {
            ImageView iv = findViewById(idImageJeux);
            iv.setOnClickListener(this);
            iv.setTag("NO");
        }
    }

    private void chargementBoutonsDistribution() {
        mBoutonRAZ = findViewById(R.id.boutonRAZ);
        mBoutonDistribueCartes = findViewById(R.id.boutonDistribue);
        mBoutonRAZ.setOnClickListener(this);
        mBoutonDistribueCartes.setOnClickListener(this);
        mBoutonRAZ.setVisibility(View.GONE);
        mBoutonDistribueCartes.setVisibility(View.GONE);
    }

    private void chargementDesTaches() {
        mBoutonDistribueTache = findViewById(R.id.boutonDistribueTache);
        mBoutonOptionTacheAjout = findViewById(R.id.boutonOptionTacheAjout);
        // Taches
        Button boutonTachesMoins = findViewById(R.id.boutonNbTacheMoins);
        Button boutonTachesPlus = findViewById(R.id.boutonNbTachePlus);
        mOptionTaches = findViewById(R.id.option_taches);
        mNbTaches = findViewById(R.id.nbTache);
        mLigneNbTaches = findViewById(R.id.ligne_nb_taches);
        mBoutonOptionTache1 = findViewById(R.id.option_tache_1);
        mBoutonOptionTache2 = findViewById(R.id.option_tache_2);
        mBoutonOptionTache3 = findViewById(R.id.option_tache_3);
        mBoutonOptionTache4 = findViewById(R.id.option_tache_4);
        mBoutonOptionTache5 = findViewById(R.id.option_tache_5);
        mBoutonOptionTache6 = findViewById(R.id.option_tache_6);
        mBoutonOptionTache7 = findViewById(R.id.option_tache_7);
        mBoutonOptionTache8 = findViewById(R.id.option_tache_8);
        mBoutonOptionTache9 = findViewById(R.id.option_tache_9);
        mBoutonOptionTache10 = findViewById(R.id.option_tache_10);
        mBoutonDistribueTache.setOnClickListener(this);
        mBoutonOptionTacheAjout.setOnClickListener(this);
        boutonTachesMoins.setOnClickListener(this);
        boutonTachesPlus.setOnClickListener(this);
        mBoutonOptionTache1.setOnClickListener(this);
        mBoutonOptionTache2.setOnClickListener(this);
        mBoutonOptionTache3.setOnClickListener(this);
        mBoutonOptionTache4.setOnClickListener(this);
        mBoutonOptionTache5.setOnClickListener(this);
        mBoutonOptionTache6.setOnClickListener(this);
        mBoutonOptionTache7.setOnClickListener(this);
        mBoutonOptionTache8.setOnClickListener(this);
        mBoutonOptionTache9.setOnClickListener(this);
        mBoutonOptionTache10.setOnClickListener(this);
        mBoutonDistribueTache.setVisibility(View.GONE);
        mBoutonOptionTacheAjout.setVisibility(View.GONE);
        mLigneNbTaches.setVisibility(View.GONE);
        mOptionTaches.setVisibility(View.GONE);
    }

    private void chargementOptions() {
        // Gestion de la distribution des cartes
        chargementBoutonsDistribution();

        // Distributions des tâches
        chargementDesTaches();

        // Mission
        chargementMissions();

        // Autre option
        chargementAutreOptions();

        // Administration
        mBoutonAdministration = findViewById(R.id.administration);
        mBoutonAdministration.setOnClickListener(this);
        mBoutonAdministration.setVisibility(View.GONE);
        afficheOptionAdmin();
    }

    private void chargementAutreOptions() {
        mBoutonEchangeCarte = findViewById(R.id.boutonEchangeCarte);
        mBoutonEchangeJeu = findViewById(R.id.boutonEchangeJeu);
        mBoutonEchangeCarte.setOnClickListener(this);
        mBoutonEchangeJeu.setOnClickListener(this);
        mBoutonEchangeCarte.setVisibility(View.GONE);
        mBoutonEchangeJeu.setVisibility(View.GONE);
    }

    private void chargementMissions() {
        mLigneNumMission = findViewById(R.id.ligne_num_mission);
        mBoutonMissionSuivante = findViewById(R.id.boutonValiderNumMission);
        mNumMission = findViewById(R.id.numMission);
        mBoutonMissionSuivante.setOnClickListener(this);
        mLigneNumMission.setVisibility(View.GONE);
        mBoutonMissionSuivante.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        String nbCarte = "";

        if (v.getTag() != null)
            switch (v.getTag().toString()) {
                case "bouton_quitter":
                    // Quitte l'application
                    finish();
                    break;

                case "newVersion":
                    // Lien vers le téléchargement de la nouvelle version
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mUrlNewVersion));
                    startActivity(intent);
                    break;

                case "boutonValider":
                    // Si 1 seul jeu ou un 1 seul salon, on le sélectionne automatiquement
                    selectionAuto();
                    // Si un jeu et un salon ne sont pas sélectionné, on ne lance pas le jeu
                    if (!verificationSelection())
                        break;
                    if (!verificationJeuInitialise())
                        break;
                    // Lancer le jeu dans le salon pour le joueur demandé
                    lancerJeu();
                    break;

                // Remise à zéro de la dernière distribution
                case "boutonRAZ":
                    // Si 1 seul jeu ou un 1 seul salon, on le sélectionne automatiquement
                    selectionAuto();
                    switch (mIdJeu) {
                        case mIdTheCrew:
                        case mIdBelote:
                            new TacheURLSansRetour().execute(urlRAZDistribution + mIdPartie);
                            Toast.makeText(this, "Distribution réinitialisée", Toast.LENGTH_SHORT).show();
                            break;
                        case mIdFiestaDeLosMuertos:
                            new TacheURLSansRetour().execute(urlInitFiesta + mIdPartie);
                            Toast.makeText(this, "Personnages attribués", Toast.LENGTH_SHORT).show();
                            break;
                        case mIdTopTen:
                            new TacheURLSansRetour().execute(urlInitTopTen + mIdPartie);
                            Toast.makeText(this, "Partie initialisée", Toast.LENGTH_SHORT).show();
                            break;
                        case mIdMajority:
                            new TacheURLSansRetour().execute(urlInitMajority + mIdPartie);
                            Toast.makeText(this, "Partie initialisée", Toast.LENGTH_SHORT).show();
                            break;
                        case mIdJustOne:
                            new TacheURLSansRetour().execute(urlInitJustOne + mIdPartie);
                            Toast.makeText(this, "Partie initialisée", Toast.LENGTH_SHORT).show();
                            break;
                        case mIdWhaaat:
                            new TacheURLSansRetour().execute(urlInitWhaaat + mIdPartie);
                            Toast.makeText(this, "Partie initialisée", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

                // Distribue les cartes dans le salon sélectionné
                case "boutonDistribue":
                    int typeCarte;
                    String nbCarteParJoueur;
                    String belote;
                    switch (mIdJeu) {
                        case mIdTheCrew:
                        default:
                            typeCarte = 1;
                            nbCarteParJoueur = "";
                            belote = "";
                            break;
                        case mIdBelote:
                            typeCarte = 4;
                            nbCarteParJoueur = "5";
                            belote = "Table";
                            break;
                    }
                    new TacheURLSansRetour().execute(urlDistribueCartes + mIdPartie + "&typeCarte=" + typeCarte + "&nbCarteParJoueur=" + nbCarteParJoueur + "&belote=" + belote);
                    Toast.makeText(this, "Distribution terminée", Toast.LENGTH_SHORT).show();
                    break;

                // Met à jour la liste des jeux via clic sur le pseudo
                case "pseudo":
                    new TacheGetXML().execute(urlGetJeux + mPseudo);
                    break;

                // Lance l'activité qui sert pour administrer
                case "administration":
                    // Lance l'activité du jeu demandé avec les paramètres
                    Intent administrationActivity = new Intent(MainActivity.this, AdministrationActivity.class);
                    startActivityForResult(administrationActivity, ADMINISTRATION_REQUEST_CODE);
                    break;

                default:
                    // Sélectionne un joueur
                    if (v.getTag().toString().startsWith("pseudo_")) {
                        String[] tag = v.getTag().toString().split("_");
                        mPseudo = mListeJoueurs.get(Integer.parseInt(tag[1])).getNomJoueur();
                        mIdJoueur = mListeJoueurs.get(Integer.parseInt(tag[1])).getId();
                        new TacheGetXML().execute(urlGetJeux + mPseudo);
                        // Sauvegarde le joueur
                        mPreferences.edit().putString(VALEUR_PSEUDO, mPseudo).apply();
                        mPreferences.edit().putInt(VALEUR_ID_JOUEUR, mIdJoueur).apply();
                        mPreferences.edit().putInt(VALEUR_ADMIN, mAdmin).apply();
                        // Masque les joueurs, affiche les jeux
                        findViewById(R.id.bloc_joueurs).setVisibility(View.GONE);
                        findViewById(R.id.tableau_jeux).setVisibility(View.VISIBLE);
                        // Valide le joueur en base
                        new TacheURLSansRetour().execute(urlValideJoueur + mPseudo);
                        // Met à jour le titre
                        TextView tvPseudo = findViewById(R.id.pseudo);
                        tvPseudo.setText(mPseudo);
                    }
            }

        switch (v.getId()) {
            // TODO : basculer les id vers les tags

            // Sélection d'un salon
            case R.id.ligne_salon1:
            case R.id.ligne_salon2:
            case R.id.ligne_salon3:
            case R.id.ligne_salon4:
            case R.id.ligne_salon5:
            case R.id.ligne_salon6:
            case R.id.ligne_salon7:
            case R.id.ligne_salon8:
                int indexSalon = Integer.parseInt(v.getTag().toString());
                afficheSalonEnBlanc(indexSalon);
                afficheOptionAdmin();
                mSalonChoisi = true;
                // Mise à jour du numéro de mission
                TextView tv = findViewById(R.id.numMission);
                tv.setText(String.valueOf(mListeSalons.get(indexSalon).getNumMission()));
                break;

            // Jeux
            case R.id.jeu_1:
            case R.id.jeu_2:
            case R.id.jeu_3:
            case R.id.jeu_4:
            case R.id.jeu_5:
            case R.id.jeu_6:
            case R.id.jeu_7:
            case R.id.jeu_8:
            case R.id.jeu_9:
            case R.id.jeu_10:
                afficheJeux(mListeJeux);
                ImageView iv = findViewById(v.getId());
                // Sélectionne le jeu
                iv.setBackgroundColor(getResources().getColor(R.color.grisTransparent));
                // TODO : mettre "jeu_x" avec l'ID du jeu en tag pour le retrouver via le tag
                iv.setTag("YES");
                int index = 0;
                for (int i = 0; i < tableIdImageJeux.length; i++) {
                    if (tableIdImageJeux[i] == iv.getId()) {
                        index = i;
                        break;
                    }
                }
                if (mListeJeux != null) {
                    // Sauvegarde le contexte
                    mIdJeu = mListeJeux.get(index).getId();
                    mJeuChoisi = true;
                    mSalonChoisi = false;
                }
                // Affiches les salons associés
                new TacheGetXML().execute(urlGetSalonsFromJeux + mPseudo + "&jeu=" + mIdJeu);
                afficheOptionAdmin();
                break;

            // Distribue les n tâches dans la salon sélectionné
            case R.id.boutonDistribueTache:
                String[] optionDemandees = checkOptionsTaches();
                String url = urlDistribueTaches + mIdPartie + "&nbTache=" + mNbTaches.getText();
                url += "&option1=" + optionDemandees[0] + "&option2=" + optionDemandees[1] + "&option3=" + optionDemandees[2] + "&option4=" + optionDemandees[3] + "&option5=" + optionDemandees[4];
                if (mOptionAjout)
                    url += "&ajout=oui";
                new TacheURLSansRetour().execute(url);
                Toast.makeText(this, "Distribution terminée", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boutonNbTacheMoins:
                if (!mNbTaches.getText().toString().equals("0"))
                    mNbTaches.setText(String.valueOf(Integer.parseInt(mNbTaches.getText().toString()) - 1));
                break;

            case R.id.boutonNbTachePlus:
                if (Integer.parseInt(mNbTaches.getText().toString()) < 20)
                    mNbTaches.setText(String.valueOf(Integer.parseInt(mNbTaches.getText().toString()) + 1));
                break;

            // Incrémente le numéro de mission
            case R.id.boutonValiderNumMission :
                int numMission = Integer.parseInt(mNumMission.getText().toString())+1;
                mNumMission.setText(String.valueOf(numMission));
                new TacheURLSansRetour().execute(urlMAJNumMission+mIdPartie+"&numMission="+numMission);
                Toast.makeText(this, "Mission mise à jour", Toast.LENGTH_SHORT).show();
                break;

            // Sélection d'une option d'une tâche
            case R.id.option_tache_1 :
            case R.id.option_tache_2 :
            case R.id.option_tache_3 :
            case R.id.option_tache_4 :
            case R.id.option_tache_5 :
            case R.id.option_tache_6 :
            case R.id.option_tache_7 :
            case R.id.option_tache_8 :
            case R.id.option_tache_9 :
            case R.id.option_tache_10:
            case R.id.boutonOptionTacheAjout:
                Button b = findViewById(v.getId());
                if (v.getTag().equals("NO")) {
                    b.setTextColor(getResources().getColor(R.color.blanc));
                    b.setTag("YES");
                } else {
                    b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    b.setTag("NO");
                }
                break;

            case R.id.boutonEchangeCarte:
                nbCarte = "1";
            case R.id.boutonEchangeJeu:
                new TacheURLSansRetour().execute(urlEchangeCarte + mIdPartie + "&nbCarte=" + nbCarte);
                Toast.makeText(this, "Action terminée", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean verificationJeuInitialise() {
        boolean verifOK = false;
        String result = "";

        try {
            URL url = new URL(urlVerifInit + mIdPartie);
            // Lecture du flux
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String stringBuffer;
            String string = "";
            while ((stringBuffer = bufferedReader.readLine()) != null) {
                string = String.format("%s%s", string, stringBuffer);
            }
            bufferedReader.close();
            result = string;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.isEmpty())
            verifOK = true;
        else
            Toast.makeText(this, "Patiente, le jeu n'est pas encore initialisé", Toast.LENGTH_SHORT).show();

        return verifOK;
    }

    private void selectionAuto() {
        if (mListeJeux.size() == 1) {
            mIdJeu = mListeJeux.get(0).getId();
            mJeuChoisi = true;
        }
        if (mListeSalons == null) {
            mSalonChoisi = true;
            mIndexSalon = 0;
        } else if (mListeSalons.size() == 1) {
            mIdPartie = mListeSalons.get(0).getIdPartie();
            mSalonChoisi = true;
            mIndexSalon = 0;
        }
    }

    private boolean verificationSelection() {
        boolean verifOK = true;
        if (!mJeuChoisi) {
            Toast.makeText(this, "Il faut choisir un jeu", Toast.LENGTH_SHORT).show();
            verifOK = false;
        }
        if (!mSalonChoisi) {
            Toast.makeText(this, "Il faut choisir un salon de jeu", Toast.LENGTH_SHORT).show();
            verifOK = false;
        }
        return verifOK;
    }

    private void lancerJeu() {
        if (mListeSalons == null) {
            Toast.makeText(this, "Erreur au lancement du jeu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarde des préférences
        String nom_salon = mListeSalons.get(mIndexSalon).getNom();
        mPreferences.edit().putString(VALEUR_PSEUDO, mPseudo).apply();
        mPreferences.edit().putInt(VALEUR_ADMIN, mAdmin).apply();
        mPreferences.edit().putInt(VALEUR_ID_SALON, mIdSalon).apply();
        mPreferences.edit().putString(VALEUR_NOM_SALON, nom_salon).apply();

        // Lance le jeu choisi
        Intent JeuActivity = null;
        int REQUEST_CODE = 0;
        switch (mIdJeu) {
            case mIdTheCrew:
                JeuActivity = new Intent(MainActivity.this, TheCrewActivity.class);
                REQUEST_CODE = THE_CREW_ACTIVITY_REQUEST_CODE;
                break;
            case mIdFiestaDeLosMuertos:
                JeuActivity = new Intent(MainActivity.this, FiestaDeLosMuertosActivity.class);
                REQUEST_CODE = FIESTA_MUERTOS_ACTIVITY_REQUEST_CODE;
                break;
            case mIdLeRoiDesNains:
                JeuActivity = new Intent(MainActivity.this, LeRoiDesNainsActivity.class);
                REQUEST_CODE = ROI_NAINS_ACTIVITY_REQUEST_CODE;
                break;
            case mIdGambit7:
                JeuActivity = new Intent(MainActivity.this, Gambit7Activity.class);
                REQUEST_CODE = MANCHOTS_BARJOTS_ACTIVITY_REQUEST_CODE;
                break;
            case mIdBelote:
                JeuActivity = new Intent(MainActivity.this, BeloteActivity.class);
                REQUEST_CODE = BELOTE_ACTIVITY_REQUEST_CODE;
                break;
            case mIdTopTen:
                JeuActivity = new Intent(MainActivity.this, TopTenActivity.class);
                REQUEST_CODE = TOPTEN_ACTIVITY_REQUEST_CODE;
                break;
            case mIdMajority:
                JeuActivity = new Intent(MainActivity.this, MajorityActivity.class);
                REQUEST_CODE = MAJORITY_ACTIVITY_REQUEST_CODE;
                break;
            case mIdTimeLine:
                JeuActivity = new Intent(MainActivity.this, TimelineActivity.class);
                REQUEST_CODE = TIMELINE_ACTIVITY_REQUEST_CODE;
                break;
            case mIdJustOne:
                JeuActivity = new Intent(MainActivity.this, JustOneActivity.class);
                REQUEST_CODE = JUSTONE_ACTIVITY_REQUEST_CODE;
                break;
            case mIdWhaaat:
                JeuActivity = new Intent(MainActivity.this, WhaaatActivity.class);
                REQUEST_CODE = WHAAAT_ACTIVITY_REQUEST_CODE;
                break;
        }
        // Lance l'activité du jeu demandé avec les paramètres
        JeuActivity.putExtra(VALEUR_PSEUDO, mPseudo);
        JeuActivity.putExtra(VALEUR_ID_JOUEUR, mIdJoueur);
        JeuActivity.putExtra(VALEUR_ID_SALON, mIdSalon);
        JeuActivity.putExtra(VALEUR_ID_PARTIE, mIdPartie);
        JeuActivity.putExtra(VALEUR_NOM_SALON, nom_salon);
        startActivityForResult(JeuActivity, REQUEST_CODE);
    }

    // Verifie quels sont les tâches sélectionnées

    /**
     * Regarde parmis les 10 boutons options les 5 premiers sélectionnés
     * @return : un tableau de chaine de taille 5 avec les options
     */
    private String[] checkOptionsTaches() {
        int nbOption = 0;
        String[] optionsDemandees = {"", "", "", "", ""};

        if (mBoutonOptionTache1.getTag().equals("YES"))
            optionsDemandees[nbOption++] = mBoutonOptionTache1.getText().toString();
        if (mBoutonOptionTache2.getTag().equals("YES"))
            optionsDemandees[nbOption++] = mBoutonOptionTache2.getText().toString();
        if (mBoutonOptionTache3.getTag().equals("YES"))
            optionsDemandees[nbOption++] = mBoutonOptionTache3.getText().toString();
        if (mBoutonOptionTache4.getTag().equals("YES"))
            optionsDemandees[nbOption++] = mBoutonOptionTache4.getText().toString();
        if (mBoutonOptionTache5.getTag().equals("YES"))
            optionsDemandees[nbOption++]=mBoutonOptionTache5.getText().toString();
        if (mBoutonOptionTache6.getTag().equals("YES") && nbOption<=5)
            optionsDemandees[nbOption++]=mBoutonOptionTache6.getText().toString();
        if (mBoutonOptionTache7.getTag().equals("YES") && nbOption<=5)
            optionsDemandees[nbOption++]=mBoutonOptionTache7.getText().toString();
        if (mBoutonOptionTache8.getTag().equals("YES") && nbOption<=5)
            optionsDemandees[nbOption++]=mBoutonOptionTache8.getText().toString();
        if (mBoutonOptionTache9.getTag().equals("YES") && nbOption<=5)
            optionsDemandees[nbOption++]=mBoutonOptionTache9.getText().toString();
        if (mBoutonOptionTache10.getTag().equals("YES") && nbOption<=5)
            optionsDemandees[nbOption]=mBoutonOptionTache10.getText().toString();
        mOptionAjout = mBoutonOptionTacheAjout.getTag().equals("YES");

        return optionsDemandees;
    }

    /**
     * Affiche l'ID du salon en blanc (et les autres en noir)
     * @param indexSalon : index de position du salon dans la liste
     */
    private void afficheSalonEnBlanc(int indexSalon) {
        ImageView iv;
        TextView tv;

        for (int i = 0; i < mListeSalons.size(); i++) {
            iv = findViewById(tableIdImageSalon[i]);
            tv = findViewById(tableIdNomSalon[i]);
            if (i == indexSalon) {
                iv.setImageResource(R.drawable.icone_check_blanc);
                tv.setTextColor(getResources().getColor(R.color.blanc));
                mIndexSalon = indexSalon;
                mIdPartie = mListeSalons.get(mIndexSalon).getIdPartie();
                mIdSalon = mListeSalons.get(mIndexSalon).getId();
            } else {
                iv.setImageResource(R.drawable.icone_check);
                tv.setTextColor(getResources().getColor(R.color.gris));
            }
        }
    }

    private void afficheOptionAdmin() {
        // Récupère l'index du joueur
        if (mListeJoueurs != null)
            for (int i = 0; i < mListeJoueurs.size(); i++) {
                if (mListeJoueurs.get(i).getNomJoueur().equals(mPseudo)) {
                    mAdmin = mListeJoueurs.get(i).getAdmin();
                    break;
                }
            }

        if (mAdmin == 1) {
            mBoutonDistribueTache.setVisibility(View.GONE);
            mBoutonOptionTacheAjout.setVisibility(View.GONE);
            mLigneNbTaches.setVisibility(View.GONE);
            mOptionTaches.setVisibility(View.GONE);
            mLigneNumMission.setVisibility(View.GONE);
            mBoutonMissionSuivante.setVisibility(View.GONE);
            mBoutonEchangeCarte.setVisibility(View.GONE);
            mBoutonEchangeJeu.setVisibility(View.GONE);
            mBoutonAdministration.setVisibility(View.VISIBLE);

            switch (mIdJeu) {
                case mIdTheCrew:
                    mBoutonRAZ.setVisibility(View.VISIBLE);
                    mBoutonRAZ.setText(R.string.raz_distribution);
                    mBoutonDistribueCartes.setVisibility(View.VISIBLE);
                    mBoutonDistribueTache.setVisibility(View.VISIBLE);
                    mBoutonOptionTacheAjout.setVisibility(View.VISIBLE);
                    mLigneNbTaches.setVisibility(View.VISIBLE);
                    mOptionTaches.setVisibility(View.VISIBLE);
                    mLigneNumMission.setVisibility(View.VISIBLE);
                    mBoutonMissionSuivante.setVisibility(View.VISIBLE);
                    mBoutonEchangeCarte.setVisibility(View.VISIBLE);
                    mBoutonEchangeJeu.setVisibility(View.INVISIBLE);
                    break;
                case mIdBelote:
                    mBoutonRAZ.setVisibility(View.VISIBLE);
                    mBoutonRAZ.setText(R.string.raz_distribution);
                    mBoutonDistribueCartes.setVisibility(View.VISIBLE);
                    break;
                case mIdTopTen:
                case mIdFiestaDeLosMuertos:
                case mIdMajority:
                case mIdJustOne:
                case mIdWhaaat:
                    mBoutonRAZ.setVisibility(View.VISIBLE);
                    mBoutonRAZ.setText(R.string.initialiser);
                    mBoutonDistribueCartes.setVisibility(View.GONE);
                    break;
                case mIdLeRoiDesNains:
                case mIdGambit7:
                default:
                    mBoutonRAZ.setVisibility(View.GONE);
                    mBoutonDistribueCartes.setVisibility(View.GONE);
                    break;
            }

        } else {
            mBoutonRAZ.setVisibility(View.GONE);
            mBoutonDistribueCartes.setVisibility(View.GONE);
            mBoutonDistribueTache.setVisibility(View.GONE);
            mBoutonOptionTacheAjout.setVisibility(View.GONE);
            mLigneNbTaches.setVisibility(View.GONE);
            mOptionTaches.setVisibility(View.GONE);
            mLigneNumMission.setVisibility(View.GONE);
            mBoutonMissionSuivante.setVisibility(View.GONE);
            mBoutonEchangeCarte.setVisibility(View.GONE);
            mBoutonEchangeJeu.setVisibility(View.GONE);
            mBoutonAdministration.setVisibility(View.GONE);
        }
    }

    private void afficheJoueurs(ArrayList<Joueur> listeJoueurs) {
        TableLayout tl = findViewById(R.id.liste_joueurs_TL);
        TableRow.LayoutParams paramsRow;
        TableRow.LayoutParams paramsTV;
        tl.removeAllViewsInLayout();
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(0, 0, 0, 0);
        tl.setLayoutParams(params);
        tl.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tl.setVisibility(View.VISIBLE);

        // Affiche les joueurs dans la liste
        if (listeJoueurs != null) {
            String[] listePseudoJoueurs = new String[listeJoueurs.size()];
            for (int i = 0; i < listeJoueurs.size(); i++) {
                if (listeJoueurs.get(i).getNew() == 1 || listeJoueurs.get(i).getAdmin() == 1) {
                    listePseudoJoueurs[i] = listeJoueurs.get(i).getNomJoueur();

                    // Création dynamique des joueurs dans des lignes du tableau
                    TableRow tr = new TableRow(this);
                    TextView tv = new CheckedTextView(this);
                    paramsRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                    paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                    // Ligne
                    paramsRow.setMargins(10, 10, 0, 0);
                    tr.setLayoutParams(paramsRow);
                    tr.setOnClickListener(this);
                    tr.setTag("pseudo_" + i);
                    // Texte
                    paramsTV.setMargins(20, 30, 0, 0);
                    tv.setLayoutParams(paramsTV);
                    tv.setText(listePseudoJoueurs[i]);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    tv.setTag(listePseudoJoueurs[i]);
                    tv.setTextSize(Dimension.SP, 30);
                    tv.setTextColor(getResources().getColor(R.color.gris));
                    tr.addView(tv);
                    // Ajout de la ligne dans la vue table
                    tl.addView(tr);
                }
            }
        }
    }

    private void afficheJeux(ArrayList<Jeu> listeJeux) {
        int index=0;
        // Affiche les jeux
        if (listeJeux != null) {
            for (int i = 0; i < listeJeux.size(); i++) {
                ImageView iv = findViewById(tableIdImageJeux[i]);
                // Image
                iv.setImageResource(tableIdResourceImageJeux[listeJeux.get(i).getId()]);
                iv.setBackgroundColor(0);
                iv.setTag("NO");
                iv.setVisibility(View.VISIBLE);
                index++;
            }
        }

        // Masque les ImageView non utilisées
        for (int i = index; i < tableIdImageJeux.length; i++) {
            ImageView iv = findViewById(tableIdImageJeux[i]);
            // Image
            iv.setBackgroundColor(0);
            iv.setTag("NO");
            iv.setVisibility(View.GONE);
        }
    }

    private void afficheSalons(ArrayList<Salon> listeSalons) {
        // Affiche la liste des salons de jeu de manière dynamique
        TableLayout tl = findViewById(R.id.liste_salons_TL);
        TableRow.LayoutParams paramsRow;
        TableRow.LayoutParams paramsIV;
        TableRow.LayoutParams paramsTV;
        tl.removeAllViewsInLayout();
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(0, 0, 0, 0);
        tl.setLayoutParams(params);
        tl.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Affiche les salons dans la liste
        if (listeSalons != null) {
            String[] listeNomSalons = new String[listeSalons.size()];
            for (int i = 0; i < listeSalons.size(); i++) {
                listeNomSalons[i] = listeSalons.get(i).getNom();

                // Création dynamique des joueurs dans des lignes du tableau
                TableRow tr = new TableRow(this);
                TextView tv = new CheckedTextView(this);
                ImageView iv = new ImageView(this);
                paramsRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                paramsIV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                // Ligne
                paramsRow.setMargins(30, 10, 0, 0);
                tr.setLayoutParams(paramsRow);
                tr.setOnClickListener(this);
                tr.setTag(String.valueOf(i)); // Index salon en tag du texte
                tr.setId(tableIdLigneSalon[i]);
                // Image
                paramsIV.setMargins(30, 30, 30, 0);
                iv.setLayoutParams(paramsIV);
                iv.setImageResource(R.drawable.icone_check);
                iv.setTag(listeSalons.get(i).getId()); // Id du salon en tag de l'image
                iv.setId(tableIdImageSalon[i]);
                tr.addView(iv);
                // Texte
                paramsTV.setMargins(0, 30, 0, 0);
                tv.setLayoutParams(paramsTV);
                tv.setText(listeNomSalons[i]);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tv.setTag(String.valueOf(i)); // Index salon en tag du texte
                tv.setId(tableIdNomSalon[i]);
                tv.setTextColor(getResources().getColor(R.color.gris));
                tr.addView(tv);
                // Ajout de la ligne dans la vue table
                tl.addView(tr);
            }
        }
    }

    public static ArrayList<Salon> parseXMLSalons(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        ArrayList<Salon> listeSalons = new ArrayList<>();

        Node noeudSalons = getNoeudUnique(doc, "Salons");
        int idSalon = 0;
        int idPartie = 0;
        int numMission = 0;
        String nomSalon = "";
        for (int i = 0; i < noeudSalons.getChildNodes().getLength(); i++) { // Parcours toutes les salons
            Node noeudSalon = noeudSalons.getChildNodes().item(i);
            Log.d("PGR-XML-Salon", noeudSalon.getNodeName());
            for (int j = 0; j < noeudSalon.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud salon
                Log.d("PGR-XML-Salon", noeudSalon.getAttributes().item(j).getNodeName() + "_" + noeudSalon.getAttributes().item(j).getNodeValue());
                if (noeudSalon.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudSalon.getAttributes().item(j).getNodeName()) {
                    case "id_salon":
                        idSalon = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "nom":
                        nomSalon = noeudSalon.getAttributes().item(j).getNodeValue();
                        break;
                    case "id_partie":
                        idPartie = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "num_mission":
                        numMission = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Salon salon = new Salon(idSalon, nomSalon, idPartie, numMission);
            listeSalons.add(salon);
        }

        return listeSalons;
    }

    private ArrayList<Jeu> parseXMLJeux(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        ArrayList<Jeu> listeJeux = new ArrayList<>();

        Node noeudJeux = getNoeudUnique(doc, "Jeux");
        int idJeu = 0;
        String nomJeu = "";
        for (int i = 0; i < noeudJeux.getChildNodes().getLength(); i++) { // Parcours toutes les jeux
            Node noeudJeu = noeudJeux.getChildNodes().item(i);
            Log.d("PGR-XML-Jeu", noeudJeu.getNodeName());
            for (int j = 0; j < noeudJeu.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud jeu
                Log.d("PGR-XML-Jeu", noeudJeu.getAttributes().item(j).getNodeName() + "_" + noeudJeu.getAttributes().item(j).getNodeValue());
                if (noeudJeu.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudJeu.getAttributes().item(j).getNodeName()) {
                    case "id_jeu":
                        idJeu = Integer.parseInt(noeudJeu.getAttributes().item(j).getNodeValue());
                        break;
                    case "nom" :
                        nomJeu = noeudJeu.getAttributes().item(j).getNodeValue();
                        break;
                }
            }
            Jeu jeu = new Jeu(idJeu, nomJeu);
            listeJeux.add(jeu);
        }

        return listeJeux;
    }

    private void parseXMLVersion(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        Node noeud = getNoeudUnique(doc, "numero");
        for (int i = 0; i < noeud.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud
            Log.d("PGR-XML-Version", noeud.getAttributes().item(i).getNodeName() + "_" + noeud.getAttributes().item(i).getNodeValue());
            if ("num".equals(noeud.getAttributes().item(i).getNodeName())) {
                mNumeroVersionDispo = noeud.getAttributes().item(i).getNodeValue();
            } else {
                throw new IllegalStateException("Unexpected value: " + noeud.getAttributes().item(i).getNodeName());
            }
        }

        noeud = getNoeudUnique(doc, "lien");
        for (int i = 0; i < noeud.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud
            Log.d("PGR-XML-Version", noeud.getAttributes().item(i).getNodeName() + "_" + noeud.getAttributes().item(i).getNodeValue());
            if ("url".equals(noeud.getAttributes().item(i).getNodeName())) {
                mUrlNewVersion = noeud.getAttributes().item(i).getNodeValue();
            } else {
                throw new IllegalStateException("Unexpected value: " + noeud.getAttributes().item(i).getNodeName());
            }
        }

        if (mNumeroVersionDispo.equals(mNumVersion)) {
            mTexteNouvelleVersion.setVisibility(View.GONE);
            Toast.makeText(this, "Bienvenue " + mPseudo, Toast.LENGTH_SHORT).show();
        } else {
            mTexteNouvelleVersion.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Une nouvelle version est disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void startChargementBDD() {
        if (t == null || !t.isAlive()) {
            t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mNbChargement < mNbTentativeMax) {
                                        if (!mChargementOK)
                                            mNbChargement++;
                                        mChargementOK = false;

                                        switch (mEtapeChargement) {
                                            case 0:
                                                // Chargement la liste des joueurs
                                                new TacheGetXML().execute(urlGetJoueurs);
                                                break;
                                            case 1:
                                                // Liste des jeux disponibles pour le joueur
                                                new TacheGetXML().execute(urlGetJeux + mPseudo);
                                                break;
                                            case 2:
                                                // Nouvelle version ?
                                                new TacheGetXML().execute(urlGetVersion);
                                                break;
                                            case 3:
                                            default:
                                                postChargementBDD();
                                                break;
                                        }
                                        majTexteChargement();
                                    } else {
                                        mTexteChargement.setText(getResources().getText(R.string.chargement_impossible));
                                        findViewById(R.id.barreDeChargement).setVisibility(View.INVISIBLE);
                                        t.interrupt();
                                    }
                                }
                            });
                            // Attend 1sec pour la tentative suivante
                            if (!mChargementOK)
                                Thread.sleep(1000);
                            else
                                Thread.sleep(100);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };
            t.start();
        }
    }

    private void majTexteChargement() {
        if (mNbChargement > 1) {
            String texte = getResources().getString(R.string.Chargement) + " tentative " + mNbChargement;
            mTexteChargement.setText(texte);
        }
    }

    private class TacheGetXML extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... strings) {
            URL url;
            Document doc = null;
            mChargementOK = true;
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
                //mMessageErreur = e.getMessage();
                mChargementOK = false;
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null && doc.getChildNodes().getLength() > 0) {
                Node noeudParent = doc.getChildNodes().item(0);
                Log.d("PGR", noeudParent.getNodeName());
                switch (noeudParent.getNodeName()) {
                    case "Salons":
                        mListeSalons = parseXMLSalons(doc);
                        afficheSalons(mListeSalons);
                        // Affiche les salons si plusieurs
                        if (mListeSalons.size() > 1)
                            findViewById(R.id.bloc_salons).setVisibility(View.VISIBLE);
                        else
                            findViewById(R.id.bloc_salons).setVisibility(View.GONE);
                        break;
                    case "Joueurs":
                        mListeJoueurs = parseNoeudsJoueur(doc);
                        afficheJoueurs(mListeJoueurs);
                        mEtapeChargement = 1;
                        chargementEtapeSuivante();
                        break;
                    case "Jeux":
                        mListeJeux = parseXMLJeux(doc);
                        afficheJeux(mListeJeux);
                        mEtapeChargement = 2;
                        chargementEtapeSuivante();
                        break;
                    case "Version":
                        parseXMLVersion(doc);
                        mEtapeChargement = 3;
                        chargementEtapeSuivante();
                        break;
                }
            }
            super.onPostExecute(doc);
        }
    }

    static ArrayList<Joueur> parseNoeudsJoueur(Document doc) {
        Node NoeudJoueurs = getNoeudUnique(doc, "Joueurs");

        String pseudo = "";
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
                    case "new":
                        nv = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "score":
                        score = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(id, pseudo, score, admin, nv, actif);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    private void chargementEtapeSuivante() {
        String texte = "Etape " + mEtapeChargement;
        TextView tv = findViewById(R.id.etapeChargement);
        tv.setText(texte);
    }

    /**
     * Classe qui permet d'appeler une URL sans traitement d'information en retour
     */
    public static class TacheURLSansRetour extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(String... strings) {
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }

            return null;
        }
    }
}

