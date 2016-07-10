
public class Rule {
	public Itemset Antecedent;
	public Itemset Consequent;
	public double confindence = 0;
	
	public Rule (Itemset a, Itemset c) {
		Antecedent = a;
		Consequent = c;
	}
	
	public void setconfidence(double d) {
		confindence = d;
	}
	public double getconfidence() {
		return confindence;
	}
	public String toString() {
		String temp = Antecedent.toString() + " -> "+Consequent.toString();
		return temp;
	}
	
}
