package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Joueur;

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

import static com.pigredorou.jeuxenvisio.R.color;
import static com.pigredorou.jeuxenvisio.R.drawable;
import static com.pigredorou.jeuxenvisio.R.id;
import static com.pigredorou.jeuxenvisio.R.layout;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.getNoeudUnique;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.parseNoeudsJoueur;
import static com.pigredorou.jeuxenvisio.outils.outilsXML.suisJeAdmin;

public class TopTenActivity extends AppCompatActivity implements View.OnClickListener {

    // URLs des actions en base
    private static final String urlTopTen = MainActivity.url + "TopTen.php?partie=";
    private static final String urlTopTenDevoilCarte = MainActivity.url + "TopTenDevoileCarte.php?partie=";
    // Tableaux des resssources
    private static final int[] imagesCartes = {0, drawable.topten_1, drawable.topten_2, drawable.topten_3, drawable.topten_4, drawable.topten_5, drawable.topten_6, drawable.topten_7, drawable.topten_8, drawable.topten_9, drawable.topten_10};
    // Variables globales
    private String mPseudo; // Pseudo du joueur
    // Auto resfresh
    Thread t;
    private int mIdSalon;
    private int mIdPartie;
    private boolean mAdmin = false; // Suis-je admin ?
    private int mValeurMaCarte;
    private boolean mCarteDevoilee;
    private int mNbLicorne;
    private int mNbCaca;
    private int mNbCartes;
    private int mNumeroManche;
    private int mValeurCarteTapis;
    private ArrayList<Joueur> mListeJoueurs;
    // Elements graphiques
    private TextView mTexteNbLicorne;
    private TextView mTexteNbCaca;
    private TextView mTexteManche;
    private TextView mTextePerdu;
    private TextView mTexteNbCartes;
    private ImageView mImageCarteTapis;
    private ImageView mImageMaCarte;
    private Button mBoutonDevoiler;
    private Button mBoutonMancheSuivante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(layout.activity_top_ten);

        // Chargement
        findViewById(id.chargement).setVisibility(View.VISIBLE);

        // ENTETE
        // Recupère les paramètres et affiche l'entête
        TextView tvPseudo = findViewById(id.pseudo);
        TextView tvNomSalon = findViewById(id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);

        // Bouton retour
        ImageView boutonRetour = findViewById(id.bouton_retour);
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(drawable.bouton_quitter);

        // Tapis
        mTexteNbLicorne = findViewById(id.licorne);
        mTexteNbCaca = findViewById(id.caca);
        mTexteManche = findViewById(id.manche);
        mImageCarteTapis = findViewById(id.carte_tapis);
        mTextePerdu = findViewById(id.perdu);
        mTexteNbCartes = findViewById(id.nbCartes);

        // Ma carte
        mImageMaCarte = findViewById(id.carte);
        // Boutons
        mBoutonDevoiler = findViewById(id.bouton_devoiler_carte);
        mBoutonMancheSuivante = findViewById(id.bouton_manche_suivante);
        mBoutonDevoiler.setOnClickListener(this);
        mBoutonMancheSuivante.setOnClickListener(this);

        // Met à jour le jeu
        startRefreshAuto();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.bouton_retour:
                finish();
                break;
            case id.bouton_devoiler_carte:
                new MainActivity.TacheURLSansRetour().execute(urlTopTenDevoilCarte + mIdPartie + "&joueur=" + mPseudo);
                desactiveBouton(mBoutonDevoiler);
                break;
            case id.bouton_manche_suivante:
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitTopTen + mIdPartie + "&manche=suivante");
                desactiveBouton(mBoutonMancheSuivante);
                break;
        }
    }

    private void desactiveBouton(Button bouton) {
        bouton.setOnClickListener(null);
        bouton.setTextColor(getResources().getColor(color.material_grey_300));
    }

    private void activeBouton(Button bouton) {
        bouton.setOnClickListener(this);
        bouton.setTextColor(getResources().getColor(color.couleurTopTen));
    }

    private void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        // Joueurs
        mListeJoueurs = parseNoeudsJoueur(doc);
        mAdmin = suisJeAdmin(mPseudo, mListeJoueurs);

        // Tapis
        parseNoeudTapis(doc);
        afficheTapis();

        // Ma carte
        parseNoeudCarte(doc);
        afficheMaCarte();
    }

    private void afficheTapis() {
        String texteManche = "Manche " + mNumeroManche + "/5";
        String texteNbCarte = mNbCartes + " carte(s) sur " + mListeJoueurs.size();
        mTexteNbLicorne.setText(String.valueOf(mNbLicorne));
        mTexteNbCaca.setText(String.valueOf(mNbCaca));
        mTexteManche.setText(texteManche);
        mTexteNbCartes.setText(texteNbCarte);
        if (mValeurCarteTapis == 0)
            mImageCarteTapis.setVisibility(View.INVISIBLE);
        else {
            mImageCarteTapis.setImageResource(imagesCartes[mValeurCarteTapis]);
            mImageCarteTapis.setVisibility(View.VISIBLE);
        }

        if (mNbCaca == 8) {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator()); //do not alter
            animation.setRepeatCount(4);
            animation.setRepeatMode(Animation.REVERSE);
            animation.getFillAfter();
            mTextePerdu.startAnimation(animation);
            mTextePerdu.setVisibility(View.VISIBLE);
            desactiveBouton(mBoutonDevoiler);
            activeBouton(mBoutonMancheSuivante);
        } else
            mTextePerdu.setVisibility(View.INVISIBLE);
    }

    private void afficheMaCarte() {
        mImageMaCarte.setImageResource(imagesCartes[mValeurMaCarte]);

        // Affiche le bouton manche suivante uniquement pour les joueurs admin
        if (mAdmin)
            mBoutonMancheSuivante.setVisibility(View.VISIBLE);
        else
            mBoutonMancheSuivante.setVisibility(View.GONE);

        // Désactive le bouton "dévoiler ma carte" si la carte a déjà été jouée
        if (mCarteDevoilee)
            desactiveBouton(mBoutonDevoiler);
        else
            activeBouton(mBoutonDevoiler);

        if (mNbCartes == mListeJoueurs.size())
            activeBouton(mBoutonMancheSuivante);
        else
            desactiveBouton(mBoutonMancheSuivante);
    }

    private void parseNoeudCarte(Document doc) {
        Node noeudCarte = getNoeudUnique(doc, "Carte");

        mValeurMaCarte = 0;

        for (int i = 0; i < noeudCarte.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud carte
            Log.d("PGR-XML-Carte", noeudCarte.getAttributes().item(i).getNodeName() + "_" + noeudCarte.getAttributes().item(i).getNodeValue());
            switch (noeudCarte.getAttributes().item(i).getNodeName()) {
                case "numero":
                    if (!noeudCarte.getAttributes().item(i).getNodeValue().isEmpty())
                        mValeurMaCarte = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "pose":
                    if (!noeudCarte.getAttributes().item(i).getNodeValue().isEmpty()) {
                        int ordrePose = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                        mCarteDevoilee = ordrePose != 0;
                    } else
                        mCarteDevoilee = false;
                    break;
            }
        }
    }

    private void parseNoeudTapis(Document doc) {
        Node noeudCarte = getNoeudUnique(doc, "Tapis");

        mValeurCarteTapis = 0;
        for (int i = 0; i < noeudCarte.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud carte
            Log.d("PGR-XML-Tapis", noeudCarte.getAttributes().item(i).getNodeName() + "_" + noeudCarte.getAttributes().item(i).getNodeValue());
            if (noeudCarte.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudCarte.getAttributes().item(i).getNodeName()) {
                case "licorne":
                    mNbLicorne = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "caca":
                    mNbCaca = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "manche":
                    mNumeroManche = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "numero":
                    mValeurCarteTapis = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "nbCartes":
                    mNbCartes = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
            }
        }
    }

    private void debug(String message) {
        Log.d("PGR", message);
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
                                    new TacheGetInfoTopTen().execute(urlTopTen + mIdPartie + "&joueur=" + mPseudo);
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
    }

    private void stopRefreshAuto() {
        t.interrupt();
    }

    private class TacheGetInfoTopTen extends AsyncTask<String, Void, Document> {

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
            findViewById(id.chargement).setVisibility(View.GONE);
            if (doc != null)
                parseXML(doc);
            super.onPostExecute(doc);
        }
    }
}