package com.pigredorou.jeuxenvisio;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TimelineActivity extends JeuEnVisioActivity implements View.OnDragListener, View.OnTouchListener {

    // Variables globales
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "timeline.php?partie=";
    private static final int[] tableauCartesTable = {R.id.carte_table_1, R.id.carte_table_3, R.id.carte_table_5, R.id.carte_table_7, R.id.carte_table_9, R.id.carte_table_11};
    private static final int[] tableauCartesMain = {R.id.carte_main_1, R.id.carte_main_2, R.id.carte_main_3, R.id.carte_main_4};
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
            ImageView iv = findViewById(value);
            iv.setOnClickListener(this);
        }

        // Zone de drop écoute le drag pour recevoir une carte
        for (int value : tableauZoneDropTable) {
            ImageView iv = findViewById(value);
            iv.setOnDragListener(this);
        }

        // Zone de drop écoute le drag pour recevoir une carte
        for (int value : tableauCartesMain) {
            ImageView iv = findViewById(value);
            iv.setOnTouchListener(this);
        }
    }

    public void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Drawable enterShape = ResourcesCompat.getDrawable(getResources(), R.drawable.fond_carte_vert, null);
        Drawable normalShape = ResourcesCompat.getDrawable(getResources(), R.drawable.shape, null);


        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_STARTED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_ENTERED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.d("PGR-OnDrag", "ACTION_DRAG_EXITED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(normalShape);
                break;
            case DragEvent.ACTION_DROP: // Evenement qui informe du laché dans une vue en écoute
                Log.d("PGR-OnDrag", "ACTION_DROP " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                // TODO : gestion du drop : appel pour enregistrer la position
                break;
            case DragEvent.ACTION_DRAG_ENDED: // Evement qui informe de la fin du drag (se produit pour toutes les vues en écoute)
                Log.d("PGR-OnDrag", "ACTION_DRAG_ENDED " + v.getId() + " " + R.id.tableau_cartes + " " + R.id.tableau_table);
                v.setBackground(normalShape);
                //startRefreshAuto(urlJeu);
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getTag().toString().startsWith("carte_main_")) {
            startDrag(v);
        }
        return false;
    }

    private void startDrag(View v) {
        stopRefreshAuto();
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);
        v.setVisibility(View.INVISIBLE);
    }
}