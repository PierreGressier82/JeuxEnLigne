package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static com.pigredorou.jeuxenvisio.outils.outilsXML.getNoeudUnique;

public class JustOneActivity extends JeuEnVisionActivity {

    // URLs des actions en base
    public static String urlJeu = MainActivity.url + "TopTen.php?partie=";
    public static final String urlTopTenDevoilCarte = MainActivity.url + "TopTenDevoileCarte.php?partie=";
    // Tableaux des resssources
    static final int[] imagesCartes = {0, R.drawable.topten_1, R.drawable.topten_2, R.drawable.topten_3, R.drawable.topten_4, R.drawable.topten_5, R.drawable.topten_6, R.drawable.topten_7, R.drawable.topten_8, R.drawable.topten_9, R.drawable.topten_10};
    // Variables globales
    private int mValeurMaCarte;
    private boolean mCarteDevoilee;
    private int mNbLicorne;
    private int mNbCaca;
    private int mNbCartes;
    private int mNumeroManche;
    private int mValeurCarteTapis;
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
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getTag().toString()) {
            case "bouton_devoiler_carte":
                new MainActivity.TacheURLSansRetour().execute(urlTopTenDevoilCarte + mIdPartie + "&joueur=" + mPseudo);
                desactiveBouton(mBoutonDevoiler);
                break;

            case "bouton_manche_suivante":
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitTopTen + mIdPartie + "&manche=suivante");
                desactiveBouton(mBoutonMancheSuivante);
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
        parseNoeudTapis(doc);
        afficheTapis();

        // Ma carte
        parseNoeudCarte(doc);
        afficheMaCarte();
    }

    void afficheTapis() {
        String texteManche = "Manche " + mNumeroManche + "/5";
        String texteNbCarte = mNbCartes + " carte(s) sur " + mListeJoueurs.size();
        mTexteNbLicorne.setText(String.valueOf(mNbLicorne));
        mTexteNbCaca.setText(String.valueOf(mNbCaca));
        mTexteManche.setText(texteManche);
        mTexteNbCartes.setText(texteNbCarte);
        if (mValeurCarteTapis == 0)
            mImageCarteTapis.setVisibility(View.INVISIBLE);
        else {
            mImageCarteTapis.setImageResource(imagesCartes[mValeurCarteTapis]);
            mImageCarteTapis.setVisibility(View.VISIBLE);
        }

        if (mNbCaca == 8) {
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
    }

    void afficheMaCarte() {
        mImageMaCarte.setImageResource(imagesCartes[mValeurMaCarte]);

        // Affiche le bouton manche suivante uniquement pour les joueurs admin
        if (mAdmin)
            mBoutonMancheSuivante.setVisibility(View.VISIBLE);
        else
            mBoutonMancheSuivante.setVisibility(View.GONE);

        // Désactive le bouton "dévoiler ma carte" si la carte a déjà été jouée
        if (mCarteDevoilee)
            desactiveBouton(mBoutonDevoiler);
        else
            activeBouton(mBoutonDevoiler);

        if (mNbCartes == mListeJoueurs.size())
            activeBouton(mBoutonMancheSuivante);
        else
            desactiveBouton(mBoutonMancheSuivante);
    }

    void parseNoeudCarte(Document doc) {
        Node noeudCarte = getNoeudUnique(doc, "Carte");

        mValeurMaCarte = 0;

        for (int i = 0; i < noeudCarte.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud carte
            Log.d("PGR-XML-Carte", noeudCarte.getAttributes().item(i).getNodeName() + "_" + noeudCarte.getAttributes().item(i).getNodeValue());
            switch (noeudCarte.getAttributes().item(i).getNodeName()) {
                case "numero":
                    if (!noeudCarte.getAttributes().item(i).getNodeValue().isEmpty())
                        mValeurMaCarte = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                    break;
                case "pose":
                    if (!noeudCarte.getAttributes().item(i).getNodeValue().isEmpty()) {
                        int ordrePose = Integer.parseInt(noeudCarte.getAttributes().item(i).getNodeValue());
                        mCarteDevoilee = ordrePose != 0;
                    } else
                        mCarteDevoilee = false;
                    break;
            }
        }
    }

    void parseNoeudTapis(Document doc) {
        Node noeudTapis = getNoeudUnique(doc, "Tapis");

        mValeurCarteTapis = 0;
        for (int i = 0; i < noeudTapis.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud tapis
            Log.d("PGR-XML-Tapis", noeudTapis.getAttributes().item(i).getNodeName() + "_" + noeudTapis.getAttributes().item(i).getNodeValue());
            if (noeudTapis.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudTapis.getAttributes().item(i).getNodeName()) {
                case "licorne":
                    mNbLicorne = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "caca":
                    mNbCaca = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "manche":
                    mNumeroManche = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "numero":
                    mValeurCarteTapis = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
                case "nbCartes":
                    mNbCartes = Integer.parseInt(noeudTapis.getAttributes().item(i).getNodeValue());
                    break;
            }
        }
    }
}