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
    @DisplayName("Test récuperation ligne avec 0 incidents sur les fragments de ligne")
    public void testGetIncidentsWithNoIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        ArrayList<LineFragmentation> results = l.getLineFragmentationWithIncident();
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Test récuperation ligne avec 1 incidents sur les fragments de ligne")
    public void testGetIncidentsWithOneIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        Incident incident = new Incident("Incendie");
        LineFragmentation lf = l.getFragements().get(1);
        ArrayList<LineFragmentation> expected = new ArrayList<LineFragmentation>();
        expected.add(lf);
        l.getFragements().get(1).setIncident(incident);
        ArrayList<LineFragmentation> results = l.getLineFragmentationWithIncident();
        assertThat(results).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test récuperation incident avec 2 incidents")
    public void testGetIncidentsWithTwoIncident() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        Incident incident = new Incident("Incendie");
        Incident incident2 = new Incident("Malaise");
        LineFragmentation lf1 = l.getFragements().get(1);
        LineFragmentation lf2 = l.getFragements().get(3);
        ArrayList<LineFragmentation> expected = new ArrayList<LineFragmentation>();
        expected.add(lf1);
        expected.add(lf2);
        l.getFragements().get(1).setIncident(incident);
        l.getFragements().get(3).setIncident(incident2);
        ArrayList<LineFragmentation> results = l.getLineFragmentationWithIncident();
        assertThat(results).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test récuperation incident avec 2 incidents différents")
    public void testGetIncidentsWithTwoIncidentDifferent() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("1");
        Incident incident = new Incident("Incendie");
        LineFragmentation lf1 = l.getFragements().get(1);
        LineFragmentation lf2 = l.getFragements().get(3);
        ArrayList<LineFragmentation> expected = new ArrayList<LineFragmentation>();
        expected.add(lf1);
        expected.add(lf2);
        l.getFragements().get(1).setIncident(incident);
        ArrayList<LineFragmentation> results = l.getLineFragmentationWithIncident();
        assertThat(results).isNotEqualTo(expected);
    }

    @Test
    @DisplayName("Test récuperation incident avec une ligne nulle")
    public void testGetIncidentsOnLineFragmentationWithLineNull() {
        assertThat(p.getLineFragmentationWithIncidentsOnLine(null)).isNull();
    }

    @Test
    @DisplayName("Test récuperation station avec 0 incident sur une station de la ligne")
    public void testGetStationWithIncidentOnLine() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("2");
        ArrayList<Station> result = l.getStationWithIncident();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test récupération station avec 1 incident sur une station de la ligne")
    public void testGetStationWith1IncidentOnLine() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("2");
        Incident incident = new Incident("Accident");
        ArrayList<Station> allStation = l.getAllStation();
        allStation.get(1).setIncident(incident);
        ArrayList<Station> expected = new ArrayList<Station>();
        expected.add(allStation.get(1));
        ArrayList<Station> result = l.getStationWithIncident();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test récupération station avec 2 incident sur une station de la ligne")
    public void testGetStationWith2IncidentOnLine() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("2");
        Incident incident = new Incident("Accident");
        Incident incident2 = new Incident("Travaux");
        ArrayList<Station> allStation = l.getAllStation();
        allStation.get(1).setIncident(incident);
        allStation.get(2).setIncident(incident2);
        ArrayList<Station> expected = new ArrayList<Station>();
        expected.add(allStation.get(1));
        expected.add(allStation.get(2));
        ArrayList<Station> result = l.getStationWithIncident();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test récupération station avec 2 incident sur une station de la ligne différnts")
    public void testGetStationWith2IncidentOnLineDifferents() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("2");
        Incident incident = new Incident("Accident");
        Incident incident2 = new Incident("Travaux");
        ArrayList<Station> allStation = l.getAllStation();
        allStation.get(1).setIncident(incident);
        allStation.get(2).setIncident(incident2);
        ArrayList<Station> expected = new ArrayList<Station>();
        expected.add(allStation.get(1));
        ArrayList<Station> result = l.getStationWithIncident();
        assertThat(result).isNotEqualTo(expected);
    }

    @Test
    @DisplayName("Test récuperation incident avec une ligne nulle")
    public void testGetIncidentsOnStationWithLineNull() {
        assertThat(p.getStationWithIncidentOnLine(null)).isNull();
    }

    @Test
    @DisplayName("Test de récupération de toutes les stations d'une ligne")
    public void testGetAllStationInLine() {
        HashMap<String, Line> lines = p.getLines();
        Line l = lines.get("3");
        ArrayList<Station> result = new ArrayList<Station>();
        result.add(new Station(0, 0, 0, "N"));
        result.add(new Station(0, 0, 0, "O"));
        result.add(new Station(0, 0, 0, "P"));
        result.add(new Station(0, 0, 0, "Q"));
        result.add(new Station(0, 0, 0, "R"));
        ArrayList<Station> expected = l.getAllStation();
        assertThat(result.size()).isEqualTo(expected.size());
    }

}
