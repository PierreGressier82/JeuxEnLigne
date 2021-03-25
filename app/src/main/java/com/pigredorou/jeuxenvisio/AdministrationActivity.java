package com.pigredorou.jeuxenvisio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Salon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.pigredorou.jeuxenvisio.JeuEnVisioActivity.getNoeudUnique;
import static com.pigredorou.jeuxenvisio.MainActivity.url;

public class AdministrationActivity extends AppCompatActivity {

    private static final String urlGetSalons = url + "getAllSalons.php";
    private ArrayList<Salon> mListeSalons;
    private ConstraintLayout mChargement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);

        new TacheGetSalonsDeJeu().execute(urlGetSalons);
    }

    void parseXML(Document doc) {
        // Masque l'écran de chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);

        // Liste des salons de jeu (avec les joueurs)
        mListeSalons = parseXMLSalonsJeu(doc);
    }

    public static ArrayList<Salon> parseXMLSalonsJeu(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        ArrayList<Salon> listeSalons = new ArrayList<>();

        Node noeudSalons = getNoeudUnique(doc, "Salons");
        int idSalon = 0;
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
                }
            }
            ArrayList<Joueur> listeJoueurs = new ArrayList<>();

            for (int k = 0; k < noeudSalon.getChildNodes().getLength(); k++) { // Parcours toutes les joueurs du salon
                Node noeudJoueur = noeudSalon.getChildNodes().item(k);
                int idJoueur = 0;
                String pseudo = "";
                int actif = 0;
                int admin = 0;
                for (int j = 0; j < noeudJoueur.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud joueur
                    Log.d("PGR-XML-Joueur", noeudJoueur.getAttributes().item(j).getNodeName() + "_" + noeudJoueur.getAttributes().item(j).getNodeValue());
                    if (noeudJoueur.getAttributes().item(j).getNodeValue().isEmpty())
                        continue;
                    switch (noeudJoueur.getAttributes().item(j).getNodeName()) {
                        case "id_joueur":
                            idJoueur = Integer.parseInt(noeudJoueur.getAttributes().item(j).getNodeValue());
                            break;
                        case "pseudo":
                            pseudo = noeudJoueur.getAttributes().item(j).getNodeValue();
                            break;
                        case "admin":
                            admin = Integer.parseInt(noeudJoueur.getAttributes().item(j).getNodeValue());
                            break;
                        case "actif":
                            actif = Integer.parseInt(noeudJoueur.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Joueur joueur = new Joueur(idJoueur, pseudo, 0, admin, 0, actif);
                listeJoueurs.add(joueur);
            }
            Salon salon = new Salon(idSalon, nomSalon, listeJoueurs);
            listeSalons.add(salon);
        }

        return listeSalons;
    }

    private class TacheGetSalonsDeJeu extends AsyncTask<String, Void, Document> {

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
            if (doc != null) {
                mChargement.setVisibility(View.GONE);
                parseXML(doc);
            }
            super.onPostExecute(doc);
        }
    }
}