package fr.ul.miage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class ControllerCommand {

    /**
     * Fonction qui permet de créer un incident avec les interactions utilisateurs
     * 
     * @param s Scanner pour lire les commandes utilisateurs
     * @return un Incident
     */
    public Incident createIncident(Scanner s) {
        System.out.println("Quelle est la rasion de l'incident ?");
        String reason = s.nextLine();
        Incident incident = new Incident(reason);
        return incident;
    }

    /**
     * Fonction qui permet de créer un incident sur une station avec les
     * interactions utilisateurs
     * 
     * @param s    Scanner pour lire les commandes utilisateurs
     * @param plan Plan du métro
     */
    public void initStationIncident(Scanner s, Plan plan) {
        Incident incident = createIncident(s);
        System.out.println("Sur quelle station est l'incident ?");
        // Afficher le nom de toutes les stations
        System.out.println();
        String station = s.nextLine();
        Station res = null;
        try {
           res = plan.getNoeuds().get(station.toUpperCase());
           res.setIncident(incident);
            System.out.println("Incident créé avec succès sur la station " + res.getName());
        } catch (Exception e) {
            System.out.println("Cette station n'existe pas, veulliez choisir une station de la liste");
            initStationIncident(s, plan);
        }
    }

    /**
     * Fonction qui permet de créer un incident sur un fraguemùent de ligne avec les
     * interactions utilisateurs
     * 
     * @param s    Scanner pour lire les commandes utilisateurs
     * @param plan Plan du métro
     */
    public void initFragLigneIncident(Scanner s, Plan plan) {
        Incident incident = createIncident(s);
        System.out.println("Sur quelle fraguement de ligne est l'incident ?");
        // Afficher le nom de tous les fraguement de ligne
        System.out.println();
        String fragLigne = s.nextLine();
        LineFragmentation res = null;
        try {
            res = plan.getArcs().get(fragLigne.toUpperCase());
            res.setIncident(incident);
            System.out.println("Incident créé avec succès sur le fraguement de ligne " + res.getName());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Cet fraguement de ligne n'existe pas, veulliez choisir une station de la liste");
            initStationIncident(s, plan);
        }
    }

    public String saisiPreference(Scanner s) {
        String preference = "";
        System.out.println("Voici les choix de preference qui s'offrent à vous : \n");
        System.out.println("Taper 1 pour chercher les itineraires les plus rapides\n");
        System.out.println("Taper 2 pour chercher les itineraires avec le moins de changement de ligne\n");
        int choix = 0;
        try {
            choix = s.nextInt();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Veuillez ne saisir que des chiffres pour les choix de préférences\n");
            saisiPreference(s);
        }
        switch (choix) {
            case 1:
                preference = "rapide";
                break;
            case 2:
                preference = "changement";
                break;
            default:
                System.out.println("Veuillez saisir un nombre de la liste\n");
                saisiPreference(s);
                break;
        }
        System.out.println("Vos choix ont été sauvegardés avec succès !");
        return preference;
    }

    /**
     * Méthode permettant d'afficher tous les incidents présent sur une ligne
     * 
     * @param s le scanner de l'application
     * @param p le plan
     */
    public void showIncidentsOnLine(Scanner s, Plan p) {
        System.out.println("Veuillez choisir le numéro de la ligne dont vous souhaitez connaitre les incidents.");
        for (Map.Entry<String, Line> entry : p.getLines().entrySet()) {
            System.out.println(entry.getKey() + ". Ligne " + entry.getKey());
        }
        String numLine = s.nextLine();
        if (p.getLines().containsKey(numLine)) {
            showIncidentOnLineFragmentationInLine(numLine, p);
            showIncidentOnStaionsInLine(numLine, p);
        }
    }

    /**
     * Méthode permettant d'afficher les incidents sur les fragments de ligne d'une
     * ligne
     * 
     * @param numLine le numéro de la ligne
     * @param p       le plan
     */
    public void showIncidentOnLineFragmentationInLine(String numLine, Plan p) {
        Line l = p.getLines().get(numLine);
        ArrayList<LineFragmentation> lineFragmentationWithIncidents = p.getLineFragmentationWithIncidentsOnLine(l);
        if (!lineFragmentationWithIncidents.isEmpty()) {
            System.out.println(
                    "Voici les incidents sur les fragments de ligne pour la ligne numéro " + numLine + ".");
            for (int i = 0; i < lineFragmentationWithIncidents.size(); i++) {
                System.out.println(lineFragmentationWithIncidents.get(i).getName() + " - Motif : "
                        + lineFragmentationWithIncidents.get(i).getIncident().getReasonOfIncident());
            }
        } else {
            System.out.println("Il n'y a aucun incident en cours sur les fragments de ligne de cette ligne.");
        }
    }

    /**
     * Méthode permettant d'afficher les incidents sur les stations d'une ligne
     * 
     * @param numLine le numéro de la ligne
     * @param p       le plan
     */
    public void showIncidentOnStaionsInLine(String numLine, Plan p) {
        Line l = p.getLines().get(numLine);
        ArrayList<Station> stationWithIncidents = p.getStationWithIncidentOnLine(l);
        if (!stationWithIncidents.isEmpty()) {
            System.out.println("Voici les incidents sur les stations pour la ligne numéro " + numLine + ".");
            for (int j = 0; j < stationWithIncidents.size(); j++) {
                System.out.println(stationWithIncidents.get(j).getName() + " - Motif : "
                        + stationWithIncidents.get(j).getIncident().getReasonOfIncident());
            }
        } else {
            System.out.println("Il n'y a aucun incident en cours sur les stations de cette ligne\n");
        }
    }

    /**
     * Méthode permettant de demander à l'utilisateur de rentré sa coordonées X
     * 
     * @param s le scanner
     * @param p le plan
     * @return la latitude rentré par l'utilisateur
     */

    public double setPositionXByUser(Scanner s, Plan p) {
        System.out.println("Veuillez rentrer votre latitude.");
        return s.nextDouble();
    }

    /**
     * Méthode permettant de demander à l'utilisateur de rentré sa coordonées Y
     * 
     * @param s le scanner
     * @param p le plan
     * @return la longitude rentré par l'utilisateur
     */
    public double setPositionYByUser(Scanner s, Plan p) {
        System.out.println("Veuillez rentrer votre longitude.");
        return s.nextDouble();
    }

    public void pathWithStep(Scanner s, Plan p, String pref) {
        System.out.println("Combien d'étape voulez-vous faire ?\n");
        System.out.println("Vous ne pouvez saisir que 3 étapes maximum\n");
        int nbStep = 0;
        try {
            nbStep = s.nextInt();
            if (nbStep > 3 || nbStep <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Veuillez saisir un nombre d'étape valide\n");
            pathWithStep(s, p, pref);
        }
        ArrayList<ArrayList<String>> path = new ArrayList<ArrayList<String>>();
        switch (pref) {
            case "rapide" :
                System.out.println("Veuillez saisir votre station de départ");
                String departureStation = s.nextLine();
                for (int i = 0; i < nbStep; i++) {
                    System.out.println("Veuillez saisir votre " + i+1 + "e arrêt");
                    String step = s.nextLine();
                    path.add(p.starA(departureStation, step));
                }
                break;
            case "changement":
                for (int i = 0; i < nbStep; i++) {

                }
                break;
        }
        System.out.println("Voici le trajet que vous devrait emprunter pour ralier les stations demandées :");
        for (int i = 0; i < nbStep; i++) {
            System.out.println("Pour aller jusqu'au " + i+1 + "e arrêt");
            p.shapingPaths(path.get(i));
        }
    }
}