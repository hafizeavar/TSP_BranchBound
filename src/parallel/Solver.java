package parallel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Solves the traveling salesman problem using Branch and Bound by utilizing Node's
 */
public class Solver {
	double[][] distances;
	double best_cost;
	int[] best_path;

	Lock lock;

	/**
	 * Constructs a new Solver and initializes distances array
	 *
	 * @param cities An ArrayList of City's
	 */
	public Solver(ArrayList<City> cities) {
		distances = new double[cities.size()][cities.size()];
		for (int i = 0; i < cities.size(); i++) {
			for (int ii = 0; ii < cities.size(); ii++)
				distances[i][ii] = cities.get(i).distance(cities.get(ii));
		}

		this.lock = new ReentrantLock();
	}

	/**
	 * Calculates the shortest (non-repeating) path between a series of nodes
	 *
	 * @return An array with the locations of the best path
	 * @throws Exception
	 */
	public int[] calculate() throws Exception {
		HashSet<Integer> location_set = new HashSet<Integer>(distances.length);
		for (int i = 0; i < distances.length; i++)
			location_set.add(i);

		best_cost = findGreedyCost(0, location_set, distances);

		int[] active_set = new int[distances.length];
		for (int i = 0; i < active_set.length; i++)
			active_set[i] = i;

		Node root = new Node(null, 0, distances, active_set, 0);

		int cores = Runtime.getRuntime().availableProcessors();

		TSPDispatcher tspDispatcher = new TSPDispatcher(root, cores, best_cost);
		best_path = tspDispatcher.calculate();

		return best_path;
	}

	/**
	 * Get current path cost
	 *
	 * @return The cost
	 */
	public double getCost() {
		return best_cost;
	}

	/**
	 * Find the greedy cost for a set of locations
	 *
	 * @param i The current location
	 * @param location_set Set of all remaining locations
	 * @param distances The 2D array containing point distances
	 * @return The greedy cost
	 */
	private double findGreedyCost(int i, HashSet<Integer> location_set, double[][] distances) {
		if (location_set.isEmpty())
			return distances[0][i];

		location_set.remove(i);

		double lowest = Double.MAX_VALUE;
		int closest = 0;
		for (int location : location_set) {
			double cost = distances[i][location];
			if (cost < lowest) {
				lowest = cost;
				closest = location;
			}
		}

		return lowest + findGreedyCost(closest, location_set, distances);
	}

}
