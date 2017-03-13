package cz.uhk.pgrf.geometry;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.uhk.pgrf.transforms.Mat4;

/**
 * interface pro WireFrameRenderer.
 * 
 * @author Tomáš Novák
 * @version 2016
 */ 

public interface Renderable {

	void setBufferedImage(BufferedImage img);

	void setView(Mat4 mat);

	void setProj(Mat4 mat);

	void draw(Objekt3D obj);

	void draw(GeometricObject obj, Mat4 mod);

	void draw(List<Objekt3D> objs);

}
