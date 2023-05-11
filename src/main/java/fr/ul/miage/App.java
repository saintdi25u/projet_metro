package fr.ul.miage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Hello world!
 *
 */

public class App 
{

	public void menu(Plan p){
		Scanner s = new Scanner(System.in);
		System.out.println("Bienvenue dans l'application");
		while(true){
			System.out.println("Si vous êtes admnistrateur,taper 1");
			System.out.println("Si vous êtes utilisateur,taper 2");
			System.out.println("Si vous voulez sortir de l'application, taper 3");
			try {
				int choix = Integer.parseInt(s.nextLine());
				switch(choix) {
					case 1:
						menuAdmin(s, p);
						break;
					case 2:
						menuUtil(s, p);
						break;
					case 3:
						System.exit(1);
						break;
					default:
						System.out.println("Désolé mais nous n'avons pas compris votre commande");
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menu(p);
			}
		}
	}

	public void menuUtil(Scanner s, Plan p){
		ControllerCommand c = new ControllerCommand();
		System.out.println("Bienvenue dans le menu utilisateur de l'application");
		boolean admin = true;
		while(admin){
			System.out.println("Si vous souhaitez sortir du menu utilisateur, taper 3");try {
				int choix = Integer.parseInt(s.nextLine());
				switch(choix){
					case 3:
						admin = false;
						System.out.println("Vous quittez le menu utilisateur");
						break;
					default:
						System.out.println("Désolé mais nous n'avons pas compris votre commande");
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menuUtil(s,p);
			}
		}
	}

	public void menuAdmin(Scanner s, Plan p){
		ControllerCommand c = new ControllerCommand();
		System.out.println("Bienvenue dans le menu administrateur de l'application");
		boolean admin = true;
		while(admin){
			System.out.println("Pour déclarer un incident sur une station, taper 1");
			System.out.println("Pour déclarer un incident entre deux station, taper 2");
			System.out.println("Si vous souhaitez sortir du menu administrateur, taper 3");
			try {
				int choix = Integer.parseInt(s.nextLine());
				switch(choix){
					case 1:
						c.initStationIncident(s, p);
						break;
					case 2:
						c.initFragLigneIncident(s, p);
						break;
					case 3:
						admin = false;
						System.out.println("Vous quittez le menu admnistrateur");
						break;
					default:
						System.out.println("Désolé mais nous n'avons pas compris votre commande");
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menuAdmin(s,p);
			}
		}

	}

    public static void main( String[] args )
    {
    	Plan p = new Plan();
		App app = new App();
		app.menu(p);
		/* 
       	ArrayList<String> paths = p.starA("V", "T");
    	for(String s :paths ) {
    		System.out.println(s+"\n");  
    	}
     	p.shapingPaths(paths);	
		 */	

    }
}
