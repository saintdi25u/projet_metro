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
        String fragLigne = s.nextLine();
        LineFragmentation res = null;
        try {
            res = plan.getArcs().get(fragLigne.toUpperCase());
            res.setIncident(incident);
            System.out.println("Incident créé avec succès sur le fraguement de ligne " + res.getName());
        } catch (Exception e) {
            System.out.println("Cet fraguement de ligne n'existe pas, veulliez choisir une station de la liste");
            initStationIncident(s, plan);
        }
    }

    public String saisiPreference(Scanner s) {
        String preference = "";
        System.out.println("Voici les choix de preference qui s'offrent à vous : \n");
        System.out.println("Taper 1 pour chercher les itineraires les plus rapides\n");
        System.out.println("Taper 2 pour chercher les itineraires avec le moins de changement de ligne\n");
        int choice = 0;
        try {
            choice = Integer.parseInt(s.nextLine());
        } catch (Exception e) {
            System.out.println("Veuillez ne saisir que des chiffres pour les choix de préférences\n");
            saisiPreference(s);
        }
        switch (choice) {
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
        } else {
            System.out.println("Veuillez rentrer une ligne valide");
            showIncidentsOnLine(s, p);
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
     * Méthode permettant de demander à l'utilisateur de rentrer sa coordonées X ou Y
     * 
     * @param s le scanner
     * @param String lattitude ou longitude selon le besoin
     * @return la latitude rentré par l'utilisateur
     */

    public float setPositionByUser(Scanner s, String axis) {
        System.out.println("Veuillez rentrer votre "+ axis + ".");
        s = new Scanner(System.in);
        float result = 0;
        try {
            result = s.nextFloat();
            if (result < 0 || result > Float.MAX_VALUE) {
                throw new IllegalArgumentException("Veuillez renseigner un nombre valide");
            }
        } catch (Exception e) {
            System.out.println("Erreur, Veuillez rentrer un nombre valide");
            setPositionByUser(s, axis);
        }
        return result;

    }

    /**
     * Fonction permettant de mettre en place la postion de l'utilisateur 
     * @param s le scanner
     * @param a classe application du programme
     */
    public void setPosition(Scanner s, App a) {
        a.setPositionX(setPositionByUser(s, "lattitude"));
		System.out.println("Latitude enregistré.");
		a.setPositionY(setPositionByUser(s, "longitude"));
		System.out.println("Longitude enregistré.");
		System.out.println("Voici vos coordonées actuelle : (" + a.getPositionX() + "," + a.getPositionY() + ")");
    }

    /**
     * Fonction qui permet de rechercher un itininéraire avec plusieurs étapes
     * @param s Scanner
     * @param p Plan
     * @param pref Preference de trajet de l'utilisateur
     * @param positionX positionX de l'utilisateur
     * @param positionY positionY de l'utilisateur
     */
    public void pathWithStep(Scanner s, Plan p, String pref, Float positionX, Float positionY) {
        System.out.println("Combien d'étape voulez-vous faire ?");
        System.out.println("Vous ne pouvez saisir que 3 étapes maximum");
        int nbStep = 0;
        try {
            nbStep = Integer.parseInt(s.nextLine());
            if (nbStep > 3 || nbStep <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Veuillez saisir un nombre d'étape valide");
            pathWithStep(s, p, pref, positionX, positionY);
        }
        float x = positionX;
        float y = positionY;
        for (int i = 0; i < nbStep; i++) {
            System.out.println("Veuillez saisir votre " + (i+1) + "e arrêt");
            String step = checkStation(p, s);
            p.findTheFinalPath(x, y, step, pref);
            x = p.getNoeuds().get(step).getPositionX();
            y = p.getNoeuds().get(step).getPositionY();
        }
        System.out.println("Veuillez saisir votre station d'arrivée");
        String arrivalStation = checkStation(p, s);
        p.findTheFinalPath(x, y, arrivalStation, pref);
    }

    /**
     * Fonction qui permet de sélectionner et de vérifier qu'une station existe
     * @param p Plan 
     * @param s Scanner
     * @return la station sélectionnée par l'utilisateur
     */
    public String checkStation(Plan p, Scanner s) {
        String station = s.nextLine();
        while (p.getNoeuds().get(station) == null) {
            System.out.println("Station invalide");
            System.out.println("Veuillez saisir une station valide"); 
            station = s.nextLine();
        }
        return station;
    }

    /**
     * Fonction qui permet de calculer l'itinéraire vers une station en fonction de la préférence utilisateur
     * @param s Scanner
     * @param p Plan
     * @param pref Préférence utilisateur
     * @param positionX positionX de l'utilisateur
     * @param positionY positionY de l'utilisateur
     */
    public void findPath(Scanner s, Plan p, String pref, Float positionX, Float positionY) {
        System.out.println("Veuillez saisir votre station d'arrivée");
        String arrivalStation = checkStation(p, s);
        p.findTheFinalPath(positionX, positionY, arrivalStation, pref);
        //System.out.println(message);
    }
}