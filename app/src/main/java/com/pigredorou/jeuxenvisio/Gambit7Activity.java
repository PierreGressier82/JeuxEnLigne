package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Gambit7Activity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {

    // Constantes
    // Variables globales
    private String mPseudo; // Pseudo du joueur
    private int mIdSalon;
    private int mIdPartie;
    private boolean mCommunicationFaite = false;
    private boolean mCommunicationAChoisir = false;
    private boolean mDetresseUtilise = false;
    private int mNbTacheAAtribuer = 0;
    private int mZoneSilence = 0;
    private int mNumeroPli = 0;
    private boolean mMajTerminee = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_gambit_7);

        // ENTETE
        TextView titre = findViewById(R.id.titre_jeu);
        // Recupère les paramètres
        TextView tvPseudo = findViewById(R.id.pseudo);
        TextView tvNomSalon = findViewById(R.id.nom_salon);
        final Intent intent = getIntent();
        mPseudo = intent.getStringExtra(MainActivity.VALEUR_PSEUDO);
        String nomSalon = intent.getStringExtra(MainActivity.VALEUR_NOM_SALON);
        mIdSalon = intent.getIntExtra(MainActivity.VALEUR_ID_SALON, 1);
        mIdPartie = intent.getIntExtra(MainActivity.VALEUR_ID_PARTIE, 1);
        tvPseudo.setText(mPseudo);
        tvNomSalon.setText(nomSalon);
        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }
}
