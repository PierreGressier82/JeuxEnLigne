package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String url = "http://julie.et.pierre.free.fr/Salon/";
    private static final String urlDistribue = url + "distribueCartes.php";
    private static final String urlRAZDistribution = url + "RAZDistribution.php";
    public static final int MAIN_JOUEUR_ACTIVITY_REQUEST_CODE=14;
    public static final String VALEUR_PSEUDO = "Pseudo";
    private Button mBoutonJ1;
    private Button mBoutonJ2;
    private Button mBoutonJ3;
    private Button mBoutonJ4;
    private LinearLayout mLayoutBoutonsJoueurs;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Charge le layout
        setContentView(R.layout.activity_main);
        mLayoutBoutonsJoueurs = findViewById(R.id.layout_boutons_joueurs);

        // TODO : Créer les boutons en dynamique selon les joueurs présents dans le salon
        mBoutonJ1 = findViewById(R.id.boutonJ1);
        mBoutonJ2 = findViewById(R.id.boutonJ2);
        mBoutonJ3 = findViewById(R.id.boutonJ3);
        mBoutonJ4 = findViewById(R.id.boutonJ4);
        Button boutonRAZ = findViewById(R.id.boutonRAZ);
        Button boutonDistribue = findViewById(R.id.boutonDistribue);
        mBoutonJ1.setTag("boutonJ1");
        mBoutonJ2.setTag("boutonJ2");
        mBoutonJ3.setTag("boutonJ3");
        mBoutonJ4.setTag("boutonJ4");
        boutonRAZ.setTag("RAZ");
        boutonDistribue.setTag("Distribue");
        mBoutonJ1.setOnClickListener(this);
        mBoutonJ2.setOnClickListener(this);
        mBoutonJ3.setOnClickListener(this);
        mBoutonJ4.setOnClickListener(this);
        boutonRAZ.setOnClickListener(this);
        boutonDistribue.setOnClickListener(this);

        mPreferences = getPreferences(MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().startsWith("boutonJ")) {
            Intent MainJoueurActivity = new Intent(MainActivity.this, MainJoueurActivity.class);
            // Sauvegarde le pseudo
            Button bouton = v.findViewById(v.getId());
            // Sauvegarde des préférences
            mPreferences.edit().putString(VALEUR_PSEUDO, bouton.getText().toString()).apply();
            // Lance l'activité "Main joueur" avec le pseudo en paramètre
            MainJoueurActivity.putExtra(VALEUR_PSEUDO, bouton.getText().toString());
            startActivityForResult(MainJoueurActivity, MAIN_JOUEUR_ACTIVITY_REQUEST_CODE);
        }

        if (v.getTag().toString().equals("Distribue")) {
            new TacheURLSansRetour().execute(urlDistribue + "?salon=1");
            Toast.makeText(this, "Distribution terminée", Toast.LENGTH_SHORT).show();
        }

        if (v.getTag().toString().equals("RAZ")) {
            new TacheURLSansRetour().execute(urlRAZDistribution);
            Toast.makeText(this, "Distribution réinitialisée", Toast.LENGTH_SHORT).show();
        }
    }

    private void cacheBoutonsJoueurs(boolean cache) {
        if (cache) {
            mBoutonJ1.setVisibility(View.GONE);
            mBoutonJ2.setVisibility(View.GONE);
            mBoutonJ3.setVisibility(View.GONE);
            mBoutonJ4.setVisibility(View.GONE);
        }
        else {
            mBoutonJ1.setVisibility(View.VISIBLE);
            mBoutonJ2.setVisibility(View.VISIBLE);
            mBoutonJ3.setVisibility(View.VISIBLE);
            mBoutonJ4.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Classe qui permet de mettre à jour en base la main d'un joueur (update main + ajout carte sur la table)
     * -> Retourne la liste des cartes du joueur demandé
     */
    class TacheURLSansRetour extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(String... strings) {
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

            return null;
        }
    }
}
