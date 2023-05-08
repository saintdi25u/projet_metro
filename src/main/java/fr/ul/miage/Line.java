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
