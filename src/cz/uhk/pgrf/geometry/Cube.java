package cz.uhk.pgrf.geometry;

import java.util.ArrayList;
import java.util.Arrays;

import cz.uhk.pgrf.transforms.Point3D;

/**
 * Tøída Krychle.
 * 
 * @author Tomáš Novák
 * @version 2016
 */

public class Cube extends GeometricObject {

	public Cube() {

		Integer[] ints = new Integer[] { 0, 1, 1, 2, 2, 3, 3, 0, 0, 4, 4, 5, 5, 6, 6, 7, 7, 4, 1, 5, 2, 6, 3, 7 };
		indexBuffer = new ArrayList<>(Arrays.asList(ints));

		vertexBuffer = new ArrayList<>();
		vertexBuffer.add(new Point3D(-1, -1, -1));
		vertexBuffer.add(new Point3D(-1, 1, -1));
		vertexBuffer.add(new Point3D(1, 1, -1));
		vertexBuffer.add(new Point3D(1, -1, -1));

		vertexBuffer.add(new Point3D(-1, -1, 1));
		vertexBuffer.add(new Point3D(-1, 1, 1));
		vertexBuffer.add(new Point3D(1, 1, 1));
		vertexBuffer.add(new Point3D(1, -1, 1));
	}
}
