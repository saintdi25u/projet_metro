package fr.ul.miage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class App {
	private String preference = "rapide";
	private double positionX;
	private double positionY;

	public String getPreference() {
		return preference;
	}

	public double getPositionX() {
		return positionX;
	}

	public void setPositionX(double positionX) {
		this.positionX = positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public void setPositionY(double positionY) {
		this.positionY = positionY;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	/**
	 * Fonction qui gère le menu générale de l'application
	 * 
	 * @param p Plan du métro
	 */
	public void menu(Plan p) {
		Scanner s = new Scanner(System.in);
		System.out.println("Bienvenue dans l'application");
		while (true) {
			System.out.println("Si vous êtes admnistrateur,taper 1");
			System.out.println("Si vous êtes utilisateur,taper 2");
			System.out.println("Si vous voulez sortir de l'application, taper 3");
			try {
				int choix = Integer.parseInt(s.nextLine());
				switch (choix) {
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
				System.out.println(
						"Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menu(p);
			}
		}
	}

	/**
	 * Fonction qui gère le menu administrateur de l'application
	 * 
	 * @param s Scanner pour lire les lignes de commandes de l'utilisateur
	 * @param p Plan du métro
	 */
	public void menuUtil(Scanner s, Plan p) {
		ControllerCommand c = new ControllerCommand();
		System.out.println("Bienvenue dans le menu utilisateur de l'application");
		boolean admin = true;
		while (admin) {
			System.out.println("Si vous souhaitez saisir vos préférence de trajet, taper 1");
			System.out.println("Si vous souhaitez vous faire localiser, taper 2");
			System.out.println("Si vous souhaitez effectuer un trajet avec plusieurs étape, taper 3");
			System.out.println("Si vous souhaitez effectuer un trajet simple, taper 4");
			System.out.println("Si vous souhaitez sortir du menu utilisateur, taper 5");
			try {
				int choix = Integer.parseInt(s.nextLine());
				switch (choix) {
					case 1:
						System.out.println("Saisir vos preferences de trajet");
						setPreference(c.saisiPreference(s));
					case 2:
						setPositionX(c.setPositionXByUser(s, p));
						System.out.println("Latitude enregistré.");
						setPositionY(c.setPositionYByUser(s, p));
						System.out.println("Longitude enregistré.");
						break;
					case 3:
						c.pathWithStep(s, p, preference);
						break;
					case 4:
						c.findPath(s, p, preference);
						break;
					case 5:
						admin = false;
						System.out.println("Vous quittez le menu utilisateur");
						break;
					default:
						System.out.println("Désolé mais nous n'avons pas compris votre commande");
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(
						"Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menuUtil(s, p);
			}
		}
	}

	/**
	 * Fonction qui gère le menu utilisateur de l'application
	 * 
	 * @param s Scanner pour lire les lignes de commandes de l'utilisateur
	 * @param p Plan du métro
	 */
	public void menuAdmin(Scanner s, Plan p) {
		ControllerCommand c = new ControllerCommand();
		System.out.println("Bienvenue dans le menu administrateur de l'application");
		boolean admin = true;
		while (admin) {
			System.out.println("Pour déclarer un incident sur une station, taper 1");
			System.out.println("Pour déclarer un incident entre deux station, taper 2");
			System.out.println("Pour voir les incidents sur une ligne, taper 3");
			System.out.println("Si vous souhaitez sortir du menu administrateur, taper 4");
			try {
				int choix = Integer.parseInt(s.nextLine());
				switch (choix) {
					case 1:
						c.initStationIncident(s, p);
						break;
					case 2:
						c.initFragLigneIncident(s, p);
						break;
					case 3:
						c.showIncidentsOnLine(s, p);
						break;
					case 4:
						admin = false;
						System.out.println("Vous quittez le menu admnistrateur");
						break;
					default:
						System.out.println("Désolé mais nous n'avons pas compris votre commande");
						break;
				}
			} catch (Exception e) {
				System.out.println(
						"Veuillez saisir les numéros 1, 2 ou 3 pour accéder aux fonctionnalités de l'application");
				menuAdmin(s, p);
			}
		}

	}

	public static void main(String[] args) {

		Plan p = new Plan();

		//App app = new App();
		//app.menu(p);
		ArrayList<String> a = p.starA("T", "V");
		for (int i = 0; i < a.size(); i++) {
			System.out.println(a.get(i));
			
		}
		p.shapingPaths(a);
	}
}
