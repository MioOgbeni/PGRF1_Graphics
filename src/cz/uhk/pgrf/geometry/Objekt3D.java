package cz.uhk.pgrf.geometry;

import java.util.ArrayList;
import java.util.List;

import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;

/**
 * Tøída obecného objektu.
 * 
 * @author Tomáš Novák
 * @version 2016
 */

public class Objekt3D {

	protected List<GeometricObject> geoObj = new ArrayList<>();
	protected Mat4 model = new Mat4Identity();

	// konstruktory
	public Objekt3D(GeometricObject geoObj) {
		this.geoObj.add(geoObj);
	}

	public Objekt3D(List<GeometricObject> geoObjs) {
		this.geoObj = geoObjs;
	}

	// vloží/nastaví modelovací matici
	public Mat4 getMat() {
		return model;
	}

	public void setMat(Mat4 model) {
		this.model = model;
	}

	// vrátí GeomObj na daném indexu
	public GeometricObject get(int i) {
		if (i >= 0 && i < geoObj.size()) {
			return geoObj.get(i);
		}
		return null;
	}

	// vrátí poèet Objektù
	public int getCount() {
		return geoObj.size();
	}

	// pøidá další Objekt
	public void add(GeometricObject geoObj) {
		this.geoObj.add(geoObj);
	}
}
