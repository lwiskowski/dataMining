import java.util.ArrayList;
import java.util.HashMap;

public class Person {
	public final String[] attributes; //read in line
	public final String classVal; //value of class for record
	public ArrayList<String> actualAtr = new ArrayList<String>(); //arraylkist of attributes
	private final HashMap<String, String> classes = new HashMap<String,String>();
	Attributes a = new Attributes();
	//hashmap used to organize them. map attributes to record values
	public Person(ArrayList<String[]> list, String[] atr, Attributes atributes) {
		attributes = atr;
		a = atributes;
		for (int i = 0; i < atr.length; i++) {
			classes.put(list.get(i)[0], atr[i]);
			actualAtr.add(list.get(i)[0]);
		}
		classVal = atr[atr.length-1];
	}
	public String getattr(String classof) {
		return classes.get(classof);
	}
	public String getclassVal() {
		return classVal;
	}
	public boolean continuous (String classof) { //test to see if an atrribute is continous or not
		String temp = "";
		for (int i =0; i < a.attrvals.size(); i++) {
			if (classof.equals(a.attrvals.get(i)[0])) {
				if (a.attrvals.get(i)[1].equals("continuous")) 
					return true;
			}
		}
		return false;
	}
}
