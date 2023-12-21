package hikingSkillTrainer;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HikingMapMaker implements ActionListener {
	int xwidth,ywidth,size1,rl1, rl2, hikesel;
	boolean selfdraw, randomhike, interactive1, saveroutes, cross;
	String choice, techchoice, dirchoice, ImageTitle;
	public hikePoint[][] hikemap;
	ArrayList<hikePoint> hps, maxhps; // this is the route
	ArrayList<Integer> directions, maxdirs;
	ArrayList<ArrayList<Integer>> posdirarray, maxposdirs;
	Graphics g;
	ArrayList<Integer> xs, ys;
	private static Object lock = new Object();
	JTextField savedir;
	JPanel jp;
	JLabel j1,j2,j3,j4,j5,j6,j7,j8,j9,j10,j11,j12,j13,j14;
	JComboBox<String> cb1, cb2, cb3;
	String[] hiketech = new String[] {"Arrow","Smiley","Strip card","Compass", "Vector", "Wind directions","Time", "View Time"};
	String[] hiketype = new String[] {"Draw a hike myself","Use a pre made image hike","Create random hike"};
	String[] hiketechD = new String[] {"Pijltjes","Oogjes","Strippenkaart","Kompas", "Vector", "Wind richtingen","Tijd", "Kijkpunt tijd"};
	String[] hiketypeD = new String[] {"Teken zelf een route","Gebruik een vooraf gemaakte plaatjes route","Genereer een willekeurige route"};
	String [] Language = new String[] {"English", "Nederlands"};
	JButton jb;
	String cl;
	
	public static void main (String ... args) {
		HikingMapMaker hmm = new HikingMapMaker();
		hikingSkillTrainer hst = new hikingSkillTrainer();
		// hps can only be made with hikemap present
		// hst size can only be judged on hps size.
		// however based on random number generated we know which picture is going to be made, and which size belongs to that
		hmm.getOptions();
		hst.cl=hmm.cl;
		if (hmm.xwidth>50||hmm.ywidth>50) {
			return;
		}
		ArrayList<String> drawpoints = new ArrayList<String>();
		HikeRoute chosenhike;
		if (hmm.selfdraw) {
			final hikeDrawer hd = new hikeDrawer();
			hd.heigth=hmm.ywidth;
			hd.width=hmm.xwidth;
			hd.size=hmm.size1;
			hd.drawButtons();
			int maxD=Math.max(hd.width, hd.heigth);
			hd.updateButtonSize(400/maxD);
			hd.maxD=maxD;
			Thread t = new Thread() {
		        public void run() {
		            synchronized(lock) {
		                while (hd.f.isVisible())
		                    try {
		                        lock.wait();
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    }
		                //System.out.println("Working now");
		                //System.out.println(hd.buttonlist.size());
		            }
		        }
		    };
		    t.start();
		    
		    hd.f.addWindowListener(new WindowAdapter() {
		        @Override
		        public void windowClosed(WindowEvent arg0) {
		            synchronized (lock) {
		                lock.notify();
		            }
		        }
		    });

		    try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// this needs to happen after the frame is closed
			
			drawpoints=hd.buttonlist;
			//System.out.println(drawpoints.size());
			// the button list contains all the points that are added as string y,x
			hmm.hps=hmm.makeImageRoute(hst, drawpoints);
			hmm.hikemap=hst.hikemap;
		} else if (hmm.hikesel==1) {
		// now we set the route as defined by the image we want to draw
			
			ArrayList<HikeRoute> hikes = hmm.readFileToRoutes(hst);
			
			Random rd = new Random();
			if (hikes.size()==0) {
				System.err.println("There are no routes yet, please draw routes first");
				return;
			} else if (hikes.size()==1){ // there is one hike
				hmm.hps = hikes.get(0).hps;
				hmm.ImageTitle=hikes.get(0).title;
				chosenhike=hikes.get(0);
			} else { // there are multiple hikes
				if (hmm.randomhike) {
					int randomint =rd.nextInt(hikes.size());
					hmm.hps = hikes.get(randomint).hps;
					hmm.ImageTitle=hikes.get(randomint).title;
					chosenhike=hikes.get(randomint);
				} else {
					String [] titles = new String[hikes.size()];
					ArrayList<String> hiketitles= new ArrayList<String>();
					for (int k=0;k<hikes.size();k++) {
						titles[k]=hikes.get(k).title;
						hiketitles.add(hikes.get(k).title);
					}
					Arrays.sort(titles);
					JPanel jp = new JPanel();
					JComboBox<String> cb2 = new JComboBox<String>(titles);
					jp.add(new JLabel("Which hike do you want"));
					jp.add(cb2);
					GridLayout gl = new GridLayout(2,2,20,5);
					jp.setLayout(gl);
					
					int result = JOptionPane.showConfirmDialog(null, jp, "Please choose the name of digital hike", JOptionPane.OK_CANCEL_OPTION);
					if (result == JOptionPane.OK_OPTION) {
						String ctitle=titles[cb2.getSelectedIndex()]; // get the title of the one in the loop
						//System.out.println(ctitle);
						int index = hiketitles.indexOf(ctitle);
						hmm.hps=hikes.get(index).hps;
						hmm.ImageTitle=hikes.get(index).title;
						chosenhike=hikes.get(index);
				    } else {
				    	System.err.println("You did not choose a hike. Please confrim your choice");
				    	return;
				    }
				}
			}
			
			// we need to check the connections, and fix them if needed
			int maxx=0;
			int maxy=0;
			for (int i=0;i<hmm.hps.size();i++) {
				int cx= hmm.hps.get(i).x;
				int cy=hmm.hps.get(i).y;
				if (cx>maxx) {
					maxx=cx;
				}
				if (cy>maxy) {
					maxy=cy;
				}
			}
			hst.xwidth=maxx+1;
			hst.ywidth=maxy+1;
			hst.size1=hmm.size1;
			hst.hikemap=hst.makeHikeMap();
			hmm.hikemap=hst.hikemap;
			// and update the hps
			hmm.hps=chosenhike.updateHikeRoute(hst.hikemap);
		} else {// we need to initiate the hikingskilltrainer
			//hst.chooseOptions(); // replace by options set here
			hst.xwidth=hmm.xwidth;
			hst.ywidth=hmm.ywidth;
			hst.size1=hmm.size1;
			hst.rl1=hmm.rl1;
			hst.rl2=100;
			hst.makeHikeMap();
			hst.cross1=hmm.cross;
			hmm.hikemap=hst.hikemap;
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
	        hmm.hps =hst.maxhps;
	        hst.directions=hst.maxdirs;
	        hmm.directions=hst.maxdirs;
	        hst.posdirarray=hst.maxposdirs;
	        hmm.posdirarray=hst.maxposdirs;
	        
	        // we need to make a hps
	        
		}
		// we need to set the hikemap size needed
		
		if (hmm.hps.size()==0){
			System.err.println("Your hike is 0 points long, please try again");
	    	return;
		}
		hmm.hps=hmm.checkConnections(hmm.hps, hst.hikemap);
		hmm.makeRoute(hmm.hps, hst);
		hmm.posdirarray=hmm.getPosDirArray(hmm.hps, hst);
		// this is completely fine, but moving to the hikingskilltrainer we lose things?
		hst.posdirarray=hmm.posdirarray;
/*		for (int k=0;k<hmm.posdirarray.size();k++) {
			ArrayList<Integer> posd = hmm.posdirarray.get(k);
			System.out.println("cposdirs"+posd.size());
		}*/
		hst.hps=hmm.hps;
		// now we have to add the point
		// we need to make the directions
		
		
		//{"Arrow","Smiley","Strip card","Compass", "Vector", "Wind directions","Time", "View Time"};
		BufferedImage route ;
		int imsize=((int)Math.sqrt(hmm.directions.size())+1)*100;
		if (hmm.techchoice=="Smiley") {
			route = hst.pointOfViewEyes(hmm.directions, imsize, imsize+50);
		} else if (hmm.techchoice=="Strip card") {
			route = hst.stripDirections(hmm.directions, hmm.posdirarray, (hmm.directions.size()/6+1)*100,720);
		} else if (hmm.techchoice=="Compass") {
			route = hst.drawCompas(hmm.directions, imsize, imsize+50);
		} else if (hmm.techchoice=="Vector") {
			route = hst.drawArrows(hmm.directions, imsize, imsize+50);
		} else if (hmm.techchoice=="Arrow") {
			route = hst.pointOfDirections(hmm.directions, imsize, imsize+50);
		} else if (hmm.techchoice=="Wind directions") {
			route = hst.writeDirections(hmm.directions, imsize, imsize+50);
		} else if (hmm.techchoice=="View Time") {
			route = hst.pointOfTime(hmm.directions, imsize, imsize+50);
		} else {
			route= hst.northTime(hmm.directions, imsize, imsize+50);
		}
		
		BufferedImage map = hst.makeMap();
		BufferedImage solution = hst.drawRoute(hmm.hps, hst.xwidth*hst.size1, hst.ywidth*hst.size1);
		BufferedImage comp =hst.makeCompleteRoute(map, route);
		if (hmm.saveroutes) {
			hst.exportImage(hmm.dirchoice +"\\"+ hmm.ImageTitle + " solution", solution);
			hst.exportImage(hmm.dirchoice +"\\"+ hmm.ImageTitle +" puzzle", comp);
		}
		// here we can activate the section to do them interactively on the computer
		if (hmm.interactive1) {
			new InteractiveHikeSolver(comp, map, hmm.hps, hmm.size1, hmm.hikemap);
		}
		if (hmm.selfdraw && hmm.saveroutes) {
			hmm.writeRouteToFile(hmm.xs, hmm.ys, hmm.ImageTitle);
		}
	}
	
	public boolean makeRoute(ArrayList<hikePoint> hps, hikingSkillTrainer hst) {
		directions = new ArrayList<Integer>();
		for (int i=1;i<hps.size();i++) {
			hikePoint np = hps.get(i);
			hikePoint cp = hps.get(i-1);
			if (cp.x==np.x) {
				if (cp.y>np.y) { //N
					directions.add(1);
				} else { //S
					directions.add(5);
				}
			} else if (cp.x>np.x){// going west
				if (cp.y==np.y) {
					directions.add(7);
				} else if (cp.y>np.y) { // NW
					directions.add(8);
				} else { //SW
					directions.add(6);
				}
			} else { // going east
				if (cp.y==np.y) {
					directions.add(3);
				} else if (cp.y>np.y) { // NE
					directions.add(2);
				} else { //SW
					directions.add(4);
				}
			}
			//System.out.print(cp.y+", "+cp.x+", dir: "+ prevdir+ " | ");
		}
		//System.out.println("");
		return true;
	}

	
	public void getOptions() {
		cl="English";

		jp = new JPanel();
		JTextField xw = new JTextField("10",2);
		JTextField yw = new JTextField("10",2);
		JTextField size = new JTextField("51",2);
		JTextField rl = new JTextField("10",2);
		JTextField imtitle = new JTextField("New route",2);
		JCheckBox interactive = new JCheckBox("",false);
		JCheckBox random = new JCheckBox("",false);
		JCheckBox saveroute= new JCheckBox("",false);
		JCheckBox crossroute= new JCheckBox("",false);
		jb = new JButton("Select folder");
		jb.addActionListener(this);
		// Done are Arrow, Vector , Smiley, Strip card, Wind directions, compass, time and povtime
		cb1 = new JComboBox<String>(Language);
		
		cb2 = new JComboBox<String>(hiketech);
		cb3 = new JComboBox<String>(hiketype);
		String userdir = System.getProperty("user.home");
		savedir= new JTextField(userdir+"\\Documents\\Hikes",2);
		File f = new File(userdir+"\\Documents\\Hikes");
		if (!f.exists()) {
			f.mkdir();
		}
		j1 = new JLabel("Choose your language");
		j2 = new JLabel("Number of columns (max 50):");
		j3 = new JLabel("Number of rows (max 50):");
		j4 = new JLabel(("Size of each block in pixels"));
		j5 = new JLabel("How do you want to create your hike");
		j6 = new JLabel("Hike length in steps (if applicable):");
		j7 = new JLabel("What is the title of your image (if applicable)");
		j8 = new JLabel("Do you want a random hike (if applicable)");
		j9 = new JLabel("What type of hike do you want");
		j10 = new JLabel("Directory for program output");
		j11 = new JLabel("Click here to change the folder destination");
		j12 = new JLabel("Do you want to save the new hike?");
		j13 = new JLabel("Do you want to solve the hike on the computer");
		j14 = new JLabel("Is a random hike allowed to cross itself?");

		cb1.addActionListener(this);
		
		
		GridLayout gl = new GridLayout(14,2,20,5);
		jp.setLayout(gl);
		jp.add(j1);
		jp.add(cb1);
		jp.add(j2);
		jp.add(xw);
		jp.add(j3);
		jp.add(yw);
		jp.add(j4);
		jp.add(size);
		jp.add(j5);
		jp.add(cb3);
		jp.add(j6);
		jp.add(rl);
		jp.add(j7);
		jp.add(imtitle);
		jp.add(j8);
		jp.add(random);
		jp.add(j9);
		jp.add(cb2);
		jp.add(j10);
		jp.add(savedir);
		jp.add(j11);
		jp.add(jb);
		jp.add(j12);
		jp.add(saveroute);
		jp.add(j13);
		jp.add(interactive);
		jp.add(j14);
		jp.add(crossroute);
		
		int result = JOptionPane.showConfirmDialog(null, jp, "Please choose the options you want for the digital hike", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			xwidth=Integer.parseInt(xw.getText());
			ywidth=Integer.parseInt(yw.getText());
			rl1=Integer.parseInt(rl.getText());
			size1=Integer.parseInt(size.getText());
			techchoice=hiketech[cb2.getSelectedIndex()];
			interactive1=interactive.isSelected();
			saveroutes=saveroute.isSelected();
			hikesel=cb3.getSelectedIndex();
			cross=crossroute.isSelected();
			if (hikesel==0) {
				this.selfdraw=true;
			} else if (hikesel==1) {
				this.randomhike=random.isSelected();
			}
			dirchoice=savedir.getText();
			ImageTitle=imtitle.getText();
	      } else {
	    	  xwidth=51;
	    	  return;
	    	  
	      }
		if (xwidth>50||ywidth>50) {
			return;
		}
		
	}
	
	public ArrayList<ArrayList<Integer>> getPosDirArray(ArrayList<hikePoint> hps, hikingSkillTrainer hst){
		ArrayList<ArrayList<Integer>> posdirarray=new ArrayList<ArrayList<Integer>>();
		int previdr =0;
		for (int i=0;i<hps.size()-1;i++) {
			ArrayList<Integer> posdirs= hst.getPosDirs(hps.get(i));
			// this needs to be done if we dont want to return
			// after the first point we need to check this
			if (i>0) {
				if ((directions.get(i)==previdr+4||directions.get(i)==previdr-4)) {
						// this means we move back towards where we came from
				} else { // we move in another direction so block the return for the strip card.
					posdirs=hst.blockReturn(posdirs, previdr);
				}
			}
			previdr=directions.get(i);
			posdirarray.add(posdirs);
		}
		return posdirarray;
	}
	
	public ArrayList<hikePoint> checkConnections(ArrayList<hikePoint> hps, hikePoint[][] hikemap) {
		for (int i=0;i<hps.size()-1;i++) {
			hikePoint cp = hps.get(i);
			int cx=cp.x;
			int cy=cp.y;
			int nx=hps.get(i+1).x;
			int ny=hps.get(i+1).y;
			if (cx==nx) {// north or south
				if (cy>ny) {//north
					hikemap[cy][cx].N=true;
					hikemap[ny][nx].S=true;
				} else {
					hikemap[cy][cx].S=true;
					hikemap[ny][nx].N=true;
				}
			} else if (cx<nx) {// going east
				if (cy==ny) {// east
					hikemap[cy][cx].E=true;
					hikemap[ny][nx].W=true;
				} else if (cy<ny){// southeast
					hikemap[cy][cx].SE=true;
					hikemap[ny][nx].NW=true;
				} else {
					hikemap[cy][cx].NE=true;
					hikemap[ny][nx].SW=true;
				}
			} else {
				if (cy==ny) {// west
					hikemap[ny][nx].E=true;
					hikemap[cy][cx].W=true;
				} else if (cy<ny){// southwest
					hikemap[ny][nx].NE=true;
					hikemap[cy][cx].SW=true;
				} else {
					hikemap[ny][nx].SE=true;
					hikemap[cy][cx].NW=true;
				}
			}
		}
		
		return hps;
	}
	
	public ArrayList<hikePoint> makeImageRoute(hikingSkillTrainer hst, ArrayList<String> points){
		ArrayList<hikePoint> hps =new ArrayList<hikePoint>();
		int maxx=0;
		int maxy=0;
		xs=new ArrayList<Integer>();
		ys=new ArrayList<Integer>();
		for (int i=0;i<points.size();i++) {
			//System.out.println(points.get(i));
			String[] ps = points.get(i).split(",");
			int cx =Integer.parseInt(ps[0]);
			int cy =Integer.parseInt(ps[1]);
			xs.add(cx);
			ys.add(cy);
			if (cx>maxx) {
				maxx=cx;
			}
			if (cy>maxy) {
				maxy=cy;
			}
		}
		// now we can initiate the hst
		hst.xwidth=maxx+1;
		hst.ywidth=maxy+1;
		hst.size1=size1;
		hst.hikemap=hst.makeHikeMap();
		hikePoint[][] hikemap=hst.hikemap;
		
		for (int i=0;i<xs.size();i++) {
			hps.add(hikemap[ys.get(i)][xs.get(i)]);
		}
		return hps;
	}
	
	public void writeRouteToFile(ArrayList<Integer> xs, ArrayList<Integer> ys, String title) {
		// here we save all the coordinates to a new file
		File f = new File(dirchoice+"\\ImageRoutes.txt");
		
		try {
			if (f.exists()) {
				// we need to append data
				FileWriter fw = new FileWriter(f,true);
				BufferedWriter bf = new BufferedWriter(fw);
				bf.write(ImageTitle);
				bf.newLine();
				bf.write(xs.toString());
				bf.newLine();
				bf.write(ys.toString());
				bf.newLine();
				bf.close();
				System.out.println("writing hikes"+f.toString());
			} else {
				FileWriter fw = new FileWriter(f,true);
				BufferedWriter bf = new BufferedWriter(fw);
				bf.write(ImageTitle);
				bf.newLine();
				bf.write(xs.toString());
				bf.newLine();
				bf.write(ys.toString());
				bf.newLine();
				bf.close();
				System.out.println("writing hikes"+f.toString());
			}
			// println for Title, title'
			// println for XS, x,x,x,x,x,x
			// println for YS, y,y,y,y,y
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<HikeRoute> readFileToRoutes(hikingSkillTrainer hst) {
		ArrayList<HikeRoute> hikeroutes= new ArrayList<HikeRoute>();
		try {
			BufferedReader bufferedReader = new BufferedReader( new FileReader(dirchoice+"\\ImageRoutes.txt"));
			Stream<String> lines = bufferedReader.lines();
			Object [] strings = lines.toArray();
			
			for (int i=0;i<strings.length;i+=3) {
				//System.out.println(strings[i].toString());
				//System.out.println(strings[i+1].toString());
				//System.out.println(strings[i+2].toString());
				HikeRoute chike = new HikeRoute(strings[i].toString(),strings[i+1].toString(),strings[i+2].toString(),hst, size1);
				hikeroutes.add(chike);
				
			}
			bufferedReader.close();
			// now we create an arraylist of routes. to which we add all routes in the file
			// we need to transform it into route in a similar way as makeimageroute
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hikeroutes;
		// parseline, for title
		// parseline, for xs
		// parseline, for ys
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("comboBoxChanged")) {
			if (cb1.getSelectedIndex()==0) {
				j1.setText("Choose your language");
				j2.setText("Number of columns (max 50):");
				j3.setText("Number of rows (max 50):");
				j4.setText("Size of each block in pixels");
				j5.setText("How do you want to create your hike?");
				j6.setText("Hike length in steps (if applicable):");
				j7.setText("What is the title of your image (if applicable)?");
				j8.setText("Do you want a random hike (if applicable)?");
				j9.setText("What type of hike do you want?");
				j10.setText("Directory for program output");
				j11.setText("Click here to change the folder destination");
				j12.setText("Do you want to save the new hike?");
				j13.setText("Solve the hike on the computer?");
				j14.setText("Is a random hike allowed to cross itself?");
				
				DefaultComboBoxModel<String> model3 = new DefaultComboBoxModel<>( hiketype);
				DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<>( hiketech);
				cb3.setModel(model3);
				cb2.setModel(model2);
				cl="English";
				jb.setText("Select folder");
				
			} else {
				j1.setText("Kies je taal");
				cl="Nederlands";
				j2.setText("Aantal kolommen (max 50):");
				j3.setText("Aantal rijen (max 50):");
				j4.setText("Ruimte tussen de punten in pixels");
				j5.setText("Hoe wil je je hike/route maken?");
				j6.setText("Aantal stappen in de route (als toepasselijk):");
				j7.setText("Wat is de naam van de route (als toepasselijk)");
				j8.setText("Wil je een willekeurige route (als toepasselijk)");
				j9.setText("Wat voor soort routetechniek wil je?");
				j10.setText("Waar moeten de routes worden opgeslagen?");
				j11.setText("Klik hier om de folder te selecteren");
				j12.setText("Wil je de nieuwe hike opslaan?");
				j13.setText("Wil je de hike op de computer oplossen?");
				j14.setText("Mag een willekeurige route zichzelf kruisen?");
				DefaultComboBoxModel<String> model3 = new DefaultComboBoxModel<>( hiketypeD);
				DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel<>( hiketechD);
				cb3.setModel(model3);
				cb2.setModel(model2);
				jb.setText("Kies een folder");
			}
		} else {
			String userdir = System.getProperty("user.home");
			File f = new File(userdir+"\\Documents\\Hikes");
			JFileChooser jfc = new JFileChooser(f);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = jfc.showOpenDialog(jp);
			if (result == JFileChooser.APPROVE_OPTION) {
				//System.out.println(jfc.getSelectedFile().toString());
				savedir.setText(jfc.getSelectedFile().toString());
			} 
		}
	}
}
