package com.pigredorou.jeuxenvisio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_ADMIN = "admin";

    /**
     * Replaces the content with the Fragment to display it.
     *
     * @param savedInstanceState Instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Affiche le bouton de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Chargement des valeurs par défaut
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        // Chargement du fragment avec les préférences
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        // Lecture d'une valeur
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String urlPref = sharedPref.getString("url", "");

        // Charge les valeurs par défaut, si vide
        if (urlPref.isEmpty()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("url", MainActivity.url);
            editor.apply();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        // Lecture de la valeur modifiée
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Toast.makeText(this, "Admin : " + sharedPref.getBoolean(KEY_PREF_ADMIN, false), Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

}