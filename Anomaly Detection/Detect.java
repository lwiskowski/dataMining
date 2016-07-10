import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.xml.ws.Dispatch;

public class Detect {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String readfile = "toy-anom-attr.txt"; //process input
		Scanner kb = new Scanner(new File(readfile)); //read in attirbutes file
		String x = kb.nextLine();
		boolean hasCV = false;
		String[] classValues = new String[0];
		Attributes datainfo = new Attributes();
		while (kb.hasNextLine()) {
			String t = kb.nextLine();
			String[] str = t.split("\\s+");
			if (str[0].equals("class")) {
				classValues = str;
				hasCV = true;
			}
			datainfo.append(str);
		}
		kb.close();
		String file2 = "toy-anom.txt";
		Scanner sc = new Scanner(new File(file2));
		ArrayList<Point> points = new ArrayList<Point>(); //read in infofile
		while(sc.hasNextLine()) {
			String temp = sc.nextLine();
			String[] str = temp.split("\\s+");

			Point p = new Point(str[0]);
			for (int i = 1; i < str.length; i++) {
				if (i == str.length-1 && hasCV) {
					p.addClassVal(str[i]);
				}
				else {
					p.addValue(Double.parseDouble(str[i]));
				}
			}
			points.add(p);
		}
		sc.close();
	//	System.out.println(points.toString());
		int k = Integer.parseInt(args[0]);
		double threshold = Double.parseDouble(args[1]);
		ArrayList<Group> output = LOF(points, k);

		//System.out.println(output.toString());
		//		for (int i = 0; i < output.size(); i++) {
		//			System.out.println(output.get(i).toString());
		//		}
		for (int i = 0; i < output.size(); i++) {
			if(threshold < output.get(i).outlierscore) {
				output.get(i).assigned = classValues[2];
			} else {
				output.get(i).assigned = classValues[1];
			}
		}
		Collections.sort(output, new Comparator<Group>() {
			public int compare(Group one, Group two) {
				double v1 = one.outlierscore;
				double v2 = two.outlierscore;
				if (v1 < v2) 
					return 1;
				else if (v1 == v2)
					return 0;
				else return -1;
			}
		});
		System.out.println("Toy data set, K value: "+k);
		System.out.println();
		for (int i = 0; i < output.size(); i++) {
			System.out.println(output.get(i).toString());
		}
		System.out.println();
		for (int i = 0; i < output.size(); i++) {
			if(threshold < output.get(i).outlierscore) {
				output.get(i).assigned = classValues[1];
			} else {
				output.get(i).assigned = classValues[2];
			}
		}
		System.out.println("Accuracy: "+Accuracy(output)); //calculate true positive and false positve rate
		System.out.println("True Positive: "+tpr(output, classValues[1]));
		System.out.println("False Positive: "+fpr(output, classValues[2]));
		System.out.println();
		readfile = "cancer-attr.txt";
		Scanner kc = new Scanner(new File(readfile)); //read in attirbutes file
		x = kc.nextLine();
		hasCV = false;
		classValues = new String[0];
		datainfo = new Attributes();
		while (kc.hasNextLine()) {
			String t = kc.nextLine();
			String[] str = t.split("\\s+");
			if (str[0].equals("class")) {
				classValues = str;
				hasCV = true;
			}
			datainfo.append(str);
		}
		kc.close();
		file2 = "cancer.txt";
		Scanner sd = new Scanner(new File(file2));
		points = new ArrayList<Point>(); //read in infofile
		while(sd.hasNextLine()) {
			String temp = sd.nextLine();
			String[] str = temp.split("\\s+");

			Point p = new Point(str[0]);
			for (int i = 1; i < str.length; i++) {
				if (i == str.length-1 && hasCV) {
					p.addClassVal(str[i]);
				}
				else {
					p.addValue(Double.parseDouble(str[i]));
				}
			}
			points.add(p);
		}
		sd.close();
		System.out.println("Experiment 1:");
		System.out.println();
		output = LOF(points, 5);
		Collections.sort(output, new Comparator<Group>() {
			public int compare(Group one, Group two) {
				double v1 = one.outlierscore;
				double v2 = two.outlierscore;
				if (v1 < v2) 
					return 1;
				else if (v1 == v2)
					return 0;
				else return -1;
			}
		});
		System.out.println();
		for (int i = 0; i < output.size(); i++) {
			System.out.println(output.get(i).toString());
		}
		System.out.println();
		System.out.println("Experiment 2:");
		System.out.println();
		for (int j = 3; j <= 10; j++) {		//run experiment 2 for all k values 3-10
			output = LOF(points, j);
			for (int i = 0; i < output.size(); i++) {
				if(threshold < output.get(i).outlierscore) {
					output.get(i).assigned = classValues[2];
				} else {
					output.get(i).assigned = classValues[1];
				}
			}
			System.out.println("k: "+j+" ");
			System.out.println("AUC is: "+auc(output, j, classValues[1], classValues[2]));
		}


	}
	public static double tpr (ArrayList<Group> g, String pos) { //calculate the true positive rate
		double tp = 0;
		double fn = 0;
		for (int i = 0; i < g.size(); i++) {
			if(g.get(i).p.getCV().equals(pos)) {
				if(g.get(i).correctlyClassified()) { //update counts for tp and false negative
					tp++;
				} else {
					fn++;
				}
			}
		}
		return tp/(tp+fn);
	}

	public static double fpr (ArrayList<Group> g, String neg) { //calcualte the false posirve rate
		double tn = 0;
		double fp = 0;
		for (int i = 0; i < g.size(); i++) {
			if (g.get(i).p.getCV().equals(neg)) { //update count
				if(g.get(i).correctlyClassified()) {
					tn++;
				} else {
					fp++;
				}
			}
		}
		return tn/(tn+fp);
	}

	public static double distance(Point a, Point b) { //simple euclidian distance
		ArrayList<Double> pa = a.getList();
		ArrayList<Double> pb = b.getList();
		double sum = 0.0;
		for (int i = 0; i < pa.size(); i++) {
			sum += Math.pow(pa.get(i)- pb.get(i), 2);
		}
		return Math.sqrt(sum);
	}

	public static ArrayList<Group> LOF(ArrayList<Point> pts, int k) {
		ArrayList<Group> g = new ArrayList<Group>();
		for (int i = 0; i < pts.size(); i++) {
			Group temp = new Group(pts.get(i));
			temp.SetNearest(nearest(pts, pts.get(i), k)); //add the k nearest neighbors 
			temp.setDensity(); //set density
			g.add(temp);
		}
		for (int i = 0; i < g.size(); i++) {
			setOutlierScore(g.get(i), pts, k); //update outlier/anomaly score for each
		}
		return g;
	}
	public static double auc (ArrayList<Group> g, int k, String pos, String neg) { //calculate AUC using trapezoidal rule
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		Collections.sort(g, new Comparator<Group>() {
			public int compare(Group one, Group two) { //sort the points
				double v1 = one.outlierscore;
				double v2 = two.outlierscore;
				if (v1 > v2) 
					return 1;
				else if (v1 == v2)
					return 0;
				else return -1;
			}
		});
		for (int m = 0; m < g.size(); m++) { //assign class values
			double threshold = g.get(m).outlierscore;
			for (int i = 0; i < g.size(); i++) {
				if(threshold < g.get(i).outlierscore) {
					g.get(i).assigned = neg;
				} else {
					g.get(i).assigned = pos;
				}
			}
			Pair temp = new Pair(tpr(g, pos), fpr(g, neg));
			System.out.println(temp.toString());
			pairs.add(temp);
		}
		System.out.println();
		double sum = 0;
		for (int i = 0; i < pairs.size()-1; i++) {
			double h = (pairs.get(i+1).tp-pairs.get(i).tp); //get height of the trapezoid
			sum += .5*(pairs.get(i+1).fp+pairs.get(i).fp)*h; //multiply by .5 and base
		}
		return sum;
	}

	public static void setOutlierScore(Group g, ArrayList<Point> pts, int k) {
		double d = 0.0;
		for (int i = 0; i < g.neighbors.size(); i++) {
			Group temp = new Group(g.neighbors.get(i));
			//	System.out.println(temp.toString());
			temp.SetNearest(nearest(pts, g.neighbors.get(i), k)); //add the k nearest neighbors 
			//System.out.println(temp.toString());
			temp.setDensity(); //set density
			d+= temp.findDensity();
			//			
			//			d += findDensity();
		}
		d /= g.neighbors.size();
		double a = d/g.findDensity();
		g.outlierscore = a;
	}
	public static double Accuracy(ArrayList<Group> g) {
		double count = 0;
		for (int i = 0; i < g.size(); i++) {
			if(g.get(i).correctlyClassified()) count++;
		}
		return count/g.size(); //accuracy of correct classification
	}

	public static ArrayList<Point> nearest (ArrayList<Point> list, Point p, int k) {
		ArrayList<DisPair> l = new ArrayList<DisPair>(); //reutrn the k nearest neighbors of a point
		for (int i = 0; i < list.size(); i++) {
			DisPair temp = new DisPair(list.get(i));
			temp.setD(distance(p, list.get(i)));
			if (temp.d == 0.0) continue;
			l.add(temp);
		}
		Collections.sort(l, new Comparator<DisPair>() {
			public int compare(DisPair one, DisPair two) { //sort to know which are closest
				double v1 = one.d;
				double v2 = two.d;
				if (v1 < v2) 
					return -1;
				else if (v1 == v2)
					return 0;
				else return 1;
			}
		});
		ArrayList<Point> closest = new ArrayList<Point>();
		for (int i = 0; i < k; i++) { //add k closest
			closest.add(l.get(i).p);
		}
		return closest;
	}




}
