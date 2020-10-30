package com.pigredorou.jeuxenligne;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Controle extends AsyncTask<String, Void, Void> {

    public static ArrayList<Carte> getCartes() {

        ArrayList<Carte> cartes = new ArrayList<Carte>();

        try {
            String myurl= "http://julie.et.pierre.free.fr/Commun/getDistribution.php";

            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            /*
             * InputStreamOperations est une classe complémentaire:
             * Elle contient une méthode InputStreamToString.
             */
            String result = InputStreamOperations.InputStreamToString(inputStream);

            // On récupère le JSON complet
            JSONObject jsonObject = new JSONObject(result);
            // On récupère le tableau d'objets qui nous concernent
            JSONArray array = new JSONArray(jsonObject.getString("personnes"));
            // Pour tous les objets on récupère les infos
            for (int i = 0; i < array.length(); i++) {
                // On récupère un objet JSON du tableau
                JSONObject obj = new JSONObject(array.getString(i));
                // On fait le lien Personne - Objet JSON
                Carte jeu = new Carte();
                jeu.setValeur(obj.getInt("valeur_carte"));
                jeu.setCouleur(obj.getString("couleur_carte"));
                // On ajoute la personne à la liste
                cartes.add(jeu);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // On retourne la liste des personnes
        return cartes;
    }

    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }
}
