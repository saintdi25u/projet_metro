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

public class PlanTest {

    @Test
    @DisplayName("Test station la plus proche")
    public void testNearestStationWithoutIncident() {
        Plan p = new Plan();
        float initialX = 6;
        float initialY = 16;
        Station stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest.getName()).isEqualTo("H");

        initialX = 30;
        initialY = 29;

        stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest.getName()).isEqualTo("W");
    }

    @Test
    @DisplayName("Test station la plus proche avec un incident juste à coté")
    public void testNearestStationWithoutIncidentWithPutIncident() {
        Plan p = new Plan();
        float initialX = 6;
        float initialY = 16;
        p.getNoeuds().get("H").setIncident(new Incident("Incendie"));
        Station stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest.getName()).isEqualTo("N");
        p.getNoeuds().get("N").setIncident(new Incident("Police"));
        stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest.getName()).isEqualTo("O");
    }

    @Test
    @DisplayName("Test station la plus proche avec mauvaise saisie de l'utilisateur")
    public void testNearestStationWithoutIncidentWithBadInputUser() {
        Plan p = new Plan();
        float initialX = Float.MAX_VALUE + 1;
        float initialY = 16;
        Station stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest).isNull();

        initialX = 10;
        initialY = Float.MIN_VALUE - 1000;
        stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());

        assertThat(stationNearest).isNull();
    }

    @Test
    @DisplayName("Test station la plus proche avec incident sur un fragment de ligne")
    public void testNearestStationWithoutIncidentWithIncidentOnLineFragmentation() {
        Plan p = new Plan();
        float initialX = 18;
        float initialY = 19;
        p.getArcs().get("BC").setIncident(new Incident("Incendie")); // On met unincident sur le fragment de ligne BC
        Station stationNearest = p.getNearestStation(initialX, initialY, new ArrayList<Station>());
        assertThat(stationNearest.getName()).isEqualTo("B");
    }

    @Test
    @DisplayName("Test station la plus proche avec incident sur un fragment de ligne")
    public void testNearestStationWithoutIncidentWithIncidentOnLineFragmentation2() {
        Plan p = new Plan();
        float initialX = 23;
        float initialY = 10;
        p.getArcs().get("JK").setIncident(new Incident("Incendie")); // On met un incident sur le fragment de ligne BC
        p.getArcs().get("JU").setIncident(new Incident("Incendie"));
        p.getArcs().get("TJ").setIncident(new Incident("Incendie"));
        p.getArcs().get("IJ").setIncident(new Incident("Incendie"));
        ArrayList<Station> stationsToDodge = new ArrayList<Station>();
        stationsToDodge.add(p.getNoeuds().get("U"));
        stationsToDodge.add(p.getNoeuds().get("T"));
        Station stationNearest = p.getNearestStation(initialX, initialY, stationsToDodge);
        assertThat(stationNearest.getName()).isEqualTo("K");
    }

    /**
     * Test dans un premier temps le cas ou la station d'arrivé existe
     * Test dans un second temps le cas ou la station d'arrivé n'existe pas
     * Test pour finir si le résultat retourné est bien celui attendu.
     */
    @Test
    @DisplayName("Test les distances à vole d'oiseau")
    public void testDistanceAFlightBird() {
        Plan plan = new Plan();

        // Test avec une station qui existe
        String stationExisting = "A";
        HashMap<String, Double> resultExisting = plan.distanceAFlightBird(stationExisting);
        assertNotNull(resultExisting);
        assertTrue(resultExisting.containsKey(stationExisting));

        // Test avec une station qui n'existe pas
        String stationNonExisting = "station";
        HashMap<String, Double> resultNonExisting = plan.distanceAFlightBird(stationNonExisting);
        assertNull(resultNonExisting);

        // Test si le résultat est bon :
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "station1");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "station2");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "station3");
        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        plan.setNoeuds(stations);
        HashMap<String, Double> result = plan.distanceAFlightBird(s3.getName());
        HashMap<String, Double> searchResult = new HashMap<String, Double>();
        searchResult.put(s1.getName(), 2.8284271247461903);
        searchResult.put(s2.getName(), 1.4142135623730951);
        searchResult.put(s3.getName(), 0.0);
        Assertions.assertEquals(searchResult, result);

    }

    /**
     * Initialisation d'un graphique simple, puis test si la méthode
     * reachableStations retourne bien les sations adjacentes de chaques stations
     */
    @Test
    @DisplayName("test les stations adjacentes")
    public void testReachableStations() {
        // Création d'une instance de votre classe
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "station1");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "station2");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "station3");
        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);

        HashMap<String, LineFragmentation> fragments = new HashMap<>();
        LineFragmentation l1 = new LineFragmentation(8, s1, s2, 1);
        LineFragmentation l2 = new LineFragmentation(9, s2, s3, 1);
        LineFragmentation l3 = new LineFragmentation(3, s1, s3, 1);
        fragments.put(s1.getName() + s2.getName(), l1);
        fragments.put(s2.getName() + s3.getName(), l2);
        fragments.put(s1.getName() + s3.getName(), l3);

        plan.setNoeuds(stations);
        plan.setArcs(fragments);

        // Appel de la méthode à tester
        HashMap<String, ArrayList<Station>> result = plan.reachableStations();

        // Vérification des résultats pour chaque station

        // Station1
        Assertions.assertTrue(result.containsKey("station1"));
        Assertions.assertTrue(result.get("station1").contains(s2));
        Assertions.assertTrue(result.get("station1").contains(s3));

        // Station2
        Assertions.assertTrue(result.containsKey("station2"));
        Assertions.assertTrue(result.get("station2").contains(s1));
        Assertions.assertTrue(result.get("station2").contains(s3));

        // Station3
        Assertions.assertTrue(result.containsKey("station3"));
        Assertions.assertTrue(result.get("station3").contains(s1));
        Assertions.assertTrue(result.get("station3").contains(s2));

    }

    /**
     * test si la méthode pileSort trie bien une HashMap
     */
    @Test
    @DisplayName("test le triage de la pile et la suppression des doublons")
    public void testPileSort() {
        Plan plan = new Plan();
        HashMap<String, Double> mapTest = new HashMap<>();
        mapTest.put("A", 5.0);
        mapTest.put("B", 3.0);
        mapTest.put("C", 5.0);
        mapTest.put("D", 2.0);
        mapTest.put("E", 4.0);
        HashMap<String, Double> result = plan.pileSort(mapTest);

        // HashMap recherché :
        HashMap<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("A", 5.0);
        expectedMap.put("E", 4.0);
        expectedMap.put("B", 3.0);
        expectedMap.put("D", 2.0);

        // Test :
        Assertions.assertEquals(expectedMap, result);
    }

    /**
     * Test si la derniere cle de la HashMap passé en paramètre est bien la bonne
     */
    @Test
    @DisplayName("test la récupération de la dernière cle d'une HashMap")
    public void testLastKeyInTheMap() {
        Plan plan = new Plan();

        HashMap<String, Double> map = new HashMap<>();
        map.put("A", 5.0);
        map.put("B", 3.0);
        map.put("C", 7.0);
        String result = plan.lastKeyInTheMap(map);
        String searchKey = "C";

        // Test :
        Assertions.assertEquals(searchKey, result);
    }

    /**
     * Initialisation d'un graphique simple, puis test si la méthode
     * pathBetweenTwoStations retourne bien les chemins possibles entre deux
     * stations
     */
    @Test
    @DisplayName("Test la methode qui renvoie les chemins possibles entre deux stations")
    public void testPathBetweenTwoStations() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "station1");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "station2");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "station3");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "station4");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);

        HashMap<String, LineFragmentation> fragments = new HashMap<>();
        LineFragmentation l1 = new LineFragmentation(8, s1, s2, 1);
        LineFragmentation l2 = new LineFragmentation(9, s2, s3, 1);
        LineFragmentation l3 = new LineFragmentation(3, s1, s3, 1);
        LineFragmentation l4 = new LineFragmentation(3, s3, s4, 1);
        fragments.put(s1.getName() + s2.getName(), l1);
        fragments.put(s2.getName() + s3.getName(), l2);
        fragments.put(s1.getName() + s3.getName(), l3);
        fragments.put(s3.getName() + s4.getName(), l4);

        plan.setNoeuds(stations);
        plan.setArcs(fragments);

        ArrayList<ArrayList<String>> searchdPaths = new ArrayList<>();
        searchdPaths.add(new ArrayList<>(Arrays.asList("station1", "station3", "station4")));
        searchdPaths.add(new ArrayList<>(Arrays.asList("station1", "station2", "station3", "station4")));

    }

    /**
     * Assertions.assertEquals(searchdPaths, result);
     * 
     * Initialisation d'un graphique simple, puis test si la méthode
     * DistanceWhithStartStation retourne bein la distance minimal entre deux
     * station
     */
    @Test
    @DisplayName("test la determination de la distance minimal d'une station avec la station de départ")
    public void testTimeWhithStartStation() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "station1");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "station2");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "station3");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "station4");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);

        HashMap<String, LineFragmentation> fragments = new HashMap<>();
        LineFragmentation l1 = new LineFragmentation(8, s1, s2, 1);
        LineFragmentation l2 = new LineFragmentation(9, s2, s3, 1);
        LineFragmentation l3 = new LineFragmentation(3, s1, s3, 1);
        LineFragmentation l4 = new LineFragmentation(3, s3, s4, 1);
        fragments.put(s1.getName() + s2.getName(), l1);
        fragments.put(s2.getName() + s3.getName(), l2);
        fragments.put(s1.getName() + s3.getName(), l3);
        fragments.put(s3.getName() + s4.getName(), l4);

        plan.setNoeuds(stations);
        plan.setArcs(fragments);

        Double result = plan.timeWhithStartStaion("station1", "station4");
        Double searchDistance = 15.0;
        Assertions.assertEquals(searchDistance, result);
    }

    /**
     * Initialisation d'un graphique simple, puis test si la méthode
     * DistanceWhithStartStation retourne bein le plus court chemin entre deux
     * stations
     */
    @Test
    @DisplayName("Test l'algorithme A*")
    public void testStarA() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "station1");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "station2");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "station3");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "station4");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);

        HashMap<String, LineFragmentation> fragments = new HashMap<>();
        LineFragmentation l1 = new LineFragmentation(8, s1, s2, 1);
        LineFragmentation l2 = new LineFragmentation(9, s2, s3, 1);
        LineFragmentation l3 = new LineFragmentation(3, s1, s3, 1);
        LineFragmentation l4 = new LineFragmentation(3, s3, s4, 1);
        fragments.put(s1.getName() + s2.getName(), l1);
        fragments.put(s2.getName() + s3.getName(), l2);
        fragments.put(s1.getName() + s3.getName(), l3);
        fragments.put(s3.getName() + s4.getName(), l4);

        plan.setNoeuds(stations);
        plan.setArcs(fragments);

        ArrayList<String> result = plan.starA("station1", "station4");
        ArrayList<String> searchResult = new ArrayList<>(Arrays.asList("station1", "station3", "station4"));
        Assertions.assertEquals(searchResult, result);

    }

    /**
     * Test si en passant deux mauvais paramètre si la méthode retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme A* si les deux entrée ne sont pas connue")
    public void testStarAError2Param() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.starA("station1", "station4");
        Assertions.assertNull(result);

    }

    /**
     * Test en passant une mauvaise station de départ en paramètre si la méthode
     * retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme A* si les deux entrée ne sont pas connue")
    public void testStarAErrorFirstParam() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.starA("station4", "B");
        Assertions.assertNull(result);

    }

    /**
     * Test en passant une mauvaise station de d'arrivé en paramètre si la méthode
     * retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme A* si les deux entrée ne sont pas connue")
    public void testStarAErrorSecondParam() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.starA("A", "station4");
        Assertions.assertNull(result);

    }

    /**
     * Test si la méthode sameElementAtEachPosition renvoie bien true si tout les
     * éléments de la liste sont identique
     * false sinon
     */
    @Test
    @DisplayName("Test la méthode qui renseigne si chaques élément d'une liste est le même ou non")
    public void testSameElementAtEachPosition() {
        Plan plan = new Plan();
        // test le résultat vrai
        ArrayList<Integer> testTrue = new ArrayList<>(Arrays.asList(1, 1, 1, 1));
        boolean resultTrue = plan.sameElementAtEachPosition(testTrue);
        boolean searchResultTrue = true;
        Assertions.assertEquals(searchResultTrue, resultTrue);

        // Test le résultat faux
        ArrayList<Integer> testFalse = new ArrayList<>(Arrays.asList(1, 1, 1, 2));
        boolean resultFalse = plan.sameElementAtEachPosition(testFalse);
        boolean searchResultFalse = false;
        Assertions.assertEquals(searchResultFalse, resultFalse);

    }

    /**
     * Test la méthode itineraryFeweLineChanges
     * si en passant deux mauvais paramètre si la méthode retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme itineraryFeweLineChanges si les deux entrée ne sont pas connue")
    public void testItineraryFeweLineChanges2Param() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.itineraryFeweLineChanges("station1", "station4");
        Assertions.assertNull(result);

    }

    /**
     * Test la méthode itineraryFeweLineChanges
     * Si en passant une mauvaise station de départ en paramètre si la méthode
     * retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme itineraryFeweLineChanges si les deux entrée ne sont pas connue")
    public void testItineraryFeweLineChangesFirstParam() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.itineraryFeweLineChanges("station4", "B");
        Assertions.assertNull(result);

    }

    /**
     * Test la méthode itineraryFeweLineChanges
     * Si en passant une mauvaise station de d'arrivé en paramètre si la méthode
     * retourne bien null
     * 
     */
    @Test
    @DisplayName("Test l'algorithme itineraryFeweLineChanges si les deux entrée ne sont pas connue")
    public void testItineraryFeweLineChangesSecondParam() {
        Plan plan = new Plan();

        // Exemple de test avec des données de stations et fragments de lignes
        HashMap<String, Station> stations = new HashMap<>();

        Station s1 = new Station(3, (float) 2.0, (float) 3.0, "A");
        Station s2 = new Station(3, (float) 3.0, (float) 4.0, "B");
        Station s3 = new Station(3, (float) 4.0, (float) 5.0, "C");
        Station s4 = new Station(3, (float) 5.0, (float) 6.0, "D");

        stations.put(s1.getName(), s1);
        stations.put(s2.getName(), s2);
        stations.put(s3.getName(), s3);
        stations.put(s4.getName(), s4);
        plan.setNoeuds(stations);

        ArrayList<String> result = plan.itineraryFeweLineChanges("A", "station4");
        Assertions.assertNull(result);

    }

}
