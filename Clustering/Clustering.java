import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import java.util.Random;

public class Clustering {


	public static void main(String[] args) throws FileNotFoundException {
		String readfile = args[0];
		Scanner kb = new Scanner(new File(readfile)); //read in attirbutes file
		String x = kb.nextLine();
		boolean hasCV = false;
		String[] classValues;
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
		String file2 = args[1];
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
		int k = Integer.parseInt(args[2]);
		if (args.length == 4) //normalize points if asked for
			points = normalize(points);
		System.out.println("K-means: ");
		System.out.println();
		ArrayList<Cluster> al = kmeans(points, k);	//do kmeans 
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i).toString());
			al.get(i).setSSE();
			System.out.println("SSE: "+al.get(i).SSE);
		}
		System.out.println();
		System.out.println("Silhouette coefficient:");
		System.out.println(sil(al, k));
		if (hasCV) {
			System.out.println();
			System.out.println("Rand Statistic: "+rand(al));
			System.out.println();
		}
		ArrayList<Cluster> test = bisectingkmeans(points, k); //do bisecting k-means
		System.out.println();
		System.out.println("Bisecting K-means: ");
		System.out.println();
		for (int i = 0; i < test.size(); i++) {
			System.out.println(test.get(i).toString());
			test.get(i).setSSE();
			System.out.println(test.get(i).SSE);
		}
		System.out.println();
		System.out.println("Silhouette coefficient:");
		System.out.println(sil(test, k));
		if (hasCV) {
			System.out.println();
			System.out.println("Rand Statistic: "+rand(test));
			System.out.println();
		}
	}

	public static double distance(Point a, Point b) { //sim ple euclidian distance
		ArrayList<Double> pa = a.getList();
		ArrayList<Double> pb = b.getList();
		double sum = 0.0;
		for (int i = 0; i < pa.size(); i++) {
			sum += Math.pow(pa.get(i)- pb.get(i), 2);
		}
		return Math.sqrt(sum);
	}

	public static ArrayList<Point> normalize(ArrayList<Point> p) { 
		double minj = Double.MAX_VALUE;
		double maxj = Double.MIN_VALUE;
		for (int i = 0; i < p.size(); i++) { //iterate through points, find min and max value
			for (int j = 0; j < p.get(i).fields.size(); j++) {
				if (minj > p.get(i).getList().get(j)) {
					minj = p.get(i).getList().get(j);
				}
				if (maxj < p.get(i).getList().get(j)) {
					maxj = p.get(i).getList().get(j);
				}
			}
		}
		for (int i = 0; i < p.size(); i++) { //iterate through points again, compute new value using min and max
			for (int j = 0; j < p.get(i).fields.size(); j++) {
				double tmpval = p.get(i).getList().get(j);
				p.get(i).getList().set(j, (tmpval-minj)/(maxj-minj));
			}
		}
		return p;
	}


	public static boolean changed(ArrayList<double[]> newcenters, ArrayList<Cluster> clusters) {

		for (int i = 0; i < newcenters.size(); i++) { //check to see if arraylist has changed
			Point center = clusters.get(i).getCentroid(); 
			ArrayList<Double> temp = center.getList();
			ArrayList<Double> temp2 = new ArrayList<Double>();// add araays to arraylst for ease of comparison
			double[] array = newcenters.get(i);
			for (int j = 0; j < array.length; j++) {
				temp2.add(array[j]);
			}
			if (!temp2.equals(temp)) {
				return false;
			}
		}
		return true;
	}

	public static double sil(ArrayList<Cluster> clusters, int k) {
		double sum = 0.0;
		int count = 0; //calculate the silhouette value
		if (k == 1 ) return 0.0;
		for (int i = 0; i < k-1; i++) {
			ArrayList<Point> points = clusters.get(i).list;
			if (points.size() < 1) continue;
			for (int j = 0; j < points.size(); j++) {
				double ai = avgdis(j, points); //calculate ai using ditance, for points in clusters
				ArrayList<Point> othercluster = clusters.get(i+1).list;
				double bi = avgdis(points.get(j), othercluster); //calculte sdistances to other clusters
				try {
					sum += (bi-ai)/Math.max(ai, bi);
					count++;
				} catch(Exception e) {
					continue;
				}
				//	}
			}
		}
		return sum/count;	

	}
	public static double avgdis(int q, ArrayList<Point> list) {
		//calculates avg distance of one point to a cluster
		double temp = 0.0;
		Point p = list.get(q);
		for (int i = 0; i < list.size(); i++) {
			if (q == i) continue;
			Point t = list.get(i);
			ArrayList<Double> pa = t.getList(); //get arraylists of values
			ArrayList<Double> pb = p.getList();
			double sum = 0.0;
			for (int j = 0; j < pa.size(); j++) {
				sum += Math.pow(pa.get(j)- pb.get(j), 2); //use euclidian distance
			}
			temp += Math.sqrt(sum);
		}
		if (list.size() > 1)
			return (temp/(list.size()-1));
		else
			return (temp/(list.size()));
	}

	public static double avgdis(Point p, ArrayList<Point> list) {
		//calculates avg distance of one point to a cluster
		double temp = 0.0;
		for (int i = 0; i < list.size(); i++) {
			Point t = list.get(i);
			ArrayList<Double> pa = t.getList();
			ArrayList<Double> pb = p.getList();
			double sum = 0.0;
			for (int j = 0; j < pa.size(); j++) {
				sum += Math.pow(pa.get(j)- pb.get(j), 2); //use euclidian distance
			}
			temp += Math.sqrt(sum);
		}
		return (temp/(list.size()));
	}

	public static double rand(ArrayList<Cluster> list) {
		int f11 = 0;
		int f00 = 0;
		int size = 0;
		for (int i = 0; i < list.size(); i++) {
			size+= list.get(i).list.size();
		}
		HashMap<String, Integer> hm = new HashMap<String, Integer>(); //use a hashmpa to store occurances of pairs rand statistic
		for (int i = 0; i < list.size(); i++) {
			ArrayList<Point> p = list.get(i).list;

			for (int j = 0; j < p.size(); j++) {
				String temp = p.get(j).getCV();
				if (hm.containsKey(temp)) {
					hm.put(temp, hm.get(temp)+1);
				} else {
					hm.put(temp, 1);
				}
			}
			for(Entry<String, Integer> e: hm.entrySet()) {
				if (e.getValue() > 1) {
					f11 += e.getValue()-1; //calculate amount of pairs
				}
			}
			hm.clear();
		}

		for (int i = 0; i < list.size()-1; i++) {
			ArrayList<Point> p = list.get(i).list;
			for (int j = i+1; j < list.size(); j++) {
				ArrayList<Point> points = list.get(j).list;

				for (int h = 0; h < points.size(); h++) {
					for (int z = 0; z < p.size(); z++) {
						if (!points.get(h).getCV().equals(p.get(z).getCV())) //iterate through the rest to check when different class values and
							//and different clusters
							f00++;
					}

				}
			}
		}
		size = size*(size-1)/2;
		return (double)(f00+f11)/(double)size; //cast to double and return
	}

	public static ArrayList<Cluster> kmeans(ArrayList<Point> points, int k) { 
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		Random r = new Random();
		while(clusters.size() < k) {
			Point rand = points.get(r.nextInt(points.size()));
			boolean b = true;
			for (int i = 0; i < clusters.size(); i++) {
				if (rand.pointid.equals(clusters.get(i).getCentroid().pointid)) {
					b = false;
					break;
				}
			}
			if(b) {
				Cluster c = new Cluster(rand);
				clusters.add(c); //set a random point to be the centroid
			}
		}

		while (true) { //iterate until the centroid no longer changes
			for (int i = 0; i < points.size(); i++) { //assign to clusters
				double mindis = Double.MAX_VALUE;
				int mink = 0;
				for (int j = 0; j < clusters.size(); j++) {
					double temp = distance(points.get(i), clusters.get(j).getCentroid());
					if (temp < mindis) {
						//marke which cluster it shoulf belong to
						mindis = temp;
						mink = j;
					}
				}
				clusters.get(mink).addPoint(points.get(i)); //assign to appropiriate cluster
			}
			ArrayList<double[]> newcenters = new ArrayList<double[]>();
			for (int i = 0; i < clusters.size(); i++) { //finding new center values
				Cluster tmp = clusters.get(i);
				ArrayList<Point> p = tmp.list;
				int size = p.get(0).fieldsSize();
				double[] cent = new double[size];
				for (int j = 0; j < p.size(); j++) {
					for (int e = 0; e < size; e++) {
						cent[e] += p.get(j).getList().get(e) / size;
					}
				}
				newcenters.add(cent);
			}
			if (!changed(newcenters, clusters)) { //check to see if center changed, if not end loop
				break;
			}
			for (int i = 0; i < clusters.size(); i++) { //reassign the new centroids, and repeat
				clusters.get(i).clearPoints();
				Point temp = new Point("centroid");
				temp.setFields(newcenters.get(i));
			}
		}

		return clusters;
	}

	public static ArrayList<Cluster> bisectingkmeans(ArrayList<Point> points, int k) {
		ArrayList<Cluster> list = kmeans(points, 1);
		while (list.size() < k) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setSSE();
			}
			Collections.sort(list, new Comparator<Cluster>() { //sort to list by sse values
				public int compare(Cluster one, Cluster two) {
					double v1 = one.SSE;
					double v2 = two.SSE;
					if (v1 > v2) 
						return 1;
					else if (v1 == v2)
						return 0;
					else return -1;
				}
			});
			Cluster temp = list.remove(list.size()-1);
			ArrayList<ArrayList<Cluster>> templist = new ArrayList<ArrayList<Cluster>>();
			int minindex = 0;
			double min = Double.MAX_VALUE;
			for (int i = 0; i < 10; i++) { //iterate 10 times
				ArrayList<Cluster> gen = kmeans(temp.list, 2); //run kmeans on current cluster
				for (int j = 0; j < gen.size(); j++) {
					gen.get(j).setSSE();
				}
				Collections.sort(gen, new Comparator<Cluster>() {
					public int compare(Cluster one, Cluster two) {
						double v1 = one.SSE;
						double v2 = two.SSE;
						if (v1 < v2) 
							return 1;
						else if (v1 == v2)
							return 0;
						else return -1;
					}
				});
				templist.add(gen);
				if (gen.get(0).SSE + gen.get(1).SSE < min) { //find lication of best cluster
					min = gen.get(0).SSE + gen.get(1).SSE;
					minindex = i;
				}
			}
			list.add(templist.get(minindex).get(0)); //choose 2 bet clusters
			list.add(templist.get(minindex).get(1));
		}
		return list;
	}
}
