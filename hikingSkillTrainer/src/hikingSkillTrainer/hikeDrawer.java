package hikingSkillTrainer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

public class hikeDrawer implements ActionListener{
	ArrayList<String> buttonlist=new ArrayList<String>();
	JFrame f= new JFrame();
	JPanel jp = new JPanel();
	JDialog jd = new JDialog();
	Graphics2D g = (Graphics2D)jp.getGraphics();
	ArrayList<JToggleButton> pressed= new ArrayList<JToggleButton>();
	int heigth, width, maxD, offset, inset, buttonsize, size;
	JToggleButton [][] buttons;
	
	public static void main(String[] args) {
		final hikeDrawer hd = new hikeDrawer();
		hd.heigth=3;
		hd.width=3;
		hd.size=51;
		hd.drawButtons();
		hd.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void updateButtonSize(int size) {
		for (int i=0;i<buttons.length;i++) {
			for (JToggleButton j:buttons[i]) {
				j.setPreferredSize(new Dimension(size,size));
			}
		}
	}
	
	public void drawButtons() {
		jp= new JPanel();
		jp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		buttons = new JToggleButton[width][heigth];
		maxD=Math.max(width, heigth);
		buttonsize=Math.max(400/maxD,10);
		offset=Math.max(200/maxD,5);
		int fsizex =400/maxD*width+inset*2*width+100;
		int fsizey =400/maxD*heigth+inset*2*heigth+200; 
		int insety = (Toolkit.getDefaultToolkit().getScreenSize().width-500)/maxD/2;
		int insetx = (Toolkit.getDefaultToolkit().getScreenSize().height-600)/maxD/2;
		
		inset=Math.min(insetx,insety);
		c.insets=new Insets(inset,inset,inset,inset);
		for (int i=0;i<width;i++) {
			c.gridx = i;
			for (int j=0;j<heigth;j++) {
				c.gridy = j;
				JToggleButton jb = new JToggleButton(i+","+j);
				buttons[i][j]=jb;
				jb.setSelected(false);
				jb.setContentAreaFilled(false);
				jb.setOpaque(true);
				jb.setBackground(Color.WHITE);
				//jb.setEnabled(true);
				jb.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						JToggleButton jb2= (JToggleButton)e.getSource();
						String cmnd = e.getActionCommand();
						String [] loc = cmnd.split(",");
						int xloc=Integer.parseInt(loc[1]);
						int yloc=Integer.parseInt(loc[0]);
						buttonlist.add(e.getActionCommand());
						pressed.add(jb2);
						jb2.setSelected(true);
						if (buttonlist.size()>1) { // this draws the lines from one to the next
							JToggleButton jb3=pressed.get(pressed.size()-2); // get the previous button
							g = (Graphics2D)jp.getGraphics();
							g.setColor(Color.BLACK);
							g.drawLine(jb2.getLocation().x+offset,jb2.getLocation().y+offset,jb3.getLocation().x+offset,jb3.getLocation().y+offset);
						}
						for (int k=0;k<width;k++) {
							for (int l=0;l<heigth;l++) {
								
								if (k-yloc==0&&l-xloc==0) {
									JToggleButton jbc = buttons[k][l];
									jbc.setBackground(Color.GREEN);
								} else if (Math.abs(k-yloc)<2 && Math.abs(l-xloc)<2) {
									JToggleButton jbc = buttons[k][l];
									jbc.setBackground(Color.LIGHT_GRAY);
									buttons[k][l].setEnabled(true);
								}else {
									buttons[k][l].setEnabled(false);
									buttons[k][l].setBackground(Color.WHITE);
	
								}
							} 
						}
						buttons[yloc][xloc].setEnabled(false);
	
						
					} 
					} );
				//jb.setPreferredSize(new Dimension(400/maxD,400/maxD));
				jb.setSize(new Dimension(400/maxD,400/maxD));
				jp.add(jb,c);
			}
		}
		
		JToggleButton jbreset= new JToggleButton("Reset");
		jbreset.addActionListener(this); 
		
		JToggleButton jbfinish = new JToggleButton("Finished");
		jbfinish.addActionListener(this); 
		JScrollPane scrPane = new JScrollPane(jp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		f.add(scrPane);
		c.gridx=0;
		c.gridwidth=width/2;
		c.gridy=heigth+1;
		jp.add(jbreset,c);
		c.gridx=width/2;
		c.gridwidth=width/2;
		c.gridy=heigth+1;
		jp.add(jbfinish,c);
		updateButtonSize(Math.max(400/maxD,10));
		
	//	updateButtonSize(20);
		fsizex=	Toolkit.getDefaultToolkit().getScreenSize().width;
		fsizey=	Toolkit.getDefaultToolkit().getScreenSize().height;
		
		f.setSize(fsizex,fsizey);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// 
		f.setVisible(true);
		g=(Graphics2D)f.getGraphics();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JToggleButton jb2= (JToggleButton)e.getSource();
		if (jb2.getActionCommand().equals("Reset")) {
			f.dispose();
			buttonlist=new ArrayList<String>();
			pressed= new ArrayList<JToggleButton>();
			for (int i=0;i<width;i++) {
				for (int j=0;j<heigth;j++) {
					buttons[i][j].setSelected(false);
					buttons[i][j].setContentAreaFilled(false);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBackground(Color.WHITE);
					buttons[i][j].setEnabled(true);
					buttons[i][j].setSize(new Dimension(400/maxD,400/maxD));
				}
			}
			
			f.setVisible(true);
			g=(Graphics2D)f.getGraphics();
		} else if (jb2.getActionCommand().equals("Finished")) {
			f.dispose();
			// here we need to give feedback to the hikingmapmaker.
			//buttonlist=new ArrayList<String>();
			
		}
	}		
		
		
}
