package hikingSkillTrainer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

public class InteractiveHikeSolver implements MouseListener {
	// needed hikeimage, hikeinstruction image
	// completeimage is better
	// start point, and directions
	BufferedImage hikeImage, points;
	ArrayList<hikePoint> hps;
	hikePoint [][] hikemap;
	int size;
	JLabel jl;
	int count,mistake; 
	int startx, starty, endx, endy;
	
	InteractiveHikeSolver (BufferedImage hikeImage, BufferedImage points, ArrayList<hikePoint> hps, int size, hikePoint [][] hikemap){
		count=0;
		mistake=0;
		this.hikemap=hikemap;
		this.hikeImage=hikeImage;
		this.points=points;
		this.hps=hps;
		this.size=size;
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();

		jl = new JLabel(new ImageIcon(hikeImage));
		jp.add(jl);
		jp.setSize(hikeImage.getWidth(), hikeImage.getHeight());
		jl.addMouseListener(this);
		
		JScrollPane scrPane = new JScrollPane(jp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrPane.setLayout(new ScrollPaneLayout());
		
		frame.add(scrPane);
		int xmax = Math.min((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), hikeImage.getWidth()+50);
		int ymax = Math.min((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-50,hikeImage.getHeight()+80);
		frame.setSize(xmax, ymax);
		frame.setVisible(true);
		// now we need to check which position we are, start at 0
		//System.out.println("point " +hps.get(0).p.x+", "+hps.get(0).p.y);
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println("clicked @ "+ e.getX()+", "+e.getY());
		// check if it is within the map or not
		if (startx<points.getWidth()) {
			// means outside
			int cx=hps.get(count+1).p.x;
			int cy =hps.get(count+1).p.y;
			hikePoint clickedpoint = findNearestPoint(e.getX(),e.getY());
			if (clickedpoint.p.x==cx&&clickedpoint.p.y==cy) {
				updateGoodGraphics();
			} else {
				updateBadGraphics(clickedpoint.p.x, clickedpoint.p.y);
			}
		}
	}
	
	public hikePoint findNearestPoint(int x, int y) {
		double dif=Double.MAX_VALUE;
		hikePoint cp = null;
		for (int i=0;i<hikemap.length;i++) {
			for (int j=0;j<hikemap[i].length;j++) {
				double dist2 =Math.pow(hikemap[i][j].p.x-x, 2)+Math.pow(hikemap[i][j].p.y-y, 2);  
				if (dist2<dif) {
					dif = dist2;
					cp=hikemap[i][j];
				}
			}
		}
		return cp;
	}
	
	public void updateInstructions() {
		Graphics2D g2= (Graphics2D)hikeImage.getGraphics();
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.RED);
		g2.drawLine(startx, starty, endx, endy);
		jl.repaint();
	}
	
	public void updateGoodGraphics() {
		Graphics2D g2= (Graphics2D)hikeImage.getGraphics();
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.GREEN);
		g2.drawLine(hps.get(count).p.x, hps.get(count).p.y, hps.get(count+1).p.x, hps.get(count+1).p.y);
		g2.fillOval(hps.get(count).p.x-5, hps.get(count).p.y-5, 10, 10);
		g2.setColor(Color.CYAN);
		g2.fillOval(hps.get(count+1).p.x-5, hps.get(count+1).p.y-5, 10, 10);
		jl.repaint();
		
/*		if (count>=1) {
			g2.setColor(Color.GREEN);
			g2.drawLine(hps.get(count).p.x, hps.get(count).p.y, hps.get(count-1).p.x, hps.get(count-1).p.y);
		}*/
		count++;
		if (count==hps.size()-1) {
			// show message of succesful solve
			//g2.setColor(Color.GREEN);
			//g2.setFont(new Font("Arial", Font.BOLD,hikeImage.getWidth()/25));
			g2.setBackground(Color.WHITE);
			//g2.clearRect(0, hikeImage.getHeight()/2-hikeImage.getWidth()/20, hikeImage.getWidth(), hikeImage.getWidth()/20);
			//g2.drawString("You have succesfully finished the puzzle", 20, hikeImage.getHeight()/2);
			// clear the image, and redraw the solution
			g2.clearRect(0,  0, hikeImage.getWidth(), hikeImage.getHeight());
			g2.setColor(Color.BLACK);
			for (int i=0;i<hps.size()-1;i++) {
				g2.drawLine(hps.get(i).p.x, hps.get(i).p.y, hps.get(i+1).p.x, hps.get(i+1).p.y);
			}
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Arial", Font.BOLD,hikeImage.getWidth()/35));
			g2.drawString("You have finished the puzzle", points.getWidth(), hikeImage.getHeight()/2);
			g2.drawString("You made "+ mistake+" mistake(s)", points.getWidth(), hikeImage.getHeight()/2+100);
		}
	}

	public void updateBadGraphics(int x,int y) {
		//System.out.println("clicked wrong");
		mistake++;
		Graphics2D g2= (Graphics2D)hikeImage.getGraphics();
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.RED);
		g2.drawLine(hps.get(count).p.x, hps.get(count).p.y, x, y);
		jl.repaint();
		
		// add losing hearts
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// when pressed in
		if (e.getX()>points.getWidth()) {
			startx=e.getX();
			starty=e.getY();
		} else {
			startx=0;
			starty=0;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//when released 
		if (e.getX()>points.getWidth()) {
			endx=e.getX();
			endy=e.getY();
			if (startx>0) {
				updateInstructions();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// monitors if cursos is in jpanel
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// monitors if cursos is in jpanel
	}
	
	
}
