package com.pigredorou.jeuxenvisio;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Arrays;

public class WhaaatActivity extends JeuEnVisioActivity {

    // Variables statiques
    private static final int[] tableIdCarteSituation = {R.id.carte_situation_1, R.id.carte_situation_2, R.id.carte_situation_3, R.id.carte_situation_4, R.id.carte_situation_5, R.id.carte_situation_6};
    private static final int[] tableIdVoteSituation = {R.id.vote_situation_1, R.id.vote_situation_2, R.id.vote_situation_3, R.id.vote_situation_4, R.id.vote_situation_5, R.id.vote_situation_6};
    private static final int[] tableIdTexteSituation = {R.id.texte_situation_1, R.id.texte_situation_2, R.id.texte_situation_3, R.id.texte_situation_4, R.id.texte_situation_5, R.id.texte_situation_6};
    private static final int[] tableIdDevoilerSituation = {R.id.devoiler_situation_1, R.id.devoiler_situation_2, R.id.devoiler_situation_3, R.id.devoiler_situation_4, R.id.devoiler_situation_5, R.id.devoiler_situation_6};
    private static final int[] tableIdObjet = {R.id.objet_1, R.id.objet_2, R.id.objet_3, R.id.objet_4, R.id.objet_5, R.id.objet_6, R.id.objet_7, R.id.objet_8, R.id.objet_9};
    private static final int[] tableIdObjetChoix = {R.id.choix_objet_1, R.id.choix_objet_2, R.id.choix_objet_3, R.id.choix_objet_4, R.id.choix_objet_5, R.id.choix_objet_6, R.id.choix_objet_7, R.id.choix_objet_8, R.id.choix_objet_9};
    private static final int[] tableIdImageObjets = {R.drawable.whaaat_objet_1, R.drawable.whaaat_objet_2, R.drawable.whaaat_objet_3, R.drawable.whaaat_objet_4, R.drawable.whaaat_objet_5, R.drawable.whaaat_objet_6, R.drawable.whaaat_objet_7, R.drawable.whaaat_objet_8, R.drawable.whaaat_objet_9, R.drawable.whaaat_objet_10, R.drawable.whaaat_objet_11, R.drawable.whaaat_objet_12, R.drawable.whaaat_objet_13, R.drawable.whaaat_objet_14, R.drawable.whaaat_objet_15, R.drawable.whaaat_objet_16, R.drawable.whaaat_objet_17, R.drawable.whaaat_objet_18, R.drawable.whaaat_objet_19, R.drawable.whaaat_objet_20, R.drawable.whaaat_objet_21, R.drawable.whaaat_objet_22, R.drawable.whaaat_objet_23, R.drawable.whaaat_objet_24, R.drawable.whaaat_objet_25, R.drawable.whaaat_objet_26, R.drawable.whaaat_objet_27, R.drawable.whaaat_objet_28, R.drawable.whaaat_objet_29, R.drawable.whaaat_objet_30, R.drawable.whaaat_objet_31, R.drawable.whaaat_objet_32, R.drawable.whaaat_objet_33, R.drawable.whaaat_objet_34, R.drawable.whaaat_objet_35, R.drawable.whaaat_objet_36, R.drawable.whaaat_objet_37, R.drawable.whaaat_objet_38, R.drawable.whaaat_objet_39, R.drawable.whaaat_objet_40, R.drawable.whaaat_objet_41, R.drawable.whaaat_objet_42, R.drawable.whaaat_objet_43, R.drawable.whaaat_objet_44, R.drawable.whaaat_objet_45, R.drawable.whaaat_objet_46, R.drawable.whaaat_objet_47, R.drawable.whaaat_objet_48, R.drawable.whaaat_objet_49, R.drawable.whaaat_objet_50, R.drawable.whaaat_objet_51, R.drawable.whaaat_objet_52, R.drawable.whaaat_objet_53, R.drawable.whaaat_objet_54, R.drawable.whaaat_objet_55, R.drawable.whaaat_objet_56, R.drawable.whaaat_objet_57, R.drawable.whaaat_objet_58, R.drawable.whaaat_objet_59, R.drawable.whaaat_objet_60, R.drawable.whaaat_objet_61, R.drawable.whaaat_objet_62, R.drawable.whaaat_objet_63, R.drawable.whaaat_objet_64, R.drawable.whaaat_objet_65, R.drawable.whaaat_objet_66, R.drawable.whaaat_objet_67, R.drawable.whaaat_objet_68, R.drawable.whaaat_objet_69, R.drawable.whaaat_objet_70, R.drawable.whaaat_objet_71, R.drawable.whaaat_objet_72, R.drawable.whaaat_objet_73, R.drawable.whaaat_objet_74, R.drawable.whaaat_objet_75, R.drawable.whaaat_objet_76, R.drawable.whaaat_objet_77, R.drawable.whaaat_objet_78, R.drawable.whaaat_objet_79, R.drawable.whaaat_objet_80, R.drawable.whaaat_objet_81, R.drawable.whaaat_objet_82, R.drawable.whaaat_objet_83, R.drawable.whaaat_objet_84, R.drawable.whaaat_objet_85, R.drawable.whaaat_objet_86, R.drawable.whaaat_objet_87, R.drawable.whaaat_objet_88, R.drawable.whaaat_objet_89, R.drawable.whaaat_objet_90, R.drawable.whaaat_objet_91, R.drawable.whaaat_objet_92, R.drawable.whaaat_objet_93, R.drawable.whaaat_objet_94, R.drawable.whaaat_objet_95, R.drawable.whaaat_objet_96, R.drawable.whaaat_objet_97, R.drawable.whaaat_objet_98, R.drawable.whaaat_objet_99, R.drawable.whaaat_objet_100, R.drawable.whaaat_objet_101, R.drawable.whaaat_objet_102, R.drawable.whaaat_objet_103, R.drawable.whaaat_objet_104, R.drawable.whaaat_objet_105, R.drawable.whaaat_objet_106, R.drawable.whaaat_objet_107, R.drawable.whaaat_objet_108, R.drawable.whaaat_objet_109, R.drawable.whaaat_objet_110, R.drawable.whaaat_objet_111, R.drawable.whaaat_objet_112, R.drawable.whaaat_objet_113, R.drawable.whaaat_objet_114, R.drawable.whaaat_objet_115, R.drawable.whaaat_objet_116, R.drawable.whaaat_objet_117, R.drawable.whaaat_objet_118, R.drawable.whaaat_objet_119, R.drawable.whaaat_objet_120, R.drawable.whaaat_objet_121, R.drawable.whaaat_objet_122, R.drawable.whaaat_objet_123, R.drawable.whaaat_objet_124, R.drawable.whaaat_objet_125, R.drawable.whaaat_objet_126, R.drawable.whaaat_objet_127, R.drawable.whaaat_objet_128, R.drawable.whaaat_objet_129, R.drawable.whaaat_objet_130, R.drawable.whaaat_objet_131, R.drawable.whaaat_objet_132, R.drawable.whaaat_objet_133, R.drawable.whaaat_objet_134, R.drawable.whaaat_objet_135, R.drawable.whaaat_objet_136, R.drawable.whaaat_objet_137, R.drawable.whaaat_objet_138, R.drawable.whaaat_objet_139, R.drawable.whaaat_objet_140, R.drawable.whaaat_objet_141, R.drawable.whaaat_objet_142, R.drawable.whaaat_objet_143, R.drawable.whaaat_objet_144, R.drawable.whaaat_objet_145, R.drawable.whaaat_objet_146, R.drawable.whaaat_objet_147, R.drawable.whaaat_objet_148, R.drawable.whaaat_objet_149, R.drawable.whaaat_objet_150, R.drawable.whaaat_objet_151, R.drawable.whaaat_objet_152, R.drawable.whaaat_objet_153, R.drawable.whaaat_objet_154, R.drawable.whaaat_objet_155, R.drawable.whaaat_objet_156, R.drawable.whaaat_objet_157, R.drawable.whaaat_objet_158, R.drawable.whaaat_objet_159, R.drawable.whaaat_objet_160, R.drawable.whaaat_objet_161, R.drawable.whaaat_objet_162, R.drawable.whaaat_objet_163, R.drawable.whaaat_objet_164, R.drawable.whaaat_objet_165, R.drawable.whaaat_objet_166, R.drawable.whaaat_objet_167, R.drawable.whaaat_objet_168, R.drawable.whaaat_objet_169, R.drawable.whaaat_objet_170, R.drawable.whaaat_objet_171, R.drawable.whaaat_objet_172, R.drawable.whaaat_objet_173, R.drawable.whaaat_objet_174, R.drawable.whaaat_objet_175, R.drawable.whaaat_objet_176, R.drawable.whaaat_objet_177, R.drawable.whaaat_objet_178, R.drawable.whaaat_objet_179, R.drawable.whaaat_objet_180, R.drawable.whaaat_objet_181, R.drawable.whaaat_objet_182, R.drawable.whaaat_objet_183, R.drawable.whaaat_objet_184, R.drawable.whaaat_objet_185, R.drawable.whaaat_objet_186, R.drawable.whaaat_objet_187, R.drawable.whaaat_objet_188, R.drawable.whaaat_objet_189, R.drawable.whaaat_objet_190, R.drawable.whaaat_objet_191, R.drawable.whaaat_objet_192, R.drawable.whaaat_objet_193, R.drawable.whaaat_objet_194, R.drawable.whaaat_objet_195, R.drawable.whaaat_objet_196, R.drawable.whaaat_objet_197, R.drawable.whaaat_objet_198, R.drawable.whaaat_objet_199, R.drawable.whaaat_objet_200, R.drawable.whaaat_objet_201, R.drawable.whaaat_objet_202, R.drawable.whaaat_objet_203, R.drawable.whaaat_objet_204, R.drawable.whaaat_objet_205, R.drawable.whaaat_objet_206, R.drawable.whaaat_objet_207, R.drawable.whaaat_objet_208, R.drawable.whaaat_objet_209, R.drawable.whaaat_objet_210, R.drawable.whaaat_objet_211, R.drawable.whaaat_objet_212, R.drawable.whaaat_objet_213, R.drawable.whaaat_objet_214, R.drawable.whaaat_objet_215, R.drawable.whaaat_objet_216, R.drawable.whaaat_objet_217, R.drawable.whaaat_objet_218, R.drawable.whaaat_objet_219, R.drawable.whaaat_objet_220, R.drawable.whaaat_objet_221, R.drawable.whaaat_objet_222, R.drawable.whaaat_objet_223, R.drawable.whaaat_objet_224, R.drawable.whaaat_objet_225, R.drawable.whaaat_objet_226, R.drawable.whaaat_objet_227, R.drawable.whaaat_objet_228, R.drawable.whaaat_objet_229, R.drawable.whaaat_objet_230, R.drawable.whaaat_objet_231, R.drawable.whaaat_objet_232, R.drawable.whaaat_objet_233, R.drawable.whaaat_objet_234, R.drawable.whaaat_objet_235, R.drawable.whaaat_objet_236, R.drawable.whaaat_objet_237, R.drawable.whaaat_objet_238, R.drawable.whaaat_objet_239, R.drawable.whaaat_objet_240};
    private static final int CHOIX_AUCUN = 0;
    private static final int CHOIX_UTILE = 1;
    private static final int CHOIX_INUTILE = 2;
    private static final int ACTIF_SELECTIONNE = 2;
    private static final int ID_EQUIPE_BLEU = 3;
    private static final int ID_EQUIPE_JAUNE = 4;
    private static final String EQUIPE_BLEU = "Bleu";
    private static final String EQUIPE_JAUNE = "Jaune";
    private static final String urlJeu = MainActivity.url + "whaaat.php?partie=";
    private static final String urlEquipe = MainActivity.url + "selectionneEquipe.php?partie=";
    private static final String urlMAJIncideObjet = MainActivity.url + "majIndice.php?partie=";
    private static final String urlMAJScore = MainActivity.url + "majScoreEquipe?score=";
    // Eléments graphiques
    private TextView mTexteScoreBleu;
    private TextView mTexteScoreJaune;
    private TextView mJoueurActif;
    private Button mBoutonPartieSuivante;
    // Varaibles globales
    private final int[] choixOjets = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Permet de stocket l'état du choix
    private final int[] idSituations = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // Permet de stocket l'ID du mot des situations
    private int mIdJoueurActif;
    private String mPseudoActif;
    private String mCouleurEquipe;
    private String mCouleurEquipeActive;
    private int mScoreBleu;
    private int mScoreJaune;
    private int mIdMotBonneReponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_whaaat);
        findViewById(R.id.chargement).setVisibility(View.VISIBLE);
        findViewById(R.id.choix_equipe).setVisibility(View.VISIBLE);

        super.onCreate(savedInstanceState);

        chargeElementsWhaaat();
        afficheChoix();

        // Refresh info jeu
        startRefreshAuto(urlJeu);
    }

    private void chargeElementsWhaaat() {
        // Texte pour choix d'équipe
        findViewById(R.id.choix_equipe_Bleu).setOnClickListener(this);
        findViewById(R.id.choix_equipe_Jaune).setOnClickListener(this);

        // Situations - Masque les votes et les boutons pour dévoiler
        for (int value : tableIdVoteSituation) {
            findViewById(value).setVisibility(View.GONE);
        }
        for (int value : tableIdDevoilerSituation) {
            findViewById(value).setVisibility(View.GONE);
            findViewById(value).setOnClickListener(this);
        }

        // Scores
        mTexteScoreBleu = findViewById(R.id.score_bleu);
        mTexteScoreJaune = findViewById(R.id.score_jaune);
        mJoueurActif = findViewById(R.id.joueur_actif);
        mCouleurEquipeActive = "";

        // Bontons
        mBoutonPartieSuivante = findViewById(R.id.bouton_partie_suivante);
        mBoutonPartieSuivante.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v != null && v.getTag() != null) {
            if (v.getTag().toString().startsWith("objet_")) {
                clickObjet(v.getTag().toString());
            } else if (v.getTag().toString().startsWith("choix_equipe_")) {
                clickEquipe(v.getTag().toString());
            } else if (v.getTag().toString().startsWith("carte_situation_")) {
                clickVoteSituation(v.getTag().toString());
            } else if (v.getTag().toString().startsWith("devoiler_situation_")) {
                clickDevoilerSituation(v.getTag().toString());
            } else if (v.getTag().toString().equals("bouton_partie_suivante")) {
                new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitWhaaat + mIdPartie + "&partieSuivante=oui");
            }

        }
    }

    private void clickDevoilerSituation(String tag) {
        int idreponse = Integer.parseInt(tag.split("_")[2]);
        boolean reponse = (idreponse == mIdMotBonneReponse);

        // Si bonne 1 à 3 points (selon nombre d'indice)
        // Si mauvaise 1 point pour l'équipe adverse
        int equipe = 0;
        int nbIndice = 0;
        for (int choixOjet : choixOjets) {
            if (choixOjet != 0) {
                nbIndice++;
            }
        }
        int score = 0;
        if (reponse) {
            switch (nbIndice) {
                case 1:
                    score = 3;
                    break;
                case 2:
                    score = 2;
                    break;
                case 3:
                    score = 1;
                    break;
            }
            switch (mCouleurEquipe) {
                case EQUIPE_BLEU:
                    equipe = ID_EQUIPE_BLEU;
                    break;
                case EQUIPE_JAUNE:
                    equipe = ID_EQUIPE_JAUNE;
                    break;
            }
        } else {
            score = 1;
            switch (mCouleurEquipe) {
                case EQUIPE_BLEU:
                    equipe = ID_EQUIPE_JAUNE;
                    break;
                case EQUIPE_JAUNE:
                    equipe = ID_EQUIPE_BLEU;
                    break;
            }
        }
        new MainActivity.TacheURLSansRetour().execute(urlMAJScore + score + "&equipe=" + equipe);
        new MainActivity.TacheURLSansRetour().execute(MainActivity.urlInitWhaaat + mIdPartie + "&tour=1");
    }

    private void clickVoteSituation(String tag) {
        int idMot = Integer.parseInt(tag.split("_")[2]);
        new MainActivity.TacheURLSansRetour().execute(urlVote + mIdPartie + "&joueur=" + mIdJoueur + "&mot=" + idMot + "&mode=add");
    }

    private void clickReponse(boolean reponse) {
    }

    private void clickEquipe(String tag) {
        String equipe = tag.split("_")[2];
        int valeurEquipe = 3;

        if (equipe.equals(EQUIPE_JAUNE)) {
            valeurEquipe = 4;
            mCouleurEquipe = EQUIPE_JAUNE;
        } else
            mCouleurEquipe = EQUIPE_BLEU;

        new MainActivity.TacheURLSansRetour().execute(urlEquipe + mIdPartie + "&joueur=" + mIdJoueur + "&equipe=" + valeurEquipe);
    }

    private void clickObjet(String tagTexte) {
        String[] tag = tagTexte.split("_");
        int position = Integer.parseInt(tag[1]);
        int idObjet = Integer.parseInt(tag[2]);
        if (indicesAutorise(position)) {
            afficheChoix();
            int actif = choixOjets[position] + 1;
            new MainActivity.TacheURLSansRetour().execute(urlMAJIncideObjet + mIdPartie + "&actif=" + actif + "&objet=" + idObjet);
        } else
            Toast.makeText(this, "Max 3 indices !", Toast.LENGTH_SHORT).show();
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
                case CHOIX_AUCUN:
                    iv.setImageResource(0);
                    break;
                case CHOIX_UTILE:
                    iv.setImageResource(R.drawable.tick_vert);
                    break;
                case CHOIX_INUTILE:
                    iv.setImageResource(R.drawable.croix_rouge);
                    break;
            }
        }
    }

    void parseXML(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        super.parseXML(doc);

        parseNoeudWhaaat(doc);

        afficheJoueurs();
        afficheEquipe();
        gestionContexte();

        mListeSituations = parseNoeudsSituations(doc);
        afficheSituations();

        mListeObjets = parseNoeudsObjets(doc);
        afficheObjets();

        mListeVotes = parseNoeudsVotes(doc);
        afficheVotes();

        // TODO : afficher les équipes des joueurs en bas
    }

    private void afficheVotes() {
        String listePseudo = "";
        int idMotPrec = 0;
        // Masque tous les votes
        for (int value : tableIdVoteSituation) {
            findViewById(value).setVisibility(View.GONE);
        }
        // Affiche les votes
        for (int i = 0; i < mListeVotes.size(); i++) {
            // Premier vote
            if (i == 0)
                idMotPrec = mListeVotes.get(i).getIdMot();
            // On change de mot ou c'est le dernier vote
            if (idMotPrec != mListeVotes.get(i).getIdMot()) {
                // Affiche la liste des pseudos
                afficheVote(idMotPrec, listePseudo);
                listePseudo = getPseudoFromIdJoueur(mListeVotes.get(i).getIdJoueur());
            } else {
                // Premier vote
                if (i == 0)
                    listePseudo = getPseudoFromIdJoueur(mListeVotes.get(i).getIdJoueur());
                else
                    listePseudo += "-" + getPseudoFromIdJoueur(mListeVotes.get(i).getIdJoueur());
            }
            idMotPrec = mListeVotes.get(i).getIdMot();
        }
        // Affiche la liste des pseudos du dernier mot
        afficheVote(idMotPrec, listePseudo);
    }

    private void afficheVote(int idMot, String listePseudo) {
        for (int i = 0; i < mListeSituations.size(); i++) {
            if (mListeSituations.get(i).getIdMot() == idMot) {
                TextView tv = findViewById(tableIdVoteSituation[i]);
                tv.setText(listePseudo);
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void gestionContexte() {
        for (int value : tableIdObjet) {
            // Si je suis le joueur actif
            // - Permet de sélectionner des indices
            if (mIdJoueurActif == mIdJoueur)
                findViewById(value).setOnClickListener(this);
            else
                findViewById(value).setOnClickListener(null);
        }

        // Vide la liste des indices si je ne suis pas le joueur actif
        if (mIdJoueurActif != mIdJoueur) {
            Arrays.fill(choixOjets, 0);
        }
    }

    private void afficheEquipe() {
        // Affiche la sélection de l'équipe si elle n'est pas encore choisie
        for (int i = 0; i < mListeJoueurs.size(); i++) {
            if (mListeJoueurs.get(i).getNomJoueur().equals(mPseudo)) {
                if (mListeJoueurs.get(i).getNomEquipe().isEmpty())
                    findViewById(R.id.choix_equipe).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.choix_equipe).setVisibility(View.GONE);
            }
        }

        // Met le fond de l'écran à la couleur de l'équipe
        LinearLayout blocJeu = findViewById(R.id.bloc_jeu);
        switch (mCouleurEquipe) {
            case EQUIPE_BLEU:
                blocJeu.setBackgroundColor(getResources().getColor(R.color.bleu));
                break;
            case EQUIPE_JAUNE:
                blocJeu.setBackgroundColor(getResources().getColor(R.color.jaune_clair));
                break;
        }
    }

    private void afficheJoueurs() {
        // Sélection d'un équipe
        // ---------------------
        LinearLayout equipeBleu = findViewById(R.id.equipe_bleu);
        LinearLayout equipeJaune = findViewById(R.id.equipe_jaune);

        equipeBleu.removeAllViewsInLayout();
        equipeJaune.removeAllViewsInLayout();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, (float) 0.5);
        params.setMargins(0, 0, 0, 0);

        equipeBleu.setLayoutParams(params);
        equipeJaune.setLayoutParams(params);

        // Affichage des joueurs de chaque équipe
        for (int i = 0; i < mListeJoueurs.size(); i++) {
            if (mListeJoueurs.get(i).getActif() == 1) {
                mIdJoueurActif = mListeJoueurs.get(i).getId();
                mPseudoActif = mListeJoueurs.get(i).getNomJoueur();
                mCouleurEquipeActive = mListeJoueurs.get(i).getNomEquipe();
            }
            if (mListeJoueurs.get(i).getNomJoueur().equals(mPseudo))
                mCouleurEquipe = mListeJoueurs.get(i).getNomEquipe();

            TableRow.LayoutParams paramsTV = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0);
            paramsTV.setMargins(10, 0, 0, 0);

            // Element
            TextView tv = new TextView(this);
            paramsTV.setMargins(20, 30, 0, 0);
            tv.setLayoutParams(paramsTV);
            tv.setText(mListeJoueurs.get(i).getNomJoueur());
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextSize(Dimension.SP, 25);
            tv.setTextColor(getResources().getColor(R.color.blanc));
            switch (mListeJoueurs.get(i).getNomEquipe()) {
                case EQUIPE_BLEU:
                    equipeBleu.addView(tv, paramsTV);
                    break;
                case EQUIPE_JAUNE:
                    equipeJaune.addView(tv, paramsTV);
                    break;
            }
        }

        // Affiche le pseudo du joeur actif dans la couleur de son équipe
        switch (mCouleurEquipeActive) {
            case EQUIPE_BLEU:
                mJoueurActif.setTextColor(getResources().getColor(R.color.bleu));
                break;
            case EQUIPE_JAUNE:
                mJoueurActif.setTextColor(getResources().getColor(R.color.jaune_clair));
                break;
        }
        mJoueurActif.setText(mPseudoActif);

        // Si c'est mon équipe qui joue mais que je ne suis pas le joueur actif, je peux "voter" pour des situations
        if (mCouleurEquipeActive.equals(mCouleurEquipe) && mIdJoueurActif != mIdJoueur) {
            activeVote(true);
            activeDevoiler(true);
        } else {
            activeVote(false);
            activeDevoiler(false);
        }

        // Si la partie est terminée, l'admin peut passer à la partie suivante
        if (mAdmin && (mScoreBleu >= 10 || mScoreJaune >= 10))
            mBoutonPartieSuivante.setVisibility(View.VISIBLE);
        else
            mBoutonPartieSuivante.setVisibility(View.GONE);
    }

    private void activeVote(boolean actif) {
        for (int value : tableIdCarteSituation) {
            if (actif)
                findViewById(value).setOnClickListener(this);
            else
                findViewById(value).setOnClickListener(null);
        }
    }

    private void activeDevoiler(boolean actif) {
        for (int value : tableIdDevoilerSituation) {
            if (actif)
                findViewById(value).setVisibility(View.VISIBLE);
            else
                findViewById(value).setVisibility(View.GONE);
        }
    }

    private void afficheObjets() {
        // Affiche les images des objets et les indices associés
        for (int i = 0; i < mListeObjets.size(); i++) {
            ImageView iv = findViewById(tableIdObjet[i]);
            int idObjet = mListeObjets.get(i).getId();
            iv.setImageResource(tableIdImageObjets[idObjet]);
            iv.setTag("objet_" + (i + 1) + "_" + mListeObjets.get(i).getId());
            iv = findViewById(tableIdObjetChoix[i]);
            switch (mListeObjets.get(i).getActif()) {
                case 1:
                    iv.setImageResource(0);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.tick_vert);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.croix_rouge);
                    break;
            }
            choixOjets[i + 1] = mListeObjets.get(i).getActif() - 1;
        }
    }

    private void afficheSituations() {
        // Affiche les situations et met en vert celle sélectionné pour le joueur actif
        for (int i = 0; i < mListeSituations.size(); i++) {
            TextView tv = findViewById(tableIdTexteSituation[i]);
            tv.setText(mListeSituations.get(i).getSituation());

            ImageView iv = findViewById(tableIdCarteSituation[i]);
            iv.setTag("carte_situation_" + mListeSituations.get(i).getIdMot());

            if (mListeSituations.get(i).getActif() == ACTIF_SELECTIONNE && mIdJoueurActif == mIdJoueur) {
                tv.setBackgroundColor(getResources().getColor(R.color.vert_clair));
                mIdMotBonneReponse = mListeSituations.get(i).getIdMot();
            } else {
                tv.setBackgroundColor(getResources().getColor(R.color.noir_transparent));
            }
        }
    }

    private void parseNoeudWhaaat(Document doc) {
        Node noeudWhaaat = getNoeudUnique(doc, "Whaaat");

        //int manche = 0;
        mScoreBleu = 0;
        mScoreJaune = 0;

        for (int i = 0; i < noeudWhaaat.getAttributes().getLength(); i++) { // Parcours tous les attributs du noeud majority
            Log.d("PGR-XML-Whaaat", noeudWhaaat.getAttributes().item(i).getNodeName() + "_" + noeudWhaaat.getAttributes().item(i).getNodeValue());
            if (noeudWhaaat.getAttributes().item(i).getNodeValue().isEmpty())
                continue;
            switch (noeudWhaaat.getAttributes().item(i).getNodeName()) {
                case "manche":
                    //manche = Integer.parseInt(noeudWhaaat.getAttributes().item(i).getNodeValue());
                    break;
                case "scoreBleu":
                    mScoreBleu = Integer.parseInt(noeudWhaaat.getAttributes().item(i).getNodeValue());
                    break;
                case "scoreJaune":
                    mScoreJaune = Integer.parseInt(noeudWhaaat.getAttributes().item(i).getNodeValue());
                    break;
            }
        }

        mTexteScoreBleu.setText(String.valueOf(mScoreBleu));
        mTexteScoreJaune.setText(String.valueOf(mScoreJaune));
    }
}