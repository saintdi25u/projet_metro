package fr.ul.miage;

public class Station {
	private int stopTime;
	private float positionX;
	private float positionY;
	private String name;
	private Incident incident;
	
	public Station(int stopTime, float positionX, float positionY, String name) {
		super();
		this.stopTime = stopTime;
		this.positionX = positionX;
		this.positionY = positionY; 
		this.name = name;
		this.incident = null;
	}
	public int getStopTime() { 
		return stopTime;
	}
	public void setStopTime(int stopTime) {
		this.stopTime = stopTime;
	}
	public float getPositionX() {
		return positionX;
	}
	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}
	public float getPositionY() {
		return positionY;
	}
	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setIncident(Incident incident){
		this.incident = incident;
	}
	public Incident getIncident() {
		return incident;
	}
	/**
	 * Cette méthode permet de supprimer un incident sur la station
	 */
	public void removeIncident() {
		if(incident != null){
			this.incident = null;
		}
	}
	@Override
	public String toString() {
		return "Station [stopTime=" + stopTime + ", positionX=" + positionX + ", positionY=" + positionY + ", name="
				+ name + ", incident=" + incident + "]";
	}
	
	
	
}	
