package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.widget.TextView;

public class WhaaatActivity extends JeuEnVisioActivity {

    static final int[] tableIdTexteSituation = {R.id.texte_situation_1, R.id.texte_situation_2, R.id.texte_situation_3, R.id.texte_situation_4, R.id.texte_situation_5, R.id.texte_situation_6};
    static final int[] tableIdObjetChoix = {R.id.objet_1, R.id.objet_2, R.id.objet_3, R.id.objet_4, R.id.objet_5, R.id.objet_6, R.id.objet_7, R.id.objet_8, R.id.objet_9};
    static final int[] tableIdObjet = {R.id.objet_1_choix, R.id.objet_2_choix, R.id.objet_3_choix, R.id.objet_4_choix, R.id.objet_5_choix, R.id.objet_6_choix, R.id.objet_7_choix, R.id.objet_8_choix, R.id.objet_9_choix};
    // Eléments graphiques
    private TextView mScoreBleu;
    private TextView mScoreJaune;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_whaaat);

        super.onCreate(savedInstanceState);


    }


}