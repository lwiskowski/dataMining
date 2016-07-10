import java.util.ArrayList;

public class Rules {
	private String classval;
	private ArrayList<Conjuncts> conjuncts;
	
	public Rules(String val) {
		classval = val;
		conjuncts = new ArrayList<Conjuncts>();
	}
	
	public void addRule(Conjuncts c) {
		conjuncts.add(c);
	}
	
	public Conjuncts getRule(int i) {
		return conjuncts.get(i);
	}
	
	public String GetClassValue() {
		return classval;
	}
	public ArrayList<Conjuncts> getRuleList() {
		return conjuncts;
	}
	public int size() {
		return conjuncts.size();
	}
	public Rules returnClone() {
		Rules r = new Rules(classval);
		for (int i = 0; i < this.conjuncts.size(); i++) {
			r.conjuncts.add(this.conjuncts.get(i));
		}
		return r;
	}
	public String toString() {
		String temp = "";
		for (int i = 0; i < conjuncts.size(); i++) {
			if (i == 0)
				temp += conjuncts.get(i).toString();
			else 
				temp += " ^ "+ conjuncts.get(i).toString();
		}
		temp += " --->"+" "+classval;
		return temp;
		
	}
}
