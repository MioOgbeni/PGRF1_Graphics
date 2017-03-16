package cz.uhk.pgrf.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import cz.uhk.pgrf.geometry.Cube;
import cz.uhk.pgrf.geometry.Krivky;
import cz.uhk.pgrf.geometry.Objekt3D;
import cz.uhk.pgrf.geometry.Osy;
import cz.uhk.pgrf.geometry.Tetrahedron;
import cz.uhk.pgrf.geometry.WireFrameRenderer;
import cz.uhk.pgrf.transforms.Camera;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;
import cz.uhk.pgrf.transforms.Mat4OrthoRH;
import cz.uhk.pgrf.transforms.Mat4PerspRH;
import cz.uhk.pgrf.transforms.Mat4RotXYZ;
import cz.uhk.pgrf.transforms.Mat4Scale;
import cz.uhk.pgrf.transforms.Vec3D;

/**
 * Třída pro kreslení do prostoru, interface prostoru.
 * 
 * @author Tomáš Novák
 * @version 2016
 **/

public class Canvas {

	private static int CLEAR_COLOR = 0xffffff;
	private JFrame frame;
	private JPanel panel;
	private BufferedImage img;
	private List<Objekt3D> gos;
	private WireFrameRenderer wfr;
	private Camera camera;
	@SuppressWarnings("unused")
	private Mat4 view;
	private Mat4 proj;
	private Mat4 model;
	private Objekt3D Cube;
	private Objekt3D Tetra;
	private Objekt3D Cubi;
	//jak rychlé budou pohyby
	private final double step = 0.5;
	private JRadioButton JPers;
	private JRadioButton JOrth;
	private JRadioButton JBezier;
	private JRadioButton JCoons;
	private JRadioButton JFerguson;
	private JCheckBox JCube;
	private JCheckBox JTetra;
	private JCheckBox JCubi;
	private Krivky krivka;
	private double sc = 1;
	private int endX;
	private int endY;
	private int posunX;
	private int posunY;

	public Canvas(int width, int height) {
		/**
		 * Nastavení okna
		 **/
		frame = new JFrame();
		frame.setTitle("PGRF1_Graphics");
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		/**
		 * Vytvoření toolbaru
		 */
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);

		JToolBar tb = new JToolBar();
		tb.setFocusable(false);
		frame.add(tb, BorderLayout.NORTH);
		vytvorTlacitka(tb);

		/**
		 * Vytváření objektů
		 */
		gos = new ArrayList<>();
		wfr = new WireFrameRenderer();
		wfr.setBufferedImage(img);

		model = new Mat4Identity();
		proj = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
		camera = new Camera(new Vec3D(-10, 0, 0), 0, 0, 1, true);
		view = camera.getViewMatrix();

		gos.add(new Objekt3D(new Osy()));
		Cube = new Objekt3D(new Cube());
		Tetra = new Objekt3D(new Tetrahedron());
		krivka = new Krivky();
		krivka.setBaseColor(Color.RED.getRGB());
		Cubi = new Objekt3D(krivka);

		draw();

		/**
		 * nastavení a použítí myši
		 */
		MouseAdapter mouse = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				endX = e.getX();
				endY = e.getY();
			}
		};
		
		panel.addMouseListener(mouse);
		
		MouseAdapter mousemove = new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// otáčení objektu BTN3 a camery BTN1       
		        posunX = endX;
		        posunY = endY;
		        
		        endX = e.getX();
		        endY = e.getY();
		        
		        int posunoutX = endX - posunX;
				int posunoutY = endY - posunY;
				switch (e.getModifiers()){
				case MouseEvent.BUTTON1_MASK:
					camera = camera.addAzimuth((double) -posunoutX * Math.PI / 720);
					camera = camera.addZenith((double) -posunoutY * Math.PI / 720);
					break;
				case MouseEvent.BUTTON3_MASK:
					Mat4 rot = new Mat4RotXYZ(0, -posunoutY * Math.PI / 180, posunoutX * Math.PI / 180);
					model = model.mul(rot);
					break;
				}
				draw();
			}

		};

		MouseAdapter mousewheel = new MouseAdapter() {
			// Přiblížení/oddálení
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					camera = camera.forward(step);
				} else {
					camera = camera.backward(step);
				}
				draw();
			}
		};

		panel.addMouseMotionListener(mousemove);
		panel.addMouseWheelListener(mousewheel);

		/**
		 * Namapování klávesnice a použití
		 */
		KeyAdapter key = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					// vpřed
					camera = camera.forward(step);
					break;
				case KeyEvent.VK_S:
					// vzad
					camera = camera.backward(step);
					break;
				case KeyEvent.VK_A:
					// levá
					camera = camera.left(step);
					break;
				case KeyEvent.VK_D:
					// pravá
					camera = camera.right(step);
					break;
				case KeyEvent.VK_Q:
					// nahoru
					camera = camera.up(step);
					break;
				case KeyEvent.VK_E:
					// dolu
					camera = camera.down(step);
					break;
				case KeyEvent.VK_R:
					// zmenšit
					sc = sc * step;
					Mat4 scale = new Mat4Scale(sc, sc, sc);
					model = model.mul(scale);
					break;
				case KeyEvent.VK_T:
					// zvětšit
					sc = sc * (1+step);
					Mat4 scale1 = new Mat4Scale(sc, sc, sc);
					model = model.mul(scale1);	
				}
				draw();
			}	
		};
		frame.addKeyListener(key);

	}

	/**
	 * vytvoření tlačítek na liště ???focusable false, jinak nejde klávesnice???
	 * 
	 * @param kontejner
	 */
	private void vytvorTlacitka(JToolBar kontejner) {
		JButton Reset = new JButton("Reset");
		Reset.setFocusable(false);
		kontejner.add(Reset);

		JPers = new JRadioButton("Perspectivní");
		JPers.setSelected(true);
		JOrth = new JRadioButton("Ortogonální");

		ButtonGroup group = new ButtonGroup();
		group.add(JPers);
		group.add(JOrth);

		JPers.setFocusable(false);
		JOrth.setFocusable(false);

		JBezier = new JRadioButton("Bezierova");
		JBezier.setSelected(true);
		JCoons = new JRadioButton("Coonsova");
		JFerguson = new JRadioButton("Fergusonova");

		ButtonGroup group1 = new ButtonGroup();
		group1.add(JBezier);
		group1.add(JCoons);
		group1.add(JFerguson);

		JBezier.setFocusable(false);
		JCoons.setFocusable(false);
		JFerguson.setFocusable(false);
		
		JCube = new JCheckBox("Krychle");
		JTetra = new JCheckBox("Jehlan");
		JCubi = new JCheckBox("Křivka");
		
		JCube.setFocusable(false);
		JTetra.setFocusable(false);
		JCubi.setFocusable(false);

		kontejner.add(JPers);
		kontejner.add(JOrth);
		kontejner.add(JBezier);
		kontejner.add(JCoons);
		kontejner.add(JFerguson);
		kontejner.add(JCube);
		kontejner.add(JTetra);
		kontejner.add(JCubi);
		
		Reset.addActionListener(e -> reset());
		JPers.addActionListener(e -> vyberPers());
		JOrth.addActionListener(e -> vyberOrth());
		JBezier.addActionListener(e -> vyberBezier());
		JCoons.addActionListener(e -> vyberCoons());
		JFerguson.addActionListener(e -> vyberFerguson());
		JCube.addActionListener(e -> vyberCube());
		JTetra.addActionListener(e -> vyberTetra());
		JCubi.addActionListener(e -> vyberCubi());
	}

	private void vyberCube() {
		if (JCube.isSelected()){
			gos.add(Cube);
			draw();
		}else{
			gos.remove(Cube);
			draw();
		}
	}

	private void vyberTetra() {
		if (JTetra.isSelected()){
			gos.add(Tetra);
			draw();
		}else{
			gos.remove(Tetra);
			draw();
		}

	}

	private void vyberCubi() {
		if (JCubi.isSelected()){
			gos.add(Cubi);
			draw();
		}else{
			gos.remove(Cubi);
			draw();
		}
	}

	private void vyberFerguson() {
		JFerguson.setSelected(true);
		krivka.setKrivka(2);
		draw();
		

	}

	private void vyberCoons() {
		JCoons.setSelected(true);
		krivka.setKrivka(1);
		draw();

	}

	private void vyberBezier() {
		JBezier.setSelected(true);
		krivka.setKrivka(0);
		draw();

	}

	private void vyberOrth() {
		proj = new Mat4OrthoRH(20, 20, 0.1, 200);
		draw();
	}

	private void vyberPers() {
		proj = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
		draw();
	}

	private void reset() {
		sc = 1;
		model = new Mat4Identity();
		camera = new Camera(new Vec3D(-10, 0, 0), 0, 0, 1, true);
		view = camera.getViewMatrix();
		proj = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
		JPers.setSelected(true);
		JBezier.setSelected(true);
		draw();
	}

	/**
	 * Vyčištění plátna
	 * 
	 * @param color
	 */
	public void clear(int color) {
		Graphics gr = img.getGraphics();
		gr.setColor(new Color(color));
		gr.fillRect(0, 0, img.getWidth(), img.getHeight());
	}

	/**
	 * Refresh, help, vykreslení prázdného plátna a startovací metoda
	 */
	public void present() {
		if (panel.getGraphics() != null)
			panel.getGraphics().drawImage(img, 0, 0, null);
	}

	public void draw() {
		clear(CLEAR_COLOR);
		for (Objekt3D go : gos) {
			go.setMat(model);
		}
		wfr.setView(camera.getViewMatrix());
		wfr.setProj(proj);
		wfr.draw(gos);
		present();
		help();
	}

	public void start() {
		clear(CLEAR_COLOR);
		draw();
		present();
		help();
		Graphics start = panel.getGraphics();
		start.setColor(Color.BLACK);
		start.setFont(new Font("TimesRoman", Font.PLAIN, 40));
		start.drawString("VYBERTE OBJEKT/Y", img.getWidth()/2-200, img.getHeight()/2-20);
	}

	public void help() {
		Graphics help = panel.getGraphics();
		help.setColor(Color.BLACK);
		help.drawString("HELP ", 10, 15);
		help.drawString("A,W,S,D -> pohyb ←↑↓→ po ose XY ", 10, 30);
		help.drawString("Q,E -> pohyb ↑↓ po ose Z ", 10, 45);
		help.drawString("R,T-> zvětšení/zmenšení o polovinu ", 10, 60);
		help.drawString("Mouse_BTN1 -> otáčení kamerou ", 10, 75);
		help.drawString("Mouse_BTN3 -> otáčení objekty ", 10, 90);
		help.drawString("Mouse_Wheel -> zooming ", 10, 105);
	}

	public static void main(String[] args) {
		Canvas canvas = new Canvas(800, 600);
		SwingUtilities.invokeLater(() -> {
			SwingUtilities.invokeLater(() -> {
				SwingUtilities.invokeLater(() -> {
					SwingUtilities.invokeLater(() -> {
						canvas.start();
					});
				});
			});
		});
	}
}
