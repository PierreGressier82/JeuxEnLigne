package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pigredorou.jeuxenvisio.objets.Carte;
import com.pigredorou.jeuxenvisio.objets.TopTen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class TopTenActivity extends JeuEnVisioActivity {

    // URLs des actions en base
    public static String urlJeu = MainActivity.url + "TopTen.php?partie=";
    public static final String urlTopTenDevoilCarte = MainActivity.url + "TopTenDevoileCarte.php?partie=";
    // Tableaux des resssources
    static final int[] imagesCartes = {0, R.drawable.topten_1, R.drawable.topten_2, R.drawable.topten_3, R.drawable.topten_4, R.drawable.topten_5, R.drawable.topten_6, R.drawable.topten_7, R.drawable.topten_8, R.drawable.topten_9, R.drawable.topten_10};
    // Eléments graphiques
    TextView mTexteNbLicorne;
    TextView mTexteNbCaca;
    TextView mTexteManche;
    TextView mTextePerdu;
    TextView mTexteNbCartes;
    ImageView mImageCarteTapis;
    ImageView mImageMaCarte;
    Button mBoutonDevoiler;
    Button mBoutonMancheSuivante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Affiche la vue
        setContentView(R.layout.activity_top_ten);

        super.onCreate(savedInstanceState);

        // Chargement des élements de TopTen
        ChargeTopTen();

        startRefreshAuto(urlJeu);
    }

    @Override
    protected void onResume() {
        startRefreshAuto(urlJeu);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getTag().toString()) {
            case "bouton_devoiler_carte":
                desactiveBouton(mBoutonDevoiler);
                new MainActivity.TacheURLSansRetour().execute(urlTopTenDevoilCarte + mIdPartie + "&joueur=" + mPseudo);
                break;

            case "bouton_manche_suivante":
                desactiveBouton(mBoutonMancheSuivante);
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitTopTen + mIdPartie + "&manche=suivante");
                break;
        }
    }

    void ChargeTopTen() {
        // Tapis
        mTexteNbLicorne = findViewById(R.id.licorne);
        mTexteNbCaca = findViewById(R.id.caca);
        mTexteManche = findViewById(R.id.manche);
        mImageCarteTapis = findViewById(R.id.carte_tapis);
        mTextePerdu = findViewById(R.id.perdu);
        mTexteNbCartes = findViewById(R.id.nbCartes);

        // Ma carte
        mImageMaCarte = findViewById(R.id.carte);
        // Boutons
        mBoutonDevoiler = findViewById(R.id.bouton_devoiler_carte);
        mBoutonMancheSuivante = findViewById(R.id.bouton_manche_suivante);
        mBoutonDevoiler.setOnClickListener(this);
        mBoutonMancheSuivante.setOnClickListener(this);
    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);

        // Tapis
        TopTen topten = parseNoeudTapis(doc);
        afficheTapis(topten);

        // Ma carte
        ArrayList<Carte> listeCartes = parseNoeudsCarte(doc, "Cartes");
        afficheMaCarte(listeCartes);
    }

    void afficheTapis(TopTen topten) {
        String texteManche = "Manche " + topten.getManche() + "/5";
        String texteNbCarte = topten.getNbCartesSurTapis() + " carte(s) sur " + mListeJoueurs.size();
        mTexteNbLicorne.setText(String.valueOf(topten.getNbLicorne()));
        mTexteNbCaca.setText(String.valueOf(topten.getNbCaca()));
        mTexteManche.setText(texteManche);
        mTexteNbCartes.setText(texteNbCarte);
        if (topten.getNumeroCarte() == 0)
            mImageCarteTapis.setVisibility(View.INVISIBLE);
        else {
            mImageCarteTapis.setImageResource(imagesCartes[topten.getNumeroCarte()]);
            mImageCarteTapis.setVisibility(View.VISIBLE);
        }

        if (topten.getNbCaca() == 8) {
            Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator()); //do not alter
            animation.setRepeatCount(4);
            animation.setRepeatMode(Animation.REVERSE);
            animation.getFillAfter();
            mTextePerdu.startAnimation(animation);
            mTextePerdu.setVisibility(View.VISIBLE);
            desactiveBouton(mBoutonDevoiler);
            activeBouton(mBoutonMancheSuivante);
        } else
            mTextePerdu.setVisibility(View.INVISIBLE);

        if (topten.getNbCartesSurTapis() == mListeJoueurs.size())
            activeBouton(mBoutonMancheSuivante);
        else
            desactiveBouton(mBoutonMancheSuivante);
    }

    void afficheMaCarte(ArrayList<Carte> listeCartes) {
        int valeurCarte = 0;
        boolean carteDevoilee = false;

        // Dans TopTen, on a qu'une seule carte en main
        if (listeCartes != null && listeCartes.size() > 0) {
            valeurCarte = listeCartes.get(0).getValeur();
            carteDevoilee = listeCartes.get(0).isPose();
        }

        mImageMaCarte.setImageResource(imagesCartes[valeurCarte]);

        // Affiche le bouton manche suivante uniquement pour les joueurs admin
        if (mAdmin)
            mBoutonMancheSuivante.setVisibility(View.VISIBLE);
        else
            mBoutonMancheSuivante.setVisibility(View.GONE);

        // Désactive le bouton "dévoiler ma carte" si la carte a déjà été jouée
        if (carteDevoilee)
            desactiveBouton(mBoutonDevoiler);
        else
            activeBouton(mBoutonDevoiler);

    }
}