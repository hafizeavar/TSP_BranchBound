package parallel;
import java.awt.geom.Point2D;

/**
 * her "city" bir name ve x,y kordinatları ile temsil edilir
 */
public class City extends Point2D.Double {
	private String name;

	/**
	 * City constructor
	 *
	 * @param name City ismi
	 * @param x kordinat x
	 * @param y kordinat y
	 */
	public City(String name, double x, double y) {
		super(x, y);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
