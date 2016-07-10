
public class Pair {
	double tp;
	double fp;
	public Pair(double a, double b) {
		tp = a;
		fp = b;
	}
	public String toString() {
		return "True positive: "+tp+" False Positive: "+fp;
	}

}
