import java.util.Comparator;

public class Laplaceval {
	Rules r;
	double d;
	public Laplaceval(Rules rule, double num) {
		r = rule;
		d = num;
	}
	public Rules getRule() {
		return r;
	}
	public double getLaplace() {
		return d;
	}
		
}
