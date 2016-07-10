import java.util.ArrayList;

public class Attributes {
	ArrayList<String[]> attrvals = new ArrayList<String[]>();
	
	public void addarray(String[] a) {
		attrvals.add(a);
	}
	public String[] getarray(int i) {
		return attrvals.get(i);
	}

}
