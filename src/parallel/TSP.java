package parallel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class TSP {
	public static void main(String[] args) {

		BufferedReader reader = readFile();
		ArrayList<City> cities = constructCityList(reader);

		// TSP algoritmasi giris
		try {

			Timer timer = new Timer();
			Solver solver = new Solver(cities);
			timer.start();
			int[] path = solver.calculate();
			timer.stop();

			printMessage(cities, timer, solver, path);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printMessage(ArrayList<City> cities, Timer timer, Solver solver, int[] path) {
		String message = cities.get(path[0]).getName();
		for (int i = 1; i < path.length; i++) {
			message += " to " + cities.get(path[i]).getName();
		}
		message += " to " + cities.get(path[0]).getName();
		message += "\nCost: " + solver.getCost();
		message += "\nTime: " + timer.getFormattedTime();
		alert(message);
	}

	private static ArrayList<City> constructCityList(BufferedReader reader) {
		int dimension = 0;
		try {
			String line;
			while (!(line = reader.readLine()).equals("NODE_COORD_SECTION")) {
				String[] entry = line.split(": ", 1);
				switch (entry[0].trim()) {
				case "TYPE":
					if (!entry[1].trim().equals("TSP"))
						throw new Exception("File not in TSP format");
					break;
				case "DIMENSION":
					dimension = Integer.parseInt(entry[1]);
					break;
				}
			}
		}
		catch (Exception e) {
			alert("Error parsing header " + e);
			System.exit(1);
		}

		ArrayList<City> cities = new ArrayList<City>(dimension);

		try {
			String line;
			while ((line = reader.readLine()) != null && !line.equals("EOF")) {
				String[] entry = line.split(" ");
				cities.add(new City(entry[0], Double.parseDouble(entry[1]), Double.parseDouble(entry[2])));
			}

			reader.close();
		}
		catch (Exception e) {
			alert("Error parsing data " + e);
			System.exit(1);
		}
		return cities;
	}

	private static BufferedReader readFile() {
		File file;
		BufferedReader reader = null;
		JFileChooser chooser = new JFileChooser();

		int response = chooser.showOpenDialog(null);

		if (response != JFileChooser.APPROVE_OPTION)
			System.exit(1);

		file = chooser.getSelectedFile();

		try {
			reader = new BufferedReader(new FileReader(file));
		}
		catch (IOException e) {
			alert("Error loading file " + e);
			System.exit(1);
		}

		return reader;
	}

	private static void alert(String message) {
		System.out.println(message);
	}
}
