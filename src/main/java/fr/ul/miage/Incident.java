package fr.ul.miage;

public class Incident {
    private String reasonOfIncident;
    private int duration;

    public Incident(String reasonOfIncident, int duration) {
        this.reasonOfIncident = reasonOfIncident;
        this.duration = duration;
    }

    public String getReasonOfIncident() {
        return reasonOfIncident;
    }
    public void setReasonOfIncident(String reasonOfIncident) {
        this.reasonOfIncident = reasonOfIncident;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    
}
