package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WhaaatActivity extends JeuEnVisioActivity {

    private static final int[] tableIdTexteSituation = {R.id.texte_situation_1, R.id.texte_situation_2, R.id.texte_situation_3, R.id.texte_situation_4, R.id.texte_situation_5, R.id.texte_situation_6};
    private static final int[] tableIdObjet = {R.id.objet_1, R.id.objet_2, R.id.objet_3, R.id.objet_4, R.id.objet_5, R.id.objet_6, R.id.objet_7, R.id.objet_8, R.id.objet_9};
    private static final int[] tableIdObjetChoix = {R.id.choix_objet_1, R.id.choix_objet_2, R.id.choix_objet_3, R.id.choix_objet_4, R.id.choix_objet_5, R.id.choix_objet_6, R.id.choix_objet_7, R.id.choix_objet_8, R.id.choix_objet_9};
    // Eléments graphiques
    private TextView mScoreBleu;
    private TextView mScoreJaune;
    // Varaibles globales
    private int[] choixOjets = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Permet de stocket l'état du choix
    private static final int CHOIX_AUCUN = 0;
    private static final int CHOIX_UTILE = 1;
    private static final int CHOIX_INUTILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_whaaat);

        super.onCreate(savedInstanceState);

        chargeElementsWhaaat();
        afficheChoix();

        // TODO : a retirer après lecture du XML
        findViewById(R.id.chargement).setVisibility(View.GONE);
    }

    private void chargeElementsWhaaat() {
        for (int value : tableIdObjet) {
            findViewById(value).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v != null && v.getTag() != null) {
            if (v.getTag().toString().startsWith("objet_")) {
                // TODO : ne pas permettre plus de 3 indices
                String tag[] = v.getTag().toString().split("_");
                int position = Integer.parseInt(tag[1]);
                if (indicesAutorise(position))
                    afficheChoix();
                else
                    Toast.makeText(this, "Déjà 3 indices !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean indicesAutorise(int position) {
        int nbIndices = 1;
        boolean indiceDejaUtilise = false;
        boolean indiceAutorise = true;
        for (int i = 0; i < choixOjets.length; i++) {
            if (choixOjets[i] != 0) {
                nbIndices++;
                if (i == position)
                    indiceDejaUtilise = true;
            }
        }
        if (nbIndices < 4 || indiceDejaUtilise)
            choixOjets[position] = (choixOjets[position] + 1) % 3;
        else
            indiceAutorise = false;

        return indiceAutorise;
    }

    private void afficheChoix() {
        for (int i = 0; i < tableIdObjetChoix.length; i++) {
            ImageView iv = findViewById(tableIdObjetChoix[i]);
            switch (choixOjets[i + 1]) {
                case 0:
                    iv.setImageResource(0);
                    break;
                case 1:
                    iv.setImageResource(R.drawable.tick_vert);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.croix_rouge);
                    break;
            }
        }
    }
}