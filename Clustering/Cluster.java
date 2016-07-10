import java.util.ArrayList;
import java.util.Comparator;

public class Cluster implements Comparator<Cluster>, Comparable<Cluster>{
	ArrayList<Point> list = new ArrayList<Point>();
	private Point center;
	public double SSE = 0.0;
	
	public Cluster(Point a) { //cluster class which stores points, and a center value
		center = a;
	}

	public Point getCentroid() {
		return center;
	}
	public void setCentroid(Point a) {
		center = a;
	}
	public void addPoint(Point a) {
		list.add(a);
	}
	public void clearPoints() {
		list.clear();
	}
	public void newCenter(Point b) {
		center = b;
	}
	public String toString() {
		return list.toString();
	}
	public void setSSE() { //compute the sse
		double temp = 0.0;
		for (int i = 0; i < list.size(); i++) {
			Point t = list.get(i);
			ArrayList<Double> pa = t.getList();
			ArrayList<Double> pb = center.getList();
			double sum = 0.0;
			for (int j = 0; j < pa.size(); j++) {
				sum += Math.pow(pa.get(j)- pb.get(j), 2);
			}
			temp += sum;
		}
		SSE = temp;
	}


	@Override
	public int compare(Cluster one, Cluster two) {
		double v1 = one.SSE;
		double v2 = two.SSE;
		if (v1 < v2) 
			return 1;
		else if (v1 == v2)
			return 0;
		else return -1;
	}

	@Override
	public int compareTo(Cluster two) {
		double v1 = this.SSE;
		double v2 = two.SSE;
		if (v1 < v2) 
			return 1;
		else if (v1 == v2)
			return 0;
		else return -1;
	}
}
