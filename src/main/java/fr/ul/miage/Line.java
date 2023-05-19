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
	 * Fonction permettant de récupérer tous les incident de la ligne
	 * 
	 * @return la liste des incident de la ligne
	 */
	public ArrayList<Incident> getIncidents() {
		ArrayList<Incident> incidents = new ArrayList<Incident>();
		// on parcourt les fragements de ligne de la ligne
		for (int i = 0; i < fragements.size(); i++) {
			// si le fragment a un incident, on ajoute dans la liste a retourner
			if (fragements.get(i).hasIncident()) {
				incidents.add(fragements.get(i).getIncident());
			}
		}
		return incidents;
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
