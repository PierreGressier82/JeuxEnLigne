package com.pigredorou.jeuxenvisio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pigredorou.jeuxenvisio.objets.Jeu;
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

    private static final String urlGetSalons = url + "getSalon.php?salon=";
    private static final String urlMAJActif = url + "majActif.php?salon=";
    private static final String urlCreeJoueur = url + "creeJoueur.php?salon=";
    private ConstraintLayout mChargement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_administration);

        // Recupère l'id du salon de jeu
        int mIdSalon = getIntent().getIntExtra(MainActivity.VALEUR_ID_SALON, 1);

        // Ecran de chargement
        mChargement = findViewById(R.id.chargement);
        mChargement.setVisibility(View.VISIBLE);

        // Bouton retour
        chargeBoutonRetour();

        new TacheGetSalonsDeJeu().execute(urlGetSalons + mIdSalon);
    }

    private void chargeBoutonRetour() {
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
    }


    private void parseXML_SJ(Document doc) {
        Salon mSalonDeJeu;

        // Masque l'écran de chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);

        // Liste des salons de jeu (avec les joueurs)
        mSalonDeJeu = parseXMLSalonJeu(doc);
        afficheSalons(mSalonDeJeu);
    }

    private void parseXML_J(Document doc) {
        ArrayList<Joueur> mListeJoueurs;
        // Liste des joueurs (pour activer ou non le joueur comme nouveau joueur)
        mListeJoueurs = parseXMLJoueurs(doc);
        afficheJoueur(mListeJoueurs);
    }

    private ArrayList<Joueur> parseXMLJoueurs(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        Node noeudJoueurs = getNoeudUnique(doc, "Joueurs");
        int idJoueur = 0;
        String nomJoueur = "";
        int admin = 0;
        int newJ = 0;
        for (int i = 0; i < noeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les salons
            Node noeudSalon = noeudJoueurs.getChildNodes().item(i);
            Log.d("PGR-XML-Joueur", noeudSalon.getNodeName());
            for (int j = 0; j < noeudSalon.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud salon
                Log.d("PGR-XML-Joueur", noeudSalon.getAttributes().item(j).getNodeName() + "_" + noeudSalon.getAttributes().item(j).getNodeValue());
                if (noeudSalon.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudSalon.getAttributes().item(j).getNodeName()) {
                    case "id":
                        idJoueur = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "pseudo":
                        nomJoueur = noeudSalon.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin":
                        admin = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "new":
                        newJ = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(idJoueur, nomJoueur, 0, admin, newJ, 0);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    private void afficheJoueur(ArrayList<Joueur> mListeJoueurs) {
        //LinearLayout ll = findViewById(R.id.listeJoueurs);

        //for (int i = 0; i < mListeJoueurs.size(); i++) {
        //    ajouteTitre(ll, "LISTE DES JOUEURS");
        //    ajouteJoueursEtJeux(ll, mListeJoueurs, mListeJoueurs.get(i).getId());
        //}
    }

    private void afficheSalons(Salon salonDeJeux) {
        LinearLayout ll = findViewById(R.id.listeSalons);

        ajouteJoueursEtJeux(ll, salonDeJeux.getListeJoueurs(), salonDeJeux.getListeJeux(), salonDeJeux.getId());
    }

    private void ajouteJoueursEtJeux(LinearLayout ll, ArrayList<Joueur> listeJoueurs, ArrayList<Jeu> listeJeux, int indexSalon) {
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
        ajouteJeux(tableauJeux, listeJeux, indexSalon);
        blocJoueursEtJeux.addView(tableauJeux);
        ll.addView(blocJoueursEtJeux);
    }

    private void ajouteJoueurs(TableLayout tl, ArrayList<Joueur> listeJoueurs, int indexSalon) {
        String tag = "joueur_" + indexSalon;
        for (int i = 0; i < listeJoueurs.size(); i++) {
            TableRow ligneJoueur = new TableRow(this);
            TableRow.LayoutParams paramsTR = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTR);
            // Actif ?
            CheckBox joueurActif = new CheckBox(this);
            joueurActif.setChecked(listeJoueurs.get(i).getActif() != 0);
            joueurActif.setTextColor(getResources().getColor(R.color.blanc));
            joueurActif.setOnClickListener(this);
            joueurActif.setTag(tag + "_" + listeJoueurs.get(i).getId());
            joueurActif.setId(View.generateViewId());
            ligneJoueur.addView(joueurActif);
            // Nom joueur
            TextView nomJoueur = new TextView(this);
            TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTV);
            nomJoueur.setText(listeJoueurs.get(i).getNomJoueur());
            nomJoueur.setTextColor(getResources().getColor(R.color.blanc));
            nomJoueur.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            nomJoueur.setTextSize(SP, 20);
            nomJoueur.setTag(tag + "_" + listeJoueurs.get(i).getId());
            ligneJoueur.addView(nomJoueur);
            // Ajout de la ligne au tabeau
            tl.addView(ligneJoueur);
        }
    }

    private void ajouteJeux(TableLayout tl, ArrayList<Jeu> listeJeux, int indexSalon) {
        String tag = "jeu_" + indexSalon;
        for (int i = 0; i < listeJeux.size(); i++) {
            TableRow ligneJoueur = new TableRow(this);
            TableRow.LayoutParams paramsTR = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTR);
            // Actif ?
            CheckBox joueurActif = new CheckBox(this);
            //joueurActif.setChecked(listeJeux.get(i).getActif() != 0);
            joueurActif.setTextColor(getResources().getColor(R.color.blanc));
            joueurActif.setOnClickListener(this);
            joueurActif.setTag(tag + "_" + listeJeux.get(i).getId());
            joueurActif.setId(View.generateViewId());
            ligneJoueur.addView(joueurActif);
            // Nom joueur
            TextView nomJoueur = new TextView(this);
            TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ligneJoueur.setLayoutParams(paramsTV);
            nomJoueur.setText(listeJeux.get(i).getNom());
            nomJoueur.setTextColor(getResources().getColor(R.color.blanc));
            nomJoueur.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            nomJoueur.setTextSize(SP, 20);
            nomJoueur.setTag(tag + "_" + listeJeux.get(i).getId());
            ligneJoueur.addView(nomJoueur);
            // Ajout de la ligne au tabeau
            tl.addView(ligneJoueur);
        }
    }

    private void ajouteNomSalon(LinearLayout ll, Salon salon) {
        TextView nomSalon = new TextView(this);
        nomSalon.setText(salon.getNom());
        nomSalon.setTextColor(getResources().getColor(R.color.blanc));
        nomSalon.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nomSalon.setTextSize(SP, 30);
        nomSalon.setBackgroundColor(getResources().getColor(R.color.noir_transparent));
        nomSalon.setGravity(View.TEXT_ALIGNMENT_CENTER);
        nomSalon.setTag("salon_" + salon.getId());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 30, 10, 0);
        nomSalon.setLayoutParams(params);

        ll.addView(nomSalon);
    }

    private void ajouteTitre(LinearLayout ll, String titre) {
        TextView nomTitre = new TextView(this);
        nomTitre.setText(titre);
        nomTitre.setTextColor(getResources().getColor(R.color.blanc));
        nomTitre.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nomTitre.setTextSize(SP, 30);
        nomTitre.setBackgroundColor(getResources().getColor(R.color.noir_transparent));
        nomTitre.setGravity(View.TEXT_ALIGNMENT_CENTER);
        nomTitre.setOnClickListener(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 30, 10, 0);
        nomTitre.setLayoutParams(params);

        ll.addView(nomTitre);
    }

    private static Salon parseXMLSalonJeu(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        Salon salonDeJeu = null;

        Node noeudSalons = getNoeudUnique(doc, "Salon");
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

            ArrayList<Jeu> listeJeux = new ArrayList<>();
            for (int k = 0; k < noeudSalon.getChildNodes().getLength(); k++) { // Parcours toutes les jeux du salon
                Node noeudJeu = noeudSalon.getChildNodes().item(k);
                int idJeu = 0;
                String nomJeu = "";
                int actif = 0;
                int admin = 0;
                for (int j = 0; j < noeudJeu.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud joueur
                    Log.d("PGR-XML-Jeu", noeudJeu.getAttributes().item(j).getNodeName() + "_" + noeudJeu.getAttributes().item(j).getNodeValue());
                    if (noeudJeu.getAttributes().item(j).getNodeValue().isEmpty())
                        continue;
                    switch (noeudJeu.getAttributes().item(j).getNodeName()) {
                        case "id_jeu":
                            idJeu = Integer.parseInt(noeudJeu.getAttributes().item(j).getNodeValue());
                            break;
                        case "nom":
                            nomJeu = noeudJeu.getAttributes().item(j).getNodeValue();
                            break;
                        case "actif":
                            actif = Integer.parseInt(noeudJeu.getAttributes().item(j).getNodeValue());
                            break;
                    }
                }
                Jeu jeu = new Jeu(idJeu, nomJeu, actif);
                listeJeux.add(jeu);
            }
            // TODO : terminer la création du salon
            salonDeJeu = new Salon(idSalon, nomSalon, listeJoueurs, listeJeux);
        }

        return salonDeJeu;
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
                    if (v.getTag().toString().startsWith("joueur_")) {
                        int actif = 0;
                        String[] tag = v.getTag().toString().split("_");
                        CheckBox cb = findViewById(v.getId());
                        if (cb.isChecked())
                            actif = 1;
                        new MainActivity.TacheURLSansRetour().execute(urlMAJActif + tag[1] + "&joueur=" + tag[2] + "&actif=" + actif);
                        Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (v.getTag().toString().startsWith("salon_")) {
                        String[] tag = v.getTag().toString().split("_");
                        lireEtAjouterJoueur(tag[1]);
                        Toast.makeText(this, "Joueur créé", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    private void lireEtAjouterJoueur(final String salon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        // Oblige a appuyer sur OK
        //builder.setCancelable(false);
        // Affichage du score final
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setTitle("Créer un nouveau joueur");
        builder.setMessage("\nPseudo : ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new MainActivity.TacheURLSansRetour().execute(urlCreeJoueur + salon + "&joueur=" + input.getText().toString());

                // Termine l'activité correctement
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
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
                parseXML_SJ(doc);
            }
            super.onPostExecute(doc);
        }
    }

    private class TacheGetJoueurs extends AsyncTask<String, Void, Document> {

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
                parseXML_J(doc);
            }
            super.onPostExecute(doc);
        }
    }
}