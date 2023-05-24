package fr.ul.miage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class Plan {
	private HashMap<String, Line> lines = new HashMap<>();
	private HashMap<String, LineFragmentation> arcs = new HashMap<>();
	private HashMap<String, Station> noeuds = new HashMap<>();

	/**
	 * Constructeur du plan.
	 * Il permet de récupérer les données des fichiers line.json et station.json,
	 * pour créer un graphe en utilisant les object Station, LineFragmentation et
	 * Line.
	 */

	public Plan() {
		String filePathLine = "samples/line.json";
		String filePathStation = "samples/station.json";

		String contentLine = null;
		String contentStation = null;
		try {
			contentLine = new String(Files.readAllBytes(new File(filePathLine).toPath()), StandardCharsets.UTF_8);
			contentStation = new String(Files.readAllBytes(new File(filePathStation).toPath()), StandardCharsets.UTF_8);

		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(content);
		JSONObject Json1 = new JSONObject(contentLine);
		JSONObject linesJson = Json1.getJSONObject("lines");
		JSONArray lineJson = linesJson.getJSONArray("line");

		JSONObject Json2 = new JSONObject(contentStation);
		JSONObject stationsJson = Json2.getJSONObject("Stations");
		JSONArray stationJson = stationsJson.getJSONArray("Station");

		for (int i = 0; i < lineJson.length(); i++) {
			JSONObject jsonObject = lineJson.getJSONObject(i);
			int num = jsonObject.getInt("num");

			ArrayList<LineFragmentation> fragements = new ArrayList<>();
			JSONArray tabFragement = jsonObject.getJSONArray("fragments");
			for (int j = 0; j < tabFragement.length(); j++) {
				JSONObject fragementJson = tabFragement.getJSONObject(j);
				String startStationName = fragementJson.getString("startStation");
				String endStationName = fragementJson.getString("endStation");
				int travelTime = fragementJson.getInt("travelTime");
				Station startStation = null;
				Station endStation = null;
				for (int k = 0; k < stationJson.length(); k++) {
					JSONObject stationCheck = stationJson.getJSONObject(k);
					String nameCheck = stationCheck.getString("name");
					if (nameCheck.equals(startStationName)) {
						int stopTime = stationCheck.getInt("stopTime");
						float positionX = stationCheck.getFloat("positionX");
						float positionY = stationCheck.getFloat("positionY");
						startStation = new Station(stopTime, positionX, positionY, startStationName);
					}
					if (nameCheck.equals(endStationName)) {
						int stopTime = stationCheck.getInt("stopTime");
						float positionX = stationCheck.getFloat("positionX");
						float positionY = stationCheck.getFloat("positionY");
						endStation = new Station(stopTime, positionX, positionY, endStationName);
					}

				}
				double dx = endStation.getPositionX() - startStation.getPositionX();
				double dy = endStation.getPositionY() - startStation.getPositionY();
				Double distance = Math.sqrt(dx * dx + dy * dy);
				LineFragmentation lineF = new LineFragmentation(travelTime, startStation, endStation, distance);
				this.noeuds.put(startStation.getName(), startStation);
				this.noeuds.put(endStation.getName(), endStation);

				fragements.add(lineF);
				this.arcs.put(startStation.getName() + endStation.getName(), lineF);

			}

			Line line = new Line(fragements, num);
			this.lines.put(String.valueOf(num), line);
		}

	}

	public HashMap<String, LineFragmentation> getArcs() {
		return arcs;
	}

	public void setArcs(HashMap<String, LineFragmentation> arcs) {
		this.arcs = arcs;
	}

	public HashMap<String, Station> getNoeuds() {
		return noeuds;
	}

	public void setNoeuds(HashMap<String, Station> noeuds) {
		this.noeuds = noeuds;
	}

	public HashMap<String, Line> getLines() {
		return lines;
	}

	public void setLines(HashMap<String, Line> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		return "Plan [lines=" + lines + "]";
	}

	/**
	 * Méthode permettant de récupérer la station la plus proche qui ne contient pas
	 * d'incident
	 * 
	 * @param posXInitial position X de l'utilisateur
	 * @param posYInitial position Y de l'utilisateur
	 * @return la station la plus proche qui ne contient pas d'incident
	 */
	public Station getNearestStation(float posXInitial, float posYInitial) {
		if (posXInitial < Float.MIN_VALUE || posXInitial > Float.MAX_VALUE || posYInitial < Float.MIN_VALUE
				|| posYInitial > Float.MAX_VALUE) {
			return null;
		}
		Station stationNearest = null;
		float distance = Float.MAX_VALUE;
		float distanceTmp;
		HashMap<String, ArrayList<Station>> reachableStation = reachableStations();
		for (Map.Entry<String, Station> entryStation : this.noeuds.entrySet()) {
			System.out.println(entryStation.getKey());
			if (!entryStation.getValue().hasIncident()) {
				ArrayList<Station> station = reachableStation.get(entryStation.getValue().getName());
				System.out.println(station);
				for (int i = 0; i < station.size(); i++) {
					if (!this.arcs.get(entryStation.getValue().getName() + station.get(i).getName())
							.hasIncident()
							|| !this.arcs.get(station.get(i).getName() + entryStation.getValue().getName())
									.hasIncident()) {
						distanceTmp = calculDistanceBetweenStation(posXInitial, posYInitial, entryStation.getValue());
						if (distanceTmp < distance) {
							distance = distanceTmp;
							stationNearest = entryStation.getValue();
						}
					}
				}
			}
		}
		return stationNearest;
	}

	/**
	 * Méthode permettant de calculer la distance entre les coordonées de
	 * l'utilisateur et une station
	 * 
	 * @param posX    position X de l'utilisateur
	 * @param posY    position Y de l'utilisateur
	 * @param station station dont on veut connaitre ld distance
	 * @return la distance
	 */
	public float calculDistanceBetweenStation(float posX, float posY, Station station) {
		return (float) Math
				.sqrt(Math.pow(station.getPositionX() - posX, 2)
						+ Math.pow(station.getPositionY() - posY, 2));
	}

	/**
	 * Méthode qui calcule les distances à vole d'oiseau entre toutes les stations
	 * et la station d'arriver
	 * 
	 * @param nameStation : nom de la station d'arrivé
	 * @return : une HashMap contenant en cle les noms des station du réseau et en
	 *         valeurs la distance à vole d'oiseau avec la station d'arrivé.
	 */
	public HashMap<String, Double> distanceAFlightBird(String nameStation) {
		HashMap<String, Double> map = new HashMap<>();
		Station end = null;
		for (String noeud : this.noeuds.keySet()) {
			if (noeud.equals(nameStation)) {
				end = noeuds.get(noeud);
			}
		}
		if (end == null) {
			return null;
		}
		for (String noeud : this.noeuds.keySet()) {
			double dx = end.getPositionX() - this.noeuds.get(noeud).getPositionX();
			double dy = end.getPositionY() - this.noeuds.get(noeud).getPositionY();
			map.put(this.noeuds.get(noeud).getName(), Math.sqrt(dx * dx + dy * dy));
		}
		return map;

	}

	/**
	 * Méthode qui détermine l'ensemble des stations pouvant être atteinte
	 * directement depuis chaque stations en parcourant un seul fragment de ligne
	 * 
	 * @return HashMap contenant en cle les noms des stations et en valeurs une
	 *         liste renseignant les stations ateignables.
	 */
	public HashMap<String, ArrayList<Station>> reachableStations() {
		HashMap<String, ArrayList<Station>> map = new HashMap<>();

		for (String keyNoeud : this.noeuds.keySet()) {
			ArrayList<Station> reachStations = new ArrayList<>();
			for (String keyLineF : this.arcs.keySet()) {
				if (this.arcs.get(keyLineF).getIncident() == null) {

					if (this.arcs.get(keyLineF).getStartStation().getName()
							.equals(this.noeuds.get(keyNoeud).getName())) {
						if (this.arcs.get(keyLineF).getEndStation().getIncident() == null) {
							reachStations.add(this.arcs.get(keyLineF).getEndStation());
						}

					}
					if (this.arcs.get(keyLineF).getEndStation().getName().equals(this.noeuds.get(keyNoeud).getName())) {
						if (this.arcs.get(keyLineF).getStartStation().getIncident() == null) {
							reachStations.add(this.arcs.get(keyLineF).getStartStation());
						}
					}
				}
			}
			HashSet<Station> supDoublons = new HashSet<>(reachStations);
			reachStations = new ArrayList<>(supDoublons);
			map.put(noeuds.get(keyNoeud).getName(), reachStations);
		}

		return map;
	}

	/**
	 * Méthode qui permet de trier par ordre décroissant une HashMap et de supprimer
	 * ses doublons.
	 * 
	 * @param map : pile utilisé dans l'algorithme a*
	 * @return : retourne une HashMap correspondant à la pile pris en entré trié
	 *         avec les doublons supprimé.
	 */
	public HashMap<String, Double> pileSort(HashMap<String, Double> map) {
		List<Map.Entry<String, Double>> triage = new ArrayList<>(map.entrySet());
		triage.sort(Collections.reverseOrder(Map.Entry.<String, Double>comparingByValue()));

		HashMap<String, Double> mapSort = new LinkedHashMap<>();
		Set<Double> supDouble = new HashSet<>();

		for (Map.Entry<String, Double> entry : triage) {
			if (!supDouble.contains(entry.getValue())) {
				mapSort.put(entry.getKey(), entry.getValue());
				supDouble.add(entry.getValue());
			}
		}

		return mapSort;
	}

	/**
	 * Méthode qui permet de récupérer la dernière cle d'une HashMap
	 * 
	 * @param map : HashMap correspondant à la pile utilisé dans l'algorithme a*
	 * @return : la dernière cle de la HashMap pris en entrée.
	 */
	public String lastKeyInTheMap(HashMap<String, Double> map) {
		String lastKey = "";
		for (String cle : map.keySet()) {
			lastKey = cle;
		}
		return lastKey;

	}

	/**
	 * Méthode permet de déterminer tous les chemins possibles entre deux stations.
	 * 
	 * @param startStation : la station de départ
	 * @param endStation   : la station d'arrivé
	 * @return : retourne une liste de liste de String représentant tous les chemins
	 *         entre les deux stations
	 */
	public ArrayList<ArrayList<String>> pathsBetweenTwoStation(String startStation, String endStation) {
		HashMap<String, ArrayList<Station>> reachableStations = reachableStations();
		ArrayList<ArrayList<String>> paths = new ArrayList<>();
		ArrayList<String> currentPath = new ArrayList<>();
		currentPath.add(startStation);

		recursiveCheck(reachableStations, startStation, endStation, paths, currentPath);

		return paths;
	}

	/**
	 * Méthode quoi effectue une vérification récursive pour trouver tous les
	 * chemins possibles entre deux stations.
	 * 
	 * @param reachableStations : HashMap contenant les stations atteignables depuis
	 *                          chaque station
	 * @param currentStation    : station actuelle
	 * @param endStation        : station d'arrivée
	 * @param paths             : liste des chemins déjà trouvés
	 * @param currentPath       : chemin actuel en cours de construction
	 */
	private void recursiveCheck(HashMap<String, ArrayList<Station>> reachableStations, String currentStation,
			String endStation, ArrayList<ArrayList<String>> paths, ArrayList<String> currentPath) {
		if (currentStation.equals(endStation)) {
			paths.add(new ArrayList<>(currentPath));
		} else {
			ArrayList<Station> nextStations = reachableStations.get(currentStation);
			for (Station nextStation : nextStations) {
				if (!currentPath.contains(nextStation.getName())) {
					currentPath.add(nextStation.getName());
					recursiveCheck(reachableStations, nextStation.getName(), endStation, paths, currentPath);
					currentPath.remove(currentPath.size() - 1);
				}
			}
		}
	}

	/**
	 * Calcule la distance entre la station de départ et une autre station.
	 * 
	 * @param startStation   : station de départ
	 * @param currentStation : autre station
	 * @return : retourne le temps de parcours le plus cours entre la station de
	 *         départ et la
	 *         station actuelle
	 */
	public Double timeWhithStartStaion(String startStation, String currentStation) {
		ArrayList<ArrayList<String>> paths = pathsBetweenTwoStation(startStation, currentStation);
		ArrayList<Integer> distancePaths = new ArrayList<>();

		for (ArrayList<String> path : paths) {
			ArrayList<LineFragmentation> lines = new ArrayList<>();
			// On place dans lines les fragments de lignes qui composent le chemin entre les
			// deux stations
			for (int i = 0; i < path.size() - 1; i++) {
				for (String keyLineF : this.arcs.keySet()) {
					if (path.get(i).equals(this.arcs.get(keyLineF).getStartStation().getName())
							&& path.get(i + 1).equals(this.arcs.get(keyLineF).getEndStation().getName())) {
						lines.add(this.arcs.get(keyLineF));
					}
				}
			}

			int tempFragementsLigne = 0;
			int tempArret = 0;
			if (lines.size() >= 1) {
				tempArret = lines.get(0).getStartStation().getStopTime();
			}

			for (LineFragmentation lineFragmentation : lines) {
				tempFragementsLigne = tempFragementsLigne + lineFragmentation.getTime();
				tempArret = tempArret + lineFragmentation.getEndStation().getStopTime();
			}
			distancePaths.add(tempFragementsLigne + tempArret);

		}

		// on determine la plus courte distance :
		double min = Double.MAX_VALUE;
		for (double distance : distancePaths) {
			if (distance < min) {
				min = distance;
			}
		}
		return min;

	}

	/**
	 * Méthode permettant de determiner le chemin le plus cours entre deux station
	 * en utilisant l'algorithme a*
	 * 
	 * @param startStationName : nom de la station de départ
	 * @param endStationName   : nom de la station d'arrivée
	 * @return : retourne une liste de string contenant l'un après l'autres tous les
	 *         noms des station qui compose le chemin le plus court
	 */
	public ArrayList<String> starA(String startStationName, String endStationName) {

		if (this.noeuds.containsKey(endStationName) && this.noeuds.containsKey(startStationName)) {
			ArrayList<String> betterPath = new ArrayList<>();
			HashMap<String, Double> pile = new HashMap<>();
			ArrayList<String> traversedNode = new ArrayList<>();
			HashMap<String, String> father = new HashMap<>();
			majLignesFragmentation();
			HashMap<String, ArrayList<Station>> reachableStations = reachableStations();
			HashMap<String, Double> distanceAVoleDOiseau = distanceAFlightBird(endStationName);

			HashMap<String, Station> node = new HashMap<>();
			for (String n : this.noeuds.keySet()) {
				node.put(this.noeuds.get(n).getName(), this.noeuds.get(n));
			}

			pile.put(startStationName, 0.0);
			father.put(startStationName, "");

			boolean stop = false;
			while (stop == false) {
				// On prend l'élément de la pile qui est la plus près de l'arriver, on le retire
				// de la pile et on le parcours
				pile = pileSort(pile);
				String key = lastKeyInTheMap(pile);
				pile.remove(key);
				traversedNode.add(key);

				// Ajout dans la pile toutes les station pouvant être atteinte depuis la station
				// parcouru
				String currentNode = traversedNode.get(traversedNode.size() - 1);
				// System.out.println(currentNode);
				for (Station s : reachableStations.get(currentNode)) {
					if (!traversedNode.contains(s.getName())) {
						double heuristic = distanceAVoleDOiseau.get(s.getName())
								+ timeWhithStartStaion(startStationName, s.getName());
						father.put(s.getName(), currentNode);
						pile.put(s.getName(), heuristic);
					}

				}

				if (pile.containsKey(endStationName)) {

					stop = true;
					// reconstitution du chemin
					betterPath.add(endStationName);
					boolean fatherFound = false;
					while (fatherFound == false) {
						String fatherStation = father.get(betterPath.get(betterPath.size() - 1));
						betterPath.add(fatherStation);
						if (fatherStation.equals(startStationName)) {
							fatherFound = true;
						}
					}

				}

			}
			Collections.reverse(betterPath);
			return betterPath;

		} else
			return null;

	}

	/**
	 * Méthode permettant de tester si chaques élément d'une liste est le même ou
	 * non
	 * 
	 * @param list : liste à tester
	 * @return : true si tout les elements de la liste sont identique, false sinon
	 */
	public boolean sameElementAtEachPosition(ArrayList<Integer> list) {
		if (list == null || list.isEmpty()) {
			return false;
		}

		Integer firstElement = list.get(0);

		for (int i = 1; i < list.size(); i++) {
			if (!list.get(i).equals(firstElement)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Méhtode permettant de mettre en forme un chemin pour connaitre de quelle
	 * station il faut prendre et sur quelle lignes
	 * 
	 * @param path : chemin à mettre en forme
	 */
	public void shapingPaths(ArrayList<String> path) {
		ArrayList<Integer> numLines = new ArrayList<>();
		ArrayList<ArrayList<String>> transformPath = new ArrayList<>();

		for (int i = 0; i < path.size() - 1; i++) {
			for (String line : this.lines.keySet()) {
				for (LineFragmentation lineFrag : this.lines.get(line).getFragements()) {
					if (path.get(i).equals(lineFrag.getStartStation().getName())
							&& path.get(i + 1).equals(lineFrag.getEndStation().getName())) {
						ArrayList<String> stationsGoodOrder = new ArrayList<>();
						stationsGoodOrder.add(path.get(i));
						stationsGoodOrder.add(path.get(i + 1));

						transformPath.add(stationsGoodOrder);
						numLines.add(this.lines.get(line).getNumLine());
					}
					if (path.get(i + 1).equals(lineFrag.getStartStation().getName())
							&& path.get(i).equals(lineFrag.getEndStation().getName())) {
						ArrayList<String> stationsNewOrder = new ArrayList<>();
						stationsNewOrder.add(path.get(i));
						stationsNewOrder.add(path.get(i + 1));

						transformPath.add(stationsNewOrder);
						numLines.add(this.lines.get(line).getNumLine());
					}
				}
			}
		}

		ArrayList<Integer> finalNumLine = new ArrayList<>();
		ArrayList<ArrayList<String>> finalTransformPath = new ArrayList<>();
		System.out.println();
		for (int i = 0; i < numLines.size(); i++) {
			ArrayList<String> startStop = new ArrayList<>();

			String start = transformPath.get(i).get(0);
			String end = transformPath.get(i).get(1);

			int num = numLines.get(i);
			if (sameElementAtEachPosition(numLines)) {
				end = transformPath.get(numLines.size() - 1).get(1);
				i = numLines.size();
			} else {
				if (i + 1 != numLines.size()) {
					while (numLines.get(i) == numLines.get(i + 1)) {
						end = transformPath.get(i + 1).get(1);
						i++;
						if (i + 1 == numLines.size()) {
							break;
						}
					}
				}
			}

			startStop.add(start);
			startStop.add(end);
			finalTransformPath.add(startStop);
			finalNumLine.add(num);

		}

		for (int i = 0; i < finalNumLine.size(); i++) {
			System.out.println(finalNumLine.get(i) + " : " + finalTransformPath.get(i).get(0) + " "
					+ finalTransformPath.get(i).get(1));
		}

	}

	public void majLignesFragmentation() {
		for (String keyLine : this.arcs.keySet()) {
			this.arcs.get(keyLine).setStartStation(this.noeuds.get(this.arcs.get(keyLine).getStartStation().getName()));
			this.arcs.get(keyLine).setEndStation(this.noeuds.get(this.arcs.get(keyLine).getEndStation().getName()));

		}
	}

	public ArrayList<LineFragmentation> getLineFragmentationWithIncidentsOnLine(Line line) {
		try {
			return line.getLineFragmentationWithIncident();
		} catch (Exception e) {
			return null;
		}
	}

	public ArrayList<Station> getStationWithIncidentOnLine(Line line) {
		try {
			return line.getStationWithIncident();
		} catch (Exception e) {
			return null;
		}
	}

}
