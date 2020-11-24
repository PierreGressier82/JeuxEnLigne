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
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.Pli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

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
    private static final String urlGetDistribue = MainActivity.url + "getDistribution.php?partie=";
    private static final String urlJoueCarte = MainActivity.url + "majTable.php?partie=";
    private static final String urlAfficheTable = MainActivity.url + "getTable.php?partie=";
    // Variables globales
    private String[] mListePseudo; // Liste des pseudos des joueurs
    private String mPseudo; // Pseudo du joueur
    private String mJoueurQuiAPris; // Pseudo du joueur qui a décidé de l'atout
    private String mAtoutChoisi; // Atout de la partie
    private int mIdSalon;
    private int mIdPartie;
    // Elements de la vue
    private ScrollView mTable;
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

        // Liste des pseudos dans l'ordre de jeu
        new BeloteActivity.TacheGetJoueurs().execute(MainActivity.urlGetJoueurs + mIdSalon);

        // Recupère la carte pour choisir l'atout
        new BeloteActivity.TacheGetCarteAtout().execute(urlGetDistribue+mIdPartie+"&joueur=Table&tri=desc");

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
                                    majable();
                                    // Mise à jour de la main du joueur
                                    new BeloteActivity.TacheGetCartesMainJoueur().execute(urlGetDistribue+mIdPartie+"&joueur="+mPseudo+"&tri=desc");
                                }
                            });
                            Thread.sleep(2000);
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
                iv.setVisibility(View.GONE);
                break;

        }

        // Mise à jour de la table
        majable();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long time;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // Appuie sur l'écran
            case MotionEvent.ACTION_DOWN:
                if (mClickCount == 1) {
                    time = System.currentTimeMillis();

                    // Si temps entre 2 clicks trop long, on retourne à 0
                    if ((time - mStartTimeClick) > MAX_DURATION_DOUBLE_CLICK)
                        mClickCount = 0;
                    // Si le précédent click n'est pas sur la même vue, on retourne à 0
                    if (mLastViewID != v.getId())
                        mClickCount = 0;
                }
                mStartTimeClick = System.currentTimeMillis();
                mClickCount++;
                mLastViewID = v.getId();
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

            boolean estCeQueJeJoueUneCarteAutorisee = false;
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
                    estCeQueJeJoueUneCarteAutorisee = true;
                else { // Sinon, on vérifie que je n'ai plus la couleur demandée dans ma main
                    // On regarde toutes les cartes de la main du joueur
                    for (int value : tableIdImageCarteMain) {
                        ImageView iv2 = findViewById(value);
                        estCeQueJeJoueUneCarteAutorisee = true;
                        if (iv2 == null)
                            break;
                        // Si une carte visible est de la couleur demandée
                        if (iv2.getVisibility() == View.VISIBLE && iv2.getTag().toString().split("_")[1].equals(couleurDemandee)) {
                            // Si j'ai la couleur demandée, je ne peux pas jouer une autre carte
                            estCeQueJeJoueUneCarteAutorisee = false;
                            messageErreur = "Tu dois jouer la bonne couleur : " + couleurDemandee;
                            break;
                        }
                    }
                }
            }
            // Si je peux jouer la couleur ou si tout le monde a joué le pli
            if (estCeQueJeJoueUneCarteAutorisee || pseudoQuiDoitJouer.equals("")) {
                new BeloteActivity.TacheJoueCarte().execute(urlJoueCarte + mIdPartie + "&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive + "&joueur=" + mPseudo);
                // Mise à jour de la table
                majable();
            } else
                Toast.makeText(getBaseContext(), messageErreur, Toast.LENGTH_SHORT).show();
        }
    }

    private void majable() {
        // Mise à jour de la table si elle est affichée
        if (mTable.getVisibility() == View.VISIBLE) {
            new BeloteActivity.TacheAfficheTable().execute(urlAfficheTable + mIdPartie);
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
        if (cartes != null) {
            ImageView carte = findViewById(R.id.table_carte_image_atout);
            String nomCarte = cartes.get(0).getCouleur() + "_" + cartes.get(0).getValeur();
            carte.setImageResource(getImageCarte(cartes.get(0).getCouleur(), cartes.get(0).getValeur()));
            carte.setTag("carte_" + nomCarte);
            carte.setOnClickListener(this);
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

    /**
     * Classe qui permet de récupère la liste des joueurs dans l'ordre de jeu
     * -> Retourne la liste
     */
    private class TacheGetJoueurs extends AsyncTask<String, Void, ArrayList<String>> {
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
            for (int i = 0; i < joueurs.size(); i++) {
                mListePseudo[i] = joueurs.get(i);
                debug(joueurs.get(i));
            }
            //affichePseudos();
            super.onPostExecute(joueurs);
        }
    }

    /**
     * Classe qui permet de récupère le pli en cours
     * -> Retourne le pli et l'affiche
     */
    private class TacheAfficheTable extends AsyncTask<String, Void, ArrayList<Pli>> {
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
     * Classe qui permet de récupérer en base la main d'un joueur
     * -> Retourne la liste des cartes du joueur demandé
     */
    private class TacheGetCartesMainJoueur extends AsyncTask<String, Void, ArrayList<Carte>> {
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
            afficheCartes(cartes);
            super.onPostExecute(cartes);
        }
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

