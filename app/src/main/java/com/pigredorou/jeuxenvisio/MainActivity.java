package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MAIN_JOUEUR_ACTIVITY_REQUEST_CODE=14;
    public static final String VALEUR_PSEUDO = "Pseudo";
    private Button boutonJ1;
    private Button boutonJ2;
    private Button boutonJ3;
    private Button boutonJ4;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Charge le layout
        setContentView(R.layout.activity_main);
        boutonJ1 = findViewById(R.id.boutonJ1);
        boutonJ2 = findViewById(R.id.boutonJ2);
        boutonJ3 = findViewById(R.id.boutonJ3);
        boutonJ4 = findViewById(R.id.boutonJ4);
        boutonJ1.setTag("boutonJ1");
        boutonJ2.setTag("boutonJ2");
        boutonJ3.setTag("boutonJ3");
        boutonJ4.setTag("boutonJ4");
        boutonJ1.setOnClickListener(this);
        boutonJ2.setOnClickListener(this);
        boutonJ3.setOnClickListener(this);
        boutonJ4.setOnClickListener(this);

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
        }
    }
}
