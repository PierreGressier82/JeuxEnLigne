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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.pigredorou.jeuxenvisio.outils.outilsXML.parseNoeudsJoueur;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.suisJeAdmin;

public class JeuEnVisioActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables globales
    String mPseudo;
    int mIdJoueur;
    int mIdPartie;
    int mMonScore;
    boolean mAdmin;
    ArrayList<Joueur> mListeJoueurs;
    // Variables statiques
    final static String urlJeu = MainActivity.url + "jeu.php?partie=";
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

        // Affiche le contexte de l'entête
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);

        // Bouton retour
        chargeBoutons();

        // Refresh info jeu
        mMajTerminee = true;
    }

    void chargeBoutons() {
        mBoutonValider = findViewById(R.id.bouton_valider);
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
        t.interrupt();
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "bouton_retour":
                stopRefreshAuto();
                finish();
                break;

            case "bouton_valider":
                //startRefreshAuto(urlJeu); -> A reprendre dans la classe fille
                desactiveBouton(mBoutonValider);
                new MainActivity.TacheURLSansRetour().execute(urlMAJ + mIdPartie + "&joueur=" + mIdJoueur);
                break;
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

    void parseXML(Document doc) {
        // Masque l'écran de chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);
        // Joueurs
        mListeJoueurs = parseNoeudsJoueur(doc);
        mAdmin = suisJeAdmin(mPseudo, mListeJoueurs);
        // A surcharger dans les classes
    }

    void afficheScore(ArrayList<Joueur> listeJoueurs) {
        String textScore;

        mMonScore = getSCoreJoueurId(listeJoueurs);
        textScore = getResources().getString(R.string.score_quipe) + " : " + mMonScore;
        TextView tv = findViewById(R.id.score);
        tv.setText(textScore);
    }

    int getSCoreJoueurId(ArrayList<Joueur> listeJoueurs) {
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