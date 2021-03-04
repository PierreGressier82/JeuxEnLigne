package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TimelineActivity extends JeuEnVisioActivity {

    // Variables globales
    // Variables statiques
    private final static String urlJeu = MainActivity.url + "timeline.php?partie=";
    // Elements graphique

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_timeline);

        super.onCreate(savedInstanceState);

        // TODO : ajouter les éléments de timeline
        findViewById(R.id.chargement).setVisibility(View.GONE); // TODO : a retier quand la lecture XML sera faite
    }

    public void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

    }
}