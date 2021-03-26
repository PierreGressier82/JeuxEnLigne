package com.pigredorou.jeuxenvisio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static androidx.annotation.Dimension.SP;
import static com.pigredorou.jeuxenvisio.JeuEnVisioActivity.getNoeudUnique;
import static com.pigredorou.jeuxenvisio.MainActivity.url;

public class AdministrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String urlGetSalons = url + "getAllSalons.php";
    private ConstraintLayout mChargement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_administration);

        // Ecran de chargement
        mChargement = findViewById(R.id.chargement);
        mChargement.setVisibility(View.VISIBLE);

        // Bouton retour
        chargeBoutonRetour();

        new TacheGetSalonsDeJeu().execute(urlGetSalons);
    }

    private void chargeBoutonRetour() {
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
    }


    private void parseXML(Document doc) {
        ArrayList<Salon> mListeSalons;

        // Masque l'écran de chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);

        // Liste des salons de jeu (avec les joueurs)
        mListeSalons = parseXMLSalonsJeu(doc);
        afficheSalons(mListeSalons);
    }

    private void afficheSalons(ArrayList<Salon> listeSalons) {
        LinearLayout ll = findViewById(R.id.listeSalons);

        for (int i = 0; i < listeSalons.size(); i++) {
            ajouteNomSalon(ll, listeSalons.get(i).getNom());
            ajouteJoueursEtJeux(ll, listeSalons.get(i).getListeJoueurs(), listeSalons.get(i).getId());
        }
    }

    private void ajouteJoueursEtJeux(LinearLayout ll, ArrayList<Joueur> listeJoueurs, int indexSalon) {
        LinearLayout blocJoueursEtJeux = new LinearLayout(this);
        LinearLayout.LayoutParams paramsLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blocJoueursEtJeux.setLayoutParams(paramsLL);

        TableLayout tableauJoueurs = new TableLayout(this);
        TableLayout.LayoutParams paramsTL = new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.5);
        tableauJoueurs.setLayoutParams(paramsTL);
        ajouteJoueurs(tableauJoueurs, listeJoueurs, indexSalon);
        blocJoueursEtJeux.addView(tableauJoueurs);

        TableLayout tableauJeux = new TableLayout(this);
        tableauJeux.setLayoutParams(paramsTL);
        //ajouteJoueurs(tableauJeux, listeJoueurs);
        blocJoueursEtJeux.addView(tableauJeux);
        ll.addView(blocJoueursEtJeux);
    }

    private void ajouteJoueurs(TableLayout tl, ArrayList<Joueur> listeJoueurs, int indexSalon) {
        String tag = "salon_" + indexSalon;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            TableRow ligneJoueur = new TableRow(this);
            TableRow.LayoutParams paramsTR = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTR);
            // Actif ?
            CheckBox joueurActif = new CheckBox(this);
            joueurActif.setChecked(listeJoueurs.get(i).getActif() != 0);
            joueurActif.setTextColor(getResources().getColor(R.color.blanc));
            joueurActif.setOnClickListener(this);
            joueurActif.setTag(tag);
            ligneJoueur.addView(joueurActif);
            // Nom joueur
            TextView nomJoueur = new TextView(this);
            TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTV);
            nomJoueur.setText(listeJoueurs.get(i).getNomJoueur());
            nomJoueur.setTextColor(getResources().getColor(R.color.blanc));
            nomJoueur.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            nomJoueur.setTextSize(SP, 20);
            nomJoueur.setTag(tag);
            ligneJoueur.addView(nomJoueur);
            // Ajout de la ligne au tabeau
            tl.addView(ligneJoueur);
        }
    }

    private void ajouteNomSalon(LinearLayout ll, String nom) {
        TextView nomSalon = new TextView(this);
        nomSalon.setText(nom);
        nomSalon.setTextColor(getResources().getColor(R.color.blanc));
        nomSalon.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nomSalon.setTextSize(SP, 30);
        nomSalon.setBackgroundColor(getResources().getColor(R.color.noir_transparent));
        nomSalon.setGravity(View.TEXT_ALIGNMENT_CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 30, 10, 0);
        nomSalon.setLayoutParams(params);

        ll.addView(nomSalon);
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

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getTag().toString()) {
                case "bouton_retour":
                    finish();
                    break;

                case "bouton_valider":
                    break;

                default:
                    if (v.getTag().toString().startsWith("salon_")) {
                        Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
                    }
            }
        }
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