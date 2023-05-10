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
	private ArrayList<Line> lines = new ArrayList<>();
	private ArrayList<LineFragmentation> arcs = new ArrayList<>();
	private ArrayList<Station> noeuds = new ArrayList<>();
	
	public Plan() {
		 String filePathLine = "src/main/resources/line.json";
		 String filePathStation = "src/main/resources/station.json";
	     		 
	     String contentLine = null;
	     String contentStation = null;
	     try {
	        contentLine = new String(Files.readAllBytes(new File(filePathLine).toPath()), StandardCharsets.UTF_8);
	        contentStation = new String(Files.readAllBytes(new File(filePathStation).toPath()), StandardCharsets.UTF_8);
	        
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	       //System.out.println(content);
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
					String nameCheck= stationCheck.getString("name");
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
				double dx = endStation.getPositionX()-startStation.getPositionX();
				double dy = endStation.getPositionY()-startStation.getPositionY();
				Double distance =  Math.sqrt(dx*dx + dy*dy);
				LineFragmentation lineF = new LineFragmentation(travelTime,startStation,endStation, distance);
				this.noeuds.add(startStation);
				this.noeuds.add(endStation);
				
				fragements.add(lineF);
				this.arcs.add(lineF);

			}
	    	
	    	Line line = new Line(fragements, num);
	    	this.lines.add(line);
		  }
	      HashSet<Station> supDoublons = new HashSet<>(this.noeuds);  
	      this.noeuds = new ArrayList<>(supDoublons);
	      this.noeuds.remove(0);
	}

	public ArrayList<LineFragmentation> getArcs() {
		return arcs;
	}

	public void setArcs(ArrayList<LineFragmentation> arcs) {
		this.arcs = arcs;
	}

	public ArrayList<Station> getNoeuds() {
		return noeuds;
	}

	public void setNoeuds(ArrayList<Station> noeuds) {
		this.noeuds = noeuds;
	}

	public ArrayList<Line> getLines() {
		return lines;
	}

	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		return "Plan [lines=" + lines + "]";
	}
	/**
	 * Méthode qui calcule les distances à vole d'oiseau entre toutes les stations et la station d'arriver
	 * @param nameStation : nom de la station d'arrivé
	 * @return : une HashMap contenant en cle les noms des station du réseau et en valeurs la distance à vole d'oiseau avec la station d'arrivé.
	 */
	public HashMap<String, Double> distanceAFlightBird(String nameStation){
		HashMap<String, Double> map = new HashMap<>();
		Station end = null;
		for (int i = 0; i <  this.noeuds.size(); i++) {
			if (this.noeuds.get(i).getName().equals(nameStation)) {
				end = this.noeuds.get(i);
			}
		}
		for (int i = 0; i < this.noeuds.size(); i++) {
			double dx = end.getPositionX() - this.noeuds.get(i).getPositionX();
			double dy = end.getPositionY() - this.noeuds.get(i).getPositionY();
			map.put(this.noeuds.get(i).getName(), Math.sqrt(dx * dx + dy * dy));
		}
		return map;

	}
	/**
	 * Méthode qui détermine l'ensemble des stations pouvant être atteinte directement depuis chaque stations en parcourant un seul fragment de ligne
	 * @return HashMap contenant en cle les noms des stations et en valeurs une liste renseignant les stations ateignables.
	 */
	public HashMap<String, ArrayList<Station>> reachableStations(){
		HashMap<String, ArrayList<Station>> map = new HashMap<>();
		
		for (Station noeud : this.noeuds) {
			ArrayList<Station> reachStations = new ArrayList<>();
		    for(LineFragmentation arc : this.arcs) {		 
		    	if (arc.getStartStation().getName().equals(noeud.getName())) {
		    		reachStations.add(arc.getEndStation());		    		
				}
		    	if (arc.getEndStation().getName().equals(noeud.getName())) {
		    		reachStations.add(arc.getStartStation());
				}
		    }
		    HashSet<Station> supDoublons = new HashSet<>(reachStations);
		    reachStations = new ArrayList<>(supDoublons);
		    
		    map.put(noeud.getName(), reachStations);
		}
		
		
		return map;
	}
	/**
	 * Méthode qui permet de trier par ordre décroissant une HashMap et de supprimer ses doublons. 
	 * @param map : pile utilisé dans l'algorithme a*
	 * @return : retourne une HashMap correspondant à la pile pris en entré trié avec les doublons supprimé.
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
	 * @param startStation : la station de départ
	 * @param endStation : la station d'arrivé
	 * @return : retourne une liste de liste de String représentant tous les chemins entre les deux stations
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
	 * Méthode quoi effectue une vérification récursive pour trouver tous les chemins possibles entre deux stations.
	 * @param reachableStations : HashMap contenant les stations atteignables depuis chaque station
	 * @param currentStation : station actuelle
	 * @param endStation : station d'arrivée
	 * @param paths : liste des chemins déjà trouvés 
	 * @param currentPath : chemin actuel en cours de construction
	 */
	private void recursiveCheck(HashMap<String, ArrayList<Station>> reachableStations, String currentStation, String endStation, ArrayList<ArrayList<String>> paths, ArrayList<String> currentPath) {
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
	 * @param startStation : station de départ
	 * @param currentStation : autre station 
	 * @return : retourne la plus courte distance entre la station de départ et la station actuelle
	 */
	public Double distanceWhithStartStaion(String startStation, String currentStation){		
		ArrayList<ArrayList<String>> paths = pathsBetweenTwoStation(startStation, currentStation);
		ArrayList<Double> distancePaths = new ArrayList<>();
		
		
		for(ArrayList<String> path : paths) {
			ArrayList<LineFragmentation> lines = new ArrayList<>();
			//On place dans lines les fragments de lignes qui composent le chemin entre les deux stations
			for (int i = 0; i < path.size()-1; i++) {
				for(LineFragmentation graphLine : this.arcs) {			
					if (path.get(i).equals(graphLine.getStartStation().getName()) && path.get(i+1).equals(graphLine.getEndStation().getName()) ) {
						lines.add(graphLine);
					}
				}
			}
			double distance = 0.0;
			for(LineFragmentation lineFragmentation: lines) {
				distance = distance+lineFragmentation.getDistance();
			}
			distancePaths.add(distance);
			
			
		}
		
		//on determine la plus courte distance : 
		double min = Double.MAX_VALUE; 
	    for (double distance : distancePaths) {
	        if (distance < min) {
	        	min = distance;
	        }
	    }
	    return min;
		
	}
	
	/**
	 * Méthode permettant de determiner le chemin le plus cours entre deux station en utilisant l'algorithme a*
	 * @param startStationName : nom de la station de départ
	 * @param endStationName : nom de la station d'arrivée
	 * @return : retourne une liste de string contenant l'un après l'autres tous les noms des station qui compose le chemin le plus court
	 */
	public ArrayList<String> starA(String startStationName, String endStationName){
		ArrayList<String> betterPath = new ArrayList<>();
		HashMap<String, Double> pile = new HashMap<>();
		ArrayList<String> traversedNode = new ArrayList<>();
		HashMap<String, String> father = new HashMap<>();
		
		HashMap<String, ArrayList<Station>> reachableStations = reachableStations();
		HashMap<String, Double> distanceAVoleDOiseau = distanceAFlightBird(endStationName);
		
		
		HashMap<String, Station> node = new HashMap<>();
		for (Station n : this.noeuds) {
			node.put(n.getName(), n);
		}
		
		pile.put(startStationName, 0.0);
		father.put(startStationName, "");
		
		boolean stop = false;
		while(stop==false) {
			//On prend l'élément de la pile qui est la plus près de l'arriver, on le retire de la pile et on le parcours
			pile = pileSort(pile);
			String key = lastKeyInTheMap(pile);
			pile.remove(key);
			traversedNode.add(key);
			
			//Ajout dans la pile toutes les station pouvant être atteinte depuis la station parcouru
			String currentNode = traversedNode.get(traversedNode.size()-1);
			//System.out.println(currentNode);
			for(Station s : reachableStations.get(currentNode)) {
				if (!traversedNode.contains(s.getName())) {
					double heuristic = distanceAVoleDOiseau.get(s.getName()) + distanceWhithStartStaion(startStationName, s.getName());
					father.put(s.getName(), currentNode);
					pile.put(s.getName(), heuristic);
				}
				
			}
			
			if (pile.containsKey(endStationName)) {
				
				stop = true;
				//reconstitution du chemin
				betterPath.add(endStationName);
				boolean fatherFound = false;
				while(fatherFound==false) {
					String fatherStation = father.get(betterPath.get(betterPath.size()-1));
					betterPath.add(fatherStation);
					if (fatherStation.equals(startStationName)) {
						fatherFound = true;
					}
				}
				
			}
			
								
		}
		Collections.reverse(betterPath);
		return betterPath;
		

	}	
	
	/**
	 * Méthode permettant de tester si chaques élément d'une liste est le même ou non
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
	 * Méhtode permettant de mettre en forme un chemin pour connaitre de quelle station il faut prendre et sur quelle lignes 
	 * @param path : chemin à mettre en forme
	 */
	public void shapingPaths(ArrayList<String> path) {
		ArrayList<Integer> numLines = new ArrayList<>();
		ArrayList<ArrayList<String>> transformPath = new ArrayList<>();
		
		for (int i = 0; i < path.size()-1; i++) {
			for(Line line : this.lines) {
				for(LineFragmentation lineFrag : line.getFragements()) {
					if (path.get(i).equals(lineFrag.getStartStation().getName()) && path.get(i+1).equals(lineFrag.getEndStation().getName())) {
						ArrayList<String> stationsGoodOrder = new ArrayList<>();
						stationsGoodOrder.add(path.get(i));
						stationsGoodOrder.add(path.get(i+1));
						
						
						transformPath.add(stationsGoodOrder);
						numLines.add(line.getNumLine());
					}
					if (path.get(i+1).equals(lineFrag.getStartStation().getName()) && path.get(i).equals(lineFrag.getEndStation().getName())) {
						ArrayList<String> stationsNewOrder = new ArrayList<>();
						stationsNewOrder.add(path.get(i));
						stationsNewOrder.add(path.get(i+1));						

						
						transformPath.add(stationsNewOrder);
						numLines.add(line.getNumLine());
					}
				}
			}
		}
		
		ArrayList<Integer>finalNumLine = new ArrayList<>();
		ArrayList<ArrayList<String>> finalTransformPath = new ArrayList<>();
		System.out.println();
		for (int i = 0; i < numLines.size(); i++) {
			ArrayList<String> startStop = new ArrayList<>();
			
			String start = transformPath.get(i).get(0);
			String end = transformPath.get(i).get(1);
			
			int num = numLines.get(i);
		//traiter le cas ou il y a qu'une seul ligne
			if(sameElementAtEachPosition(numLines)) {
				end = transformPath.get(numLines.size()-1).get(1);
				i = numLines.size();
			}else {
				if (i+1 != numLines.size()) {
					while(numLines.get(i) == numLines.get(i+1)) {
					    end = transformPath.get(i+1).get(1);
						i++;
					}				
				}	
			}
					
			startStop.add(start);
			startStop.add(end);
			finalTransformPath.add(startStop);
			finalNumLine.add(num);
			
			
		}
		
		for (int i = 0; i < finalNumLine.size(); i++) {
			System.out.println(finalNumLine.get(i)+" : "+finalTransformPath.get(i).get(0)+" "+finalTransformPath.get(i).get(1) );
		}
		
		
	}
	
	


	

	
	
}
