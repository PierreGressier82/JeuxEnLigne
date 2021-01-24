package com.pigredorou.jeuxenvisio;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Pli;
import com.pigredorou.jeuxenvisio.objets.Tache;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ManchotsBarjotsActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, View.OnDragListener {

    // Constantes
    // Tableaux des resssources
    private static final int[] imagesJaune = {0, R.drawable.jaune_1, R.drawable.jaune_2, R.drawable.jaune_3, R.drawable.jaune_4, R.drawable.jaune_5, R.drawable.jaune_6, R.drawable.jaune_7, R.drawable.jaune_8, R.drawable.jaune_9};
    private static final int[] imagesRose = {0, R.drawable.rose_1, R.drawable.rose_2, R.drawable.rose_3, R.drawable.rose_4, R.drawable.rose_5, R.drawable.rose_6, R.drawable.rose_7, R.drawable.rose_8, R.drawable.rose_9};
    private static final int[] imagesVert = {0, R.drawable.vert_1, R.drawable.vert_2, R.drawable.vert_3, R.drawable.vert_4, R.drawable.vert_5, R.drawable.vert_6, R.drawable.vert_7, R.drawable.vert_8, R.drawable.vert_9};
    private static final int[] imagesBleu = {0, R.drawable.bleu_1, R.drawable.bleu_2, R.drawable.bleu_3, R.drawable.bleu_4, R.drawable.bleu_5, R.drawable.bleu_6, R.drawable.bleu_7, R.drawable.bleu_8, R.drawable.bleu_9};
    private static final int[] imagesFusee = {0, R.drawable.fusee_1, R.drawable.fusee_2, R.drawable.fusee_3, R.drawable.fusee_4};
    private static final int[] tableIdPseudo = {R.id.table_pseudo_joueur1, R.id.table_pseudo_joueur2, R.id.table_pseudo_joueur3, R.id.table_pseudo_joueur4, R.id.table_pseudo_joueur5};
    private static final int[] tableIdImageCarteMain = {R.id.carte_1, R.id.carte_2, R.id.carte_3, R.id.carte_4, R.id.carte_5, R.id.carte_6, R.id.carte_7, R.id.carte_8, R.id.carte_9, R.id.carte_10, R.id.carte_11, R.id.carte_12, R.id.carte_13, R.id.carte_14, R.id.carte_15, R.id.carte_16, R.id.carte_17, R.id.carte_18, R.id.carte_19, R.id.carte_20};
    private static final int[] tableIdImageCartePli = {R.id.table_carte_image_joueur1, R.id.table_carte_image_joueur2, R.id.table_carte_image_joueur3, R.id.table_carte_image_joueur4, R.id.table_carte_image_joueur5};
    private static final int[] tableLignesTaches = {R.id.ligne_tache1, R.id.ligne_tache2, R.id.ligne_tache3, R.id.ligne_tache4};
    private static final int[] tableTaches1 = {R.id.tache_joueur1, R.id.tache_joueur2, R.id.tache_joueur3, R.id.tache_joueur4, R.id.tache_joueur5};
    private static final int[] tableTaches2 = {R.id.tache2_joueur1, R.id.tache2_joueur2, R.id.tache2_joueur3, R.id.tache2_joueur4, R.id.tache2_joueur5};
    private static final int[] tableTaches3 = {R.id.tache3_joueur1, R.id.tache3_joueur2, R.id.tache3_joueur3, R.id.tache3_joueur4, R.id.tache3_joueur5};
    private static final int[] tableTaches4 = {R.id.tache4_joueur1, R.id.tache4_joueur2, R.id.tache4_joueur3, R.id.tache4_joueur4, R.id.tache4_joueur5};
    private static final int[] tableCommunication = {R.id.communication_joueur1, R.id.communication_joueur2, R.id.communication_joueur3, R.id.communication_joueur4, R.id.communication_joueur5};
    // URLs des actions en base
    private static final String urlJoueCarte = MainActivity.url + "majTable.php?partie=";
    private static final String urlCommuniqueCarte = MainActivity.url + "majCommunication.php?partie=";
    private static final String urlRealiseTache = MainActivity.url + "realiseTache.php?partie=";
    private static final String urlAttribueTache = MainActivity.url + "attribueTache.php?partie=";
    private static final String urlTheCrew = MainActivity.url + "theCrew.php?partie=";
    private static final int MAX_DURATION_CLICK = 500;
    private static final int MAX_DURATION_DOUBLE_CLICK = 2000;
    Thread t;
    // Variables globales
    private String[] mListePseudo; // Liste des pseudos des joueurs
    private String mPseudo; // Pseudo du joueur
    private String mCommandant; // Pseudo du commandant de la partie (fusée 4)
    private int mIdSalon;
    private int mIdPartie;
    private boolean mCommunicationFaite = false;
    private boolean mCommunicationAChoisir = false;
    private boolean mDetresseUtilise = false;
    private int mNbTacheAAtribuer = 0;
    private int mZoneSilence = 0;
    private int mNumeroPli = 0;
    private boolean mMajTerminee = true;
    // Elements de la vue
    private LinearLayout mTable;
    private ImageView mCarteActive;
    private TextView mTitrePli;
    private TextView mHeureRefresh;
    private TextView mObjectifCommun;
    // Detresse
    private ImageView mImageDetresse;
    private TableLayout mTableauCommunication;
    private TextView mCommPseudoJoueur1;
    private TextView mCommPseudoJoueur2;
    private TextView mCommPseudoJoueur3;
    private TextView mCommPseudoJoueur4;
    private TextView mCommPseudoJoueur5;
    private TextView mCommJoueur4;
    private TextView mCommJoueur5;
    private ImageView mBoutonComm;
    // Tâches
    private TextView mTitreTaches;
    private TextView mTitreTachesAAtribuer;
    private TableLayout mTableauTaches;
    private TextView mTachePseudoJoueur1;
    private TextView mTachePseudoJoueur2;
    private TextView mTachePseudoJoueur3;
    private TextView mTachePseudoJoueur4;
    private TextView mTachePseudoJoueur5;
    private TextView mTaches1Joueur4;
    private TextView mTaches1Joueur5;
    private TextView mTaches2Joueur4;
    private TextView mTaches2Joueur5;
    private TextView mTaches3Joueur4;
    private TextView mTaches3Joueur5;
    private TextView mTaches4Joueur4;
    private TextView mTaches4Joueur5;
    // Auto resfresh
    private Button mBoutonRefreshAuto;
    private Boolean mRefreshAuto;
    // Gestion du double clic
    private int mClickCount = 0;
    private int mLastViewID = 0;
    private long mStartTimeClick;
    private long mDurationClick;
    // Drag & drop
    private View vueArrivee = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_the_crew);

        // ENTETE
        TextView titre = findViewById(R.id.titre_jeu);
        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);
        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

        // Affiche un message chargement le temps de récupérer les informations en base
        TextView textResultat = findViewById(R.id.resultat);
        textResultat.setText(R.string.Chargement);

        // Table
        mTitrePli = findViewById(R.id.titre_pli);
        mTitrePli.setOnClickListener(this);
        mTable = findViewById(R.id.table);

        // Communications
        chargeVuesCommunication();
        mImageDetresse = findViewById(R.id.bouton_detresse);
        mImageDetresse.setOnClickListener(this);

        // Objectifs commun
        mObjectifCommun = findViewById(R.id.objectif_commun);

        // Taches
        chargeVuesTaches();

        // Refresh auto
        startRefreshAuto();
        mBoutonRefreshAuto = findViewById(R.id.bouton_refresh);
        mBoutonRefreshAuto.setOnClickListener(this);
        mHeureRefresh = findViewById(R.id.heure_refresh);
        // TODO : Signal de détresse : Choix d'une carte pour la passer à son voisin (change le pseudo du joueur avec un tag pour retirer du joueur mais mettre en attente la carte

        // Drag & drop
        findViewById(R.id.tableau_cartes).setOnDragListener(this);
        findViewById(R.id.tableau_table).setOnDragListener(this);
    }

    private void chargeVuesCommunication() {
        // Communication
        TextView titreCommunication = findViewById(R.id.titre_communication);
        titreCommunication.setOnClickListener(this);
        mTableauCommunication = findViewById(R.id.tableau_communication);
        mCommPseudoJoueur1 = findViewById(R.id.comm_pseudo_joueur1);
        mCommPseudoJoueur2 = findViewById(R.id.comm_pseudo_joueur2);
        mCommPseudoJoueur3 = findViewById(R.id.comm_pseudo_joueur3);
        mCommPseudoJoueur4 = findViewById(R.id.comm_pseudo_joueur4);
        mCommPseudoJoueur5 = findViewById(R.id.comm_pseudo_joueur5);
        mCommJoueur4 = findViewById(R.id.communication_joueur4);
        mCommJoueur5 = findViewById(R.id.communication_joueur5);
        mBoutonComm = findViewById(R.id.bouton_communication);
        mBoutonComm.setOnClickListener(this);
    }

    private void chargeVuesTaches() {
        mTitreTaches = findViewById(R.id.titre_objectif);
        mTitreTaches.setOnClickListener(this);
        mTitreTachesAAtribuer = findViewById(R.id.titre_tache_aatribuer);
        mTableauTaches = findViewById(R.id.tableau_taches);
        mTachePseudoJoueur1 = findViewById(R.id.tache_pseudo_joueur1);
        mTachePseudoJoueur2 = findViewById(R.id.tache_pseudo_joueur2);
        mTachePseudoJoueur3 = findViewById(R.id.tache_pseudo_joueur3);
        mTachePseudoJoueur4 = findViewById(R.id.tache_pseudo_joueur4);
        mTachePseudoJoueur5 = findViewById(R.id.tache_pseudo_joueur5);
        mTaches1Joueur4 = findViewById(R.id.tache_joueur4);
        mTaches1Joueur5 = findViewById(R.id.tache_joueur5);
        mTaches2Joueur4 = findViewById(R.id.tache2_joueur4);
        mTaches2Joueur5 = findViewById(R.id.tache2_joueur5);
        mTaches3Joueur4 = findViewById(R.id.tache3_joueur4);
        mTaches3Joueur5 = findViewById(R.id.tache3_joueur5);
        mTaches4Joueur4 = findViewById(R.id.tache4_joueur4);
        mTaches4Joueur5 = findViewById(R.id.tache4_joueur5);
        mTitreTachesAAtribuer.setVisibility(View.GONE);
    }

    private void affichePseudos(ArrayList<Joueur> listeJoueurs) {
        mListePseudo = new String[listeJoueurs.size()];
        for (int i = 0; i < listeJoueurs.size(); i++) {
            mListePseudo[i] = listeJoueurs.get(i).getNomJoueur();
            debug(listeJoueurs.get(i).getNomJoueur());
        }

        switch (mListePseudo.length) {
            case 5:
                mCommPseudoJoueur5.setText(mListePseudo[4]);
                mCommPseudoJoueur5.setVisibility(View.VISIBLE);
                mCommJoueur5.setVisibility(View.VISIBLE);
                mTachePseudoJoueur5.setText(mListePseudo[4]);
                mTachePseudoJoueur5.setVisibility(View.VISIBLE);
                mTaches1Joueur5.setVisibility(View.VISIBLE);
                mTaches2Joueur5.setVisibility(View.VISIBLE);
                mTaches3Joueur5.setVisibility(View.VISIBLE);
                mTaches4Joueur5.setVisibility(View.VISIBLE);
            case 4:
                mCommPseudoJoueur4.setText(mListePseudo[3]);
                mCommPseudoJoueur4.setVisibility(View.VISIBLE);
                mCommJoueur4.setVisibility(View.VISIBLE);
                mTachePseudoJoueur4.setText(mListePseudo[3]);
                mTachePseudoJoueur4.setVisibility(View.VISIBLE);
                mTaches1Joueur4.setVisibility(View.VISIBLE);
                mTaches2Joueur4.setVisibility(View.VISIBLE);
                mTaches3Joueur4.setVisibility(View.VISIBLE);
                mTaches4Joueur4.setVisibility(View.VISIBLE);
            default:
                mCommPseudoJoueur3.setText(mListePseudo[2]);
                mTachePseudoJoueur3.setText(mListePseudo[2]);
                mCommPseudoJoueur2.setText(mListePseudo[1]);
                mTachePseudoJoueur2.setText(mListePseudo[1]);
                mCommPseudoJoueur1.setText(mListePseudo[0]);
                mTachePseudoJoueur1.setText(mListePseudo[0]);
                break;
        }
    }

    private String getPseudoQuiDoitJouer() {
        String pseudoQuiDoitJoueur = "";
        ImageView iv;
        TextView tv;
        // Parcours les images pour savoir si c'est mon tour
        for (int i = 0; i < tableIdImageCartePli.length; i++) {
            iv = findViewById(tableIdImageCartePli[i]);
            tv = findViewById(tableIdPseudo[i]);
            // On prends le pseudo de la première image invisible
            if (iv.getVisibility() == View.INVISIBLE) {
                pseudoQuiDoitJoueur = tv.getText().toString();
                debug("pseudoQuiDoitJoueur :" + pseudoQuiDoitJoueur);
                break;
            }
        }
        return pseudoQuiDoitJoueur;
    }

    private void startRefreshAutoWithDelai() {
        if (t == null || !t.isAlive()) {
            t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (mMajTerminee) {
                                        mMajTerminee = false;
                                        updateTextView();
                                        // Mise à jour complète
                                        new TacheGetInfoTheCrew().execute(urlTheCrew + mIdPartie + "&joueur=" + mPseudo);
                                    }
                                }
                            });
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };

            t.start();
            debug("start refresh");
        }
        mRefreshAuto = true;
    }

    private void startRefreshAuto() {
        // Mise à jour complète
        mMajTerminee = false;
        new TacheGetInfoTheCrew().execute(urlTheCrew + mIdPartie + "&joueur=" + mPseudo);
        startRefreshAutoWithDelai();
        mRefreshAuto = true;
    }

    private void stopRefreshAuto() {
        t.interrupt();
        mRefreshAuto = false;
    }

    private void updateTextView() {
        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "hh:mm:ss"; // 12:00:00
        mHeureRefresh.setText(DateFormat.format(time, noteTS));

        //String date = "dd MMMMM yyyy"; // 01 January 2013
        //titre.setText(DateFormat.format(date, noteTS));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_refresh:
                if (mRefreshAuto) {
                    mBoutonRefreshAuto.setTextColor(getResources().getColor(R.color.blanc));
                    stopRefreshAuto();
                } else {
                    mBoutonRefreshAuto.setTextColor(getResources().getColor(R.color.noir));
                    startRefreshAuto();
                }
                break;

            case R.id.bouton_retour:
                finish();
                break;

            case R.id.bouton_communication:
                if (mNbTacheAAtribuer == 0 && (mZoneSilence < 2 || mNumeroPli >= mZoneSilence)) {
                    if (!mCommunicationFaite) {
                        if (mCommunicationAChoisir) {
                            mBoutonComm.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            mCommunicationAChoisir = false;
                        } else {
                            mBoutonComm.setBackgroundColor(getResources().getColor(R.color.blanc));
                            Toast.makeText(this, "Choisi la carte à communiquer", Toast.LENGTH_SHORT).show();
                            mCommunicationAChoisir = true;
                        }
                    }
                } else if (mNbTacheAAtribuer > 0)
                    Toast.makeText(this, "Communication après attribution des tâches", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Communication possible à partir du pli " + mZoneSilence, Toast.LENGTH_SHORT).show();
                break;

            case R.id.titre_pli:
                if (mTable.getVisibility() == View.GONE)
                    mTable.setVisibility(View.VISIBLE);
                else
                    mTable.setVisibility(View.GONE);
                break;

            case R.id.titre_communication:
                if (mTableauCommunication.getVisibility() == View.GONE)
                    mTableauCommunication.setVisibility(View.VISIBLE);
                else
                    mTableauCommunication.setVisibility(View.GONE);
                break;

            case R.id.titre_objectif:
                if (mTableauTaches.getVisibility() == View.GONE)
                    mTableauTaches.setVisibility(View.VISIBLE);
                else
                    mTableauTaches.setVisibility(View.GONE);
                break;

            case R.id.tache_joueur1:
            case R.id.tache_joueur2:
            case R.id.tache_joueur3:
            case R.id.tache_joueur4:
            case R.id.tache_joueur5:
            case R.id.tache2_joueur1:
            case R.id.tache2_joueur2:
            case R.id.tache2_joueur3:
            case R.id.tache2_joueur4:
            case R.id.tache2_joueur5:
            case R.id.tache3_joueur1:
            case R.id.tache3_joueur2:
            case R.id.tache3_joueur3:
            case R.id.tache3_joueur4:
            case R.id.tache3_joueur5:
            case R.id.tache4_joueur1:
            case R.id.tache4_joueur2:
            case R.id.tache4_joueur3:
            case R.id.tache4_joueur4:
            case R.id.tache4_joueur5:
                clicTache(v);
                break;

            case R.id.bouton_detresse:
                if (mDetresseUtilise) {
                    mImageDetresse.setImageResource(R.drawable.detresse_noir);
                    mDetresseUtilise = false;
                } else {
                    mImageDetresse.setImageResource(R.drawable.detresse);
                    mDetresseUtilise = true;
                    // Affichage des fleches pour passer une carte au voisin de droite ou gauche
                }
                break;
            default:
                if (v.getTag().toString().startsWith("tacheAAttribuer")) {
                    clicTacheAAttribuer(v);
                }
                break;
        }
    }

    private void clicTache(View v) {
        TextView tache = findViewById(v.getId());
        String[] chaine = tache.getTag().toString().split("_"); // ex : tache_bleu_2_0
        String couleurTacheActive = chaine[1];
        String valeurTacheActive = chaine[2];
        int realise = Integer.parseInt(chaine[3]);
        if (realise == 0)
            realise = 1;
        else
            realise = 0;
        String url = urlRealiseTache + mIdPartie + "&valeur_carte=" + valeurTacheActive + "&couleur_carte=" + couleurTacheActive + "&realise=" + realise;
        tache.setTag("tache_" + couleurTacheActive + "_" + valeurTacheActive + "_" + realise);
        debug(url);
        // Mise à jour de la tache en base
        new MainActivity.TacheURLSansRetour().execute(url);
        // TODO : Si au moins une tache et toutes sont realisees => feu d'artifice
        //ImageView iv = findViewById(R.id.feu_artifice);
        //iv.setVisibility(View.VISIBLE);
    }

    private void clicTacheAAttribuer(View v) {
        String[] chaine = v.getTag().toString().split("_"); // ex : tacheAAttribuer_bleu_2
        String couleurTacheActive = chaine[1];
        String valeurTacheActive = chaine[2];
        String url = urlAttribueTache + mIdPartie + "&pseudo=" + mPseudo + "&valeur_carte=" + valeurTacheActive + "&couleur_carte=" + couleurTacheActive;
        debug(url);
        new MainActivity.TacheURLSansRetour().execute(url);
        if (--mNbTacheAAtribuer == 0) {
            HorizontalScrollView hs = findViewById(R.id.HS_taches_a_attribuer);
            hs.setVisibility(View.GONE);
            mTitreTachesAAtribuer.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long time;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            // Appuie sur l'écran
            case MotionEvent.ACTION_DOWN:
                if (v.getTag() != null && v.getTag().toString().startsWith("carte_")) {
                    jourCarteFromMainJoueur(v);
                } else {
                    time = System.currentTimeMillis();

                    if (mClickCount > 2)
                        mClickCount = 0;
                    // Si temps entre 2 clicks trop long, on retourne à 0
                    if ((time - mStartTimeClick) > MAX_DURATION_DOUBLE_CLICK)
                        mClickCount = 0;
                    // Si le précédent click n'est pas sur la même vue, on retourne à 0
                    if (mLastViewID != v.getId())
                        mClickCount = 0;

                    mStartTimeClick = System.currentTimeMillis();
                    mClickCount++;
                    mLastViewID = v.getId();
                }
                Log.d("PGR-onTouch", "ACTION_DOWN " + mClickCount + " " + v.getId() + " ");
                break;

            // Relanche l'écran
            case MotionEvent.ACTION_UP:
                // Temps en appuie et relache
                time = System.currentTimeMillis() - mStartTimeClick;
                mDurationClick = mDurationClick + time;
                if (mClickCount == 2) {
                    if (mDurationClick <= MAX_DURATION_CLICK) {
                        // On a un double clic !
                        doublicClic(v);
                        Log.d("PGR-onTouch", "ACTION_UP -> Double clic");
                    }
                    mClickCount = 0;
                    mDurationClick = 0;
                    break;
                }
                Log.d("PGR-onTouch", "ACTION_UP " + mClickCount + " " + v.getId() + " ");
                break;
            default:
                if (mClickCount > 2)
                    mClickCount = 0;
                Log.d("PGR-onTouch", "AUTRE ACTION " + mClickCount + " " + v.getId() + " evt " + event.getAction());
                break;
        }
        return true;
    }

    /**
     * Gestion des actions liées à un double clic
     *
     * @param v : la vue sur laquelle le double clic a été réalisé
     */
    private void doublicClic(View v) {
        // Cartes de la main du joueur
        if (v.getTag() != null && v.getTag().toString().startsWith("carte_"))
            jourCarteFromMainJoueur(v);
        else if (v.getTag() != null && v.getTag().toString().startsWith("tacheAAttribuer")) {
            clicTacheAAttribuer(v);
        } else {
            switch (v.getId()) {
                case R.id.tache_joueur1:
                case R.id.tache_joueur2:
                case R.id.tache_joueur3:
                case R.id.tache_joueur4:
                case R.id.tache_joueur5:
                case R.id.tache2_joueur1:
                case R.id.tache2_joueur2:
                case R.id.tache2_joueur3:
                case R.id.tache2_joueur4:
                case R.id.tache2_joueur5:
                case R.id.tache3_joueur1:
                case R.id.tache3_joueur2:
                case R.id.tache3_joueur3:
                case R.id.tache3_joueur4:
                case R.id.tache3_joueur5:
                    clicTache(v);
                    break;
                case R.id.table_carte_image_joueur1:
                case R.id.table_carte_image_joueur2:
                case R.id.table_carte_image_joueur3:
                case R.id.table_carte_image_joueur4:
                case R.id.table_carte_image_joueur5:
                    clicAnnulCarte(v);
                    break;
            }
        }
    }

    private void clicAnnulCarte(View v) {
        ImageView iv = findViewById(v.getId());
        String[] chaine = iv.getTag().toString().split("_");
        new MainActivity.TacheURLSansRetour().execute(MainActivity.urlAnnulCarte + mIdPartie + "&couleur_carte=" + chaine[1] + "&valeur_carte=" + chaine[2]);
        Toast.makeText(this, "Carte " + chaine[2] + " " + chaine[1] + " annulée", Toast.LENGTH_SHORT).show();
    }

    private int getImageCarte(String couleurCarte, int valeurCarte) {
        int ressource;
        switch (couleurCarte) {
            case "jaune":
                ressource = imagesJaune[valeurCarte];
                break;
            case "rose":
                ressource = imagesRose[valeurCarte];
                break;
            case "bleu":
                ressource = imagesBleu[valeurCarte];
                break;
            case "vert":
                ressource = imagesVert[valeurCarte];
                break;
            default:
                ressource = imagesFusee[valeurCarte];
                break;
        }

        return ressource;
    }

    /**
     * Affiche les cartes dans la vue
     *
     * @param cartes : Liste des cartes à afficher
     */
    private void afficheCartes(ArrayList<Carte> cartes) {
        //private void afficheCartes() {
        TableRow tableauCartes = findViewById(R.id.tableau_cartes);
        tableauCartes.removeAllViewsInLayout();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5, 0, 5, 0);
        tableauCartes.setLayoutParams(params);

        // Affiche les cartes
        if (cartes != null)
            for (int i = 0; i < cartes.size(); i++) {
                ImageView carte = new ImageView(this);
                String nomCarte = cartes.get(i).getCouleur() + "_" + cartes.get(i).getValeur();
                params = new TableRow.LayoutParams(250, 450, 0);
                params.setMargins(5, 0, 0, 0);
                carte.setLayoutParams(params);
                carte.setImageResource(getImageCarte(cartes.get(i).getCouleur(), cartes.get(i).getValeur()));
                carte.setTag("carte_" + nomCarte);
                carte.setId(tableIdImageCarteMain[i]);
                carte.setOnLongClickListener(this);
                tableauCartes.addView(carte);
            }
    }

    private void affichePliEnCours(ArrayList<Pli> plis) {
        TextView pseudo;
        ImageView imageCarte;
        int nbJoueur = mListePseudo.length;
        int positionPremierJoueur = 0;
        int positionJoueur;
        // On récupère la place du premier joueur
        if (plis != null && plis.size() > 0) {
            for (int j = 0; j < nbJoueur; j++) {
                if (mListePseudo[j].equals(plis.get(0).getJoueur())) {
                    positionPremierJoueur = j;
                    debug("posPremJoueur " + positionPremierJoueur);
                }
            }
        } else {
            // Si on a pas encore joué, on positionne le commandant comme premier joueur
            for (int i = 0; i < nbJoueur; i++)
                if (mListePseudo[i].equals(mCommandant)) {
                    positionPremierJoueur = i;
                    break;
                }
        }
        // On parcours les id des pseudo
        for (int i = 0; i < tableIdPseudo.length; i++) {
            pseudo = findViewById(tableIdPseudo[i]);
            imageCarte = findViewById(tableIdImageCartePli[i]);
            // Si moins de 5 joueurs, on retire de l'écran
            if (i >= nbJoueur) {
                pseudo.setVisibility(View.GONE);
                imageCarte.setVisibility(View.GONE);
            }
            // Affichage de tous les pseudo dans l'ordre de jeu, même si le joueur n'a pas encore joué
            else {
                positionJoueur = (positionPremierJoueur + i) % nbJoueur;
                debug("posJoueurNonJoué " + i + " positionPremierJoueur " + positionPremierJoueur + " nbJoueur " + nbJoueur + " positionJoueur" + positionJoueur);
                // Si le joueur est le commmandant, on affiche le nom en noir
                String pseudoTexte = mListePseudo[positionJoueur];
                if (mListePseudo[positionJoueur].equals(mCommandant)) {
                    pseudo.setTextColor(getResources().getColor(R.color.noir));
                } else
                    pseudo.setTextColor(getResources().getColor(R.color.blanc));
                pseudo.setText(pseudoTexte);
                pseudo.setVisibility(View.VISIBLE);

                if (plis != null && i < plis.size()) {
                    imageCarte.setImageResource(getImageCarte(plis.get(i).getCarte().getCouleur(), plis.get(i).getCarte().getValeur()));
                    imageCarte.setTag("pli_" + plis.get(i).getCarte().getCouleur() + "_" + plis.get(i).getCarte().getValeur());
                    imageCarte.setVisibility(View.VISIBLE);
                    // Rend clicable uniquement la dernière carte posée si c'est ma carte
                    if ((i + 1) == plis.size() && mPseudo.equals(pseudoTexte))
                        imageCarte.setOnTouchListener(this);
                    else
                        imageCarte.setOnTouchListener(null);
                } else {
                    imageCarte.setOnTouchListener(null);
                    imageCarte.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private void afficheTaches(ArrayList<Tache> taches) {
        int ligneTache = 0;
        int[] nbTacheParJoueur = new int[mListePseudo.length];
        TableRow trTacheAAttribuer = findViewById(R.id.taches_a_attribuer);
        trTacheAAttribuer.removeAllViewsInLayout();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5, 0, 5, 0);
        trTacheAAttribuer.setLayoutParams(params);
        mNbTacheAAtribuer = 0;

        for (int i = 0; i < taches.size(); i++) {
            String pseudoTache = taches.get(i).getJoueur();
            String texte = String.valueOf(taches.get(i).getCarte().getValeur());
            String option = taches.get(i).getOption();
            if (!(option.equals(" ") || option.equals("")))
                texte += "(" + option + ") ";
            else
                texte += "";
            // TACHE NON ATTRIBUEES
            if (pseudoTache.equals("")) {
                mNbTacheAAtribuer++; // Masque la ligne si aucune tache non attribuée
                if (i == 0) // Affiche le titre si au moins une tache à attribuer
                    mTitreTachesAAtribuer.setVisibility(View.VISIBLE);
                TextView tv = new TextView(this);
                tv.setText(texte);
                tv.setTextSize(Dimension.SP, 30);
                tv.setTextColor(getResources().getColor(getCouleurCarte(taches.get(i).getCarte().getCouleur())));
                params.setMargins(50, 0, 0, 0);
                tv.setLayoutParams(params);
                tv.setTag("tacheAAttribuer_" + taches.get(i).getCarte().getCouleur() + "_" + taches.get(i).getCarte().getValeur());
                tv.setOnTouchListener(this);
                trTacheAAttribuer.addView(tv);

            }
            // TACHE ATTRIBUEES A UN JOUEUR
            else {
                if (i == 0) // Aucune tache a attribuee, on masque le titre
                    mTitreTachesAAtribuer.setVisibility(View.GONE);
                int indexPseudo = getIndexPseudo(pseudoTache);

                // Affichage ligne
                if (ligneTache < ++nbTacheParJoueur[indexPseudo] && ligneTache < tableLignesTaches.length) {
                    TableRow tr = findViewById(tableLignesTaches[ligneTache]);
                    tr.setVisibility(View.VISIBLE);
                    ligneTache++;

                    // On vide toutes cases de la ligne
                    for (int j = 0; j < mListePseudo.length; j++) {
                        TextView tv;
                        switch (ligneTache) {
                            case 1:
                            default:
                                tv = findViewById(tableTaches1[j]);
                                break;
                            case 2:
                                tv = findViewById(tableTaches2[j]);
                                break;
                            case 3:
                                tv = findViewById(tableTaches3[j]);
                                break;
                            case 4:
                                tv = findViewById(tableTaches4[j]);
                                break;
                        }
                        tv.setText("");
                    }
                }

                TextView tva;
                switch (nbTacheParJoueur[indexPseudo]) {
                    case 1:
                    default:
                        tva = findViewById(tableTaches1[getIndexPseudo(pseudoTache)]);
                        break;
                    case 2:
                        tva = findViewById(tableTaches2[getIndexPseudo(pseudoTache)]);
                        break;
                    case 3:
                        tva = findViewById(tableTaches3[getIndexPseudo(pseudoTache)]);
                        break;
                    case 4:
                        tva = findViewById(tableTaches4[getIndexPseudo(pseudoTache)]);
                        break;
                }
                tva.setText(texte);
                tva.setTextColor(getResources().getColor(getCouleurCarte(taches.get(i).getCarte().getCouleur())));
                String tag = "tache_" + taches.get(i).getCarte().getCouleur() + "_" + taches.get(i).getCarte().getValeur();
                if (taches.get(i).isRealise()) {
                    tva.setBackgroundColor(getResources().getColor(R.color.blanc));
                    tag += "_1";
                } else {
                    tva.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tag += "_0";
                }
                tva.setTag(tag);
                tva.setOnClickListener(this);
            }
        }

        // Liste des pseudos
        if (ligneTache == 0) {
            TableRow tr = findViewById(R.id.ligne_tache_pseudo);
            tr.setVisibility(View.GONE);
        } else {
            TableRow tr = findViewById(R.id.ligne_tache_pseudo);
            tr.setVisibility(View.VISIBLE);
        }
        // Masque les lignes vides
        for (int i = ligneTache; i < tableLignesTaches.length; i++) {
            TableRow tr = findViewById(tableLignesTaches[i]);
            tr.setVisibility(View.GONE);
        }
    }

    private void afficheCommunications(ArrayList<Pli> plis) {
        int nbPlis = plis.size();
        boolean pseudoTrouve = false;
        // Supprime toutes les com
        for (int value : tableCommunication) {
            TextView tva = findViewById(value);
            tva.setText("");
        }

        // Affiche les nouvelles com
        for (int i = 0; i < nbPlis; i++) {
            if (plis.get(i).getNomJoueur().equals(mPseudo)) {
                mCommunicationFaite = true;
                mBoutonComm.setImageResource(R.drawable.jeton_communication_on);
                pseudoTrouve = true;
            }
            String texte = plis.get(i).getCarte().getValeur() + " " + plis.get(i).getCommunication();
            TextView tva = findViewById(tableCommunication[getIndexPseudo(plis.get(i).getJoueur())]);
            tva.setText(texte);
            tva.setTextColor(getResources().getColor(getCouleurCarte(plis.get(i).getCarte().getCouleur())));
        }
        if (!pseudoTrouve && !mCommunicationAChoisir) {
            mCommunicationFaite = false;
            mBoutonComm.setImageResource(R.drawable.jeton_communication_off);
            mBoutonComm.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            mTableauCommunication.setVisibility(View.GONE);
        }
        if (nbPlis > 0)
            mTableauCommunication.setVisibility(View.VISIBLE);
    }

    private int getIndexPseudo(String pseudo) {
        int index = 0;
        for (int i = 0; i < mListePseudo.length; i++) {
            if (mListePseudo[i].equals(pseudo)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getCouleurCarte(String couleur) {
        int idCouleur;
        switch (couleur) {
            case "bleu":
                idCouleur = R.color.bleu;
                break;
            case "jaune":
                idCouleur = R.color.jaune;
                break;
            case "rose":
                idCouleur = R.color.rose;
                break;
            case "vert":
                idCouleur = R.color.vert;
                break;
            case "fusee":
            default:
                idCouleur = R.color.noir;
                break;
        }
        return idCouleur;
    }

    private void debug(String message) {
        Log.d("PGR", message);
    }

    private void parseXML(Document doc) {

        Element element = doc.getDocumentElement();
        element.normalize();

        // Cartes que le joueur a en main
        ArrayList<Carte> listeCartesMainJoueur = parseNoeudsCarte(doc);
        afficheCartes(listeCartesMainJoueur);

        // Joueurs
        ArrayList<Joueur> listeJoueurs = parseNoeudsJoueur(doc);
        affichePseudos(listeJoueurs);

        // Pli en cours
        ArrayList<Pli> listeCartesPliEnCours = parseNoeudsPli(doc, "Pli");
        affichePliEnCours(listeCartesPliEnCours);

        // Communications
        ArrayList<Pli> listeCommunications = parseNoeudsPli(doc, "Communications");
        afficheCommunications(listeCommunications);

        // Mission
        parseMission(doc);

        // Taches
        ArrayList<Tache> listeTaches = parseNoeudsTache(doc);
        afficheTaches(listeTaches);
    }

    private ArrayList<Carte> parseNoeudsCarte(Document doc) {
        Node NoeudCartes = getNoeudUnique(doc, "Cartes");

        String couleur = "";
        int valeur = 0;
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
                }
            }
            Carte carte = new Carte(couleur, valeur);
            listeCartes.add(carte);
        }

        return listeCartes;
    }

    private ArrayList<Joueur> parseNoeudsJoueur(Document doc) {
        Node NoeudJoueurs = getNoeudUnique(doc, "Joueurs");

        String pseudo = "";
        int admin = 0;
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        if (NoeudJoueurs.getAttributes().item(0).getNodeName().equals("commandant"))
            mCommandant = NoeudJoueurs.getAttributes().item(0).getNodeValue();

        for (int i = 0; i < NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
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
            Joueur joueur = new Joueur(pseudo, admin);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    private ArrayList<Pli> parseNoeudsPli(Document doc, String nomDuNoeud) {
        Node NoeudCartes = getNoeudUnique(doc, nomDuNoeud);

        String pseudo = "";
        String couleur = "";
        int valeur = 0;
        String com = "";
        ArrayList<Pli> listePli = new ArrayList<>();

        if (nomDuNoeud.equals("Pli")) {
            mNumeroPli = Integer.parseInt(NoeudCartes.getAttributes().item(0).getNodeValue());
            String titrePli = getResources().getString(R.string.pli_en_cours) + " - numéro " + mNumeroPli;
            mTitrePli.setText(titrePli);
        }

        for (int i = 0; i < NoeudCartes.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudCartes.getChildNodes().item(i);
            Log.d("PGR-XML-Pli", noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Pli", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "joueur":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "couleur":
                        couleur = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "valeur":
                        valeur = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "com":
                        com = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                }
            }
            Pli pli = new Pli(pseudo, new Carte(couleur, valeur), com);
            listePli.add(pli);
        }

        return listePli;
    }

    private ArrayList<Tache> parseNoeudsTache(Document doc) {
        Node NoeudTaches = getNoeudUnique(doc, "Taches");

        String pseudo = "";
        String couleur = "";
        int valeur = 0;
        String option = "";
        boolean realise = false;
        ArrayList<Tache> listeTaches = new ArrayList<>();

        for (int i = 0; i < NoeudTaches.getChildNodes().getLength(); i++) { // Parcours toutes les taches
            Node noeudCarte = NoeudTaches.getChildNodes().item(i);
            Log.d("PGR-XML-Tache", noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud tache
                Log.d("PGR-XML-Tache", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "joueur":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "couleur":
                        couleur = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "valeur":
                        valeur = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "option_tache":
                        option = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "realise":
                        realise = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue()) == 1;
                        break;
                }
            }
            Tache tache = new Tache(pseudo, new Carte(couleur, valeur), option, realise);
            listeTaches.add(tache);
        }

        return listeTaches;
    }

    private Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
    }

    private void parseMission(Document doc) {
        Node noeudMission = getNoeudUnique(doc, "Mission");
        // num + description + zone_silence
        for (int j = 0; j < noeudMission.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud
            Log.d("PGR-XML-Mission", noeudMission.getAttributes().item(j).getNodeName() + "_" + noeudMission.getAttributes().item(j).getNodeValue());
            switch (noeudMission.getAttributes().item(j).getNodeName()) {
                case "num":
                    String titreMission = "Mission " + noeudMission.getAttributes().item(j).getNodeValue();
                    mTitreTaches.setText(titreMission);
                    break;
                case "description":
                    mObjectifCommun.setText(noeudMission.getAttributes().item(j).getNodeValue());
                    break;
                case "zone_silence":
                    if (!noeudMission.getAttributes().item(j).getNodeValue().isEmpty())
                        mZoneSilence = Integer.parseInt(noeudMission.getAttributes().item(j).getNodeValue());
                    else
                        mZoneSilence = 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + noeudMission.getAttributes().item(j).getNodeName());
            }
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);
        String[] chaine = mCarteActive.getTag().toString().split("_"); // ex : carte_bleu_2
        String couleurCarteActive = chaine[1];
        String valeurCarteActive = chaine[2];

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Sauvegarde du contexte de départ
                Log.d("PGR-OnDrag", "ACTION_DRAG_STARTED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                vueArrivee = null;
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_ENTERED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_EXITED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                Log.d("PGR-OnDrag", "ACTION_DROP " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                if (v.getId() == R.id.tableau_table)
                    vueArrivee = v;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_ENDED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(normalShape);
                if (event.getResult() && vueArrivee != null && vueArrivee.getId() == R.id.tableau_table && v.getId() == R.id.tableau_table) {
                    // Carte jouée
                    Log.d("PGR-Drag&Drop", urlJoueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&joueur=" + mPseudo);
                    new TacheJoueCarte().execute(urlJoueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&joueur=" + mPseudo);
                } else {
                    Log.d("PGR-Drag&Drop", "Drop ignoré");
                }
                startRefreshAuto();
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getTag() != null && v.getTag().toString().startsWith("carte_")) {
            jourCarteFromMainJoueur(v);
        }

        return true;
    }

    private void jourCarteFromMainJoueur(View v) {
        mCarteActive = findViewById(v.getId());
        String[] chaine = mCarteActive.getTag().toString().split("_"); // ex : carte_bleu_2
        String couleurCarteActive = chaine[1];
        String valeurCarteActive = chaine[2];

        // Si c'est pour communiquer
        // -------------------------
        if (mCommunicationAChoisir) {
            boolean autorise = true;
            // Est-ce que la communication de cette carte est autorisée ?
            if (couleurCarteActive.equals("fusee")) // Les autres vérifications sont faites côtés php
                autorise = false;
            if (autorise) {
                new TacheCommuniqueCarte().execute(urlCommuniqueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&pseudo=" + mPseudo + "&silence=" + mZoneSilence);
            } else
                Toast.makeText(getBaseContext(), "Cette carte n'est pas autorisée", Toast.LENGTH_SHORT).show();
        }
        // Joue la carte si c'est mon tour
        // -------------------------------
        else {
            boolean jeJoueUneCarteAutorisee = false;
            // A qui est-ce le tour ?
            String pseudoQuiDoitJouer = getPseudoQuiDoitJouer();
            String messageErreur = "C'est à " + pseudoQuiDoitJouer + " de jouer";
            // Quelle est la couleur de la première carte jouée dans ce pli ?
            if (pseudoQuiDoitJouer.equals(mPseudo)) { // Si c'est mon tour dans ce pli, on regarde la couleur jouée
                ImageView iv = findViewById(tableIdImageCartePli[0]); // Première carte jouée dans le pli en cours
                String couleurDemandee = "";
                if (iv != null && iv.getVisibility() == View.VISIBLE)
                    couleurDemandee = iv.getTag().toString().split("_")[1]; // Couleur de la première carte du pli
                // Si je joue la couleur demandée => OK
                if (couleurDemandee.equals("") || couleurDemandee.equals(couleurCarteActive))
                    jeJoueUneCarteAutorisee = true;
                else { // Sinon, on vérifie que je n'ai plus la couleur demandée dans ma main
                    // On regarde toutes les cartes de la main du joueur
                    for (int value : tableIdImageCarteMain) {
                        ImageView iv2 = findViewById(value);
                        jeJoueUneCarteAutorisee = true;
                        if (iv2 == null)
                            break;
                        // Si une carte visible est de la couleur demandée
                        if (iv2.getVisibility() == View.VISIBLE && iv2.getTag().toString().split("_")[1].equals(couleurDemandee)) {
                            // Si j'ai la couleur demandée, je ne peux pas jouer une autre carte
                            jeJoueUneCarteAutorisee = false;
                            messageErreur = "C'est " + couleurDemandee + " demandé !";
                            break;
                        }
                    }
                }
            }
            // Si je peux jouer la couleur ou si tout le monde a joué le pli
            if (jeJoueUneCarteAutorisee || pseudoQuiDoitJouer.equals("")) {
                //new TacheJoueCarte().execute(urlJoueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&joueur=" + mPseudo);
                startDrag(v);
            } else {
                Toast.makeText(getBaseContext(), messageErreur, Toast.LENGTH_SHORT).show();
                stopRefreshAuto(); // Stop le refresh auto pour laisse l'animation se faire
                Animation animation = new AlphaAnimation((float) 0.5, 0);
                animation.setDuration(200);
                animation.setInterpolator(new LinearInterpolator()); //do not alter
                animation.setRepeatCount(6);
                animation.setRepeatMode(Animation.REVERSE);
                animation.getFillAfter();
                mCarteActive.startAnimation(animation);
                startRefreshAutoWithDelai(); // Relance tout de suite l'animation mais avec un délai de 2s avant de démarrer
            }
        }
    }

    private void startDrag(View v) {
        stopRefreshAuto();
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);
        v.setVisibility(View.INVISIBLE);
        mCarteActive = findViewById(v.getId());
    }

    /**
     * Classe qui permet de mettre à jour en base la main d'un joueur (update main + ajout carte sur la table)
     * -> Retourne la liste des cartes du joueur demandé
     */
    private class TacheJoueCarte extends AsyncTask<String, Void, Integer> {
        String result;

        @Override
        protected Integer doInBackground(String... strings) {
            //ArrayList<Carte> cartes = new ArrayList<>();
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

            return result.length();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Toast.makeText(getBaseContext(), "Ce n'est pas ton tour !!", Toast.LENGTH_SHORT).show();
            } else {
                // Masque la carte
                mCarteActive.setVisibility(View.GONE);
            }
            super.onPostExecute(integer);
        }
    }

    /**
     * Classe qui permet de communiquer en base la carte d'un joueur
     */
    private class TacheCommuniqueCarte extends AsyncTask<String, Void, Integer> {
        String result;

        @Override
        protected Integer doInBackground(String... strings) {
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

            return result.length();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Toast.makeText(getBaseContext(), "Cette carte n'est pas autorisée", Toast.LENGTH_SHORT).show();
            } else {
                mCommunicationFaite = true;
                mCommunicationAChoisir = false;
                mBoutonComm.setImageResource(R.drawable.jeton_communication_on);
                mBoutonComm.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            super.onPostExecute(integer);
        }
    }

    /**
     * Classe qui permet de récupérer en base toutes les informations du jeu The Crew d'un joueur
     * -> Retourne l'ensemble des informations à afficher au joueurs sous forme XML
     * * - Cartes de la main du joueur
     * * - Liste des joueurs
     * * - Cartes du pli en cours
     * * - Nom du commandant
     * * - Communications des joueurs
     * * - Numéro et descriptions de la mission
     * * - Liste des tâches et status (réalisé ou non)
     */
    private class TacheGetInfoTheCrew extends AsyncTask<String, Void, Document> {

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
            parseXML(doc);
            mMajTerminee = true;
            super.onPostExecute(doc);
        }
    }

}
