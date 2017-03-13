package cz.uhk.pgrf.geometry;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;
import cz.uhk.pgrf.transforms.Point3D;
import cz.uhk.pgrf.transforms.Vec3D;

/**
 * Zobrazovací øetìzec.
 * 
 * @author Tomáš Novák
 * @version 2016
 */ 

public class WireFrameRenderer implements Renderable {

    private BufferedImage img;
    private Mat4 view;
    private Mat4 proj;
    private Mat4 model;

    //nastavené matic pohledu a projekce
    @Override
    public void setBufferedImage(BufferedImage img) {
        this.img = img;
    }

    @Override
    public void setView(Mat4 view) {
        this.view = view;
    }

    @Override
    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
    
    //zobrazovací øetìzec
    @Override
    public void draw(GeometricObject geoObj, Mat4 model) {

    	if(geoObj.isTransferable()){
    		this.model = geoObj.getMat().mul(model);
    	} else {
    		this.model = new Mat4Identity();
    	}

        final Mat4 finTransform = this.model.mul(view).mul(proj);
        List<Integer> ints = geoObj.getIB();

        for (int i = 0; i < ints.size(); i += 2) {
        	//transformace
            int indexA = geoObj.getIB().get(i);
            int indexB = geoObj.getIB().get(i + 1);

            Point3D vertexA = geoObj.getVB().get(indexA);
            Point3D vertexB = geoObj.getVB().get(indexB);

            vertexA = vertexA.mul(finTransform);
            vertexB = vertexB.mul(finTransform);

            Vec3D vecA = null;
            Vec3D vecB = null;

            //oøezání w
            double wmin = 0.1;
                    
            if (vertexA.getW() > vertexB.getW()){
                Point3D temp = vertexA;
                vertexA=vertexB;
                vertexB=temp;
            }
            
            if(vertexB.getW() < wmin){
            	continue;
            }
            
            if(vertexA.getW() < wmin){
                double t = (wmin-vertexA.getW())/(vertexB.getW()-vertexA.getW());
                vertexA = vertexA.mul(1-t).add(vertexB.mul(t));
            }
            
            //4D -> 3D dehomog
            if (vertexA.dehomog().isPresent()) {
                vecA = vertexA.dehomog().get();
            }
            
            if (vertexA.dehomog().isPresent()) {
                vecB = vertexB.dehomog().get();
            }
            // oøezání objemem
            	//3D -> 2D
            if(vecA == null || vecB == null){
            	continue;
            }
            if( Math.min(vecA.getX(),vecB.getX()) >  1.0 || 
                Math.max(vecA.getX(),vecB.getX()) < -1.0 ||
                Math.min(vecA.getY(),vecB.getY()) >  1.0 ||
                Math.max(vecA.getY(),vecB.getY()) < -1.0 ||
                Math.min(vecA.getZ(),vecB.getZ()) >  1.0 ||
                Math.max(vecA.getZ(),vecB.getZ()) < -1.0)
            {
            	continue;
            }
                
            //upravení na okno
            int x1 = (int) ((vecA.getX() + 1) * (img.getWidth() - 1) / 2 + 0.5);
            int y1 = (int) ((1 - vecA.getY()) * (img.getHeight() - 1) / 2 + 0.5);

            int x2 = (int) ((vecB.getX() + 1) * (img.getWidth() - 1) / 2 + 0.5);
            int y2 = (int) ((1 - vecB.getY()) * (img.getHeight() - 1) / 2 + 0.5);

            //vykreslení
            Graphics g = img.getGraphics();
            g.setColor(new Color(geoObj.getColor(i/2)));
            g.drawLine(x1, y1, x2, y2);
        }

    }

    //vykreslí list objektù
    @Override
    public void draw(List<Objekt3D> objs) {
        for (Objekt3D obj : objs) {
            draw(obj);
        }

    }
    
    //vykreslí 1 daný objekt
    @Override
    public void draw(Objekt3D obj){
        for(int i = 0; i < obj.getCount();i++){
            draw(obj.get(i),obj.getMat());
        }
    }

}