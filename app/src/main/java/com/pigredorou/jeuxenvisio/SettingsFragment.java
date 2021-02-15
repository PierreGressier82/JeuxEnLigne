package com.pigredorou.jeuxenvisio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    /**
     * Called during onCreate(Bundle) to supply the preferences for this
     * fragment. This calls setPreferenceFromResource to get the preferences
     * from the XML file.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment
     *                           should be rooted with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        //findPreference(R.xml.root_preferences);

        Preference pMethodeSelection = findPreference(getString(R.string.selection));
        Objects.requireNonNull(pMethodeSelection).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "Selection " + preference.getExtras().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Listener
        Objects.requireNonNull(findPreference("url")).setOnPreferenceChangeListener(this);
        Objects.requireNonNull(findPreference("admin")).setOnPreferenceChangeListener(this);
        Objects.requireNonNull(findPreference("pseudo")).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences mesPref = requireContext().getSharedPreferences(MainActivity.KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences prefParDefaut = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String pseudo = mesPref.getString(MainActivity.VALEUR_PSEUDO, "");

        switch (preference.getKey()) {
            case "url":
                Toast.makeText(getContext(), preference.getExtras().toString(), Toast.LENGTH_SHORT).show();
                break;
            case "admin":
                int admin = 1;
                // On récupère la valeur avant le changement
                if (prefParDefaut.getBoolean(preference.getKey(), false))
                    admin = 0;
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlMajAdmin + pseudo + "&admin=" + admin);
                Toast.makeText(getContext(), "Mise à jour enregistrée", Toast.LENGTH_SHORT).show();
                break;
            case "pseudo":
                // Ne rien faire
                break;
            default:
                Toast.makeText(getContext(), "Autre pref" + preference.getKey(), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        SharedPreferences mesPref = requireContext().getSharedPreferences(MainActivity.KEY_PREFERENCES, Context.MODE_PRIVATE);
        if ("pseudo".equals(preference.getKey())) {
            // Top en base que le joueur n'est pas attribué
            String pseudo = mesPref.getString(MainActivity.VALEUR_PSEUDO, "");
            new MainActivity.TacheURLSansRetour().execute(MainActivity.urlNewJoueur + pseudo);
            // Mise à jour du pseudo à vide dans les préférences de l'application
            mesPref.edit().remove(MainActivity.VALEUR_PSEUDO).apply();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Relance de l'application
            //Intent mStartActivity = new Intent(getContext(), MainActivity.class);
            //int mPendingIntentId = 123123;
            //PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            //AlarmManager mgr = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            //Objects.requireNonNull(mgr).set(AlarmManager.RTC, System.currentTimeMillis(), mPendingIntent);
            System.exit(0);
        } else {
            Toast.makeText(getContext(), "OPC pref" + preference.getKey(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}