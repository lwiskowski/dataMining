import java.util.ArrayList;

import javax.lang.model.element.NestingKind;

public class Group {
	Point p;
	String assigned = "";

	public Group(Point a) {
		p = a;
	}

	ArrayList<Point> neighbors = new ArrayList<Point>(); //knearest neigbors
	double density = 0;
	public double outlierscore = 0;

	public void addNeighbor(Point a) {
		neighbors.add(a);
	}
	public boolean correctlyClassified() {
		if(p.getCV().equals(assigned))
			return true;
		else
			return false;
	}
	public void setDensity() {
		density = findDensity();
	}

	public double getDensity() {
		return density;
	}

	public void setScore(double a) {
		outlierscore = a;
	}
	public void SetNearest(ArrayList<Point> p) {
		neighbors = p;
	}


	public double findDensity() {
		//density of a group
		double temp = 0.0;
		for (int i = 0; i < neighbors.size(); i++) { //calculate desnity using point and k nearest neighbors
			Point t = neighbors.get(i);
			ArrayList<Double> pa = t.getList();
			ArrayList<Double> pb = p.getList();
			double sum = 0.0;
			for (int j = 0; j < pa.size(); j++) {
				sum += Math.pow(pa.get(j)- pb.get(j), 2); //use euclidian distance
			}
			temp += Math.sqrt(sum);
		}
		return ((neighbors.size())/temp);
	}

	public String toString() {
		String temp = p.pointid + " density " + density+ " anomaly Score " + outlierscore;
		return temp;

	}
}
