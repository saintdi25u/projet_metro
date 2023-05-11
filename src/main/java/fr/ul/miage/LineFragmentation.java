package fr.ul.miage;

public class LineFragmentation {
	private int time;
	private Station startStation;
	private Station endStation;
	private Double distance;
	private Incident incident;
	public LineFragmentation(int time, Station startStation, Station endStation, double distance) {
		super();
		this.time = time;
		this.startStation = startStation;
		this.endStation = endStation;
		this.distance = distance;
		this.incident = null; 
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public Station getStartStation() {
		return startStation;
	}
	public void setStartStation(Station startStation) {
		this.startStation = startStation;
	}
	public Station getEndStation() {
		return endStation;
	}
	public void setEndStation(Station endStation) {
		this.endStation = endStation;
	}
	@Override
	public String toString() {
		return "LineFragmentation [time=" + time + ", startStation=" + startStation + ", endStation=" + endStation
				+ ", distance=" + distance + "]";
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public void setIncident(Incident incident){
		this.incident = incident;
	}
	public Incident getIncident() {
		return incident;
	}
	/**
	 * Cette méthode permet de supprimer un incident sur le fragment de ligne
	 */
	public void removeIncident() {
		if(incident != null){
			this.incident = null;
		}
	}
	
	
}
