package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Salon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    protected static final String url = "http://julie.et.pierre.free.fr/Salon/";
    protected static final String urlGetJoueurs = url + "getJoueurs.php?salon=";
    private static final String urlDistribueCartes = url + "distribueCartes.php";
    private static final String urlDistribueTaches = url + "distribueTaches.php";
    private static final String urlRAZDistribution = url + "RAZDistribution.php?salon=";
    private static final String urlGetSalons = url + "getSalons.php";
    public static final int MAIN_JOUEUR_ACTIVITY_REQUEST_CODE=14;
    public static final String VALEUR_PSEUDO = "Pseudo";
    public static final String VALEUR_ID_SALON = "idSalon";
    public static final String VALEUR_NOM_SALON = "NomSalon";
    private int mSalon;
    private String mPseudo;
    private boolean mJoueurChoisi = false;
    private Button mBoutonValider;
    // Cartes
    private Button mBoutonRAZ;
    private Button mBoutonDistribueCartes;
    // Taches
    private Button mBoutonTachesMoins;
    private Button mBoutonTachesPlus;
    private Button mBoutonDistribueTache;
    private TextView mNbTaches;
    private TableLayout mOptionTaches;
    private LinearLayout mLigneNbTaches;
    private Button mBoutonOptionTache1;
    private Button mBoutonOptionTache2;
    private Button mBoutonOptionTache3;
    private Button mBoutonOptionTache4;
    private Button mBoutonOptionTache5;
    private Button mBoutonOptionTache6;
    private Button mBoutonOptionTache7;
    private Button mBoutonOptionTache8;
    private Button mBoutonOptionTache9;
    private Button mBoutonOptionTache10;
    // Mission
    private LinearLayout mLigneNumMission;
    private TextView mNumMission;
    private Button mBoutonMissionSuivante;
    // Autres options
    private Button mBoutonEchangeCarte;
    private Button mBoutonEchangeJeu;

    private SharedPreferences mPreferences;
    private Spinner mListeDeroulanteSalons;
    private Spinner mListeDeroulanteJoueurs;
    private ArrayList<Joueur> mListeJoueurs = new ArrayList<>();
    private ArrayList<Salon> mListeSalons = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapterSalons;
    private ArrayAdapter<String> mArrayAdapterJoueurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Charge le layout
        setContentView(R.layout.activity_main);

        // On recupère les préférences du joueur
        mPreferences = getPreferences(MODE_PRIVATE);
        mSalon = mPreferences.getInt(VALEUR_ID_SALON, 1);
        mPseudo = mPreferences.getString(VALEUR_PSEUDO, "");
        // TODO : prendre en compte les préférences récupérées

        // Liste des salons de jeu
        mListeDeroulanteSalons = findViewById(R.id.liste_salons);
        mListeDeroulanteSalons.setOnItemSelectedListener(this);
        mListeDeroulanteSalons.setTag("salons");
        mListeDeroulanteSalons.setSelection(mSalon-1);
        mArrayAdapterSalons = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        mArrayAdapterSalons.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListeDeroulanteSalons.setAdapter(mArrayAdapterSalons);
        new TacheGetSalons().execute(urlGetSalons);

        // Liste des joueurs
        mListeDeroulanteJoueurs = findViewById(R.id.liste_joueurs);
        mListeDeroulanteJoueurs.setOnItemSelectedListener(this);
        mArrayAdapterJoueurs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        mArrayAdapterJoueurs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListeDeroulanteJoueurs.setAdapter(mArrayAdapterJoueurs);

        // Bouton valider
        mBoutonValider = findViewById(R.id.boutonValider);
        mBoutonValider.setOnClickListener(this);

        // Gestion de la distribution des cartes
        mBoutonRAZ = findViewById(R.id.boutonRAZ);
        mBoutonDistribueCartes = findViewById(R.id.boutonDistribue);
        mBoutonRAZ.setOnClickListener(this);
        mBoutonDistribueCartes.setOnClickListener(this);
        mBoutonRAZ.setVisibility(View.GONE);
        mBoutonDistribueCartes.setVisibility(View.GONE);

        // Distributions des tâches
        mBoutonDistribueTache = findViewById(R.id.boutonDistribueTache);
        mBoutonTachesMoins = findViewById(R.id.boutonNbTacheMoins);
        mBoutonTachesPlus = findViewById(R.id.boutonNbTachePlus);
        mOptionTaches = findViewById(R.id.option_taches);
        mNbTaches = findViewById(R.id.nbTache);
        mLigneNbTaches = findViewById(R.id.ligne_nb_taches);
        mBoutonOptionTache1 = findViewById(R.id.option_tache_1);
        mBoutonOptionTache2 = findViewById(R.id.option_tache_2);
        mBoutonOptionTache3 = findViewById(R.id.option_tache_3);
        mBoutonOptionTache4 = findViewById(R.id.option_tache_4);
        mBoutonOptionTache5 = findViewById(R.id.option_tache_5);
        mBoutonOptionTache6 = findViewById(R.id.option_tache_6);
        mBoutonOptionTache7 = findViewById(R.id.option_tache_7);
        mBoutonOptionTache8 = findViewById(R.id.option_tache_8);
        mBoutonOptionTache9 = findViewById(R.id.option_tache_9);
        mBoutonOptionTache10 = findViewById(R.id.option_tache_10);
        mBoutonDistribueTache.setOnClickListener(this);
        mBoutonTachesMoins.setOnClickListener(this);
        mBoutonTachesPlus.setOnClickListener(this);
        mBoutonOptionTache1.setOnClickListener(this);
        mBoutonOptionTache2.setOnClickListener(this);
        mBoutonOptionTache3.setOnClickListener(this);
        mBoutonOptionTache4.setOnClickListener(this);
        mBoutonOptionTache5.setOnClickListener(this);
        mBoutonOptionTache6.setOnClickListener(this);
        mBoutonOptionTache7.setOnClickListener(this);
        mBoutonOptionTache8.setOnClickListener(this);
        mBoutonOptionTache9.setOnClickListener(this);
        mBoutonOptionTache10.setOnClickListener(this);
        mBoutonDistribueTache.setVisibility(View.GONE);
        mLigneNbTaches.setVisibility(View.GONE);
        mOptionTaches.setVisibility(View.GONE);

        // Mission
        mLigneNumMission = findViewById(R.id.ligne_num_mission);
        mBoutonMissionSuivante = findViewById(R.id.boutonValiderNumMission);
        mNumMission = findViewById(R.id.numMission);
        mBoutonMissionSuivante.setOnClickListener(this);
        mLigneNumMission.setVisibility(View.GONE);
        mBoutonMissionSuivante.setVisibility(View.GONE);
        // TODO : récupèrer en base le numéro de mission de la partie

        // Autre option
        mBoutonEchangeCarte = findViewById(R.id.boutonEchangeCarte);
        mBoutonEchangeJeu = findViewById(R.id.boutonEchangeJeu);
        mBoutonEchangeCarte.setVisibility(View.GONE);
        mBoutonEchangeJeu.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boutonValider:
                Intent MainJoueurActivity = new Intent(MainActivity.this, MainJoueurActivity.class);
                // Sauvegarde le pseudo
                Button bouton = v.findViewById(v.getId());
                int id_salon = mListeSalons.get(mListeDeroulanteSalons.getSelectedItemPosition()).getId();
                String nom_salon = mListeDeroulanteSalons.getSelectedItem().toString();
                String pseudo = mListeDeroulanteJoueurs.getSelectedItem().toString();
                // Sauvegarde des préférences
                mPreferences.edit().putString(VALEUR_PSEUDO, pseudo).apply();
                mPreferences.edit().putInt(VALEUR_ID_SALON, id_salon).apply();
                // Lance l'activité "Main joueur" avec le pseudo et l'id du salon en paramètre
                MainJoueurActivity.putExtra(VALEUR_PSEUDO, pseudo);
                MainJoueurActivity.putExtra(VALEUR_ID_SALON, id_salon);
                MainJoueurActivity.putExtra(VALEUR_NOM_SALON, nom_salon);
                startActivityForResult(MainJoueurActivity, MAIN_JOUEUR_ACTIVITY_REQUEST_CODE);
                break;

            case R.id.boutonDistribue :
                new TacheURLSansRetour().execute(urlDistribueCartes + "?typeCarte=1&salon="+ mListeSalons.get(mListeDeroulanteSalons.getSelectedItemPosition()).getId());
                Toast.makeText(this, "Distribution terminée", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boutonRAZ :
                new TacheURLSansRetour().execute(urlRAZDistribution+mListeSalons.get(mListeDeroulanteSalons.getSelectedItemPosition()).getId());
                Toast.makeText(this, "Distribution réinitialisée", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boutonDistribueTache :
                new TacheURLSansRetour().execute(urlDistribueTaches + "?salon="+ mListeSalons.get(mListeDeroulanteSalons.getSelectedItemPosition()).getId()+"&nbTache="+mNbTaches.getText());
                Toast.makeText(this, "Distribution terminée", Toast.LENGTH_SHORT).show();
                break;

            case R.id.boutonNbTacheMoins :
                if (!mNbTaches.getText().toString().equals("0"))
                    mNbTaches.setText(String.valueOf(Integer.parseInt(mNbTaches.getText().toString())-1));
                break;

            case R.id.boutonNbTachePlus :
                mNbTaches.setText(String.valueOf(Integer.parseInt(mNbTaches.getText().toString())+1));
                break;

            case R.id.boutonValiderNumMission :
                mNumMission.setText(String.valueOf(Integer.parseInt(mNumMission.getText().toString())+1));
                // TODO : MAJ en base du numéro de mission
                //new TacheURLSansRetour().execute(urlDistribueTaches + "?salon="+ mListeSalons.get(mListeDeroulanteSalons.getSelectedItemPosition()).getId()+"&nbTache="+mNbTaches.getText());
                Toast.makeText(this, "En cours de dev", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ligne_pseudo_j1 :
            case R.id.ligne_pseudo_j2 :
            case R.id.ligne_pseudo_j3 :
            case R.id.ligne_pseudo_j4 :
            case R.id.ligne_pseudo_j5 :
                ImageView iv;
                switch(mListeJoueurs.size()) {
                    case 5 :
                        iv = findViewById(R.id.pseudo_joueur5);
                        iv.setImageResource(R.drawable.icone_check);
                    case 4 :
                        iv = findViewById(R.id.pseudo_joueur4);
                        iv.setImageResource(R.drawable.icone_check);
                    case 3 :
                        iv = findViewById(R.id.pseudo_joueur3);
                        iv.setImageResource(R.drawable.icone_check);
                    default :
                        iv = findViewById(R.id.pseudo_joueur2);
                        iv.setImageResource(R.drawable.icone_check);
                        iv = findViewById(R.id.pseudo_joueur1);
                        iv.setImageResource(R.drawable.icone_check);
                }

                switch (v.getId()) {
                    case R.id.ligne_pseudo_j1 :
                        iv = findViewById(R.id.pseudo_joueur1);
                        break;
                    case R.id.ligne_pseudo_j2 :
                        iv = findViewById(R.id.pseudo_joueur2);
                        break;
                    case R.id.ligne_pseudo_j3 :
                        iv = findViewById(R.id.pseudo_joueur3);
                        break;
                    case R.id.ligne_pseudo_j4 :
                        iv = findViewById(R.id.pseudo_joueur4);
                        break;
                    case R.id.ligne_pseudo_j5 :
                    default :
                        iv = findViewById(R.id.pseudo_joueur5);
                        break;
                }
                iv.setImageResource(R.drawable.icone_check_blanc);
                mPseudo = iv.getTag().toString();
                mJoueurChoisi = true;
                break;

            case R.id.option_tache_1 :
            case R.id.option_tache_2 :
            case R.id.option_tache_3 :
            case R.id.option_tache_4 :
            case R.id.option_tache_5 :
            case R.id.option_tache_6 :
            case R.id.option_tache_7 :
            case R.id.option_tache_8 :
            case R.id.option_tache_9 :
            case R.id.option_tache_10 :
                Button b = findViewById(v.getId());
                if(v.getTag().equals("NO")) {
                    b.setTextColor(getResources().getColor(R.color.blanc));
                    b.setTag("YES");
                }
                else {
                    b.setTextColor(getResources().getColor(R.color.noir));
                    b.setTag("NO");
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.liste_salons:
                new TacheGetJoueursSalon().execute(urlGetJoueurs + mListeSalons.get(position).getId());
                break;
            case R.id.liste_joueurs:
                if (mListeJoueurs.get(position).getAdmin() == 1) {
                    mBoutonRAZ.setVisibility(View.VISIBLE);
                    mBoutonDistribueCartes.setVisibility(View.VISIBLE);
                    mBoutonDistribueTache.setVisibility(View.VISIBLE);
                    mLigneNbTaches.setVisibility(View.VISIBLE);
                    mOptionTaches.setVisibility(View.VISIBLE);
                    mLigneNumMission.setVisibility(View.VISIBLE);
                    mBoutonMissionSuivante.setVisibility(View.VISIBLE);
                    mBoutonEchangeCarte.setVisibility(View.VISIBLE);
                    mBoutonEchangeJeu.setVisibility(View.VISIBLE);
                } else {
                    mBoutonRAZ.setVisibility(View.GONE);
                    mBoutonDistribueCartes.setVisibility(View.GONE);
                    mBoutonDistribueTache.setVisibility(View.GONE);
                    mLigneNbTaches.setVisibility(View.GONE);
                    mOptionTaches.setVisibility(View.GONE);
                    mLigneNumMission.setVisibility(View.GONE);
                    mBoutonMissionSuivante.setVisibility(View.GONE);
                    mBoutonEchangeCarte.setVisibility(View.GONE);
                    mBoutonEchangeJeu.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void afficheJoueurs(ArrayList<Joueur> listeJoueurs) {
        TableLayout tl = findViewById(R.id.liste_joueurs_CTV);
        TableRow tr;
        TextView tv;
        ImageView iv;
        TableRow.LayoutParams paramsRow;
        TableRow.LayoutParams paramsIV;
        TableRow.LayoutParams paramsTV;
        tl.removeAllViewsInLayout();
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 0);
        params.setMargins(0, 0, 0, 0);
        tl.setLayoutParams(params);
        tl.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        for(int i=0; i<mArrayAdapterJoueurs.getCount();)
            mArrayAdapterJoueurs.remove(mArrayAdapterJoueurs.getItem(0));
        mArrayAdapterJoueurs.notifyDataSetChanged();
        // Affiche les joueurs dans la liste
        if (listeJoueurs != null) {
            String[] listePseudoJoueurs = new String[listeJoueurs.size()];
            for (int i = 0; i < listeJoueurs.size(); i++) {
                listePseudoJoueurs[i] = listeJoueurs.get(i).getNomJoueur();

                mArrayAdapterJoueurs.add(listePseudoJoueurs[i]);
                mArrayAdapterJoueurs.notifyDataSetChanged();

                paramsRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                paramsIV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
                tr = new TableRow(this);
                tv = new CheckedTextView(this);
                iv = new ImageView(this);
                // Ligne
                paramsRow.setMargins(10, 10, 0, 0);
                tr.setLayoutParams(paramsRow);
                tr.setOnClickListener(this);
                // Image
                paramsIV.setMargins(10, 30, 30, 0);
                iv.setLayoutParams(paramsIV);
                iv.setImageResource(R.drawable.icone_check);
                switch (i) {
                    case 0 :
                        tr.setId(R.id.ligne_pseudo_j1);
                        iv.setId(R.id.pseudo_joueur1);
                        break;
                    case 1 :
                        tr.setId(R.id.ligne_pseudo_j2);
                        iv.setId(R.id.pseudo_joueur2);
                        break;
                    case 2 :
                        tr.setId(R.id.ligne_pseudo_j3);
                        iv.setId(R.id.pseudo_joueur3);
                        break;
                    case 3 :
                        tr.setId(R.id.ligne_pseudo_j4);
                        iv.setId(R.id.pseudo_joueur4);
                        break;
                    case 4 :
                        tr.setId(R.id.ligne_pseudo_j5);
                        iv.setId(R.id.pseudo_joueur5);
                        break;
                }
                iv.setTag(listePseudoJoueurs[i]);
                tr.addView(iv);
                // Texte
                paramsTV.setMargins(0, 30, 0, 0);
                tv.setLayoutParams(paramsTV);
                tv.setText(listePseudoJoueurs[i]);
                tv.setTag(listePseudoJoueurs[i]);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                tr.addView(tv);
                // Ajout de la ligne dans la vue table
                tl.addView(tr);
            }
        }
    }

    private void afficheSalons(ArrayList<Salon> listeSalons) {
        // Affiche les salons dans la liste
        if (listeSalons != null) {
            String[] listeNomSalons = new String[listeSalons.size()];
            for (int i = 0; i < listeSalons.size(); i++) {
                listeNomSalons[i] = listeSalons.get(i).getNom();
                mArrayAdapterSalons.add(listeNomSalons[i]);
                mArrayAdapterSalons.notifyDataSetChanged();
            }
        }
    }

    /**
     * Classe qui permet de récupérer la liste des joueurs du salon et de l'afficher dans une liste déroulante
     * -> Retourne la liste des joeurs du salon
     */
    private class TacheGetSalons extends AsyncTask<String, Void, ArrayList<Salon>> {
        String result;
        @Override
        protected ArrayList<Salon> doInBackground(String... strings) {
            URL url;
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    String[] chaine = stringBuffer.split("_");
                    Salon salon = new Salon(Integer.parseInt(chaine[0]), chaine[1]);
                    mListeSalons.add(salon);
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }

            return mListeSalons;
        }

        @Override
        protected void onPostExecute(ArrayList<Salon> listeSalons) {
            afficheSalons(listeSalons);
            super.onPostExecute(listeSalons);
        }
    }

    /**
     * Classe qui permet de récupérer la liste des joueurs du salon et de l'afficher dans une liste déroulante
     * -> Retourne la liste des joeurs du salon
     */
    private class TacheGetJoueursSalon extends AsyncTask<String, Void, ArrayList<Joueur>> {
        String result;
        @Override
        protected ArrayList<Joueur> doInBackground(String... strings) {
            URL url;
            mListeJoueurs.clear();
            try {
                // l'URL est en paramètre donc toujours 1 seul paramètre
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    String[] chaine = stringBuffer.split("_");
                    Joueur joueur = new Joueur(chaine[1], chaine[0], Integer.parseInt(chaine[2]));
                    mListeJoueurs.add(joueur);
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }

            return mListeJoueurs;
        }

        @Override
        protected void onPostExecute(ArrayList<Joueur> listeJoueurs) {
            afficheJoueurs(listeJoueurs);
            super.onPostExecute(listeJoueurs);
        }
    }

    /**
     * Classe qui permet d'appeler une URL sans traitement d'information en retour
     */
    static class TacheURLSansRetour extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(String... strings) {
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
