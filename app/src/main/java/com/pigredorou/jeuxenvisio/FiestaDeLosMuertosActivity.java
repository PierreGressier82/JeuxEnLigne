package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class FiestaDeLosMuertosActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mZoneSaisie;
    Button mBoutonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masque le bar de titre de l'activité
        Objects.requireNonNull(getSupportActionBar()).hide();
        // Bloque la mise en veille
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Affiche la vue
        setContentView(R.layout.activity_fiesta_de_los_muertos);

        // Bouton retour
        ImageView boutonRetour = findViewById(R.id.bouton_retour);
        boutonRetour.setOnClickListener(this);
        boutonRetour.setImageResource(R.drawable.bouton_quitter);

        // Saisie
        mZoneSaisie = findViewById(R.id.zone_saisie);
        mZoneSaisie.setInputType(InputType.TYPE_CLASS_TEXT);

        // Bouton valider
        mBoutonValider = findViewById(R.id.bouton_valider);
        mBoutonValider.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_retour:
                finish();
                break;
            case R.id.bouton_valider:
                Toast.makeText(this, mZoneSaisie.getText().toString(), Toast.LENGTH_SHORT).show();
                ;
                break;
        }
    }
}
