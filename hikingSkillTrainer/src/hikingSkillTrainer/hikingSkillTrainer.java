package hikingSkillTrainer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;


public class hikingSkillTrainer {
	int xwidth,ywidth,size1,rl1, rl2;
	boolean cross1;
	String choice, techchoice, dirchoice;
	public hikePoint[][] hikemap;
	ArrayList<hikePoint> hps, maxhps; // this is the route
	ArrayList<Integer> directions, maxdirs;
	ArrayList<ArrayList<Integer>> posdirarray, maxposdirs;
	Graphics g;
	String cl;
	
	public void exportImage(String imageName, BufferedImage image) {
	    File file = new File(imageName+".png");
	    int counter=0;
	    String newName=imageName;
	    while (file.exists()) {
	    	newName=imageName+counter;
	    	file=new File(newName+".png");
	    	counter++;
	    }
	    imageName=newName;
	    try {
	        System.out.println("Exporting image: "+imageName+".png");
	        FileOutputStream out = new FileOutputStream(imageName+".png");
	        ImageIO.write(image, "png", out);
	        out.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }  
	}
	
	public BufferedImage makeMap () {
		BufferedImage mapImage = new BufferedImage(xwidth*size1, ywidth*size1, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = mapImage.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, xwidth*size1, ywidth*size1);
		 g.setColor(Color.BLACK);
	        for (int i=0;i<ywidth;i++) {
				for (int j=0;j<xwidth;j++) {
					g.fillOval(hikemap[i][j].p.x-5, hikemap[i][j].p.y-5, 10, 10);
					g.drawOval(hikemap[i][j].p.x-5, hikemap[i][j].p.y-5, 10, 10);
					if (i==0) { // only check towards the bottom topside
						if (j==xwidth-1) { // right top, nothing to check
							
						} else {
							// check E, and SE
							hikePoint hp=hikemap[i][j];
							if (hikemap[i][j].E) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y);
							}
							if (hikemap[i][j].SE) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y+size1);
							}

						}

					} else if (i==ywidth-1) { // bottomside
						if (j==xwidth-1) { // right bottom, check N
							hikePoint hp=hikemap[i][j];
							if (hikemap[i][j].N) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x, hp.p.y-size1);
							}
						} else {
							// check N, NE, E
							hikePoint hp=hikemap[i][j];
							if (hikemap[i][j].E) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y);
							}
							if (hikemap[i][j].NE) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y-size1);
							}
							if (hikemap[i][j].N) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x, hp.p.y-size1);
							}
						}
					} else { // any other i
						if (j==xwidth-1) { // only check towards the north // rightside
							// check N
							hikePoint hp=hikemap[i][j];
							if (hikemap[i][j].N) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x, hp.p.y-size1);
							}
						} else {
							// check N, NE, E, SE
							hikePoint hp=hikemap[i][j];
							if (hikemap[i][j].E) {
								
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y);
							}
							if (hikemap[i][j].SE) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y+size1);
							}
							if (hikemap[i][j].NE) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x+size1, hp.p.y-size1);
							}
							if (hikemap[i][j].N) {
								g.drawLine(hp.p.x, hp.p.y, hp.p.x, hp.p.y-size1);
							}
						}
					}
				}
			}
	        g.setColor(Color.RED);
	        g.fillOval(hps.get(0).p.x-5, hps.get(0).p.y-5, 10, 10);
	        g.drawOval(hps.get(0).p.x-5, hps.get(0).p.y-5, 10, 10);
	        return mapImage;
	}
	
		public static void main (String ... args) {
		hikingSkillTrainer hst = new hikingSkillTrainer();
		hst.cl="English";
		hst.chooseOptions();
		hst.makeHikeMap();
		
		int count=0;
        boolean route =false;
        hst.maxhps= new ArrayList<hikePoint>();
        while (!route && count<hst.rl2){
        	
			route=hst.makeRoute();
			count++;
			if (hst.hps.size()>hst.maxhps.size()) {
				hst.maxhps=hst.hps;
				hst.maxdirs=hst.directions;
				hst.maxposdirs=hst.posdirarray;
			}
		}
        hst.hps=hst.maxhps;
        hst.directions=hst.maxdirs;
        hst.posdirarray=hst.maxposdirs;
	    
	    
		
		// this draws the map we always need
		// Lets try with buffered image
		BufferedImage map =hst.makeMap();
		hst.exportImage(hst.dirchoice+"\\Map", map);
		
		// now we need to create the methods for drawing routes
		BufferedImage VectorMap; 
		int imsize=((int)Math.sqrt(hst.directions.size())+1)*100;
		VectorMap=hst.drawArrows(hst.directions,imsize,imsize+100);
	    hst.exportImage(hst.dirchoice+"\\VectorDirections", VectorMap);
	    
	    // we always show the solution
	    BufferedImage dr = hst.drawRoute(hst.hps,hst.xwidth*hst.size1,hst.ywidth*hst.size1);
	    hst.exportImage(hst.dirchoice+"\\Solution", dr);
	    
	    // for Arrow route
	    BufferedImage pov = hst.pointOfDirections(hst.directions,imsize,imsize+100);
	    hst.exportImage(hst.dirchoice+"\\ArrowDirections",pov);
	    
	    // For smiley route
	    BufferedImage poe = hst.pointOfViewEyes(hst.directions, imsize,imsize+100);
	    hst.exportImage(hst.dirchoice+"\\SmileyDirections", poe);

	    // for strip card
	    BufferedImage strip;
	    strip=hst.stripDirections(hst.directions, hst.posdirarray, (hst.directions.size()/6+1)*100,720);
	    hst.exportImage(hst.dirchoice+"\\StripDirections", strip);
	    
	    //For wind directions
	    BufferedImage writeDirections=hst.writeDirections(hst.directions, imsize, imsize+100);
	    hst.exportImage(hst.dirchoice+"\\WindDirections", writeDirections);
	    
	    // for compass east over
	    BufferedImage compas=hst.drawCompas(hst.directions, imsize, imsize+100);
	    hst.exportImage(hst.dirchoice+"\\Compass", compas);
	    
	    //for pov time
	    BufferedImage povtime=hst.pointOfTime(hst.directions, imsize, imsize+100);
	    hst.exportImage(hst.dirchoice+"\\POVtime", povtime);

	    // for north time
	    BufferedImage Ntime=hst.northTime(hst.directions, imsize, imsize+100);
	    hst.exportImage(hst.dirchoice+"\\Northtime", Ntime);

	    //{"Arrow","Smiley","Strip card","Compass", "Vector", "Wind directions","Time", "View Time"};
	    BufferedImage comp=null;
	    if (hst.techchoice=="Vector") {
	    	comp=hst.makeCompleteRoute(map,VectorMap);
	    } else if (hst.techchoice=="Smiley") {
	    	comp=hst.makeCompleteRoute(map,poe);
	    } else if (hst.techchoice=="Strip card") {
	    	comp=hst.makeCompleteRoute(map,strip);
	    } else if (hst.techchoice=="Compass") {
	    	comp=hst.makeCompleteRoute(map,compas);
	    } else if (hst.techchoice=="Wind directions") {
	    	comp=hst.makeCompleteRoute(map,writeDirections);
	    } else if (hst.techchoice=="Time") {
	    	comp=hst.makeCompleteRoute(map,Ntime);
	    }else if (hst.techchoice=="View Time") {
	    	comp=hst.makeCompleteRoute(map,povtime);
	    } else {
	    	comp=hst.makeCompleteRoute(map,pov);
	    }
	    
	    hst.exportImage(hst.dirchoice+"\\HikingSkillTrainerPuzzle", comp);
	    
	 // drawing map
	 		JFrame frame = new JFrame("Solve the " +hst.techchoice+" route");
	    JLabel picLabel = new JLabel(new ImageIcon(comp));	    
	    frame.add(picLabel);
	    frame.setSize(comp.getWidth()+50,comp.getHeight()+50);
	    frame.setVisible(true);
	    
	}
	
	public BufferedImage makeCompleteRoute (BufferedImage map, BufferedImage route) {
		BufferedImage VectorImage = new BufferedImage(map.getWidth()+route.getWidth(), Math.max(map.getHeight(),route.getHeight()), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = VectorImage.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, VectorImage.getWidth(), VectorImage.getHeight());
		g.setColor(Color.BLACK);
		g.drawImage(map, null, 0, 0);
		g.drawImage(route, null, map.getWidth(),0);
		return VectorImage;
	}
	
	
	public boolean makeRoute() {
		hps = new ArrayList<hikePoint>();
		directions = new ArrayList<Integer>();
		posdirarray=new ArrayList<ArrayList<Integer>>();
//		System.out.println("dirsize start "+directions.size());
//		System.out.println("hpssize start "+hps.size());
		int startx=0;
		int starty= 0;
		Random random = new Random();
		if (choice=="Random") {
			startx= random.nextInt(xwidth);
			starty= random.nextInt(ywidth);
		} else if (choice=="Centre"){
			startx=xwidth/2;
			starty=ywidth/2;
		} else {
			if (random.nextBoolean()) { // for x edge
				if (random.nextBoolean()){ //for x=0
					startx=0;
					starty= random.nextInt(ywidth);
				} else {
					startx=xwidth-1;
					starty= random.nextInt(ywidth);
				}
			} else {// for y edge
				if (random.nextBoolean()){ //for x=0
					starty=0;
					startx= random.nextInt(xwidth);
				} else {
					starty=ywidth-1;
					startx= random.nextInt(xwidth);
				}
			}
		}
		hikePoint cp =hikemap[starty][startx];
		hikePoint prevcp =hikemap[starty][startx];
		hps.add(cp);
		int prevdir=0;
		System.out.println("Starting new route from: "+cp.y+", "+cp.x);
		//System.out.println(cp.y+", "+cp.x);
		for (int i=0;i<rl1;i++) {
			ArrayList<Integer> posdirs= getPosDirs(cp);
			ArrayList<Integer> posdirsfull= getPosDirs(cp);
			posdirs = blockReturn(posdirs,prevdir);
			posdirsfull = blockReturn(posdirsfull,prevdir);
			posdirarray.add(posdirsfull);
			int dir = getDir(posdirs);
			if (dir==0) {
				System.err.println("Not enough connections, ending route here");
				//System.out.println("dirsize a:"+directions.size());
				return false;
			}
			prevcp=cp;
			cp =getNextPoint(cp,dir);
			if (!cross1) {
				//System.out.println("Checking for doubles: " +posdirs.size());
				//System.out.println("Checking for doubles: step 2 " +blockDoublePoints(cp));
				while (posdirs.size()>0 && blockDoublePoints(cp)) {
					//System.out.println("Checking for doubles: step 2 " +blockDoublePoints(cp) +cp.y+","+cp.x);
					cp=prevcp;
					//System.out.println("Checking for doubles: returning to "  +cp.y+","+cp.x);
					// this means the point is already in the list
					// we need to remove the current dir from 
					for (int j=0;j<posdirs.size();j++) {
						if (posdirs.get(j)==dir) {
							posdirs.remove(j);
						}
					}
					if (posdirs.size()>0) {
						dir=getDir(posdirs);
						if (dir==0) {
							System.err.println("Not enough connections, ending route here");
							//System.out.println("dirsize a:"+directions.size());
							return false;
						}
						//System.out.println("Moving from here after doubles "  +cp.y+","+cp.x);
						cp =getNextPoint(cp,dir);
					}

				}
				if (posdirs.size()==0) {
					System.err.println("Not enough connections, ending route here");
					//System.out.println("dirsize b:"+directions.size());
					return false;
				}
				hps.add(cp);
				directions.add(dir);
			} else {
				hps.add(cp);
				directions.add(dir);
			}
			prevdir=dir;
			//System.out.print(cp.y+", "+cp.x+" | ");
		}
		//System.out.println("");
		return true;
	}
	public boolean blockDoublePoints(hikePoint cp){
		boolean doublepoint = false;
		for (int i=0;i<hps.size();i++){
			hikePoint tp = hps.get(i);
			if (tp.x==cp.x&&tp.y==cp.y) {
				doublepoint=true;
			}
		}
		return doublepoint;	
	}
	
	public int getDir(ArrayList<Integer> dirs) {
		Random random = new Random();
		if (dirs.size()==0) {
			System.err.println("Not enough connections, ending route here");
			//System.out.println("dirsize c:"+directions.size());
			return 0;
		}
		int dirchoice = random.nextInt(dirs.size());
		int dir = dirs.get(dirchoice);
		return dir;
	}
	
	public ArrayList<Integer> blockReturn(ArrayList<Integer> l, int prevdir){
		for (int i=0;i<l.size();i++) {
			if ((l.get(i)==prevdir+4||l.get(i)==prevdir-4)&&prevdir!=0) {
				l.remove(i);
			}
		}
		return l;
	}
	
	public hikePoint getNextPoint(hikePoint cp, int dir) {
		hikePoint np = hikemap[0][0];
		switch (dir) {
		case 1: //north
			return hikemap[cp.y-1][cp.x];
		case 2:
			return hikemap[cp.y-1][cp.x+1];
		case 3:
			return hikemap[cp.y][cp.x+1];
		case 4:
			return hikemap[cp.y+1][cp.x+1];
		case 5:
			return hikemap[cp.y+1][cp.x];
		case 6:
			return hikemap[cp.y+1][cp.x-1];
		case 7:
			return hikemap[cp.y][cp.x-1];
		case 8:
			return hikemap[cp.y-1][cp.x-1];
		}
		return np;
	}
	
	public void printpoints() {
		for (int i=0;i<ywidth;i++) {
			for (int j=0;j<xwidth;j++) {
				hikePoint cp =hikemap[i][j];
				ArrayList<Integer> posdirs= getPosDirs(cp);
				System.out.println("");
				System.out.print("nexpoint: ");
				System.out.print(cp.p.x+", "+cp.p.y+", dirs:");
				System.out.println(i+", "+j);
				for (int k=0;k<posdirs.size();k++) {
					System.out.print(posdirs.get(k)+", ");
				}
			}
		}
	}
	
	public ArrayList<Integer> getPosDirs(hikePoint cp){
		ArrayList<Integer> posdirs = new ArrayList<Integer>(0);
		if (cp.N) {
			posdirs.add(1);
		}
		if (cp.NE) {
			posdirs.add(2);
		}
		if (cp.E) {
			posdirs.add(3);
		}
		if (cp.SE) {
			posdirs.add(4);
		}
		if (cp.S) {
			posdirs.add(5);
		}
		if (cp.SW) {
			posdirs.add(6);
		}
		if (cp.W) {
			posdirs.add(7);
		}
		if (cp.NW) {
			posdirs.add(8);
		}
		return posdirs;
	}
	
	public void chooseOptions() {
		JPanel jp = new JPanel();
		JTextField xw = new JTextField("10",2);
		JTextField yw = new JTextField("10",2);
		JTextField size = new JTextField("51",2);
		JTextField rl = new JTextField("10",2);
		JTextField r2 = new JTextField("100",2);
		JCheckBox cross = new JCheckBox("",false);
		String[] spos = new String[] {"Random","Centre","Edge"};
		JComboBox<String> cb = new JComboBox<String>(spos);
		String[] hiketech = new String[] {"Arrow","Smiley","Strip card","Compass", "Vector", "Wind directions","Time", "View Time"};
		// Done are Arrow, Vector , Smiley, Strip card, Wind directions, 
		JComboBox<String> cb2 = new JComboBox<String>(hiketech);
		String userdir = System.getProperty("user.home");
		JTextField savedir= new JTextField(userdir+"\\Documents\\Hikes",2);
		GridLayout gl = new GridLayout(9,2,20,5);
		jp.setLayout(gl);
		jp.add(new JLabel("Number of columns:"));
		jp.add(xw);
		jp.add(new JLabel("Number of rows:"));
		jp.add(yw);
		jp.add(new JLabel("Size of each block in pixels"));
		jp.add(size);
		jp.add(new JLabel("What is the desired route length in steps:"));
		jp.add(rl);
		jp.add(new JLabel("What is the maximum amount of calculations:"));
		jp.add(r2);
		jp.add(new JLabel("Allow the route to cross itself"));
		jp.add(cross);
		jp.add(new JLabel("Where do you want the hike to start"));
		jp.add(cb);
		jp.add(new JLabel("What kind of hike do you want"));
		jp.add(cb2);
		jp.add(new JLabel("Where do you want to save the image output"));
		jp.add(savedir);
		
		int result = JOptionPane.showConfirmDialog(null, jp, "Please choose the options you want for the digital hike", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			xwidth=Integer.parseInt(xw.getText());
			ywidth=Integer.parseInt(yw.getText());
			size1=Integer.parseInt(size.getText());
			rl1=Integer.parseInt(rl.getText());
			rl2=Integer.parseInt(r2.getText());
			cross1=cross.isSelected();
			choice=spos[cb.getSelectedIndex()];
			techchoice=hiketech[cb2.getSelectedIndex()];
			dirchoice=savedir.getText();
			File f = new File(dirchoice);
			if (!f.exists()) {
				f.mkdir();
			}
	      }
	}
	
	public hikePoint[][] makeHikeMap() {
		hikemap = new hikePoint[ywidth][xwidth];
		Random random = new Random();
		for (int i=0;i<ywidth;i++) { // first initiate the map
			for (int j=0;j<xwidth;j++) {
				hikemap[i][j]= new hikePoint();
				hikemap[i][j].setPoint(new Point(j*size1+size1/2+1,i*size1+size1/2+1));
				hikemap[i][j].x=j;
				hikemap[i][j].y=i;
			}
		}
		for (int i=0;i<ywidth;i++) {
			for (int j=0;j<xwidth;j++) {
				//System.out.print(i*size+size/2+1);
				//System.out.print(","+ (j*size+size/2+1)+"|");
				
				if (i==0) { // top, only to E, or SE
					if (j==xwidth-1) {
						
					} else {
						hikemap[i][j].E= random.nextBoolean();
						if (hikemap[i][j].E) {
							hikemap[i][j+1].W=true;
						}
						hikemap[i][j].SE= random.nextBoolean();
						if (hikemap[i][j].SE) {
							hikemap[i+1][j+1].NW=true;
						}
					}
				} else if (i==ywidth-1) { // bottomside
					if (j==xwidth-1) { // right bottom, check N
						hikemap[i][j].N= random.nextBoolean();
						if (hikemap[i][j].N) {
							hikemap[i-1][j].S=true;
						}
					} else {
						hikemap[i][j].N= random.nextBoolean();
						if (hikemap[i][j].N) {
							hikemap[i-1][j].S=true;
						}
						hikemap[i][j].NE= random.nextBoolean();
						if (hikemap[i][j].NE) {
							hikemap[i-1][j+1].SW=true;
						}
						hikemap[i][j].E= random.nextBoolean();
						if (hikemap[i][j].E) {
							hikemap[i][j+1].W=true;
						}
					}
				} else { // any other i
				
					if (j==xwidth-1) { // only check towards the north // rightside
						hikemap[i][j].N= random.nextBoolean();
						if (hikemap[i][j].N) {
							hikemap[i-1][j].S=true;
						}
					} else {
						hikemap[i][j].N= random.nextBoolean();
						if (hikemap[i][j].N) {
							//System.out.println("setting to the south");
							hikemap[i-1][j].S=true;
							//System.out.println(hikemap[i+1][j].S);
							//System.out.println(i+"," +j);
						}
						hikemap[i][j].NE= random.nextBoolean();
						if (hikemap[i][j].NE) {
							hikemap[i-1][j+1].SW=true;
						}
						hikemap[i][j].E= random.nextBoolean();
						if (hikemap[i][j].E) {
							hikemap[i][j+1].W=true;
						}
						hikemap[i][j].SE= random.nextBoolean();
						if (hikemap[i][j].SE) {
							hikemap[i+1][j+1].NW=true;
						}
					}
				}
				/*
				 * these are not needed if we map them to the other points directly as well
				 * hikemap[i][j].S= random.nextBoolean();
				hikemap[i][j].SW= random.nextBoolean();
				hikemap[i][j].W= random.nextBoolean();
				hikemap[i][j].NW= random.nextBoolean();
				*/
			}
			
		}
		// now we have the point array and the connections
		// these need to be checked and limited at the edges.
		return hikemap;
	}
	
public int drawSeperators(Graphics g, int size, int ysize) {
	int ymax=(int)Math.sqrt(size);
	
	for (int i=0;i<ymax;i++) {
		g.drawLine((i+1)*100,0,(i+1)*100,(ymax+1)*100);
	}
	
    return ymax;
}

public BufferedImage drawCompas(ArrayList<Integer> dir, int xsize, int ysize) {
	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, xsize, ysize);
	 g.setColor(Color.BLACK);
        int ymax=drawSeperators(g,dir.size(),ysize);
        int xcount =0;
        int ycount=0;
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        for (int i=0;i<dir.size();i++) {

        	writeDegrees(dir.get(i),ycount,xcount,g);
        	ycount++;
        	if (ycount>ymax) {
        		xcount++;
        		ycount=0;
        	}
        }
        
        g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22));
    	if (cl.equals("English")) {
    		g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
    		g.drawString("The compass goes over east", 5, ysize-10);
    	} else {
    		g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
    		g.drawString("Het kompas is oostom", 5, ysize-10);
    	}
        return VectorImage;
	 }

public void writeDegrees(int dir, int number, int col, Graphics2D g){
	switch (dir) {
   	case 1: // north
    		g.drawString("0°", 30+(col*100), 50+(number*100));
    		break;
    	case 2: //NE
    		g.drawString("45°", 25+(col*100), 50+(number*100));
    		break;
    	
    	case 3: //E
    		g.drawString("90°", 25+(col*100), 50+(number*100));
    		break;
    	case 4: //SE
    		g.drawString("135°", 20+(col*100), 50+(number*100));
    		break;

    	case 5: //S
    		g.drawString("180°", 20+(col*100), 50+(number*100));
    		break;
    	case 6: //SW
    		g.drawString("225°", 20+(col*100), 50+(number*100));    
    		break;
    	case 7: //W
    		g.drawString("270°", 20+(col*100), 50+(number*100));    		
    		break;
    	case 8: //NW
    		g.drawString("315°", 20+(col*100), 50+(number*100));
    		break;
    	}
    }



	public BufferedImage drawArrows(ArrayList<Integer> dir, int xsize, int ysize) {
		BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = VectorImage.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, xsize, ysize);
		g.setColor(Color.BLACK);
	    int ymax=drawSeperators(g,dir.size(),ysize);
	    g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22));
		if (cl.equals("English")) {
			g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
			g.drawString("The arrow indicates the direction w.r.t. north", 5, ysize-10);
		} else {
			g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
			g.drawString("De pijl geeft de richting t.o.v. het noorden", 5, ysize-10);
		}
	    int xcount =0;
	    int ycount=0;
	    for (int i=0;i<dir.size();i++) {
		drawArrow(dir.get(i),ycount,xcount,g);
		ycount++;
		if (ycount>ymax) {
			xcount++;
			ycount=0;
	    	}
	    }
    	return VectorImage;
	}

	public void drawArrow(int dir, int number, int col, Graphics2D g){
    	switch (dir) {
    	case 1: // north
    		g.drawLine(50+(col*100), 80+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 50+(col*100), 20+(number*100));
    		break;
    	case 2: //NE
    		g.drawLine(20+(col*100), 80+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 80+(col*100), 20+(number*100));
    		break;
    	
    	case 3: //E
    		g.drawLine(80+(col*100), 50+(number*100), 20+(col*100),50 +(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 80+(col*100), 50+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 80+(col*100), 50+(number*100));
    		break;
    	case 4: //SE
    		g.drawLine(20+(col*100), 20+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 80+(col*100), 80+(number*100));
    		break;

    	case 5: //S
    		g.drawLine(50+(col*100), 80+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 50+(col*100), 80+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 50+(col*100), 80+(number*100));
    		break;
    	case 6: //SW
    		g.drawLine(20+(col*100), 80+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 20+(col*100), 80+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 20+(col*100), 80+(number*100));
    		break;
    	case 7: //W
    		g.drawLine(80+(col*100), 50+(number*100), 20+(col*100),50 +(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 20+(col*100), 50+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 20+(col*100), 50+(number*100));
    		break;
    	case 8: //NW
    		g.drawLine(20+(col*100), 20+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 20+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 20+(col*100), 20+(number*100));
    		break;
    	}
    }

public BufferedImage writeDirections(ArrayList<Integer> dir, int xsize, int ysize) {
	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, xsize,ysize);
	 g.setColor(Color.BLACK);
	 g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
	 int ymax=drawSeperators(g,dir.size(),ysize);
        
        int xcount =0;
        int ycount=0;
        g.setColor(Color.BLACK);
         
        
        for (int i=0;i<dir.size();i++) {

        	drawString(dir.get(i),ycount,xcount,g);
        	ycount++;
        	if (ycount>ymax) {
        		xcount++;
        		ycount=0;
        	}
        }
        g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22));
		if (cl.equals("English")) {
			g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
			g.drawString("The letters indicate the wind directions", 5, ysize-10);
		} else {
			g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
			g.drawString("De pijl geeft de windrichting aan", 5, ysize-10);
		}

  	 
    //this.setSize(500,500);
     return VectorImage;
	 }
	    
	public void drawString(int dir, int number, int col, Graphics2D g){
    	switch (dir) {
    	case 1: // north
    		g.drawString("N", 40+(col*100), 50+(number*100));
    		break;
    	case 2: //NE
    		g.drawString("NE/NO", 15+(col*100), 50+(number*100));
    		break;
    	
    	case 3: //E
    		g.drawString("E/O", 32+(col*100), 50+(number*100));
    		break;
    	case 4: //SE
    		g.drawString("SE/ZO", 15+(col*100), 50+(number*100));
    		break;

    	case 5: //S
    		g.drawString("S/Z", 32+(col*100), 50+(number*100));
    		break;
    	case 6: //SW
    		g.drawString("SW/ZW", 15+(col*100), 50+(number*100));    
    		break;
    	case 7: //W
    		g.drawString("W", 40+(col*100), 50+(number*100));    		
    		break;
    	case 8: //NW
    		g.drawString("NW", 35+(col*100), 50+(number*100));
    		break;
    	}
    }

public BufferedImage pointOfViewEyes(ArrayList<Integer> dirs, int xsize, int ysize) { 
	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, xsize,ysize);
	 g.setColor(Color.BLACK);g.setColor(Color.BLACK);
    
    //this.setSize(500,500);
    int ymax=drawSeperators(g,dirs.size(),ysize);
    g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22));
	if (cl.equals("English")) {
		g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
		g.drawString("The eyes indicate the movement direction", 5, ysize-10);
	} else {
		g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
		g.drawString("De ogen geven de beweegrichting", 5, ysize-10);
	}
    int xcount =0;
    int ycount=0;
    int prevdir=1;
    for (int i=0;i<dirs.size();i++) {
    	drawEyes(dirs.get(i),ycount,xcount, prevdir, g);
    	ycount++;
    	if (ycount>ymax) {
    		xcount++;
    		ycount=0;
    	}
    	prevdir=dirs.get(i);
    }
    return VectorImage;
}

    public void drawEyes(int dir, int number, int col, int prevdir, Graphics g){
		int newdir = dir-prevdir+1;
		if (newdir<1) {
			newdir+=8;
		}
		//if facing north arrows are correct. So if dir ==1 keep arrows.
		/* if NW, NW becomes 1, this means newdir=newdir-prevdir+1 ==2-2+1=1
		 * if NW, then N becomes 8, meaning 1-2+1=0, if smaller than 1 add 8
		 * 
		 */
		int[]xps,yps;
		Polygon p;
    	switch (newdir) {
    	case 1: // north
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(35+(col*100), 20+(number*100), 10, 10);
    		g.fillOval(55+(col*100), 20+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		
    		break;
    	case 2: //NE
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(55+(col*100), 25+(number*100), 10, 10);
    		g.fillOval(70+(col*100), 25+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;
    	
    	case 3: //E
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(60+(col*100), 40+(number*100), 10, 10);
    		g.fillOval(75+(col*100), 40+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;
    	case 4: //SE
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(55+(col*100), 55+(number*100), 10, 10);
    		g.fillOval(70+(col*100), 55+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;

    	case 5: //S
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(35+(col*100), 60+(number*100), 10, 10);
    		g.fillOval(55+(col*100), 60+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;
    	case 6: //SW
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(35+(col*100), 55+(number*100), 10, 10);
    		g.fillOval(20+(col*100), 55+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;
    	case 7: //W
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(30+(col*100), 40+(number*100), 10, 10);
    		g.fillOval(15+(col*100), 40+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);
    		break;
    	case 8: //NW
    		//g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.YELLOW);
    		g.fillOval(50+(col*100)-40, 50+(number*100)-40, 80, 80);
    		g.setColor(Color.BLACK);
    		g.fillOval(35+(col*100), 25+(number*100), 10, 10);
    		g.fillOval(20+(col*100), 25+(number*100), 10, 10);
    		xps = new int[] {50+(col*100),40+(col*100),30+(col*100),40+(col*100),50+(col*100),60+(col*100),70+(col*100),60+(col*100),50+(col*100)};
    		yps = new int[] {85+(number*100),80+(number*100),70+(number*100),75+(number*100),80+(number*100),75+(number*100),70+(number*100),80+(number*100),85+(number*100)};
    		p = new Polygon(xps,yps,9);
    		g.fillPolygon(p);

    		break;

    	}
    }
  public BufferedImage pointOfTime(ArrayList<Integer> dirs, int xsize, int ysize) { 
    	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = VectorImage.createGraphics();
    	g.setBackground(Color.WHITE);
    	g.clearRect(0, 0, xsize,ysize);
        g.setColor(Color.BLACK);
        int ymax=drawSeperators(g,dirs.size(),ysize);
        g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22)); 
        if (cl.equals("English")) {
        	g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
        	g.drawString("The hours indicate the direction of arrival", 5, ysize-10);
        } else {
        	g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
        	g.drawString("De uren geven aan waar je vandaan komt", 5, ysize-10);
        }
        //this.setSize(500,500);
        int xcount =0;
        int ycount=0;
        int prevdir=1;
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        for (int i=0;i<dirs.size();i++) {
        	drawpovTime(dirs.get(i),ycount,xcount, prevdir, g);
        	ycount++;
        	if (ycount>ymax) {
        		xcount++;
        		ycount=0;
        	}
        	prevdir=dirs.get(i);
        }
        return VectorImage;
     }

    	public void drawpovTime(int dir, int number, int col, int prevdir, Graphics2D g){
    		int newdir = dir-prevdir+1;
    		if (newdir<1) {
    			newdir+=8;
    		}
    		//if facing north arrows are correct. So if dir ==1 keep arrows.
    		/* if NW, NW becomes 1, this means newdir=newdir-prevdir+1 ==2-2+1=1
    		 * if NW, then N becomes 8, meaning 1-2+1=0, if smaller than 1 add 8
    		 * 
    		 */
    		Random rd = new Random();
    		int hour = rd.nextInt(24);
    		int thour=hour;
    		int min;
    		if (hour>11) {
    			thour=hour-12;
    		}
        	switch (newdir) {
        	case 1: // north
        		min =(thour)*5+30;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		
        		break;
        	case 2: //NE
        		min =(thour)*5+38;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	
        	case 3: //E
        		min =(thour)*5+45;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	case 4: //SE
        		min =(thour)*5+53;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;

        	case 5: //S
        		min =(thour)*5;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	case 6: //SW
        		min =(thour)*5+8;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	case 7: //W
        		min =(thour)*5+15;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	case 8: //NW
        		min =(thour)*5+23;
        		if (min>59) {
        			min=min-60;
        		}
        		if (min<10) {
        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
        		} else {
        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
        		}
        		break;
        	}
        }

	  public BufferedImage northTime(ArrayList<Integer> dirs, int xsize, int ysize) { 
    	    	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
    	    	Graphics2D g = VectorImage.createGraphics();
    	    	g.setBackground(Color.WHITE);
    	    	g.clearRect(0, 0, xsize,ysize);
    	        g.setColor(Color.BLACK);
    	        g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22)); 
    	        int ymax=drawSeperators(g,dirs.size(),ysize);
    	        
    	        if (cl.equals("English")) {
    	        	g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
    	        	g.drawString("The hours indicate North", 5, ysize-10);
    	        } else {
    	        	g.drawString("Begin richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
    	        	g.drawString("De uren geven het noorden aan", 5, ysize-10);
    	        }
    	        //this.setSize(500,500);
    	        
    	        int xcount =0;
    	        int ycount=0;
    	        int prevdir=1;
    	        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
    	        for (int i=0;i<dirs.size();i++) {
    	        	drawTime(dirs.get(i),ycount,xcount, prevdir, g);
    	        	ycount++;
    	        	if (ycount>ymax) {
    	        		xcount++;
    	        		ycount=0;
    	        	}
    	        	prevdir=dirs.get(i);
    	        }
    	        return VectorImage;
    	     }

    	    	public void drawTime(int dir, int number, int col, int prevdir, Graphics2D g){
    	    		//if facing north arrows are correct. So if dir ==1 keep arrows.
    	    		/* if NW, NW becomes 1, this means newdir=newdir-prevdir+1 ==2-2+1=1
    	    		 * if NW, then N becomes 8, meaning 1-2+1=0, if smaller than 1 add 8
    	    		 * 
    	    		 */
    	    		Random rd = new Random();
    	    		int hour = rd.nextInt(24);
    	    		int thour=hour;
    	    		int min;
    	    		if (hour>11) {
    	    			thour=hour-12;
    	    		}
    	        	switch (dir) {
    	        	case 1: // north
    	        		min =(thour)*5;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		
    	        		break;
    	        	case 2: //NE
    	        		min =(thour)*5+8;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	
    	        	case 3: //E
    	        		min =(thour)*5+15;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	case 4: //SE
    	        		min =(thour)*5+23;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;

    	        	case 5: //S
    	        		min =(thour)*5+30;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	case 6: //SW
    	        		min =(thour)*5+38;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	case 7: //W
    	        		min =(thour)*5+45;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	case 8: //NW
    	        		min =(thour)*5+53;
    	        		if (min>59) {
    	        			min=min-60;
    	        		}
    	        		if (min<10) {
    	        			g.drawString(hour+":0"+min, 20+(col*100), 50+(number*100));
    	        		} else {
    	        			g.drawString(hour+":"+min, 20+(col*100), 50+(number*100));	
    	        		}
    	        		break;
    	        	}
    	        }

    
    
public BufferedImage pointOfDirections (ArrayList<Integer> dirs, int xsize, int ysize) { 
	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, xsize,ysize);
    g.setColor(Color.BLACK);
    g.setFont(new Font("TimesRoman", Font.PLAIN, xsize/22)); 
    
    //this.setSize(500,500);
    int ymax=drawSeperators(g,dirs.size(),ysize);
	if (cl.equals("English")) {
		g.drawString("Start facing north, read from top to bottom", 5, ysize-ymax*6-10);
		g.drawString("The next arrow is seen from your eyepoint", 5, ysize-10);
	} else {
		g.drawString("Begint richting noorden, lees naar beneden", 5, ysize-ymax*6-10);
		g.drawString("De pijlen zijn getekend vanuit jouw oogpunt", 5, ysize-ymax*0-10);
	}

    int xcount =0;
    int ycount=0;
    int prevdir=1;
    for (int i=0;i<dirs.size();i++) {
    	drawpovArrow(dirs.get(i),ycount,xcount, prevdir, g);
    	ycount++;
    	if (ycount>ymax) {
    		xcount++;
    		ycount=0;
    	}
    	prevdir=dirs.get(i);
    }
    return VectorImage;
 }

	public void drawpovArrow(int dir, int number, int col, int prevdir, Graphics2D g){
		int newdir = dir-prevdir+1;
		if (newdir<1) {
			newdir+=8;
		}
		//if facing north arrows are correct. So if dir ==1 keep arrows.
		/* if NW, NW becomes 1, this means newdir=newdir-prevdir+1 ==2-2+1=1
		 * if NW, then N becomes 8, meaning 1-2+1=0, if smaller than 1 add 8
		 * 
		 */
    	switch (newdir) {
    	case 1: // north
    		g.drawLine(50+(col*100), 80+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 50+(col*100), 20+(number*100));
    		break;
    	case 2: //NE
    		g.drawLine(20+(col*100), 80+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 80+(col*100), 20+(number*100));
    		break;
    	
    	case 3: //E
    		g.drawLine(80+(col*100), 50+(number*100), 20+(col*100),50 +(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 80+(col*100), 50+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 80+(col*100), 50+(number*100));
    		break;
    	case 4: //SE
    		g.drawLine(20+(col*100), 20+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 80+(col*100), 80+(number*100));
    		break;

    	case 5: //S
    		g.drawLine(50+(col*100), 80+(number*100), 50+(col*100), 20+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 50+(col*100), 80+(number*100));
    		g.drawLine(80+(col*100), 50+(number*100), 50+(col*100), 80+(number*100));
    		break;
    	case 6: //SW
    		g.drawLine(20+(col*100), 80+(number*100), 80+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 20+(col*100), 80+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 20+(col*100), 80+(number*100));
    		break;
    	case 7: //W
    		g.drawLine(80+(col*100), 50+(number*100), 20+(col*100),50 +(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 20+(col*100), 50+(number*100));
    		g.drawLine(50+(col*100), 80+(number*100), 20+(col*100), 50+(number*100));
    		break;
    	case 8: //NW
    		g.drawLine(20+(col*100), 20+(number*100), 80+(col*100), 80+(number*100));
    		g.drawLine(20+(col*100), 50+(number*100), 20+(col*100), 20+(number*100));
    		g.drawLine(50+(col*100), 20+(number*100), 20+(col*100), 20+(number*100));
    		break;
    	}
    }

public BufferedImage stripDirections (ArrayList<Integer> dirs, ArrayList<ArrayList<Integer>> posdirs2, int xsize, int ysize) { 
	BufferedImage VectorImage = new BufferedImage(Math.max(xsize, 250), ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, Math.max(xsize, 250),ysize);
    g.setColor(Color.BLACK);
    g.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
    int prevdir=0;
//	Here the posdirs suddenly is empty?
    //System.out.println("posd"+posdirs2.size());

    for (int i=0;i<dirs.size();i++) {
    	drawStrip(dirs.get(i),prevdir,i, g, posdirs2);
    	prevdir=dirs.get(i);
    }
    return VectorImage;
}
	
	public void drawStrip(int dir, int prevdir, int i, Graphics2D g, ArrayList<ArrayList<Integer>> posdirs){
		// i the indicator of how many directions we have drawn. So this gives the height.
		Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        String[] dirs;
        if (cl.equals("English")) {
        	dirs = new String[] {"North","North-east","East","South-east","South","South-west","West","North-west"};
        } else {
        	dirs = new String[] {"Noord","Noord-oost","Oost","Zuid-oost","Zuid","Zuid-west","West","Noord-west"};
        }
		if (prevdir==0) {
			if (cl.equals("English")) {//only in the first direction
				g.drawString("Start in this direction" , 20, 665);
				g.drawString(dirs[dir-1], 20, 685);
				g.drawString("Read bottom to the top" , 20, 710);
				
				return;
			} else {
				g.drawString("Begin in deze richting" , 20, 665);
				g.drawString("Lees van onder naar boven" , 20, 710);
				g.drawString(dirs[dir-1], 20, 685);
				return;
			}
		}
		
		// now we need to see which directions are possible to determine the options
		int returndir = prevdir+4;
		if (returndir>8) {
			returndir-=8;
		} // this gives the direction to start checking from
		
		ArrayList<Integer> posdir = posdirs.get(i);
		int posdirsize=posdir.size();
		// this now holds all possible directions from the point where we came from;
		// And not the one returning
		
		int countright=0;
		int countleft=0;
		if (dir==returndir) {// this means return
			countleft=-1;
		}else if(dir<returndir) {// this means we need to check the connections on the right
			for (int j=dir+1;j<returndir;j++) {
				for (int k=0;k<posdir.size();k++)  {
					if (posdir.get(k)==j) {
						countright++;
					}
				}
			}
			countleft=posdirsize-countright-1;
		} else {
			for (int j=returndir+1;j<dir;j++) {
				for (int k=0;k<posdir.size();k++)  {
					if (posdir.get(k)==j) {
						countleft++;
					}
				}
			}
			countright=posdirsize-countleft-1;
		}
		//System.out.println("i: "+i+" dir:"+ dir+", returndir:"+returndir+", left:"+countleft+", right:"+countright);
		// Here we can draw the correct line
		int startheight=i;
		int startwidth=0;
		while (startheight>6) {
			startheight=startheight-6;
			startwidth++;
		}
		drawLines(g2,startheight,countleft,countright, startwidth);
		
    }
	
	public void drawLines (Graphics2D g2, int startheight, int countleft, int countright, int startwidth) {
		g2.drawLine(50+(100*startwidth), 700-(startheight*100), 50+(100*startwidth), 600-(startheight*100));
		switch (countleft) {
		case -1:
			g2.setColor(Color.RED);
			g2.drawLine(50+(100*startwidth), 700-(startheight*100), 50+(100*startwidth), 600-(startheight*100));
			g2.setColor(Color.BLACK);
			break;
		case 0:
			break;
		case 1:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 650-(startheight*100));
			break;
		case 2:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 680-(startheight*100));
			break;
		case 3:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 650-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 680-(startheight*100));
			break;
		case 4:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 640-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 660-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 680-(startheight*100));
			break;
		case 5:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 615-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 633-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 650-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 667-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 685-(startheight*100));
			break;
		case 6:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 610-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 625-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 640-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 660-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 675-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 20+(100*startwidth), 690-(startheight*100));
			break;
		}
		
		switch (countright) {
		case 0:
			break;
		case 1:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 650-(startheight*100));
			break;
		case 2:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 680-(startheight*100));
			break;
		case 3:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 650-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 680-(startheight*100));
			break;
		case 4:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 620-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 640-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 660-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 680-(startheight*100));
			break;
		case 5:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 615-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 633-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 650-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 667-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 685-(startheight*100));
			break;
		case 6:
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 610-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 625-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 640-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 660-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 675-(startheight*100));
			g2.drawLine(50+(100*startwidth), 650-(startheight*100), 80+(100*startwidth), 690-(startheight*100));
			break;
		}
	}

public BufferedImage drawRoute (ArrayList<hikePoint> hps, int xsize, int ysize) { 
	BufferedImage VectorImage = new BufferedImage(xsize, ysize, BufferedImage.TYPE_INT_RGB);
	Graphics2D g = VectorImage.createGraphics();
	g.setBackground(Color.WHITE);
	g.clearRect(0, 0, xsize,ysize);
    g.setColor(Color.RED);
    g.setStroke(new BasicStroke(2));
    g.drawOval(hps.get(0).p.x-5, hps.get(0).p.y-5, 10, 10);
    for (int i=0;i<hps.size()-1;i++) {
    	Point p1 =hps.get(i).p;
    	Point p2 =hps.get(i+1).p;
    	g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	
    }
    return VectorImage;
	}
}


