package fr.ul.miage;

import java.util.Scanner;

public class ControllerCommand {

    public Incident createIncident(Scanner s){
        System.out.println("Quelle est la rasion de l'incident ?");
        String reason = s.nextLine();
        Incident incident = new Incident(reason);
        return incident;
    }

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
}
