
public class ConjunctPair {
	Conjuncts c;
	double l;
	
	public ConjunctPair(Conjuncts con, double num) {
		c = con;
		l = num;
	}
	
	public Conjuncts getConjunct() {
		return c;
	}
	
	public double getLaplace() {
		return l;
	}
}
