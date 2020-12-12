package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Pli;

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

public class BeloteActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // Constantes
    // Tableaux des resssources
    private static final int[] imagesPique = {0, 0, 0, 0, 0, 0, 0, R.drawable.pique_7, R.drawable.pique_8, R.drawable.pique_9, R.drawable.pique_10, R.drawable.pique_valet, R.drawable.pique_dame, R.drawable.pique_roi, R.drawable.pique_as};
    private static final int[] imagesCoeur = {0, 0, 0, 0, 0, 0, 0, R.drawable.coeur_7, R.drawable.coeur_8, R.drawable.coeur_9, R.drawable.coeur_10, R.drawable.coeur_valet, R.drawable.coeur_dame, R.drawable.coeur_roi, R.drawable.coeur_as};
    private static final int[] imagesTrefle = {0, 0, 0, 0, 0, 0, 0, R.drawable.trefle_7, R.drawable.trefle_8, R.drawable.trefle_9, R.drawable.trefle_10, R.drawable.trefle_valet, R.drawable.trefle_dame, R.drawable.trefle_roi, R.drawable.trefle_as};
    private static final int[] imagesCarreau = {0, 0, 0, 0, 0, 0, 0, R.drawable.carreau_7, R.drawable.carreau_8, R.drawable.carreau_9, R.drawable.carreau_10, R.drawable.carreau_valet, R.drawable.carreau_dame, R.drawable.carreau_roi, R.drawable.carreau_as};
    private static final int[] tableIdPseudo = {R.id.table_pseudo_joueur1, R.id.table_pseudo_joueur2, R.id.table_pseudo_joueur3, R.id.table_pseudo_joueur4, R.id.table_pseudo_joueur5};
    private static final int[] tableIdImageCarteMain = {R.id.carte_1, R.id.carte_2, R.id.carte_3, R.id.carte_4, R.id.carte_5, R.id.carte_6, R.id.carte_7, R.id.carte_8, R.id.carte_9, R.id.carte_10, R.id.carte_11, R.id.carte_12, R.id.carte_13, R.id.carte_14, R.id.carte_15, R.id.carte_16, R.id.carte_17, R.id.carte_18, R.id.carte_19, R.id.carte_20};
    private static final int[] tableIdImageCartePli = {R.id.table_carte_image_joueur1, R.id.table_carte_image_joueur2, R.id.table_carte_image_joueur3, R.id.table_carte_image_joueur4, R.id.table_carte_image_joueur5};
    // URLs des actions en base
    private static final String urlBelote = MainActivity.url + "belote.php?partie=";
    private static final String urlJoueCarte = MainActivity.url + "majTable.php?partie=";
    private static final String urlDistribueBelote = MainActivity.url + "distribueBelote.php?partie=";
    // Variables globales
    private String[] mListePseudo; // Liste des pseudos des joueurs
    private String mPseudo; // Pseudo du joueur
    private String mJoueurQuiAPris; // Pseudo du joueur qui a décidé de l'atout
    private String mAtoutChoisi; // Atout de la partie
    private int mIdSalon;
    private int mIdPartie;
    private int mNumeroPli=0;
    private ArrayList<Carte> mListeCartesMainJoueur;
    private ArrayList<Pli> mListeCartesPliEnCours;
    private ArrayList<Joueur> mListeJoueurs;
    // Elements de la vue
    private LinearLayout mTable;
    private ImageView mCarteActive;
    private TextView mTitrePli;
    private TextView mHeureRefresh;
    // Auto resfresh
    private Button mBoutonRefreshAuto;
    private Boolean mRefreshAuto;
    Thread t;
    // Gestion du double clic
    int mClickCount = 0;
    int mLastViewID = 0;
    long mStartTimeClick;
    long mDurationClick;
    static final int MAX_DURATION_CLICK = 500;
    static final int MAX_DURATION_DOUBLE_CLICK = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_belote);

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

        // Table
        mTitrePli = findViewById(R.id.titre_pli);
        mTitrePli.setOnClickListener(this);
        // TODO : mise à jour du numero de plis en cours
        mTable = findViewById(R.id.table);

        // Refresh auto
        startRefreshAuto();
        mBoutonRefreshAuto = findViewById(R.id.bouton_refresh);
        mBoutonRefreshAuto.setOnClickListener(this);
        mHeureRefresh = findViewById(R.id.heure_refresh);
    }

    private String getPseudoQuiDoitJouer() {
        String pseudoQuiDoitJoueur = "";
        ImageView iv;
        TextView tv;
        // Parcourt les images pour savoir si c'est mon tour
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
                                    updateTextView();
                                    // Mise à jour complète
                                    new TacheGetInfoBelote().execute(urlBelote+mIdPartie+"&joueur="+mPseudo);
                                }
                            });
                            Thread.sleep(1000);
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

            case R.id.titre_pli:
                if (mTable.getVisibility() == View.GONE)
                    mTable.setVisibility(View.VISIBLE);
                else
                    mTable.setVisibility(View.GONE);
                break;

            case R.id.table_carte_image_atout :
                String tag = v.getTag().toString();
                String[] chaine = tag.split("_");
                Toast.makeText(this, "Atout choisi :"+chaine[1], Toast.LENGTH_SHORT).show();
                // Todo : distribuer les cartes en fonction de l'atout choisi par le joueur
                ImageView iv = findViewById(v.getId());
                switch (chaine[1]) {
                    default:
                    case "coeur" :
                        iv.setImageResource(R.drawable.coeur);
                        break;
                    case "pique" :
                        iv.setImageResource(R.drawable.pique);
                        break;
                    case "trefle" :
                        iv.setImageResource(R.drawable.trefle);
                        break;
                    case "carreau" :
                        iv.setImageResource(R.drawable.carreau);
                        break;
                }
                new MainActivity.TacheURLSansRetour().execute(urlDistribueBelote+mIdPartie+"&joueur="+mPseudo+"&couleur="+chaine[1]);
                // TODO : masquer les choix des couleurs et afficher les pseudos
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long time;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            // Appuie sur l'écran
            case MotionEvent.ACTION_DOWN:
                time = System.currentTimeMillis();

                if (mClickCount > 2 )
                    mClickCount=0;
                // Si temps entre 2 clicks trop long, on retourne à 0
                if ((time - mStartTimeClick) > MAX_DURATION_DOUBLE_CLICK)
                    mClickCount = 0;
                // Si le précédent click n'est pas sur la même vue, on retourne à 0
                if (mLastViewID != v.getId())
                    mClickCount = 0;

                mStartTimeClick = System.currentTimeMillis();
                mClickCount++;
                mLastViewID = v.getId();
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
                if (mClickCount > 2 )
                    mClickCount=0;
                Log.d("PGR-onTouch", "AUTRE ACTION " + mClickCount + " " + v.getId() + " evt "+event.getAction());
                break;
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

            boolean JeJoueUneCarteAutorisee = false;
            // A qui est-ce le tour ?
            String pseudoQuiDoitJouer = getPseudoQuiDoitJouer();
            String messageErreur = "C'est à " + pseudoQuiDoitJouer + " de jouer";
            // Quelle est la couleur de la première carte jouée dans ce pli ?
            if (pseudoQuiDoitJouer.equals(mPseudo)) { // Si c'est mon tour dans ce pli, on regarde la couleur jouée
                ImageView iv = findViewById(tableIdImageCartePli[0]); // Première carte jouée dans le pli en cours
                String couleurDemandee="";
                if (iv!=null && iv.getVisibility()==View.VISIBLE)
                    couleurDemandee = iv.getTag().toString().split("_")[1]; // Couleur de la première carte du pli
                // Si je joue la couleur demandée => OK
                if (couleurDemandee.equals("") || couleurDemandee.equals(couleurCarteActive))
                    JeJoueUneCarteAutorisee = true;
                else { // Sinon, on vérifie que je n'ai plus la couleur demandée dans ma main
                    // On regarde toutes les cartes de la main du joueur
                    for (int value : tableIdImageCarteMain) {
                        ImageView iv2 = findViewById(value);
                        JeJoueUneCarteAutorisee = true;
                        if (iv2 == null)
                            break;
                        // Si une carte visible est de la couleur demandée
                        if (iv2.getVisibility() == View.VISIBLE && iv2.getTag().toString().split("_")[1].equals(couleurDemandee)) {
                            // Si j'ai la couleur demandée, je ne peux pas jouer une autre carte
                            JeJoueUneCarteAutorisee = false;
                            messageErreur = "C'est " + couleurDemandee + " demandé !";
                            break;
                        }
                    }
                }
            }
            // Si je peux jouer la couleur ou si tout le monde a joué le pli
            if (JeJoueUneCarteAutorisee || pseudoQuiDoitJouer.equals("")) {
                new BeloteActivity.TacheJoueCarte().execute(urlJoueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&joueur=" + mPseudo);
            } else
                Toast.makeText(getBaseContext(), messageErreur, Toast.LENGTH_SHORT).show();
        }
    }

    private int getImageCarte(String couleurCarte, int valeurCarte) {
        int ressource=0;
        switch (couleurCarte) {
            case "pique":
                ressource = imagesPique[valeurCarte];
                break;
            case "coeur":
                ressource = imagesCoeur[valeurCarte];
                break;
            case "trefle":
                ressource = imagesTrefle[valeurCarte];
                break;
            case "carreau":
                ressource = imagesCarreau[valeurCarte];
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
                carte.setOnTouchListener(this);
                tableauCartes.addView(carte);
            }
    }

    /**
     * Affiche la carte atout proposée dans la vue
     *
     * @param cartes : Liste des cartes à afficher
     */
    private void afficheCarteAtout(ArrayList<Carte> cartes) {
        // Affiche la carte
        if (cartes != null && cartes.size()>0) {
            ImageView carte = findViewById(R.id.table_carte_image_atout);
            String couleur = cartes.get(0).getCouleur();
            String nomCarte = couleur + "_" + cartes.get(0).getValeur();
            carte.setImageResource(getImageCarte(couleur, cartes.get(0).getValeur()));
            carte.setTag("carte_" + nomCarte);
            carte.setOnClickListener(this);

            // Masque la couleur pour le 2ème tour (affiche tout en premier)
            ImageView iv;
            switch (couleur) {
                default:
                case "coeur" :
                    iv = findViewById(R.id.image_2eme_tour_coeur);
                    break;
                case "pique" :
                    iv = findViewById(R.id.image_2eme_tour_pique);
                    break;
                case "trefle" :
                    iv = findViewById(R.id.image_2eme_tour_trefle);
                    break;
                case "carreau" :
                    iv = findViewById(R.id.image_2eme_tour_carreau);
                    break;
            }
            iv.setVisibility(View.GONE);
        }
    }

    private void afficheTable(ArrayList<Pli> plis) {
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
                if (mListePseudo[i].equals(mJoueurQuiAPris)) {
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
                String pseudoTexte=mListePseudo[positionJoueur];
                if (mListePseudo[positionJoueur].equals(mJoueurQuiAPris)) {
                    pseudo.setTextColor(getResources().getColor(R.color.noir));
                    //pseudoTexte="^"+mCommandant+"^";
                }
                else
                    pseudo.setTextColor(getResources().getColor(R.color.blanc));
                pseudo.setText(pseudoTexte);
                pseudo.setVisibility(View.VISIBLE);

                if (plis!=null && i < plis.size()) {
                    imageCarte.setImageResource(getImageCarte(plis.get(i).getCarte().getCouleur(), plis.get(i).getCarte().getValeur()));
                    imageCarte.setTag("carte_" + plis.get(i).getCarte().getCouleur() + "_" + plis.get(i).getCarte().getValeur());
                    imageCarte.setVisibility(View.VISIBLE);
                } else {
                    imageCarte.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private void affichePseudos(ArrayList<Joueur> listeJoueurs) {
        mListePseudo = new String[listeJoueurs.size()];
        for (int i = 0; i < listeJoueurs.size(); i++) {
            mListePseudo[i] = listeJoueurs.get(i).getNomJoueur();
            debug(listeJoueurs.get(i).getNomJoueur());
        }

        //switch (mListePseudo.length) {
        //    case 5:
        //        mCommPseudoJoueur5.setText(mListePseudo[4]);
        //        mCommPseudoJoueur5.setVisibility(View.VISIBLE);
        //        mCommJoueur5.setVisibility(View.VISIBLE);
        //        mTachePseudoJoueur5.setText(mListePseudo[4]);
        //        mTachePseudoJoueur5.setVisibility(View.VISIBLE);
        //        mTaches1Joueur5.setVisibility(View.VISIBLE);
        //        mTaches2Joueur5.setVisibility(View.VISIBLE);
        //        mTaches3Joueur5.setVisibility(View.VISIBLE);
        //        mTaches4Joueur5.setVisibility(View.VISIBLE);
        //    case 4:
        //        mCommPseudoJoueur4.setText(mListePseudo[3]);
        //        mCommPseudoJoueur4.setVisibility(View.VISIBLE);
        //        mCommJoueur4.setVisibility(View.VISIBLE);
        //        mTachePseudoJoueur4.setText(mListePseudo[3]);
        //        mTachePseudoJoueur4.setVisibility(View.VISIBLE);
        //        mTaches1Joueur4.setVisibility(View.VISIBLE);
        //        mTaches2Joueur4.setVisibility(View.VISIBLE);
        //        mTaches3Joueur4.setVisibility(View.VISIBLE);
        //        mTaches4Joueur4.setVisibility(View.VISIBLE);
        //    default:
        //        mCommPseudoJoueur3.setText(mListePseudo[2]);
        //        mTachePseudoJoueur3.setText(mListePseudo[2]);
        //        mCommPseudoJoueur2.setText(mListePseudo[1]);
        //        mTachePseudoJoueur2.setText(mListePseudo[1]);
        //        mCommPseudoJoueur1.setText(mListePseudo[0]);
        //        mTachePseudoJoueur1.setText(mListePseudo[0]);
        //        break;
        //}
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
            //for (int i = 0; i < nbJoueur; i++)
            //    if (mListePseudo[i].equals(mCommandant)) {
            //        positionPremierJoueur = i;
            //        break;
            //    }
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
                String pseudoTexte=mListePseudo[positionJoueur];
                //if (mListePseudo[positionJoueur].equals(mCommandant)) {
                //    pseudo.setTextColor(getResources().getColor(R.color.noir));
                //    //pseudoTexte="^"+mCommandant+"^";
                //}
                //else
                    pseudo.setTextColor(getResources().getColor(R.color.blanc));
                pseudo.setText(pseudoTexte);
                pseudo.setVisibility(View.VISIBLE);

                if (plis!=null && i < plis.size()) {
                    imageCarte.setImageResource(getImageCarte(plis.get(i).getCarte().getCouleur(), plis.get(i).getCarte().getValeur()));
                    imageCarte.setTag("carte_" + plis.get(i).getCarte().getCouleur() + "_" + plis.get(i).getCarte().getValeur());
                    imageCarte.setVisibility(View.VISIBLE);
                } else {
                    imageCarte.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    private int getIndexPseudo(String pseudo) {
        int index=0;
        for (int i=0;i<mListePseudo.length;i++) {
            if(mListePseudo[i].equals(pseudo)) {
                index=i;
                break;
            }
        }
        return index;
    }

    private void debug(String message) {
        Log.d("PGR", message);
    }

    private void parseXML(Document doc) {

        Element element=doc.getDocumentElement();
        element.normalize();

        // Cartes que le joueur a en main
        mListeCartesMainJoueur = parseNoeudsCarte(doc, "Cartes");
        afficheCartes(mListeCartesMainJoueur);

        // Joueurs
        mListeJoueurs = parseNoeudsJoueur(doc);
        affichePseudos(mListeJoueurs);

        // Pli en cours
        mListeCartesPliEnCours = parseNoeudsPli(doc, "Pli");
        affichePliEnCours(mListeCartesPliEnCours);

        // Atout
        ArrayList<Carte> mListeCarteAtout = parseNoeudsCarte(doc, "Atout");
        afficheCarteAtout(mListeCarteAtout);
    }

    private ArrayList<Carte> parseNoeudsCarte(Document doc, String nomNoeud) {
        Node NoeudCartes = getNoeudUnique(doc, nomNoeud);

        String couleur="";
        int valeur=0;
        ArrayList<Carte> listeCartes = new ArrayList<>();

        for (int i=0; i<NoeudCartes.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudCartes.getChildNodes().item(i);
            Log.d("PGR-XML-Carte",noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Carte", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "couleur" :
                        couleur = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "valeur" :
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

        String pseudo="";
        String equipe="";
        int admin=0;
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        for (int i=0; i<NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudJoueurs.getChildNodes().item(i);
            Log.d("PGR-XML-Joueur",noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Joueur", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "pseudo" :
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin" :
                        admin = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "equipe" :
                        equipe = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "contrat" :
                        String contrat = noeudCarte.getAttributes().item(j).getNodeValue();
                        if (!contrat.equals("") && !contrat.equals("non"))
                            mAtoutChoisi = contrat;
                        break;
                    case "annonce1" :
                        break;
                    case "annonce2" :
                        break;
                }
            }
            Joueur joueur = new Joueur(pseudo, equipe, admin);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    private ArrayList<Pli> parseNoeudsPli(Document doc, String nomDuNoeud) {
        Node NoeudCartes = getNoeudUnique(doc, nomDuNoeud);

        String pseudo="";
        String couleur="";
        int valeur=0;
        String com="";
        ArrayList<Pli> listePli = new ArrayList<>();

        if(nomDuNoeud.equals("Pli")) {
            mNumeroPli = Integer.parseInt(NoeudCartes.getAttributes().item(0).getNodeValue());
            String titrePli = getResources().getString(R.string.pli_en_cours) + " - numéro " + mNumeroPli;
            mTitrePli.setText(titrePli);
        }

        for (int i=0; i<NoeudCartes.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudCartes.getChildNodes().item(i);
            Log.d("PGR-XML-Pli",noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Pli", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "joueur" :
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "couleur" :
                        couleur = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "valeur" :
                        valeur = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "com" :
                        com = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                }
            }
            Pli pli = new Pli(pseudo,new Carte(couleur, valeur), com);
            listePli.add(pli);
        }

        return listePli;
    }

    private Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission  = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
    }

    /**
     * Classe qui permet de récupérer la carte atout proposée
     * -> Retourne la carte atout proposée (21ème carte de la distribution)
     */
    private class TacheGetCarteAtout extends AsyncTask<String, Void, ArrayList<Carte>> {
        String result;

        @Override
        protected ArrayList<Carte> doInBackground(String... strings) {
            ArrayList<Carte> cartes = new ArrayList<>();
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    String[] chaine = stringBuffer.split("_");
                    Carte carte = new Carte(chaine[0], Integer.parseInt(chaine[1]));
                    cartes.add(carte);
                    string = String.format("%s%s", string, stringBuffer);

                }
                bufferedReader.close();
                result = string;
            } catch (IOException e) {
                e.printStackTrace();
                result = e.toString();
            }

            return cartes;
        }

        @Override
        protected void onPostExecute(ArrayList<Carte> cartes) {
            afficheCarteAtout(cartes);
            super.onPostExecute(cartes);
        }
    }

    /**
     * Classe qui permet de récupérer en base toutes les informations du jeu Belote d'un joueur
     * -> Retourne l'ensemble des informations à afficher au joueurs sous forme XML
     * * - Cartes de la main du joueur
     * * - Cartes Atout sur la table
     * * - Liste des joueurs
     * * - Cartes du pli en cours
     * * - Historique des plis joués
     */
    private class TacheGetInfoBelote extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... strings) {
            URL url;
            Document doc=null;
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
            super.onPostExecute(doc);
        }
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
}

