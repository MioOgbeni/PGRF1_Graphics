package cz.uhk.pgrf.geometry;

import java.util.ArrayList;

import cz.uhk.pgrf.transforms.Cubic;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Point3D;

/**
 * Tøída pro vytvoøení kubik.
 * 
 * @author Tomáš Novák
 * @version 2016
 */

public class Krivky extends GeometricObject {

	private Cubic krivka;
	private Point3D bod1;
	private Point3D bod2;
	private Point3D bod3;
	private Point3D bod4;
	private int body;

	public static final Point3D p1 = new Point3D(-2, -2, -2);
	public static final Point3D p2 = new Point3D(-2, -2, 0);
	public static final Point3D p3 = new Point3D(0, -2, -2);
	public static final Point3D p4 = new Point3D(-2, -2, -3);

	// construktory
	public Krivky() {
		this(null, null, null, null, null);
	}

	public Krivky(Mat4 krivka, final Point3D bod1, final Point3D bod2, final Point3D bod3, final Point3D bod4) {
		this(100, krivka, bod1, bod2, bod3, bod4);
	}

	public Krivky(double presnost, Mat4 krivka, final Point3D bod1, final Point3D bod2, final Point3D bod3,
			final Point3D bod4) {
		this((int) (1.0 / presnost), krivka, bod1, bod2, bod3, bod4);
	}

	public Krivky(int body, Mat4 krivka, Point3D bod1, Point3D bod2, Point3D bod3, Point3D bod4) {
		super();
		if (body < 0)
			return;
		if (krivka == null)
			krivka = Cubic.BEZIER;
		if (bod1 == null)
			bod1 = p1;
		if (bod2 == null)
			bod2 = p2;
		if (bod3 == null)
			bod3 = p3;
		if (bod4 == null)
			bod4 = p4;
		this.bod1 = bod1;
		this.bod2 = bod2;
		this.bod3 = bod3;
		this.bod4 = bod4;
		this.body = body;
		vertexBuffer = new ArrayList<>();
		indexBuffer = new ArrayList<>();
		this.krivka = new Cubic(krivka, bod1, bod2, bod3, bod4);
		for (int i = 0; i < body; i++) {
			vertexBuffer.add(this.krivka.compute((double) i / body));
			if (i != 0) {
				indexBuffer.add(i - 1);
				indexBuffer.add(i);
			}
		}
	}

	// vloží již hotovou køivku
	public void setKrivka(Mat4 krivka) {
		this.krivka = new Cubic(krivka, bod1, bod2, bod3, bod4);
		vertexBuffer = new ArrayList<>();
		indexBuffer = new ArrayList<>();
		for (int i = 0; i < body; i++) {
			vertexBuffer.add(this.krivka.compute((double) i / body));
			if (i != 0) {
				indexBuffer.add(i - 1);
				indexBuffer.add(i);
			}
		}
	}

	// vloží køivku podle typu køivky z transforms
	public void setKrivka(int typKrivky) {
		Mat4 krivka;
		switch (typKrivky) {
		case 1:
			krivka = Cubic.COONS;
			break;
		case 2:
			krivka = Cubic.FERGUSON;
			break;
		default:
			krivka = Cubic.BEZIER;
			break;
		}
		;
		setKrivka(krivka);
	}
}
