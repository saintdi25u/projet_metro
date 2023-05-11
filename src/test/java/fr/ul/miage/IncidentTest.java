package fr.ul.miage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class IncidentTest {
    @Test
    @DisplayName("Création incident sur station")
    public void creaStation(){
        Incident incident = new Incident("testStation");
        Station station = new Station(1,1,1,"A");
        station.setIncident(incident);
        assertEquals(incident, station.getIncident());
    }

    @Test
    @DisplayName("Création incident sur fraguement de ligne")
    public void creaFragLigne(){
        Incident incident = new Incident("testFragLigne");
        LineFragmentation fragLigne = new LineFragmentation(0, null, null, 0);
        fragLigne.setIncident(incident);
        assertEquals(incident, fragLigne.getIncident());
    }

    @Test
    @DisplayName("Suppression incident sur station")
    public void supprStation(){
        Incident incident = new Incident("testStation");
        Station station = new Station(1,1,1,"A");
        station.setIncident(incident);
        station.removeIncident();
        assertNull(station.getIncident());
    }

    @Test
    @DisplayName("Suppression incident sur fraguement de ligne")
    public void supprFragLigne(){
        Incident incident = new Incident("testFragLigne");
        LineFragmentation fragLigne = new LineFragmentation(0, null, null, 0);
        fragLigne.setIncident(incident);
        fragLigne.removeIncident();
        assertNull(fragLigne.getIncident());
    }
}
