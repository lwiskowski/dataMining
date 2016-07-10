
public class attributes {

	String item;
	String val1;
	String val2;
	
	public attributes(String[] array) {
		item = array[0];
		val1 = array[1];
		val2 = array[2];
	}
	
	public String  getItem() {
		return item;
	}
	public String getVal1(){
		return val1;
	} 
	public String getVal2() {
		return val2;
	}
}
