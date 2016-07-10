
public class Conjuncts {
	String attr;
	String attrval;
	String eqop;
	String total;
	public Conjuncts(String attribute, String value, String operator) {
		attr = attribute;
		attrval = value;
		eqop = operator;
		total = attr+" "+eqop+" "+attrval;
	}
	public String getattr() {
		return attr;
	} 
	public String getattrval() {
		return attrval;
	}
	public String toString() {
		return total;
	}
	
}
