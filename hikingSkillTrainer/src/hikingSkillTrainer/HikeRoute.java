package hikingSkillTrainer;

import java.util.ArrayList;

public class HikeRoute {
	ArrayList<Integer> xs,ys;
	String title;
	ArrayList<hikePoint> hps;
	int maxx,maxy;
	
	HikeRoute(String title, String xstring, String ystring, hikingSkillTrainer hst, int size1){
		this.title=title;
		hps=new ArrayList<hikePoint>();
		maxx=0;
		maxy=0;
		xs=new ArrayList<Integer>();
		ys=new ArrayList<Integer>();
		xstring=xstring.replace("[", "");
		xstring=xstring.replace("]", "");
		xstring=xstring.replace(" ", "");
		ystring=ystring.replace(" ", "");
		ystring=ystring.replace("]", "");
		ystring=ystring.replace("[", "");
		String [] xarray =xstring.split(",");
		String [] yarray =ystring.split(",");
		for (int i=0;i<xarray.length;i++) {
			//System.out.println(xarray[i]+","+yarray[i]);
			int cx =Integer.parseInt(xarray[i]);
			int cy =Integer.parseInt(yarray[i]);
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
		
	}
	
	
	public ArrayList<hikePoint> updateHikeRoute(hikePoint[][] hikemap) {
		hps=new ArrayList<hikePoint>();
		//System.out.println(hikemap.length);
		for (int i=0;i<xs.size();i++) {
			hps.add(hikemap[ys.get(i)][xs.get(i)]);
		}
		return hps;
	}
}
