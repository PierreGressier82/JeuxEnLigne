package com.pigredorou.jeuxenligne;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultat;
    private Button boutonJ1;
    private Button boutonJ2;
    private Button boutonJ3;
    private Button boutonJ4;
    //private LinearLayout boutonsJoueurs;
    //private ArrayList<Carte> cartes = new ArrayList<Carte>();

    private static int[] imagesJaune = {0, R.drawable.jaune_1, R.drawable.jaune_2, R.drawable.jaune_3, R.drawable.jaune_4, R.drawable.jaune_5, R.drawable.jaune_6, R.drawable.jaune_7, R.drawable.jaune_8, R.drawable.jaune_9};
    private static int[] imagesRose = {0, R.drawable.rose_1, R.drawable.rose_2, R.drawable.rose_3, R.drawable.rose_4, R.drawable.rose_5, R.drawable.rose_6, R.drawable.rose_7, R.drawable.rose_8, R.drawable.rose_9};
    private static int[] imagesVert = {0, R.drawable.vert_1, R.drawable.vert_2, R.drawable.vert_3, R.drawable.vert_4, R.drawable.vert_5, R.drawable.vert_6, R.drawable.vert_7, R.drawable.vert_8, R.drawable.vert_9};
    private static int[] imagesBleu = {0, R.drawable.bleu_1, R.drawable.bleu_2, R.drawable.bleu_3, R.drawable.bleu_4, R.drawable.bleu_5, R.drawable.bleu_6, R.drawable.bleu_7, R.drawable.bleu_8, R.drawable.bleu_9};
    private static int[] imagesFusee = {0, R.drawable.fusee_1, R.drawable.fusee_2, R.drawable.fusee_3, R.drawable.fusee_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Charge le layout
        setContentView(R.layout.activity_main);
        resultat = findViewById(R.id.resultat);
        boutonJ1 = findViewById(R.id.boutonJ1);
        boutonJ2 = findViewById(R.id.boutonJ2);
        boutonJ3 = findViewById(R.id.boutonJ3);
        boutonJ4 = findViewById(R.id.boutonJ4);
        Button boutonRAZ = findViewById(R.id.boutonReset);
        boutonJ1.setTag("boutonJ1");
        boutonJ2.setTag("boutonJ2");
        boutonJ3.setTag("boutonJ3");
        boutonJ4.setTag("boutonJ4");
        boutonRAZ.setTag("boutonRAZ");
        boutonJ1.setOnClickListener(this);
        boutonJ2.setOnClickListener(this);
        boutonJ3.setOnClickListener(this);
        boutonJ4.setOnClickListener(this);
        boutonRAZ.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().startsWith("boutonJ")) {
            resultat.setText(R.string.Chargement);
            Button boutonJoueur = findViewById(v.getId());
            new TacheDistribueCartes().execute("http://julie.et.pierre.free.fr/Commun/getDistribution.php?joueur="+boutonJoueur.getText());
            cacheBoutonsJoueurs(true);
        }

        if (v.getTag().toString().startsWith("boutonRAZ")) {
            cacheBoutonsJoueurs(false);
            TableRow tableauCartes = findViewById(R.id.tableau_cartes);
            tableauCartes.removeAllViewsInLayout();
        }

        if (v.getTag().toString().startsWith("carte_")) {
            ImageView carte = findViewById(v.getId());
            carte.setVisibility(View.GONE);
            //Toast.makeText(this, carte.getTag().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Affiche les cartes dans la vue
     * @param cartes : Liste des cartes à afficher
     */
    private void afficheCartes(ArrayList<Carte> cartes) {
    //private void afficheCartes() {
        TableRow tableauCartes = findViewById(R.id.tableau_cartes);
        tableauCartes.removeAllViewsInLayout();
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(5,0,5,0);
        tableauCartes.setLayoutParams(params);

        // Affiche les cartes
        if (cartes != null)
            for (int i=0;i<cartes.size();i++) {
                ImageView carte = new ImageView(this);
                String nomCarte = cartes.get(i).getCouleur()+"_"+cartes.get(i).getValeur();

                carte.setImageResource(getImageCarte(cartes.get(i).getCouleur(), cartes.get(i).getValeur()));
                carte.setTag("carte_"+nomCarte);
                carte.setId(setId(i));
                carte.setOnClickListener(this);
                tableauCartes.addView(carte);
            }
    }

    private int setId(int idCarte) {
        int retour=0;
        switch (idCarte) {
            case 0 :
                retour=R.id.carte_1;
                break;
            case 1 :
                retour=R.id.carte_2;
                break;
            case 2 :
                retour=R.id.carte_3;
                break;
            case 3 :
                retour=R.id.carte_4;
                break;
            case 4 :
                retour=R.id.carte_5;
                break;
            case 5 :
                retour=R.id.carte_6;
                break;
            case 6 :
                retour=R.id.carte_7;
                break;
            case 7 :
                retour=R.id.carte_8;
                break;
            case 8 :
                retour=R.id.carte_9;
                break;
            case 9 :
                retour=R.id.carte_10;
                break;
            case 10 :
                retour=R.id.carte_11;
                break;
            case 11 :
                retour=R.id.carte_12;
                break;
            case 12 :
                retour=R.id.carte_13;
                break;
            case 13 :
                retour=R.id.carte_14;
                break;
            case 14 :
                retour=R.id.carte_15;
                break;
            case 15 :
                retour=R.id.carte_16;
                break;
            case 16 :
                retour=R.id.carte_17;
                break;
            case 17 :
                retour=R.id.carte_18;
                break;
            case 18 :
                retour=R.id.carte_19;
                break;
            case 19 :
                retour=R.id.carte_20;
                break;
        }

        return retour;
    }

    private void cacheBoutonsJoueurs(boolean cache) {
        if (cache) {
            boutonJ1.setVisibility(View.GONE);
            boutonJ2.setVisibility(View.GONE);
            boutonJ3.setVisibility(View.GONE);
            boutonJ4.setVisibility(View.GONE);
        }
        else {
            boutonJ1.setVisibility(View.VISIBLE);
            boutonJ2.setVisibility(View.VISIBLE);
            boutonJ3.setVisibility(View.VISIBLE);
            boutonJ4.setVisibility(View.VISIBLE);
            resultat.setVisibility(View.VISIBLE);
        }
    }

    private int getImageCarte(String couleurCarte, int valeurCarte) {
        int ressource=0;
        switch (couleurCarte) {
            case "jaune":
                ressource=imagesJaune[valeurCarte];
                break;
            case "rose":
                ressource=imagesRose[valeurCarte];
                break;
            case "bleu":
                ressource=imagesBleu[valeurCarte];
                break;
            case "vert":
                ressource=imagesVert[valeurCarte];
                break;
            default:
                ressource=imagesFusee[valeurCarte];
                break;
        }

        return ressource;
    }

    private class TacheDistribueCartes extends AsyncTask<String, Void, ArrayList<Carte>> {
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
            resultat.setText(result);
            afficheCartes(cartes);
            resultat.setVisibility(View.GONE);
            super.onPostExecute(cartes);
        }
    }}
