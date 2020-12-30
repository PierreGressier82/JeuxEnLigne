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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Crane;
import com.pigredorou.jeuxenvisio.objets.Joueur;

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
    // Elements graphiques
    ImageView mImageCrane;
    TextView mContextePersonnage;
    Crane mMonCrane;
    // Refresh auto
    Thread t;
    // Variables globales
    private String mPseudo;
    TextView mNomPersonnage;
    private int mIdSalon;
    EditText mZoneSaisie;
    Button mBoutonValider;
    private int mIdPartie;
    private ArrayList<Joueur> mListeJoueurs;

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
                Toast.makeText(this, mZoneSaisie.getText().toString(), Toast.LENGTH_SHORT).show();
                mNomPersonnage.setVisibility(View.INVISIBLE);
                mContextePersonnage.setVisibility(View.INVISIBLE);
                mImageCrane.setImageResource(R.drawable.fiesta_crane_ferme_1);
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
        mMonCrane = parseNoeudsCrane(doc, "MonCrane");
        afficheMonCrane(mMonCrane);

        // Pli en cours
        //mListeCartesPliEnCours = parseNoeudsPliFromDoc(doc);
        //affichePliEnCours(mListeCartesPliEnCours);
        //afficheScore();

        // Atout
        //ArrayList<Carte> mListeCarteAtout = parseNoeudsCarte(doc, "Atout");
        //afficheCarteAtout(mListeCarteAtout);

        // Historique des plis joués
        //parseEtAfficheNoeudsHisto(doc);

        // Score
        //parseNoeudsEquipeFromDoc(doc);
        //afficheScore();
    }

    private void afficheMonCrane(Crane monCrane) {
        String contexte = "(" + monCrane.getContexte() + ")";
        mNomPersonnage.setText(monCrane.getPersonnage());
        mContextePersonnage.setText(contexte);
    }

    private Crane parseNoeudsCrane(Document doc, String nomNoeud) {
        Node noeudMonCrane = getNoeudUnique(doc, nomNoeud);

        String pseudo = "";
        String personnage = "";
        String contexte = "";
        String mot = "";
        int tourDeJeu = 0;
        Crane monCrane = null;

        for (int j = 0; j < noeudMonCrane.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud MonCrane
            Log.d("PGR-XML-Crane", noeudMonCrane.getAttributes().item(j).getNodeName() + "_" + noeudMonCrane.getAttributes().item(j).getNodeValue());
            switch (noeudMonCrane.getAttributes().item(j).getNodeName()) {
                case "pseudo":
                    pseudo = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "tourDeJeu":
                    tourDeJeu = Integer.parseInt(noeudMonCrane.getAttributes().item(j).getNodeValue());
                    break;
                case "personnage":
                    personnage = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "contexte":
                    contexte = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
                case "mot":
                    mot = noeudMonCrane.getAttributes().item(j).getNodeValue();
                    break;
            }
        }
        monCrane = new Crane(personnage, contexte);

        return monCrane;
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
