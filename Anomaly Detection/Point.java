import java.util.ArrayList;
import java.util.Arrays;

public class Point {
	
	public String pointid;
	public ArrayList<Double> fields = new ArrayList<Double>(); //values of fields for a point
	private String classval = null;
	
	public Point (String a) {
		pointid = a;
	}
	public void addValue(Double a) {
		fields.add(a);
	}
	public void addClassVal(String a) {
		classval = a;
	}
	public String getCV() {
		return classval;
	}
	public int fieldsSize() {
		return fields.size();
	}
	public ArrayList<Double> getList() {
		return fields;
	}
	public void setFields(double[] a) { //allows adding of fields for a point
		ArrayList<Double> temp = new ArrayList<Double>();
		for (int i = 0; i < a.length; i++) {
			temp.add(a[i]);
		}
		fields = temp;
	}
	public String toString() {
		String temp = "pointID: "+pointid+ " "+ fields.toString()+" "+classval;		
		return temp;
	}
	
}
