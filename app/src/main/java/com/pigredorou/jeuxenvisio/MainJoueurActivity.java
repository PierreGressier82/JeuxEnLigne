package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Objects;

public class MainJoueurActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextResultat;
    private String mPseudo;
    private int mIdSalon;
    private ImageView mCarteActive;
    private ScrollView mTable;
    private static final int[] imagesJaune = {0, R.drawable.jaune_1, R.drawable.jaune_2, R.drawable.jaune_3, R.drawable.jaune_4, R.drawable.jaune_5, R.drawable.jaune_6, R.drawable.jaune_7, R.drawable.jaune_8, R.drawable.jaune_9};
    private static final int[] imagesRose = {0, R.drawable.rose_1, R.drawable.rose_2, R.drawable.rose_3, R.drawable.rose_4, R.drawable.rose_5, R.drawable.rose_6, R.drawable.rose_7, R.drawable.rose_8, R.drawable.rose_9};
    private static final int[] imagesVert = {0, R.drawable.vert_1, R.drawable.vert_2, R.drawable.vert_3, R.drawable.vert_4, R.drawable.vert_5, R.drawable.vert_6, R.drawable.vert_7, R.drawable.vert_8, R.drawable.vert_9};
    private static final int[] imagesBleu = {0, R.drawable.bleu_1, R.drawable.bleu_2, R.drawable.bleu_3, R.drawable.bleu_4, R.drawable.bleu_5, R.drawable.bleu_6, R.drawable.bleu_7, R.drawable.bleu_8, R.drawable.bleu_9};
    private static final int[] imagesFusee = {0, R.drawable.fusee_1, R.drawable.fusee_2, R.drawable.fusee_3, R.drawable.fusee_4};
    private static final int[] tableIdPseudo = {R.id.table_pseudo_joueur1, R.id.table_pseudo_joueur2, R.id.table_pseudo_joueur3, R.id.table_pseudo_joueur4, R.id.table_pseudo_joueur5};
    private static final int[] tableIdCarte = {R.id.table_carte_joueur1, R.id.table_carte_joueur2, R.id.table_carte_joueur3, R.id.table_carte_joueur4, R.id.table_carte_joueur5};
    private static final String url = "http://julie.et.pierre.free.fr/Salon/";
    private static final String urlGetDistribue = url + "getDistribution.php";
    private static final String urlJoueCarte = url + "majTable.php";
    private static final String urlAfficheTable = url + "getTable.php?salon=";
    private static final String urlAfficheTache = url + "getTaches.php?salon=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        textPseudo.setText(mPseudo);

        // Recupère les cartes du joueur
        if (mPseudo != null)
            new MainJoueurActivity.TacheGetCartesMainJoueur().execute(urlGetDistribue + "?joueur=" + mPseudo + "&salon=" + mIdSalon);
        else {
            Toast.makeText(this, "Impossible de trouver les cartes du joueur", Toast.LENGTH_SHORT).show();
            mTextResultat.setText(R.string.chargement_impossible);
        }

        // Table
        mTable = findViewById(R.id.table);
        ImageView boutonTable = findViewById(R.id.bouton_table);
        boutonTable.setOnClickListener(this);
        new TacheAfficheTable().execute(urlAfficheTable+mIdSalon);

        // Entete
        TextView titre = findViewById(R.id.titre_jeu);
        titre.setOnClickListener(this);

        // Taches
        new TacheAfficheTaches().execute(urlAfficheTache+mIdSalon);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bouton_retour :
                finish();
                break;
            case R.id.bouton_table :
                if (mTable.getVisibility() == View.GONE)
                    mTable.setVisibility(View.VISIBLE);
                else
                    mTable.setVisibility(View.GONE);
                break;
            default:
                if (v.getTag() != null && v.getTag().toString().startsWith("carte_")) {
                    mCarteActive = findViewById(v.getId());
                    String[] chaine = mCarteActive.getTag().toString().split("_"); // ex : carte_bleu_2
                    String couleurCarteActive = chaine[1];
                    String valeurCarteActive = chaine[2];

                    new TacheJoueCarte().execute(urlJoueCarte + "?salon="+mIdSalon+"&couleur_carte="+couleurCarteActive+"&valeur_carte="+valeurCarteActive+"&joueur="+mPseudo);
                }
                break;
        }

        // Mise à jour de la table si elle est affichée
        if (mTable.getVisibility() == View.VISIBLE) {
            new TacheAfficheTable().execute(urlAfficheTable + mIdSalon);
        }
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

    private void afficheTable(ArrayList<Pli> plis) {
        TextView pseudo;
        TextView carte;
        for (int i=0; i<tableIdPseudo.length; i++) {
            pseudo = findViewById(tableIdPseudo[i]);
            carte = findViewById(tableIdCarte[i]);
            if (i < plis.size()) {
                pseudo.setText(plis.get(i).getJoueur());
                pseudo.setVisibility(View.VISIBLE);
                carte.setText(String.valueOf(plis.get(i).getCarte().getValeur()));
                carte.setTextColor(getResources().getColor(getCouleurCarte(plis.get(i).getCarte().getCouleur())));
                carte.setVisibility(View.VISIBLE);
            } else {
                pseudo.setVisibility(View.GONE);
                carte.setVisibility(View.GONE);
            }
        }
    }

    private void afficheTaches(ArrayList<Pli> plis) {
        String pseudoPrec="";
        TableRow ligneTaches = findViewById(R.id.tableau_taches);
        ligneTaches.removeAllViewsInLayout();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5, 5, 5, 5);
        ligneTaches.setLayoutParams(params);

        TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TextView tvTache = new TextView(this);
        paramsTV.setMargins(5, 0, 20, 0);
        tvTache.setLayoutParams(paramsTV);
        tvTache.setTextColor(getResources().getColor(R.color.noir));
        tvTache.setText(R.string.taches);
        tvTache.setTextSize(20);
        tvTache.setGravity(View.TEXT_ALIGNMENT_CENTER);
        ligneTaches.addView(tvTache);

        // Affiche les taches
        for (int i=0; i<plis.size(); i++) {
            if (!pseudoPrec.equals(plis.get(i).getJoueur())) {
                if (pseudoPrec!="") {
                    // Affiche une case de séparation
                    TextView tvVide = new TextView(this);
                    paramsTV.setMargins(50, 0, 5, 0);
                    tvVide.setLayoutParams(paramsTV);
                    tvVide.setTextColor(getResources().getColor(R.color.noir));
                    tvVide.setText("  |  ");
                    tvVide.setTextSize(20);
                    ligneTaches.addView(tvVide);
                }

                String texte = plis.get(i).getJoueur() + " : ";
                TextView tvPseudo = new TextView(this);
                paramsTV.setMargins(15, 0, 5, 0);
                tvPseudo.setLayoutParams(paramsTV);
                tvPseudo.setText(texte);
                tvPseudo.setTextColor(getResources().getColor(R.color.blanc));
                tvPseudo.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tvPseudo.setTextSize(20);
                ligneTaches.addView(tvPseudo);
                pseudoPrec=plis.get(i).getJoueur();
            }
            String texte = plis.get(i).getCarte().getValeur() + " " + plis.get(i).getCarte().getOption();
            TextView tvValeurTache = new TextView(this);
            paramsTV.setMargins(5, 0, 5, 0);
            tvValeurTache.setLayoutParams(paramsTV);
            tvValeurTache.setText(texte);
            tvValeurTache.setTextColor(getResources().getColor(getCouleurCarte(plis.get(i).getCarte().getCouleur())));
            tvValeurTache.setGravity(View.TEXT_ALIGNMENT_CENTER);
            tvValeurTache.setTextSize(20);
            tvValeurTache.setTypeface(tvTache.getTypeface(), Typeface.BOLD);
            ligneTaches.addView(tvValeurTache);
        }
    }

    private int getCouleurCarte(String couleur) {
        int idCouleur;
        switch (couleur) {
            case "bleu" :
                idCouleur=R.color.bleu;
                break;
            case "jaune" :
                idCouleur=R.color.jaune;
                break;
            case "rose" :
                idCouleur=R.color.rose;
                break;
            case "vert" :
                idCouleur=R.color.vert;
                break;
            case "fusee" :
            default:
                idCouleur=R.color.noir;
                break;
        }
        return idCouleur;
    }

    /**
     * Classe qui permet de récupère le pli en cours
     * -> Retourne le pli et l'affiche
     */
    class TacheAfficheTable extends AsyncTask<String, Void, ArrayList<Pli>> {
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
     * Classe qui permet de récupère les taches
     * -> Retourne les taches et les affiche
     */
    class TacheAfficheTaches extends AsyncTask<String, Void, ArrayList<Pli>> {
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
                    if(chaine[3]!=null)
                        pli = new Pli(chaine[0], new Carte(chaine[1], Integer.parseInt(chaine[2]),chaine[3]));
                    else
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
            afficheTaches(plis);
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

