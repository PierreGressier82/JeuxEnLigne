package com.pigredorou.jeuxenvisio.outils;

import android.util.Log;

import com.pigredorou.jeuxenvisio.objets.Joueur;
import com.pigredorou.jeuxenvisio.objets.Salon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
        int nv = 0;
        int id = 0;
        int score = 0;
        int actif = 0;
        ArrayList<Joueur> listeJoueurs = new ArrayList<>();

        for (int i = 0; i < NoeudJoueurs.getChildNodes().getLength(); i++) { // Parcours toutes les cartes
            Node noeudCarte = NoeudJoueurs.getChildNodes().item(i);
            for (int j = 0; j < noeudCarte.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud carte
                Log.d("PGR-XML-Joueur", noeudCarte.getAttributes().item(j).getNodeName() + "_" + noeudCarte.getAttributes().item(j).getNodeValue());
                if (noeudCarte.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudCarte.getAttributes().item(j).getNodeName()) {
                    case "id":
                        id = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "pseudo":
                        pseudo = noeudCarte.getAttributes().item(j).getNodeValue();
                        break;
                    case "admin":
                        admin = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "new":
                        nv = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                    case "score":
                        score = Integer.parseInt(noeudCarte.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Joueur joueur = new Joueur(id, pseudo, score, admin, nv, actif);
            listeJoueurs.add(joueur);
        }

        return listeJoueurs;
    }

    public static ArrayList<Salon> parseXMLSalons(Document doc) {
        Element element = doc.getDocumentElement();
        element.normalize();

        ArrayList<Salon> listeSalons = new ArrayList<>();

        Node noeudSalons = getNoeudUnique(doc, "Salons");
        int idSalon = 0;
        int idPartie = 0;
        int numMission = 0;
        String nomSalon = "";
        for (int i = 0; i < noeudSalons.getChildNodes().getLength(); i++) { // Parcours toutes les salons
            Node noeudSalon = noeudSalons.getChildNodes().item(i);
            Log.d("PGR-XML-Salon", noeudSalon.getNodeName());
            for (int j = 0; j < noeudSalon.getAttributes().getLength(); j++) { // Parcours tous les attributs du noeud salon
                Log.d("PGR-XML-Salon", noeudSalon.getAttributes().item(j).getNodeName() + "_" + noeudSalon.getAttributes().item(j).getNodeValue());
                if (noeudSalon.getAttributes().item(j).getNodeValue().isEmpty())
                    continue;
                switch (noeudSalon.getAttributes().item(j).getNodeName()) {
                    case "id_salon":
                        idSalon = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "nom":
                        nomSalon = noeudSalon.getAttributes().item(j).getNodeValue();
                        break;
                    case "id_partie":
                        idPartie = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                    case "num_mission":
                        numMission = Integer.parseInt(noeudSalon.getAttributes().item(j).getNodeValue());
                        break;
                }
            }
            Salon salon = new Salon(idSalon, nomSalon, idPartie, numMission);
            listeSalons.add(salon);
        }

        return listeSalons;
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
