package parallel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Work Pool implementasyonu
 * 
 * @author havar
 *
 * @param <Node>
 */
public class WorkPool<Node> {
	/**
	 * Node düğümlerini eklediğimiz pool
	 */
	private BlockingQueue<Node> pool;

	/** thread sayısı */
	private int numworkers;

	/** iş sayısını güncellemek için kullanacağımız lock **/
	private Lock lock;

	/** bekleyen düğüm sayısı **/
	int count;

	/** thread leri sonlandıracak düğüm **/
	Node dummy;

	public WorkPool(int n, Node dummy) throws Exception {
		if (dummy == null)
			throw new Exception(" dummy can not be null ");
		this.dummy = dummy;
		this.numworkers = n;
		count = 0;
		this.lock = new ReentrantLock();
		this.pool = new LinkedBlockingQueue<Node>(1000);
	}

	public Node getWork(int me) throws Exception {
		int workcount;

		// Mutex
		lock.lock();
		workcount = count - 1;
		count = workcount;
		lock.unlock();

		// pool da iş kalmazsa thread ler take() de beklemeye başlar son gelen thread thread sayısı kadar dummy işi pool a atar
		if (workcount == -numworkers) {
			for (int i = 0; i < numworkers; ++i) {
				try {
					pool.put(dummy);
				}
				catch (Exception e) {
					throw e;
				}
			}

		}
		Node t = null;
		try {

			// pooldan yeni is cekme - is yoksa bekler -
			t = pool.take();
		}
		catch (InterruptedException e) {
			System.out.println(e.toString());
			throw e;
		}
		return t;
	}

	public void putWork(int me, Node item) throws Exception {

		// Mutex
		lock.lock();
		count++;
		System.out.println(count);
		lock.unlock();
		try {
			// pool a is ekle
			pool.put(item);
		}
		catch (Exception e) {
			throw e;
		}

	}
}
