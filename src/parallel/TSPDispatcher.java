package parallel;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TSPDispatcher {

	WorkPool<Node> workPool;

	/** optimalCost ve optimalPath degerlerini guncellemek icin kullanilan lock */
	private Lock lock;

	/** pool da is kalmadiginda threadleri sonlandırmak icin eklenir **/
	private Node dummy;

	/** En iyi yol ve maliyeti */

	private double optimalCost;

	private int[] optimalPath;

	/** calisan thread sayisi */
	private int totalWorkers;

	/** Tüm threadlerin sonlandigini belirlemek icin */
	private CyclicBarrier barrier;

	public TSPDispatcher(Node root, int n, double initialCost) throws Exception {
		dummy = new Node(null, 0, null, null, 0);
		this.lock = new ReentrantLock();
		this.totalWorkers = n;
		this.optimalCost = initialCost;
		this.barrier = new CyclicBarrier(n + 1);
		this.workPool = new WorkPool<Node>(totalWorkers, dummy);
		this.workPool.putWork(0, root);

	}

	/**
	 * Worker
	 *
	 */
	private class Worker implements Runnable {
		int id;

		public Worker(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				Node curr = workPool.getWork(id);
				while (curr != dummy) {
					if (curr.isTerminal()) {

						double cost = curr.getPathCost();

						if (cost < optimalCost) {
							// Mutex
							lock.lock();
							optimalCost = cost;
							optimalPath = curr.getPath();
							lock.unlock();
						}
					}
					else {
						if (workPool.count >= totalWorkers) // pool daki thread sayisi
							traverse(curr);
						else
							childGenerator(curr);
					}

					curr = workPool.getWork(id);
				}

				// barrier thread sayisi kadar eksiltilir
				barrier.await();
			}
			catch (Exception e) {
				System.out.println(e.toString());
			}

		}

		private void childGenerator(Node curr) throws Exception {
			Node[] children = curr.generateChildren();
			for (Node child : children) {
				if (child.getLowerBound() <= optimalCost) {
					workPool.putWork(id, child);
				}
			}
		}

		private void traverse(Node parent) {
			Node[] children = parent.generateChildren();

			for (Node child : children) {
				if (child.isTerminal()) {
					double cost = child.getPathCost();
					if (cost < optimalCost) {

						lock.lock();
						optimalCost = cost;
						optimalPath = child.getPath();
						lock.unlock();

					}
				}
				else if (child.getLowerBound() <= optimalCost) {
					traverse(child);
				}
			}
		}

	}

	/**
	 * thread leri baslatir
	 */
	private void dispatch() {
		Thread[] threads = new Thread[totalWorkers];
		int i = 0;
		for (Thread t : threads) {
			t = new Thread(new Worker(i++));
			t.start();
		}

	}

	public int[] calculate() throws Exception {
		dispatch();

		// thread sayisi + 1 : tum threadler sonlaninca barrier tamamlanir.
		barrier.await();

		return optimalPath;
	}

}
