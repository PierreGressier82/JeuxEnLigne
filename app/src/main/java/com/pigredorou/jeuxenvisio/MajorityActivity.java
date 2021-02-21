package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MajorityActivity extends AppCompatActivity implements View.OnClickListener {

    // Variables globales
    private String mPseudo;
    private int mIdPartie;
    private int mMethodeSelection;
    private boolean mVoteFait;
    // Elements graphique
    private ImageView mCarteA;
    private ImageView mCarteB;
    private ImageView mCarteC;
    private Button mBoutonValider;
    private Button mBoutonMajorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_majority);

        // Chargement
        findViewById(R.id.chargement).setVisibility(View.GONE);

        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        int idSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        mMethodeSelection = intent.getIntExtra(MainActivity.VALEUR_METHODE_SELECTION, MainActivity.mSelectionDragAndDrop);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);
        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);

        // Cartes pour le vote
        chargeCartes();
        // Boutons
        chargeBoutons();

        // Chargement
        // TODO : A déplacer après lecture de la base
        findViewById(R.id.chargement).setVisibility(View.GONE);
    }

    private void chargeBoutons() {
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonValider.setOnClickListener(this);
        mVoteFait = false;
        mBoutonMajorite = findViewById(R.id.bouton_majorite);
        mBoutonMajorite.setOnClickListener(this);
    }

    private void chargeCartes() {
        mCarteA = findViewById(R.id.carte_a);
        mCarteA.setOnClickListener(this);
        mCarteB = findViewById(R.id.carte_b);
        mCarteB.setOnClickListener(this);
        mCarteC = findViewById(R.id.carte_c);
        mCarteC.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "bouton_retour":
                finish();
                break;
            case "carte_a":
            case "carte_b":
            case "carte_c":
                afficheCarteVote(v.getTag().toString());
                mVoteFait = true;
                break;

            case "bouton_valider":
                if (mVoteFait) {
                    mBoutonValider.setTextColor(getResources().getColor(R.color.noir));
                    mBoutonValider.setOnClickListener(null);
                }
                // TODO : verifier qu'une carte a été choisie
                break;

            case "bouton_majorite":
                // Désactive le bouton
                mBoutonMajorite.setTextColor(getResources().getColor(R.color.noir));
                mBoutonMajorite.setOnClickListener(null);
                break;
        }

    }

    private void afficheCarteVote(String tag) {
        mCarteA.setImageResource(R.drawable.majority_carte_a);
        mCarteB.setImageResource(R.drawable.majority_carte_b);
        mCarteC.setImageResource(R.drawable.majority_carte_c);
        mCarteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteC.setBackgroundResource(R.drawable.fond_carte_blanc);

        switch (tag) {
            case "carte_a":
                mCarteA.setImageResource(R.drawable.majority_carte_vote_a);
                mCarteA.setBackgroundResource(R.drawable.fond_carte_rouge);
                break;
            case "carte_b":
                mCarteB.setImageResource(R.drawable.majority_carte_vote_b);
                mCarteB.setBackgroundResource(R.drawable.fond_carte_rouge);
                break;
            case "carte_c":
                mCarteC.setImageResource(R.drawable.majority_carte_vote_c);
                mCarteC.setBackgroundResource(R.drawable.fond_carte_rouge);
                break;
        }
    }
}