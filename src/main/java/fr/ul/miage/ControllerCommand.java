package fr.ul.miage;

import java.util.Scanner;

public class ControllerCommand {

    /**
     * Fonction qui permet de créer un incident avec les interactions utilisateurs
     * @param s Scanner pour lire les commandes utilisateurs
     * @return un Incident
     */
    public Incident createIncident(Scanner s){
        System.out.println("Quelle est la rasion de l'incident ?");
        String reason = s.nextLine();
        Incident incident = new Incident(reason);
        return incident;
    }

    /**
     * Fonction qui permet de créer un incident sur une station avec les interactions utilisateurs
     * @param s Scanner pour lire les commandes utilisateurs
     * @param plan Plan du métro
     */
    public void initStationIncident(Scanner s, Plan plan){
        Incident incident = createIncident(s);
        System.out.println("Sur quelle station est l'incident ?");
        // Afficher le nom de toutes les stations
        System.out.println();
        String station = s.nextLine();
        boolean trouve = false;
        Station res = null;
        for (Station st : plan.getNoeuds()) {
            if (st.getName().contains(station.toUpperCase())) {
                res = st;
                trouve = true;
            }
        }
        if(!trouve){
            System.out.println("Cette station n'existe pas, veulliez choisir une station de la liste");
            initStationIncident(s, plan);
        } else {
            res.setIncident(incident);
            System.out.println("Incident créé avec succès sur la station " + res.getName());
        }
        
    }

    /**
     * Fonction qui permet de créer un incident sur un fraguemùent de ligne avec les interactions utilisateurs
     * @param s Scanner pour lire les commandes utilisateurs
     * @param plan Plan du métro
     */
    public void initFragLigneIncident(Scanner s, Plan plan) {
        Incident incident = createIncident(s);
        System.out.println("Sur quelle fraguement de ligne est l'incident ?");
        // Afficher le nom de tous les fraguement de ligne
        System.out.println();
        String fragLigne = s.nextLine();
        boolean trouve = false;
        LineFragmentation res = null;
        for (LineFragmentation lf : plan.getArcs()) {
            if (lf.getName().contains(fragLigne.toUpperCase())) {
                res = lf;
                trouve = true;
            }
        }
        if(!trouve){
            System.out.println("Cet fraguement de ligne n'existe pas, veulliez choisir une station de la liste");
            initStationIncident(s, plan);
        } else {
            res.setIncident(incident);
            System.out.println("Incident créé avec succès sur le fraguement de ligne " + res.getName());
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
}
