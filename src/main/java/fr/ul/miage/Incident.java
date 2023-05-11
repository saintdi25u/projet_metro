package fr.ul.miage;

public class Incident {
    private String reasonOfIncident;

    public Incident(String reasonOfIncident) {
        this.reasonOfIncident = reasonOfIncident;
    }

    public String getReasonOfIncident() {
        return reasonOfIncident;
    }
    public void setReasonOfIncident(String reasonOfIncident) {
        this.reasonOfIncident = reasonOfIncident;
    }
    @Override
    public String toString() {
        return "Raison de l'incident : " + reasonOfIncident;
    }

}
