package fr.ul.miage;

import java.util.ArrayList;

public class Line {
	private ArrayList<LineFragmentation> fragements;
	private int numLine;

	public Line(ArrayList<LineFragmentation> fragements, int numLine) {
		super();
		this.fragements = fragements;
		this.numLine = numLine;
	}

	/**
	 * Fonction permettant de récupérer tous les incident présents sur les fragments
	 * de ligne de la ligne
	 * 
	 * @return la liste des incidents des fragments de ligne de la ligne
	 */
	public ArrayList<LineFragmentation> getLineFragmentationWithIncident() {
		ArrayList<LineFragmentation> incidents = new ArrayList<LineFragmentation>();
		// on parcourt les fragements de ligne de la ligne
		for (int i = 0; i < fragements.size(); i++) {
			// si le fragment a un incident, on ajoute dans la liste a retourner
			if (fragements.get(i).hasIncident()) {
				incidents.add(fragements.get(i));
			}
		}
		return incidents;
	}

	/**
	 * Fonction permettant de récupérer tous les incident des stations présentes sur
	 * la ligne
	 * 
	 * @return la liste des stations sur les stations de la ligne
	 */
	public ArrayList<Station> getStationWithIncident() {
		ArrayList<Station> stations = new ArrayList<Station>();
		for (int i = 0; i < fragements.size(); i++) {
			if (fragements.get(i).getStartStation().hasIncident()) {
				stations.add(fragements.get(i).getStartStation());
			}
			if (i == fragements.size() - 1) {
				if (fragements.get(i).getEndStation().hasIncident()) {
					stations.add(fragements.get(i).getEndStation());
				}
			}
		}
		return stations;
	}

	/**
	 * Fonction permettant de récupérer la liste des stations présente sur une ligne
	 * 
	 * @return la liste des stations présente sur une ligne
	 */
	public ArrayList<Station> getAllStation() {
		ArrayList<Station> stations = new ArrayList<Station>();
		for (int i = 0; i < fragements.size(); i++) {
			stations.add(fragements.get(i).getStartStation());
			if (i == fragements.size() - 1) {
				stations.add(fragements.get(i).getEndStation());
			}
		}
		return stations;
	}

	public ArrayList<LineFragmentation> getFragements() {
		return fragements;
	}

	public void setFragements(ArrayList<LineFragmentation> fragements) {
		this.fragements = fragements;
	}

	public int getNumLine() {
		return numLine;
	}

	public void setNumLine(int numLine) {
		this.numLine = numLine;
	}

	@Override
	public String toString() {
		return "Line [fragements=" + fragements + ", numLine=" + numLine + "]\n";
	}

}
