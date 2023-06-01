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
	public Station getNearestStation(float posXInitial, float posYInitial, ArrayList<Station> stationToDodge) {
		if (posXInitial < Float.MIN_VALUE || posXInitial > Float.MAX_VALUE || posYInitial < Float.MIN_VALUE
				|| posYInitial > Float.MAX_VALUE) {
			return null;
		}
		Station stationNearest = null;
		float distance = Float.MAX_VALUE;
		float distanceTmp;
		HashMap<String, ArrayList<Station>> reachableStation = reachableStations();
		for (Map.Entry<String, Station> entryStation : this.noeuds.entrySet()) {
			if (!stationToDodge.contains(entryStation.getValue())) {
				if (!entryStation.getValue().hasIncident()) {
					ArrayList<Station> station = reachableStation.get(entryStation.getValue().getName());
					for (int i = 0; i < station.size(); i++) {
						LineFragmentation lf = findLineFragmentation(entryStation.getValue().getName(),
								station.get(i).getName());
						if (!lf.hasIncident()) {
							distanceTmp = calculDistanceBetweenStation(posXInitial, posYInitial,
									entryStation.getValue());
							if (distanceTmp < distance) {
								distance = distanceTmp;
								stationNearest = entryStation.getValue();
							}
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
					// Vérifie si l'arc ne possède pas de station avec incident 

					if (this.arcs.get(keyLineF).getStartStation().getName() 
					// Vérifie si la station de départ du fragement correspond à la station actuel
							.equals(this.noeuds.get(keyNoeud).getName())) {
						if (this.arcs.get(keyLineF).getEndStation().getIncident() == null) { 
							// Vérifie si la station d'arrivée de l'arc ne possède d'incident
							reachStations.add(this.arcs.get(keyLineF).getEndStation());
						}

					}
					// Vérifie si la station d'arrivée du fragement correspond à la station actuel
					if (this.arcs.get(keyLineF).getEndStation().getName().equals(this.noeuds.get(keyNoeud).getName())) {
						// Vérifie si la station de départ de l'arc ne possède pas d'incidente 
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
			// On place dans lines les fragments de lignes qui composent le chemin entre les deux stations		
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
			HashMap<String, ArrayList<Station>> reachableStations = reachableStations();
			majLignesFragmentation();

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
				if (reachableStations.containsKey(currentNode)) {
					for (Station s : reachableStations.get(currentNode)) {
						if (!traversedNode.contains(s.getName())) {
							double heuristic = distanceAVoleDOiseau.get(s.getName())
									+ timeWhithStartStaion(startStationName, s.getName());
							father.put(s.getName(), currentNode);
							pile.put(s.getName(), heuristic);
						}

					}
				} else
					return null;

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
			if (betterPath.size() == 0) {
				return null;
			} else
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
	 * @return : le chemin mis en forme sous la forme d'un string
	 */
	public String shapingPaths(ArrayList<String> path) {
		ArrayList<Integer> numLines = new ArrayList<>();
		ArrayList<ArrayList<String>> transformPath = new ArrayList<>();

		for (int i = 0; i < path.size() - 1; i++) {
			//parcours les chemins
			for (String line : this.lines.keySet()) {
				//parcours chaques lignes
				for (LineFragmentation lineFrag : this.lines.get(line).getFragements()) {
					//parcours chaques fragement de lignes
					//test la correspondance avec le fragement de ligne
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
		for (int i = 0; i < numLines.size(); i++) {
			// mise en forme du résultat avec les bon fragements de lignes
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
		String result = "";
		for (int i = 0; i < finalNumLine.size(); i++) {
			result = result + "Prendre la ligne " + finalNumLine.get(i) + " de la station "
					+ finalTransformPath.get(i).get(0) + " jusqu'a la station " + finalTransformPath.get(i).get(1)
					+ "\n";
		}
		return result;
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

	/**
	 * Méhtode permettant de chercher si un fragement de ligne existe entre deux
	 * station
	 * 
	 * @param startStation : nom de la station de depart
	 * @param endStation   : nom de la station d'arrivé
	 * @return : le fragement de ligne en question s'il existe, null sinon
	 */
	public LineFragmentation findLineFragmentation(String startStation, String endStation) {

		if (this.arcs.containsKey(startStation + endStation)) {
			return this.arcs.get(startStation + endStation);
		} else {
			if (this.arcs.containsKey(endStation + startStation)) {
				return this.arcs.get(endStation + startStation);
			} else
				return null;
		}
	}

	/**
	 * Méhtode permettant de determiner le numéro de la ligne à laquel appartiens un
	 * fragement de ligne
	 * 
	 * @param fragement : fragement de ligne concerné
	 * @return : le numéro de la ligne qui contient le fragement de ligne
	 */
	public Integer numLine(LineFragmentation fragement) {
		for (String cle : this.lines.keySet()) {
			Line line = this.lines.get(cle);
			for (int i = 0; i < line.getFragements().size(); i++) {
				if (line.getFragements().get(i) == fragement) {
					return line.getNumLine();
				}
			}
		}
		return null;
	}

	/**
	 * Méhtode permettant de supprimer les element con sécutif en double d'une liste
	 * d'integer
	 * Par exemple si la liste contient (1,1,1,2,2,2,3,3,1,1) alors elle retournera
	 * : (1,2,3,1)
	 * 
	 * @param list : liste d'integer à traiter
	 * @return : la liste traité
	 */
	public ArrayList<Integer> findCahngeInThePaths(ArrayList<Integer> list) {
		int decompte = list.size();
		sortie: for (int i = 0; i < decompte - 1; i++) {
			while (list.get(i) == list.get(i + 1)) {

				list.remove(i + 1);

				decompte--;
				if (decompte == 1 || i == decompte - 1) {
					break sortie;
				}

			}
		}

		return list;
	}

	/**
	 * Méhtode permettant de chercher l'itineraire ayant le moins de changement de
	 * ligne entre deux stations.
	 * 
	 * @param startStation : nom de la station de depart
	 * @param endStation   : nom de la station d'arrivé
	 * @return : l'itineraire sous la forme d'une liste de string.
	 */
	public ArrayList<String> itineraryFeweLineChanges(String startStationName, String endStationName) {
		if (this.noeuds.containsKey(endStationName) && this.noeuds.containsKey(startStationName)) {
			ArrayList<ArrayList<String>> paths = pathsBetweenTwoStation(startStationName, endStationName);
			if (paths.size() == 0) {
				return null;
			}
			for (int i = 0; i < paths.size(); i++) {//on regarde tout les chemins entre deux stations
				for (int j = 0; j < paths.get(i).size(); j++) {
					//contrôle si des incidents sont présent
					if (this.noeuds.get(paths.get(i).get(j)).hasIncident()) {
						paths.remove(i);
						break;
					}
					if (j < paths.get(i).size() - 1) {
						if (findLineFragmentation(this.noeuds.get(paths.get(i).get(j)).getName(),
								this.noeuds.get(paths.get(i).get(j + 1)).getName()).hasIncident()) {
							paths.remove(i);
							break;
						}
					}

				}
			}

			ArrayList<Integer> numChanges = new ArrayList<>();
			for (ArrayList<String> path : paths) {
				ArrayList<Integer> numsLine = new ArrayList<>();
				for (int i = 0; i < path.size() - 1; i++) {
					LineFragmentation line = findLineFragmentation(path.get(i), path.get(i + 1));
					numsLine.add(numLine(line));
				}

				numsLine = findCahngeInThePaths(numsLine);
				// ajout dans la liste le nombre de changement de ligne pour ce chemin
				numChanges.add(numsLine.size());

			}

			int position = 0;
			int smallestElement = numChanges.get(0);

			for (int i = 1; i < numChanges.size(); i++) {
				if (numChanges.get(i) < smallestElement) {
					smallestElement = numChanges.get(i);
					position = i;
				}
			}
			// retourne le chemin avec le moins de changement de ligne
			return paths.get(position);

		} else
			return null;
	}

	/**
	 * Méhtode permettant de faire le lien entre la station la plus proche de
	 * l'utilisateur et la le chemin le plus cours entre cette station et la station
	 * d'arrivée
	 * Tiens en compte le fait de ne pas avoir de chemin possible entre deux station
	 * et donc que l'utilisateur doive marcher
	 * 
	 * @param posXInitial    : position en X de l'utilisateur
	 * @param posYInitial    : position en Y de l'utilisateur
	 * @param endStationName : nom de la station d'arrivée
	 * @param userDemande    : type de parcours demandé
	 * 
	 */
	public void findTheFinalPath(float posXInitial, float posYInitial, String endStationName, String userDemande) {
		String message = "";
		ArrayList<Station> stationToDodge = new ArrayList<>();
		boolean end = false;
		Station endStation = this.noeuds.get(endStationName);
		while (end == false) {
			//répétition des instructions tant qu'un intinéraire n'a pas été trouvé
			Station nearestStation = getNearestStation(posXInitial, posYInitial, new ArrayList<Station>());
			ArrayList<String> itinerary = new ArrayList<>();
			switch (userDemande) {
				case "changement":
					itinerary = itineraryFeweLineChanges(nearestStation.getName(), endStation.getName());
					break;
				case "rapide":
					itinerary = starA(nearestStation.getName(), endStation.getName());
					break;

			}
			if (itinerary != null) {
				// Une fois trouvé, affichage de l'itinéraire
				message = "Voici les etapes a suivres : \n" + "Marcher jusqu'a la station " + nearestStation.getName()
						+ "\n" + shapingPaths(itinerary) + message;
				System.out.println(message);
				end = true;
			} else {
				// s'il n'existe pas d'itinéraire on test en recherchant  en partant de la station la plus proche de la station de départ
				ArrayList<Station> oldnearestStation = new ArrayList<Station>();
				oldnearestStation.add(nearestStation);
				Station newNearestStation = getNearestStation(posXInitial, posYInitial, oldnearestStation);
				ArrayList<String> testItinerary = new ArrayList<>();
				switch (userDemande) {
					case "changement":
						testItinerary = itineraryFeweLineChanges(newNearestStation.getName(), endStation.getName());
						break;
					case "rapide":
						testItinerary = starA(newNearestStation.getName(), endStation.getName());
						break;

				}
				if (testItinerary != null) {
					// Une fois trouvé, affichage de l'itinéraire
					message = "Voici les etapes a suivres : \n" + "Marcher jusqu'a la station "
							+ newNearestStation.getName() + "\n" + shapingPaths(testItinerary) + message;
					System.out.println(message);
					end = true;
				} else {
					// si toujours aucun itinéraire n'est trouvé on teste en prenant une autre station d'arrivé (proche de celle de base)
					stationToDodge.add(endStation);
					endStation = getNearestStation(this.noeuds.get(endStation.getName()).getPositionX(),
							this.noeuds.get(endStation.getName()).getPositionY(), stationToDodge);
					message = "Pour finir marcher de la station " + endStation.getName() + " jusqu'a la station "
							+ endStationName;
				}
			}
		}
	}

}
