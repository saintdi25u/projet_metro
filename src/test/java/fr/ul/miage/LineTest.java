package fr.ul.miage;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    Plan p = new Plan();

    @Test
    @DisplayName("Test récuperation incident avec 0 incidents")
    public void testGetIncidentsWithNoIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        ArrayList<Incident> results = l.getIncidents();
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Test récuperation incident avec 1 incidents")
    public void testGetIncidentsWithOneIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        Incident incident = new Incident("Incendie");
        ArrayList<Incident> expected = new ArrayList<Incident>();
        expected.add(incident);
        l.getFragements().get(1).setIncident(incident);
        ArrayList<Incident> results = l.getIncidents();
        assertThat(results).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test récuperation incident avec 2 incidents")
    public void testGetIncidentsWithTwoIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        Incident incident = new Incident("Incendie");
        Incident incident2 = new Incident("Malaise");
        ArrayList<Incident> expected = new ArrayList<Incident>();
        expected.add(incident);
        expected.add(incident2);
        l.getFragements().get(1).setIncident(incident);
        l.getFragements().get(3).setIncident(incident2);
        ArrayList<Incident> results = l.getIncidents();
        assertThat(results).isEqualTo(expected);
    }
}
