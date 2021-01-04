package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
    private static final String urlValidMot = MainActivity.url + "validMotFiesta.php?partie=";
    // Elements graphiques
    private ImageView mImageCrane;
    private TextView mContextePersonnage;
    private Crane mMonCrane;
    // Refresh auto
    private Thread t;
    // Variables globales
    private int[] mListeIdPersonnage = {R.id.personnage1, R.id.personnage2, R.id.personnage3, R.id.personnage4, R.id.personnage5, R.id.personnage6, R.id.personnage7, R.id.personnage8};
    private int[] mListeIdPersoArdoise = {R.id.nom_ardoise_1, R.id.nom_ardoise_2, R.id.nom_ardoise_3, R.id.nom_ardoise_4, R.id.nom_ardoise_5, R.id.nom_ardoise_6, R.id.nom_ardoise_7, R.id.nom_ardoise_8};
    private int[] mListeIdMot = {R.id.mot_ardoise_1, R.id.mot_ardoise_2, R.id.mot_ardoise_3, R.id.mot_ardoise_4, R.id.mot_ardoise_5, R.id.mot_ardoise_6, R.id.mot_ardoise_7, R.id.mot_ardoise_8};
    private String mPseudo;
    private String mMot;
    private String mPersonnageSelectionne;
    private TextView mNomPersonnage;
    private EditText mZoneSaisie;
    private Button mBoutonValider;
    private ArrayList<Joueur> mListeJoueurs;
    private ArrayList<Personnage> mListePersonnages;
    private ArrayList<Crane> mListeCranes;
    private ArrayList<TourDeJeuCrane> mListeTourDeJeu;
    private int mIdSalon;
    private int mIdPartie;
    private int mTourDeJeu;
    private int mNbJoueurValides;
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
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
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
                new MainActivity.TacheURLSansRetour().execute(urlValidMot + mIdPartie + "&joueur=" + mPseudo + "&mot=" + mZoneSaisie.getText().toString() + "&tourDeJeu=" + (mTourDeJeu + 1));
                mZoneSaisie.setText("");
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
            debug("start refresh");
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

    private void debug(String message) {
        Log.d("PGR", message);
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

        // Mon Crane
        mMonCrane = parseNoeudsMonCrane(doc);
        afficheMonCrane(mMonCrane);

        // Cranes
        mListeCranes = parseNoeudsCranes(doc);

        // Tour de jeu
        mListeTourDeJeu = parseNoeudsTourDeJeu(doc);
        afficheNbJoueursValides();
        afficheMots();

        // Personnages
        mListePersonnages = parseNoeudsPersonnage(doc);
        affichePersonnages();

        // Déductions
        //parseEtAfficheNoeudsDeduction(doc);

    }

    private void afficheMots() {
        for (int i = 0; i < mListeIdMot.length; i++) {
            TextView tv = findViewById(mListeIdMot[i]);
            if (i < mListeCranes.size())
                tv.setText(mListeTourDeJeu.get(i + 12).getMot());
            else
                tv.setText("");
        }

    }

    private void affichePersonnages() {
        for (int i = 0; i < mListeIdPersonnage.length; i++) {
            TextView tv = findViewById(mListeIdPersonnage[i]);
            String textePerso = mListePersonnages.get(i).getNom();
            if (!mListePersonnages.get(i).getContexte().equals(""))
                textePerso += "\n (" + mListePersonnages.get(i).getContexte() + ")";
            tv.setText(textePerso);
            tv.setTag(mListePersonnages.get(i).getNom());
        }
    }

    private void afficheNbJoueursValides() {
        TextView tv = findViewById(R.id.validation_joueurs);
        String texteNbJoueur = mNbJoueurValides + "/" + mListeJoueurs.size();
        tv.setText(texteNbJoueur);

        // Rend clickable que les lignes ayant un mot
        for (int value = 0; value < mListeIdPersoArdoise.length; value++) {
            tv = findViewById(mListeIdPersoArdoise[value]);
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
                if (!mot.equals(""))
                    mNbJoueurValides++;
                TourDeJeuCrane tourDeJeuCrane = new TourDeJeuCrane(numeroTour, mot, new Crane(idCrane), pseudo);
                listeTourDeJeu.add(tourDeJeuCrane);
            }
            if (mNbJoueurValides == 4 && numeroTour < 4)
                mNbJoueurValides = 0;
        }

        return listeTourDeJeu;
    }

    private ArrayList<Crane> parseNoeudsCranes(Document doc) {
        Node NoeudPersonnages = getNoeudUnique(doc, "Cranes");

        int idCrane = 0;
        String personnage = "";
        String contexte = "";
        ArrayList<Crane> listeCranes = new ArrayList<>();

        for (int i = 0; i < NoeudPersonnages.getChildNodes().getLength(); i++) { // Parcours tous les cranes
            Node noeudCrane = NoeudPersonnages.getChildNodes().item(i);
            Log.d("PGR-XML-Crane", noeudCrane.getNodeName());
            for (int j = 0; j < noeudCrane.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud Crane
                Log.d("PGR-XML-Crane", noeudCrane.getAttributes().item(j).getNodeName() + "_" + noeudCrane.getAttributes().item(j).getNodeValue());
                switch (noeudCrane.getAttributes().item(j).getNodeName()) {
                    case "idCrane":
                        idCrane = Integer.parseInt(noeudCrane.getAttributes().item(j).getNodeValue());
                        break;
                    case "personnage":
                        personnage = noeudCrane.getAttributes().item(j).getNodeValue();
                        break;
                    case "contexte":
                        contexte = noeudCrane.getAttributes().item(j).getNodeValue();
                        break;
                }
            }
            Crane crane = new Crane(idCrane, new Personnage(personnage, contexte));
            listeCranes.add(crane);
        }

        return listeCranes;
    }

    private ArrayList<Personnage> parseNoeudsPersonnage(Document doc) {
        Node NoeudPersonnages = getNoeudUnique(doc, "Personnages");

        String nom = "";
        String contexte = "";
        ArrayList<Personnage> listePersonnages = new ArrayList<>();

        for (int i = 0; i < NoeudPersonnages.getChildNodes().getLength(); i++) { // Parcours tous les personnnages
            Node noeudPerso = NoeudPersonnages.getChildNodes().item(i);
            Log.d("PGR-XML-Personnage", noeudPerso.getNodeName());
            for (int j = 0; j < noeudPerso.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud Personnage
                Log.d("PGR-XML-Personnage", noeudPerso.getAttributes().item(j).getNodeName() + "_" + noeudPerso.getAttributes().item(j).getNodeValue());
                switch (noeudPerso.getAttributes().item(j).getNodeName()) {
                    case "nom":
                        nom = noeudPerso.getAttributes().item(j).getNodeValue();
                        break;
                    case "contexte":
                        contexte = noeudPerso.getAttributes().item(j).getNodeValue();
                        break;
                }
            }
            Personnage personnage = new Personnage(nom, contexte);
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

        String pseudo = "";
        String personnage = "";
        String contexte = "";
        Crane monCrane = null;

        for (int j = 0; j < noeudMonCrane.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud MonCrane
            Log.d("PGR-XML-Crane", noeudMonCrane.getAttributes().item(j).getNodeName() + "_" + noeudMonCrane.getAttributes().item(j).getNodeValue());
            switch (noeudMonCrane.getAttributes().item(j).getNodeName()) {
                case "pseudo":
                    pseudo = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "tourDeJeu":
                    mTourDeJeu = Integer.parseInt(noeudMonCrane.getAttributes().item(j).getNodeValue());
                    break;
                case "personnage":
                    personnage = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "contexte":
                    contexte = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "motValide":
                    mMotValide = noeudMonCrane.getAttributes().item(j).getNodeValue().equals("oui");
                    break;
                case "mot":
                    mMot = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
            }
            monCrane = new Crane(new Personnage(personnage, contexte));
        }

        return monCrane;
    }

    private void afficheMonCrane(Crane monCrane) {
        String contexte = monCrane.getPersonnage().getContexte();

        ConstraintLayout crane = findViewById(R.id.crane);
        LinearLayout ardoise = findViewById(R.id.ardoise);
        LinearLayout personnages = findViewById(R.id.personnages);
        LinearLayout clavier = findViewById(R.id.clavier);
        if (mTourDeJeu == 4 && mNbJoueurValides == 4) {
            crane.setVisibility(View.GONE);
            clavier.setVisibility(View.GONE);
            ardoise.setVisibility(View.VISIBLE);
            personnages.setVisibility(View.VISIBLE);
        } else {
            crane.setVisibility(View.VISIBLE);
            clavier.setVisibility(View.VISIBLE);
            ardoise.setVisibility(View.GONE);
            personnages.setVisibility(View.GONE);
        }

        Log.d("DEBUG", mTourDeJeu + "-" + mNbJoueurValides);

        if (!contexte.equals(""))
            contexte = "(" + contexte + ")";

        if (!mMotValide)
            mTourDeJeu--;

        // On n'affiche le personnage que si le joueur n'a pas encore écrit de mot au premier tour
        if (!mMotValide && mTourDeJeu == 0) {
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

        if (mMotValide || mTourDeJeu == 4) {
            mZoneSaisie.setFocusable(false);
            mBoutonValider.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            mBoutonValider.setClickable(false);
        } else {
            mZoneSaisie.setFocusable(true);
            mBoutonValider.setTextColor(getResources().getColor(R.color.blanc));
            mBoutonValider.setClickable(true);
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
            parseXML(doc);
            super.onPostExecute(doc);
        }
    }

}
