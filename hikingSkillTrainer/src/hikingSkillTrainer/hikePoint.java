package hikingSkillTrainer;

import java.awt.Point;

public class hikePoint {
		boolean N,NE,E,SE,S,SW,W,NW;
		Point p;
		int x,y;
		
		hikePoint(){
			N=NE=E=SE=S=SW=W=NW=false;
		}
		
		public void setPoint(Point p) {
			this.p=p;
		}
	}
