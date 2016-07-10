import java.util.ArrayList;

public class Attributes {
	private ArrayList<String[]> attr;
	
	public Attributes() {
		attr =  new ArrayList<String[]>();
	}
	
	public void append(String[] a) {
		attr.add(a);
	}
	public ArrayList<String[]> getList() {
		return attr;
	}
}
