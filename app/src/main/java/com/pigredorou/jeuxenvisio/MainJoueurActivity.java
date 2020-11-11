package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.Pli;
import com.pigredorou.jeuxenvisio.objets.Tache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainJoueurActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // Constantes
    private static final int[] imagesJaune = {0, R.drawable.jaune_1, R.drawable.jaune_2, R.drawable.jaune_3, R.drawable.jaune_4, R.drawable.jaune_5, R.drawable.jaune_6, R.drawable.jaune_7, R.drawable.jaune_8, R.drawable.jaune_9};
    private static final int[] imagesRose = {0, R.drawable.rose_1, R.drawable.rose_2, R.drawable.rose_3, R.drawable.rose_4, R.drawable.rose_5, R.drawable.rose_6, R.drawable.rose_7, R.drawable.rose_8, R.drawable.rose_9};
    private static final int[] imagesVert = {0, R.drawable.vert_1, R.drawable.vert_2, R.drawable.vert_3, R.drawable.vert_4, R.drawable.vert_5, R.drawable.vert_6, R.drawable.vert_7, R.drawable.vert_8, R.drawable.vert_9};
    private static final int[] imagesBleu = {0, R.drawable.bleu_1, R.drawable.bleu_2, R.drawable.bleu_3, R.drawable.bleu_4, R.drawable.bleu_5, R.drawable.bleu_6, R.drawable.bleu_7, R.drawable.bleu_8, R.drawable.bleu_9};
    private static final int[] imagesFusee = {0, R.drawable.fusee_1, R.drawable.fusee_2, R.drawable.fusee_3, R.drawable.fusee_4};
    private static final int[] tableIdPseudo = {R.id.table_pseudo_joueur1, R.id.table_pseudo_joueur2, R.id.table_pseudo_joueur3, R.id.table_pseudo_joueur4, R.id.table_pseudo_joueur5};
    private static final int[] tableIdCarte = {R.id.table_carte_joueur1, R.id.table_carte_joueur2, R.id.table_carte_joueur3, R.id.table_carte_joueur4, R.id.table_carte_joueur5};
    private static final int[] tableIdImageCarte = {R.id.table_carte_image_joueur1, R.id.table_carte_image_joueur2, R.id.table_carte_image_joueur3, R.id.table_carte_image_joueur4, R.id.table_carte_image_joueur5};
    private static final String urlGetDistribue = MainActivity.url + "getDistribution.php";
    private static final String urlJoueCarte = MainActivity.url + "majTable.php?salon=";
    private static final String urlAfficheTable = MainActivity.url + "getTable.php?salon=";
    private static final String urlAfficheTache = MainActivity.url + "getTaches.php?salon=";
    private static final String urlGetCommandant = MainActivity.url + "getCommandant.php?salon=";
    private static final String urlGetOjectifCommun = MainActivity.url + "getObjectif.php?salon=";
    private static final String urlCommuniqueCarte = MainActivity.url + "majCommunication.php?salon=";
    // Variables globales
    private String[] mListePseudo; // Liste des pseudos des joueurs
    private String mPseudo; // Pseudo du joueur
    private String mCommandant; // Pseudo du commandant de la partie (fusée 4)
    private int mIdSalon;
    private boolean mCommunicationFaite = false;
    private boolean mCommunicationAChoisir = false;
    // Elements de la vue
    private ScrollView mTable;
    private ImageView mCarteActive;
    private TextView mTextResultat;
    private TextView mTitre;
    private TextView mHeureRefresh;
    private TextView mObjectifCommun;
    // Communication
    private TextView mCommPseudoJoueur1;
    private TextView mCommPseudoJoueur2;
    private TextView mCommPseudoJoueur3;
    private TextView mCommPseudoJoueur4;
    private TextView mCommPseudoJoueur5;
    private TextView mCommJoueur1;
    private TextView mCommJoueur2;
    private TextView mCommJoueur3;
    private TextView mCommJoueur4;
    private TextView mCommJoueur5;
    private ImageView mBoutonComm;
    // Tâches
    private TextView mTachePseudoJoueur1;
    private TextView mTachePseudoJoueur2;
    private TextView mTachePseudoJoueur3;
    private TextView mTachePseudoJoueur4;
    private TextView mTachePseudoJoueur5;
    private TextView mTaches1Joueur1;
    private TextView mTaches1Joueur2;
    private TextView mTaches1Joueur3;
    private TextView mTaches1Joueur4;
    private TextView mTaches1Joueur5;
    private TextView mTaches2Joueur1;
    private TextView mTaches2Joueur2;
    private TextView mTaches2Joueur3;
    private TextView mTaches2Joueur4;
    private TextView mTaches2Joueur5;
    // Auto resfresh
    private Button mBoutonRefreshAuto;
    private Boolean mRefreshAuto;
    Thread t;
    // Gestion du double clic
    int mClickCount = 0;
    int mLastViewID = 0;
    long mStartTimeClick;
    long mDurationClick;
    static final int MAX_DURATION_CLICK = 200;
    static final int MAX_DURATION_DOUBLE_CLICK = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_main_joueur);

        // ENTETE
        mTitre = findViewById(R.id.titre_jeu);
        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);
        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setTag("boutonRetour");
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

        // Liste des pseudos dans l'ordre de jeu
        new MainJoueurActivity.TacheGetJoueurs().execute(MainActivity.urlGetJoueurs+mIdSalon);

        // Nom du commandant pour la partie
        new MainJoueurActivity.TacheGetCommandant().execute(urlGetCommandant+mIdSalon);

        // Affiche un message chargement le temps de récupérer les informations en base
        mTextResultat = findViewById(R.id.resultat);
        mTextResultat.setText(R.string.Chargement);
        // Recupère les cartes du joueur
        if (mPseudo != null)
            new MainJoueurActivity.TacheGetCartesMainJoueur().execute(urlGetDistribue + "?joueur=" + mPseudo + "&salon=" + mIdSalon);
        else {
            Toast.makeText(this, "Impossible de trouver les cartes du joueur", Toast.LENGTH_SHORT).show();
            mTextResultat.setText(R.string.chargement_impossible);
        }

        // Table
        mTable = findViewById(R.id.table);
        ImageView boutonTable = findViewById(R.id.bouton_table);
        boutonTable.setOnClickListener(this);
        new TacheAfficheTable().execute(urlAfficheTable+mIdSalon);

        // Communications
        chargeVuesCommunication();
        // TODO : Si click sur bouton + double clic sur carte du jeu, affectation automatique de la communication
        // TODO : Si 1 seule carte '=", sinon + forte ou + faible carte
        // TODO : Répondre choix impossible si pas un de ces cas

        // Objectifs commun
        mObjectifCommun = findViewById(R.id.objectif_commun);
        new TacheGetDescription().execute(urlGetOjectifCommun+mIdSalon);
        
        // Taches
        new TacheGetTaches().execute(urlAfficheTache+mIdSalon);
        chargeVuesTaches();
        // TODO : affichage et affectation des taches
        // TODO : si toutes les taches sont réalisés => Feu d'artifice !!
        // TODO : proposer de passer à la mission suivante : numMission+1, distribue les cartes, distribue taches, reset communications
        // TODO : Si plus aucune carte dans la main du joueur, faire un refresh complet toutes les 5 secondes

        // Refresh auto
        startRefreshAuto();
        mBoutonRefreshAuto = findViewById(R.id.bouton_refresh);
        mBoutonRefreshAuto.setOnClickListener(this);
        mHeureRefresh = findViewById(R.id.heure_refresh);
    }

    private void chargeVuesCommunication() {
        mCommPseudoJoueur1 = findViewById(R.id.comm_pseudo_joueur1);
        mCommPseudoJoueur2 = findViewById(R.id.comm_pseudo_joueur2);
        mCommPseudoJoueur3 = findViewById(R.id.comm_pseudo_joueur3);
        mCommPseudoJoueur4 = findViewById(R.id.comm_pseudo_joueur4);
        mCommPseudoJoueur5 = findViewById(R.id.comm_pseudo_joueur5);
        mCommJoueur1 = findViewById(R.id.communication_joueur1);
        mCommJoueur2 = findViewById(R.id.communication_joueur2);
        mCommJoueur3 = findViewById(R.id.communication_joueur3);
        mCommJoueur4 = findViewById(R.id.communication_joueur4);
        mCommJoueur5 = findViewById(R.id.communication_joueur5);
        mBoutonComm = findViewById(R.id.bouton_communication);
        mBoutonComm.setOnClickListener(this);
    }

    private void chargeVuesTaches() {
        mTachePseudoJoueur1 = findViewById(R.id.tache_pseudo_joueur1);
        mTachePseudoJoueur2 = findViewById(R.id.tache_pseudo_joueur2);
        mTachePseudoJoueur3 = findViewById(R.id.tache_pseudo_joueur3);
        mTachePseudoJoueur4 = findViewById(R.id.tache_pseudo_joueur4);
        mTachePseudoJoueur5 = findViewById(R.id.tache_pseudo_joueur5);
        mTaches1Joueur1 = findViewById(R.id.tache_joueur1);
        mTaches1Joueur2 = findViewById(R.id.tache_joueur2);
        mTaches1Joueur3 = findViewById(R.id.tache_joueur3);
        mTaches1Joueur4 = findViewById(R.id.tache_joueur4);
        mTaches1Joueur5 = findViewById(R.id.tache_joueur5);
        mTaches2Joueur1 = findViewById(R.id.tache2_joueur1);
        mTaches2Joueur2 = findViewById(R.id.tache2_joueur2);
        mTaches2Joueur3 = findViewById(R.id.tache2_joueur3);
        mTaches2Joueur4 = findViewById(R.id.tache2_joueur4);
        mTaches2Joueur5 = findViewById(R.id.tache2_joueur5);
        mTaches1Joueur1.setTag("Todo");
        mTaches1Joueur2.setTag("Todo");
        mTaches1Joueur3.setTag("Todo");
        mTaches1Joueur4.setTag("Todo");
        mTaches1Joueur5.setTag("Todo");
        mTaches2Joueur1.setTag("Todo");
        mTaches2Joueur2.setTag("Todo");
        mTaches2Joueur3.setTag("Todo");
        mTaches2Joueur4.setTag("Todo");
        mTaches2Joueur5.setTag("Todo");
    }

    private void affichePseudos() {
        switch (mListePseudo.length) {
            case 5:
                mCommPseudoJoueur5.setText(mListePseudo[4]);
                mCommPseudoJoueur5.setVisibility(View.VISIBLE);
                mCommJoueur5.setVisibility(View.VISIBLE);
                mTachePseudoJoueur5.setText(mListePseudo[4]);
                mTachePseudoJoueur5.setVisibility(View.VISIBLE);
                mTaches1Joueur5.setVisibility(View.VISIBLE);
                mTaches2Joueur5.setVisibility(View.VISIBLE);
            case 4:
                mCommPseudoJoueur4.setText(mListePseudo[3]);
                mCommPseudoJoueur4.setVisibility(View.VISIBLE);
                mCommJoueur4.setVisibility(View.VISIBLE);
                mTachePseudoJoueur4.setText(mListePseudo[3]);
                mTachePseudoJoueur4.setVisibility(View.VISIBLE);
                mTaches1Joueur4.setVisibility(View.VISIBLE);
                mTaches2Joueur4.setVisibility(View.VISIBLE);
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
        String pseudoQuiDoitJoueur="";
        ImageView iv;
        TextView tv;
        // Parcourt les images pour savoir si c'est mon tour
        for(int i=0;i<tableIdImageCarte.length;i++) {
            iv = findViewById(tableIdImageCarte[i]);
            tv = findViewById(tableIdPseudo[i]);
            // On prends le pseudo de la première image invisible
            if(iv.getVisibility()==View.INVISIBLE) {
                pseudoQuiDoitJoueur=tv.getText().toString();
                debug("pseudoQuiDoitJoueur :"+pseudoQuiDoitJoueur);
                break;
            }
        }
        return pseudoQuiDoitJoueur;
    }

    private void startRefreshAuto() {
        if (t == null || !t.isAlive()) {
            t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(5000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateTextView();
                                    majable();
                                    // TODO : Ajouter la MAJ des taches
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
        switch(v.getId()) {
            case R.id.bouton_refresh :
                if (mRefreshAuto) {
                    mBoutonRefreshAuto.setTextColor(getResources().getColor(R.color.blanc));
                    stopRefreshAuto();
                }
                else {
                    mBoutonRefreshAuto.setTextColor(getResources().getColor(R.color.noir));
                    startRefreshAuto();
                }
                break;
            case R.id.bouton_retour :
                finish();
                break;
            case R.id.bouton_communication :
                if (!mCommunicationFaite) {
                    mBoutonComm.setBackgroundColor(getResources().getColor(R.color.blanc));
                    Toast.makeText(getBaseContext(),"Choisi la carte à communiquer", Toast.LENGTH_SHORT).show();
                    mCommunicationAChoisir=true;
                }
                break;
            case R.id.bouton_table :
                if (mTable.getVisibility() == View.GONE)
                    mTable.setVisibility(View.VISIBLE);
                else
                    mTable.setVisibility(View.GONE);
                break;
            case R.id.tache_joueur1 :
            case R.id.tache_joueur2 :
            case R.id.tache_joueur3 :
            case R.id.tache_joueur4 :
            case R.id.tache_joueur5 :
            case R.id.tache2_joueur1 :
            case R.id.tache2_joueur2 :
            case R.id.tache2_joueur3 :
            case R.id.tache2_joueur4 :
            case R.id.tache2_joueur5 :
                TextView tache = findViewById(v.getId());
                if (tache.getTag().toString().equals("Todo")) {
                    tache.setTag("Done");
                    tache.setBackgroundColor(getResources().getColor(R.color.blanc));
                }
                else {
                    tache.setTag("Todo");
                    tache.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                // TODO : MAJ en base de la réalisation de la tache
                break;
            default:
                break;
        }

        // Mise à jour de la table
        majable();
        // Mise à jour des tâches
        new TacheGetTaches().execute(urlAfficheTache + mIdSalon);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long time;
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            // Appuie sur l'écran
            case MotionEvent.ACTION_DOWN:
                if (mClickCount == 1) {
                    time = System.currentTimeMillis();

                    // Si temps entre 2 clicks trop long, on retourne à 0
                    if ( (time- mStartTimeClick)>MAX_DURATION_DOUBLE_CLICK)
                        mClickCount=0;
                    if ( mLastViewID != v.getId())
                        mClickCount=0;
                }
                mStartTimeClick = System.currentTimeMillis();
                mClickCount++;
                mLastViewID = v.getId();
                break;
            // Relanche l'écran
            case MotionEvent.ACTION_UP:
                // Temps en appuie et relache
                time = System.currentTimeMillis() - mStartTimeClick;
                mDurationClick =  mDurationClick + time;
                if(mClickCount == 2)
                {
                    if(mDurationClick <= MAX_DURATION_CLICK)
                    {
                        // On a un double clic !
                        doublicClic(v);
                    }
                    mClickCount = 0;
                    mDurationClick = 0;
                    break;
                }
        }
        return true;
    }

    /**
     * Gestion des actions liées à un double clic
     * @param v : la vue sur laquelle le double clic a été réalisé
     */
    private void doublicClic(View v) {
        if (v.getTag() != null && v.getTag().toString().startsWith("carte_")) {
            mCarteActive = findViewById(v.getId());
            String[] chaine = mCarteActive.getTag().toString().split("_"); // ex : carte_bleu_2
            String couleurCarteActive = chaine[1];
            String valeurCarteActive = chaine[2];

            // Si c'est pour communiquer
            if (mCommunicationAChoisir) {
                Toast.makeText(getBaseContext(), "En cours de dev", Toast.LENGTH_SHORT).show();
                // Todo : valeur communication ? => +, =, - ?
                new TacheCommuniqueCarte().execute(urlCommuniqueCarte+mIdSalon+"&couleur_carte="+couleurCarteActive+"&valeur_carte="+valeurCarteActive+"&joueur="+mPseudo);
                mCommunicationAChoisir=false;
            }
            // Joue la carte si c'est mon tour
            else {
                String pseudoQuiDoitJouer = getPseudoQuiDoitJouer();
                // Si c'est mon tour ou si tout le monde a joué le pli
                if (pseudoQuiDoitJouer.equals(mPseudo) || pseudoQuiDoitJouer.equals("")) {

                    new TacheJoueCarte().execute(urlJoueCarte+mIdSalon+"&couleur_carte="+couleurCarteActive+"&valeur_carte="+valeurCarteActive+"&joueur="+mPseudo);
                    // Mise à jour de la table
                    majable();
                }
                else
                    Toast.makeText(getBaseContext(), "C'est à "+pseudoQuiDoitJouer+" de jouer", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void majable() {
        // Mise à jour de la table si elle est affichée
        if (mTable.getVisibility() == View.VISIBLE) {
            new TacheAfficheTable().execute(urlAfficheTable + mIdSalon);
        }
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
                carte.setId(setId(i));
                carte.setOnTouchListener(this);
                tableauCartes.addView(carte);
            }
    }

    private int setId(int idCarte) {
        int retour = 0;
        switch (idCarte) {
            case 0:
                retour = R.id.carte_1;
                break;
            case 1:
                retour = R.id.carte_2;
                break;
            case 2:
                retour = R.id.carte_3;
                break;
            case 3:
                retour = R.id.carte_4;
                break;
            case 4:
                retour = R.id.carte_5;
                break;
            case 5:
                retour = R.id.carte_6;
                break;
            case 6:
                retour = R.id.carte_7;
                break;
            case 7:
                retour = R.id.carte_8;
                break;
            case 8:
                retour = R.id.carte_9;
                break;
            case 9:
                retour = R.id.carte_10;
                break;
            case 10:
                retour = R.id.carte_11;
                break;
            case 11:
                retour = R.id.carte_12;
                break;
            case 12:
                retour = R.id.carte_13;
                break;
            case 13:
                retour = R.id.carte_14;
                break;
            case 14:
                retour = R.id.carte_15;
                break;
            case 15:
                retour = R.id.carte_16;
                break;
            case 16:
                retour = R.id.carte_17;
                break;
            case 17:
                retour = R.id.carte_18;
                break;
            case 18:
                retour = R.id.carte_19;
                break;
            case 19:
                retour = R.id.carte_20;
                break;
        }

        return retour;
    }

    private void afficheTable(ArrayList<Pli> plis) {
        TextView pseudo;
        TextView carte;
        ImageView imageCarte;
        int nbJoueur=mListePseudo.length;
        int positionPremierJoueur=0;
        int positionJoueur=0;
        // On récupère la place du premier joueur
        if (plis != null && plis.size()>0) {
            for (int j=0;j<nbJoueur;j++) {
                if (mListePseudo[j].equals(plis.get(0).getJoueur())) {
                    positionPremierJoueur=j;
                    debug("posPremJoueur " + positionPremierJoueur);
                }
            }
        }
        else {
            // Si on a pas encore joué, on positionne le commandant comme premier joueur
            for(int i=0;i<nbJoueur;i++)
                if(mListePseudo[i].equals(mCommandant)) {
                    positionPremierJoueur=i;
                    break;
                }
        }
        // On parcours les id des pseudo
        for (int i=0; i<tableIdPseudo.length; i++) {
            pseudo = findViewById(tableIdPseudo[i]);
            carte = findViewById(tableIdCarte[i]);
            imageCarte = findViewById(tableIdImageCarte[i]);
            // Si moins de 5 joueurs, on retire de l'écran
            if (i>=nbJoueur) {
                pseudo.setVisibility(View.GONE);
                carte.setVisibility(View.GONE);
                imageCarte.setVisibility(View.GONE);
            }
            // Affichage de tous les pseudo dans l'ordre de jeu, même si le joueur n'a pas encore joué
            else {
                positionJoueur = (positionPremierJoueur+i)%nbJoueur;
                debug("posJoueurNonJoué " + i + " positionPremierJoueur " + positionPremierJoueur + " nbJoueur " + nbJoueur + " positionJoueur" + positionJoueur);
                // Si le joueur est le commmandant, on affiche le nom en noir
                if (mListePseudo[positionJoueur].equals(mCommandant))
                    pseudo.setTextColor(getResources().getColor(R.color.noir));
                else
                    pseudo.setTextColor(getResources().getColor(R.color.blanc));
                pseudo.setText(mListePseudo[positionJoueur]);
                pseudo.setVisibility(View.VISIBLE);

                if (i < plis.size()) {
                    imageCarte.setImageResource(getImageCarte(plis.get(i).getCarte().getCouleur(), plis.get(i).getCarte().getValeur()));
                    imageCarte.setVisibility(View.VISIBLE);
                    carte.setText(String.valueOf(plis.get(i).getCarte().getValeur()));
                    carte.setTextColor(getResources().getColor(getCouleurCarte(plis.get(i).getCarte().getCouleur())));
                    carte.setVisibility(View.VISIBLE);
                } else {
                    imageCarte.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private void afficheTaches(ArrayList<Tache> taches) {
        // TODO : Affiche une ligne avec les taches non attribuées
        // TODO : Afficher les tâches par joueur
    }

    // Ancienne fonction utilisée avant refonte
    private void afficheTaches2(ArrayList<Pli> plis) {
        String pseudoPrec="";
        TableRow ligneTaches = findViewById(R.id.tableau_taches);
        ligneTaches.removeAllViewsInLayout();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5, 5, 5, 5);
        ligneTaches.setLayoutParams(params);

        TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TextView tvTache = new TextView(this);
        paramsTV.setMargins(5, 0, 20, 0);
        tvTache.setLayoutParams(paramsTV);
        tvTache.setTextColor(getResources().getColor(R.color.noir));
        tvTache.setText(R.string.tachesAAtribuer);
        tvTache.setTextSize(20);
        tvTache.setGravity(View.TEXT_ALIGNMENT_CENTER);
        ligneTaches.addView(tvTache);

        // Affiche les taches
        for (int i=0; i<plis.size(); i++) {
            if (!pseudoPrec.equals(plis.get(i).getJoueur())) {
                if (pseudoPrec!="") {
                    // Affiche une case de séparation
                    TextView tvVide = new TextView(this);
                    paramsTV.setMargins(50, 0, 5, 0);
                    tvVide.setLayoutParams(paramsTV);
                    tvVide.setTextColor(getResources().getColor(R.color.noir));
                    tvVide.setText("  |  ");
                    tvVide.setTextSize(20);
                    ligneTaches.addView(tvVide);
                }

                String texte = plis.get(i).getJoueur() + " : ";
                TextView tvPseudo = new TextView(this);
                paramsTV.setMargins(15, 0, 5, 0);
                tvPseudo.setLayoutParams(paramsTV);
                tvPseudo.setText(texte);
                tvPseudo.setTextColor(getResources().getColor(R.color.blanc));
                tvPseudo.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tvPseudo.setTextSize(20);
                ligneTaches.addView(tvPseudo);
                pseudoPrec=plis.get(i).getJoueur();
            }
            String texte = String.valueOf(plis.get(i).getCarte().getValeur());
            String option = ""; //plis.get(i).getCarte().getOption();
            if (!option.startsWith(" "))
                texte += "(" + option + ") ";
            else
                texte += "";
            TextView tvValeurTache = new TextView(this);
            paramsTV.setMargins(5, 0, 5, 0);
            tvValeurTache.setLayoutParams(paramsTV);
            tvValeurTache.setText(texte);
            tvValeurTache.setTextColor(getResources().getColor(getCouleurCarte(plis.get(i).getCarte().getCouleur())));
            tvValeurTache.setGravity(View.TEXT_ALIGNMENT_CENTER);
            tvValeurTache.setTextSize(20);
            tvValeurTache.setTypeface(tvTache.getTypeface(), Typeface.BOLD);
            ligneTaches.addView(tvValeurTache);
        }
    }

    private int getCouleurCarte(String couleur) {
        int idCouleur;
        switch (couleur) {
            case "bleu" :
                idCouleur=R.color.bleu;
                break;
            case "jaune" :
                idCouleur=R.color.jaune;
                break;
            case "rose" :
                idCouleur=R.color.rose;
                break;
            case "vert" :
                idCouleur=R.color.vert;
                break;
            case "fusee" :
            default:
                idCouleur=R.color.noir;
                break;
        }
        return idCouleur;
    }

    private void debug(String message) {
        Log.d("PGR",message);
    }

    /**
     * Classe qui permet de récupère la liste des joueurs dans l'ordre de jeu
     * -> Retourne la liste
     */
    class TacheGetJoueurs extends AsyncTask<String, Void, ArrayList<String>> {
        String result;

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> pseudoJoueurs = new ArrayList<>();
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    String[] chaine = stringBuffer.split("_");
                    pseudoJoueurs.add(chaine[1]);
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }

            return pseudoJoueurs;
        }

        @Override
        protected void onPostExecute(ArrayList<String> joueurs) {
            mListePseudo = new String[joueurs.size()];
            for(int i=0;i<joueurs.size();i++) {
                mListePseudo[i] = joueurs.get(i);
                debug(joueurs.get(i));
            }
            affichePseudos();
            super.onPostExecute(joueurs);
        }
    }

    /**
     * Classe qui récupère le pseudo du commandant
     * -> Retourne le pseudo du commandant
     */
    class TacheGetCommandant extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... strings) {
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

            return result;
        }

        @Override
        protected void onPostExecute(String commandant) {
            mCommandant = commandant;
            super.onPostExecute(commandant);
        }
    }

    /**
     * Classe qui récupère la description de la mission
     * -> Affiche la description à l'écran
     */
    class TacheGetDescription extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... strings) {
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

            return result;
        }

        @Override
        protected void onPostExecute(String objectif) {
            mObjectifCommun.setText(objectif);
            super.onPostExecute(objectif);
        }
    }

    /**
     * Classe qui permet de récupère le pli en cours
     * -> Retourne le pli et l'affiche
     */
    class TacheAfficheTable extends AsyncTask<String, Void, ArrayList<Pli>> {
        String result;

        @Override
        protected ArrayList<Pli> doInBackground(String... strings) {
            ArrayList<Pli> plis = new ArrayList<>();
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    String[] chaine = stringBuffer.split("_");
                    Pli pli;
                    pli = new Pli(chaine[0], new Carte(chaine[1], Integer.parseInt(chaine[2])));
                    plis.add(pli);
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }

            return plis;
        }

        @Override
        protected void onPostExecute(ArrayList<Pli> plis) {
            afficheTable(plis);
            super.onPostExecute(plis);
        }
    }

    /**
     * Classe qui permet de récupère les taches
     * -> Retourne les taches et les affiche
     */
    class TacheGetTaches extends AsyncTask<String, Void, ArrayList<Tache>> {
        String result;

        @Override
        protected ArrayList<Tache> doInBackground(String... strings) {
            ArrayList<Tache> taches = new ArrayList<>();
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    String[] chaine = stringBuffer.split("_");
                    Tache tache;
                    // TODO : recupérer les taches
                    //if(chaine[3]!=null)
                    //    tache = new Tache(chaine[0], new Carte(chaine[1], Integer.parseInt(chaine[2]), chaine[3]));
                    //else
                        tache = new Tache(chaine[0], new Carte(chaine[1], Integer.parseInt(chaine[2])));
                    taches.add(tache);
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }

            return taches;
        }

        @Override
        protected void onPostExecute(ArrayList<Tache> taches) {
            afficheTaches(taches);
            super.onPostExecute(taches);
        }
    }

    /**
    * Classe qui permet de récupérer en base la main d'un joueur
    * -> Retourne la liste des cartes du joueur demandé
    */
    class TacheGetCartesMainJoueur extends AsyncTask<String, Void, ArrayList<Carte>> {
        String result;
        @Override
        protected ArrayList<Carte> doInBackground(String... strings) {
            //String myurl= "http://julie.et.pierre.free.fr/Commun/getDistribution.php";
            ArrayList<Carte> cartes = new ArrayList<>();
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    String[] chaine = stringBuffer.split("_");
                    Carte carte = new Carte(chaine[0], Integer.parseInt(chaine[1]));
                    cartes.add(carte);
                    string = String.format("%s%s", string, stringBuffer);

                }
                bufferedReader.close();
                result = string;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }

            return cartes;
        }

        @Override
        protected void onPostExecute(ArrayList<Carte> cartes) {
            mTextResultat.setText(result);
            afficheCartes(cartes);
            mTextResultat.setVisibility(View.GONE);
            super.onPostExecute(cartes);
        }
    }

    /**
     * Classe qui permet de mettre à jour en base la main d'un joueur (update main + ajout carte sur la table)
     * -> Retourne la liste des cartes du joueur demandé
     */
    class TacheJoueCarte extends AsyncTask<String, Void, Integer> {
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
                }
                else {
                    // Masque la carte
                    mCarteActive.setVisibility(View.GONE);
                }
            super.onPostExecute(integer);
        }
    }

    /**
     * Classe qui permet de communiquer en base la carte d'un joueur
     */
    class TacheCommuniqueCarte extends AsyncTask<String, Void, Void> {
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

        @Override
        protected void onPostExecute(Void aVoid) {
            mCommunicationFaite=true;
            mBoutonComm.setImageResource(R.drawable.jeton_communication_on);
            mBoutonComm.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            super.onPostExecute(aVoid);
        }
    }
}

