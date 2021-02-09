package com.pigredorou.jeuxenvisio.outils;

import android.util.Log;

import com.pigredorou.jeuxenvisio.objets.Joueur;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class outilsXML {
    public static Node getNoeudUnique(Document doc, String nomDuNoeud) {
        NodeList listeNoeudsMission = doc.getElementsByTagName(nomDuNoeud);
        Node noeud = null;
        if (listeNoeudsMission.getLength() > 0) {
            noeud = listeNoeudsMission.item(0);
        }

        return noeud;
    }

    public static ArrayList<Joueur> parseNoeudsJoueur(Document doc) {
        Node NoeudJoueurs = getNoeudUnique(doc, "Joueurs");

        String pseudo = "";
        int admin = 0;
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        for (int i = 0; i < NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudJoueurs.getChildNodes().item(i);
            Log.d("PGR-XML-Joueur", noeudCarte.getNodeName());
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Joueur", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                if (noeudCarte.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "pseudo":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin":
                        admin = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(pseudo, admin);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    public static boolean suisJeAdmin(String pseudo, ArrayList<Joueur> listeJoueurs) {
        boolean suisAdmin = false;

        for (int i = 0; i < listeJoueurs.size(); i++) {
            // Suis-je admin ?
            if (pseudo.equals(listeJoueurs.get(i).getNomJoueur()) && listeJoueurs.get(i).getAdmin() == 1) {
                suisAdmin = true;
                break;
            }
        }
        return suisAdmin;
    }

}
