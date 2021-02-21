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
    private Button mBoutonValider;
    private ImageView mCarteVoteA;
    private ImageView mCarteVoteB;
    private ImageView mCarteVoteC;
    private ImageView mCarteVoteMajority;

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
        chargeCartesVote();
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
    }

    private void chargeCartesVote() {
        mCarteVoteA = findViewById(R.id.carte_vote_a);
        mCarteVoteB = findViewById(R.id.carte_vote_b);
        mCarteVoteC = findViewById(R.id.carte_vote_c);
        mCarteVoteMajority = findViewById(R.id.carte_vote_majority);
        mCarteVoteA.setOnClickListener(this);
        mCarteVoteB.setOnClickListener(this);
        mCarteVoteC.setOnClickListener(this);
        mCarteVoteMajority.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "bouton_retour":
                finish();
                break;
            case "carte_vote_a":
            case "carte_vote_b":
            case "carte_vote_c":
            case "carte_vote_majority":
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
        }

    }

    private void afficheCarteVote(String tag) {
        mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_blanc);
        mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_blanc);

        switch (tag) {
            case "carte_vote_a":
                mCarteVoteA.setBackgroundResource(R.drawable.fond_carte_orange);
                break;
            case "carte_vote_b":
                mCarteVoteB.setBackgroundResource(R.drawable.fond_carte_orange);
                break;
            case "carte_vote_c":
                mCarteVoteC.setBackgroundResource(R.drawable.fond_carte_orange);
                break;
            case "carte_vote_majority":
                mCarteVoteMajority.setBackgroundResource(R.drawable.fond_carte_orange);
                break;
        }
    }
}