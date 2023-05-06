package fr.ul.miage;

public class Station {
	private int stopTime;
	private float positionX;
	private float positionY;
	private String name;
	public Station(int stopTime, float positionX, float positionY, String name) {
		super();
		this.stopTime = stopTime;
		this.positionX = positionX;
		this.positionY = positionY;
		this.name = name;
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
	@Override
	public String toString() {
		return "Station [stopTime=" + stopTime + ", positionX=" + positionX + ", positionY=" + positionY + ", name="
				+ name + "]";
	}
	
	
}	
