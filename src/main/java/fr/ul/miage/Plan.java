package fr.ul.miage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Plan {
	private ArrayList<Line> lines = new ArrayList<>();

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
				LineFragmentation lineF = new LineFragmentation(travelTime, startStation, endStation);
				// System.out.println(lineF.toString());
				fragements.add(lineF);

			}

			Line line = new Line(fragements, num);
			// System.out.println(line.toString());
			this.lines.add(line);
		}

	}

	public Station getNearestStation(float posXInitial, float posYInitial) {
		Station stationNearest = null;
		float distance = Float.MAX_VALUE;
		float distanceTmp;
		ArrayList<String> stationsKnown = new ArrayList<String>();
		// pour chaque ligne présent dans le plan
		for (Line line : this.lines) {
			// pour chaque fragment de ligne présent sur la ligne line
			for (LineFragmentation lf : line.getFragements()) {
				if (!stationsKnown.contains(lf.getStartStation().getName())) {
					Station startStation = lf.getStartStation();
					distanceTmp = (float) Math.sqrt(Math.pow(startStation.getPositionX() - posXInitial, 2)
							+ Math.pow(startStation.getPositionY() - posYInitial, 2));
					if (distanceTmp < distance) {
						distance = distanceTmp;
						stationNearest = startStation;
					}
					stationsKnown.add(startStation.getName());
				}
				if (!stationsKnown.contains(lf.getEndStation().getName())) {
					Station endStation = lf.getEndStation();
					distanceTmp = (float) Math.sqrt(Math.pow(endStation.getPositionX() - posXInitial, 2)
							+ Math.pow(endStation.getPositionY() - posYInitial, 2));
					if (distanceTmp < distance) {
						distance = distanceTmp;
						stationNearest = endStation;
					}
					stationsKnown.add(endStation.getName());
				}
			}
		}
		return stationNearest;
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

}
