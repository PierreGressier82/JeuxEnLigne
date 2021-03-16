package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TimelineActivity extends JeuEnVisioActivity implements View.OnDragListener {

    // Variables globales
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "timeline.php?partie=";
    private static final int[] tableauCartesTable = {R.id.carte_table_1, R.id.carte_table_3, R.id.carte_table_5, R.id.carte_table_7, R.id.carte_table_9, R.id.carte_table_11};
    private static final int[] tableauZoneDropTable = {R.id.drop_0, R.id.drop_2, R.id.drop_4, R.id.drop_6, R.id.drop_8, R.id.drop_10, R.id.drop_12};
    // Elements graphique

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_timeline);

        super.onCreate(savedInstanceState);

        chargeTable();

        // TODO : ajouter les éléments de timeline
        findViewById(R.id.chargement).setVisibility(View.GONE); // TODO : a retier quand la lecture XML sera faite
    }

    private void chargeTable() {
        // Carte écoute le click pour avoir le détail de la carte
        for (int value : tableauCartesTable) {
            TextView tv = findViewById(value);
            tv.setOnClickListener(this);
        }

        // Zone de drop écoute le drag pour recevoir une carte
        for (int value : tableauZoneDropTable) {
            TextView tv = findViewById(value);
            tv.setOnDragListener(this);
        }
    }

    public void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }
}