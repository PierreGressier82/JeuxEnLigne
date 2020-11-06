package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainJoueurActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextResultat;
    private String mPseudo;
    private ImageView mCarteActive;
    private ImageView mBoutonTable;
    private ScrollView mTable;
    private static final int[] imagesJaune = {0, R.drawable.jaune_1, R.drawable.jaune_2, R.drawable.jaune_3, R.drawable.jaune_4, R.drawable.jaune_5, R.drawable.jaune_6, R.drawable.jaune_7, R.drawable.jaune_8, R.drawable.jaune_9};
    private static final int[] imagesRose = {0, R.drawable.rose_1, R.drawable.rose_2, R.drawable.rose_3, R.drawable.rose_4, R.drawable.rose_5, R.drawable.rose_6, R.drawable.rose_7, R.drawable.rose_8, R.drawable.rose_9};
    private static final int[] imagesVert = {0, R.drawable.vert_1, R.drawable.vert_2, R.drawable.vert_3, R.drawable.vert_4, R.drawable.vert_5, R.drawable.vert_6, R.drawable.vert_7, R.drawable.vert_8, R.drawable.vert_9};
    private static final int[] imagesBleu = {0, R.drawable.bleu_1, R.drawable.bleu_2, R.drawable.bleu_3, R.drawable.bleu_4, R.drawable.bleu_5, R.drawable.bleu_6, R.drawable.bleu_7, R.drawable.bleu_8, R.drawable.bleu_9};
    private static final int[] imagesFusee = {0, R.drawable.fusee_1, R.drawable.fusee_2, R.drawable.fusee_3, R.drawable.fusee_4};
    private static final String url = "http://julie.et.pierre.free.fr/Salon/";
    private static final String urlGetDistribue = url + "getDistribution.php";
    private static final String urlJoueCarte = url + "majTable.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Affiche la vue
        setContentView(R.layout.activity_main_joueur);

        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setTag("boutonRetour");
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

        mTextResultat = findViewById(R.id.resultat);
        mTextResultat.setText(R.string.Chargement);

        // Recupère et affiche le pseudo du joueur
        TextView textPseudo = findViewById(R.id.pseudo);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        textPseudo.setText(mPseudo);

        // Recupère les cartes du joueur
        if (mPseudo != null)
            new MainJoueurActivity.TacheGetCartesMainJoueur().execute(urlGetDistribue + "?joueur=" + mPseudo);
        else {
            Toast.makeText(this, "Impossible de trouver les cartes du joueur", Toast.LENGTH_SHORT).show();
            mTextResultat.setText(R.string.chargement_impossible);
        }

        // Table
        mBoutonTable = findViewById(R.id.bouton_table);
        mBoutonTable.setOnClickListener(this);
        mBoutonTable.setTag("table");
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().startsWith("carte_")) {
            mCarteActive = findViewById(v.getId());
            String[] chaine = mCarteActive.getTag().toString().split("_"); // ex : carte_bleu_2
            String couleurCarteActive = chaine[1];
            String valeurCarteActive = chaine[2];

            new TacheJoueCarte().execute(urlJoueCarte + "?salon=1&couleur_carte=" + couleurCarteActive + "&valeur_carte=" + valeurCarteActive +"&joueur=" + mPseudo);
        }

        if(v.getTag().toString().equals("boutonRetour")) {
            finish();
        }

        if(v.getTag().toString().equals("table")) {
            mTable = findViewById(R.id.table);
            if (mTable.getVisibility() == View.GONE)
                mTable.setVisibility(View.VISIBLE);
            else
                mTable.setVisibility(View.GONE);
        }

        // TODO : Mettre à jour le contenu de la table en dynamique  -> getTable.php
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
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5, 0, 5, 0);
        tableauCartes.setLayoutParams(params);

        // Affiche les cartes
        if (cartes != null)
            for (int i = 0; i < cartes.size(); i++) {
                ImageView carte = new ImageView(this);
                String nomCarte = cartes.get(i).getCouleur() + "_" + cartes.get(i).getValeur();

                carte.setImageResource(getImageCarte(cartes.get(i).getCouleur(), cartes.get(i).getValeur()));
                carte.setTag("carte_" + nomCarte);
                carte.setId(setId(i));
                carte.setOnClickListener(this);
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

    /**
    * Classe qui permet de récupérer en base la main d'un joueur
    * -> Retourne la liste des cartes du joueur demandé
    */
    private class TacheGetCartesMainJoueur extends AsyncTask<String, Void, ArrayList<Carte>> {
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
                    //Toast.makeText(getBaseContext(), "Carte " + mValeurCarteActive + " " + mCouleurCarteActive + " jouée", Toast.LENGTH_SHORT).show();
                    // Masque la carte
                    mCarteActive.setVisibility(View.GONE);
                }
            super.onPostExecute(integer);
        }
    }
}

