import java.util.ArrayList;

public class Itemset {
	public ArrayList<String> values;
	public Itemset() {
		values = new ArrayList<String>();
	}
	
	public void additem(String item) {
		values.add(item);
	}
	
	public String getString(int i) {
		return values.get(i);
	}
	public ArrayList<String> getList() {
		return values;
	}
	public String toString() {
		return values.toString();
	}
	public Itemset(ArrayList<String> u) {
		values = new ArrayList<String>();
		for (int i = 0; i < u.size(); i++) {
			String str = u.get(i);
			values.add(str);
		}

	}
}
