package com.pigredorou.jeuxenvisio;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Gambit7Activity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {

    // Constantes
    // Variables globales
    private String mPseudo; // Pseudo du joueur
    private int mIdSalon;
    private int mIdPartie;
    private TextView mTextView;
    private EditText mReponse;
    private ProgressBar mSablier;
    private CountDownTimer mCompteurARebours;
    private int mValeurProgression = 30;
    private Button mBoutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_gambit7);

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
        // Text
        mTextView = findViewById(R.id.temps_restant);
        mSablier = findViewById(R.id.sablier);
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonValider.setOnClickListener(this);
        mReponse = findViewById(R.id.reponse);
        mReponse.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start != 0) {
                    String reponse = "";
                    String text = s.toString().replace(" ", "");
                    int len = text.length();

                    for (int i = 0; i < len; i++) {
                        if (i != 0 && ((len - i) % 3) == 0)
                            reponse += " ";
                        reponse += text.charAt(i);
                    }
                    mReponse.setText(reponse);
                    mReponse.setSelection(reponse.length());
                }
            }
        });
        mValeurProgression = 30;
        startChrono();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bouton_retour) {
            finish();
        }

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    private void startChrono() {
        mSablier.setIndeterminate(false);
        mSablier.setMax(mValeurProgression);
        mSablier.setProgress(mValeurProgression);

        mCompteurARebours = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                String texte = String.valueOf(millisUntilFinished / 1000);
                mTextView.setText(texte);
                mSablier.setProgress((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                mBoutonValider.setOnClickListener(null);
                mBoutonValider.setBackgroundColor(getResources().getColor(R.color.jaune));
                mTextView.setText("Temps écoulé");
            }
        }.start();

    }
}