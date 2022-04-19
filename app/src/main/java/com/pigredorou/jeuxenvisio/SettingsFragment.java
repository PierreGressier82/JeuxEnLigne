package com.pigredorou.jeuxenvisio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Salon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.pigredorou.jeuxenvisio.MainActivity.url;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String urlGetSalons = url + "getAllSalons.php";
    public static String[] mListeNomSalons;

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

        // Création de liste liste des salons
        //new TacheGetXML().execute(urlGetSalons);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences mesPref = requireContext().getSharedPreferences(MainActivity.KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences prefParDefaut = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String pseudo = mesPref.getString(MainActivity.VALEUR_PSEUDO, "");

        if (preference instanceof MultiSelectListPreference) {
            if (preference.getKey().startsWith("salon_")) {
                MultiSelectListPreference prefSalon = findPreference(preference.getKey());
                String toast = "";
                for (int i = 0; i < prefSalon.getValues().size(); i++) {
                    toast += prefSalon.getEntries()[i] + "-";
                }
                Toast.makeText(getContext(), "Toast " + toast + " New " + newValue.toString(), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Autre pref" + preference.getKey(), Toast.LENGTH_SHORT).show();

        }

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
                if (preference.getKey().startsWith("salon_")) {
                    MultiSelectListPreference prefSalon = findPreference(preference.getKey());
                    String toast = "";
                    for (int i = 0; i < prefSalon.getValues().size(); i++) {
                        toast += prefSalon.getEntries()[i] + "-";
                    }
                    Toast.makeText(getContext(), "Toast " + toast + " New " + newValue.toString(), Toast.LENGTH_SHORT).show();
                } else
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

    static Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
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
            Salon salon = new Salon(idSalon, nomSalon, listeJoueurs, listeJeux);
            listeSalons.add(salon);
        }

        return listeSalons;
    }

    private void chargeEtAfficheListeSalons(ArrayList<Salon> listeSalons) {
        // Affiche les salons dans la liste
        if (listeSalons != null) {

            // Ajoute un catégorie "salons"
            PreferenceScreen root = getPreferenceScreen();
            PreferenceCategory prefSalons = new PreferenceCategory(getContext());
            prefSalons.setTitle(getResources().getString(R.string.salons));
            root.addPreference(prefSalons);

            // Pour chaque salons crée une liste de préférence
            for (int i = 0; i < listeSalons.size(); i++) {
                final MultiSelectListPreference listPref = new MultiSelectListPreference(getContext());
                listPref.setOrder(i);
                listPref.setDialogTitle(listeSalons.get(i).getNom());
                listPref.setTitle(listeSalons.get(i).getNom());
                listPref.setKey("salon_" + listeSalons.get(i).getNom());
                listPref.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.icone_salon, null));
                ArrayList<Joueur> listeJoueurs = listeSalons.get(i).getListeJoueurs();
                int nbJoueursActif = 0;
                String[] listePseudo = new String[listeJoueurs.size()];
                String[] listeIdJoueur = new String[listeJoueurs.size()];
                final HashSet<String> result = new HashSet<>();
                for (int j = 0; j < listeJoueurs.size(); j++) {
                    if (listeJoueurs.get(j).getActif() == 1) {
                        nbJoueursActif++;
                        result.add(listeJoueurs.get(j).getNomJoueur());
                    }
                    listePseudo[j] = listeJoueurs.get(j).getNomJoueur();
                    listeIdJoueur[j] = String.valueOf(listeJoueurs.get(j).getId());
                }
                if (listeJoueurs.size() == nbJoueursActif)
                    listPref.setSummary(listeJoueurs.size() + " joueurs (tous actifs)");
                else
                    listPref.setSummary(listeJoueurs.size() + " joueurs dont " + nbJoueursActif + " actifs");
                listPref.setEntries(listePseudo);
                listPref.setEntryValues(listeIdJoueur);
                // TODO : charger les joueurs non actif => trouver comment faire !
                //Collections.addAll(result, listePseudo);
                listPref.setDefaultValue(result);
                listPref.setOnPreferenceChangeListener(this);
                prefSalons.addPreference(listPref);
            }
        }
    }


    private class TacheGetXML extends AsyncTask<String, Void, Document> {

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
                chargeEtAfficheListeSalons(parseXMLSalonsJeu(doc));
            }
            super.onPostExecute(doc);
        }
    }
}