import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;



public class SequentialCover {

	public static void main(String[] args) throws FileNotFoundException {
		String readfile = args[0];
		Scanner kb = new Scanner(new File(readfile)); //file processing
		ArrayList<String[]> attr = new ArrayList<String[]>();
		ArrayList<Pair> atrpairs = new ArrayList<Pair>();
		Attributes a = new Attributes();
		while (kb.hasNextLine()) {
			String temp = kb.nextLine();
			String[] str = temp.split("\\s+");
			for (int i = 1; i < str.length; i++) {
				Pair tmpPair = new Pair(str[0], str[i]);
				atrpairs.add(tmpPair);
			}
			a.addarray(str);
			attr.add(str);
		}


		kb.close();
		ArrayList<Person> ppl = new ArrayList<Person>();
		TreeMap<String, Integer> classvalues = new TreeMap<String, Integer>();//use a tree map to sort the classvalues
		//will give which is the class with least occurence
		String trainingset = args[1];
		Scanner sc = new Scanner(new File(trainingset));
		while(sc.hasNextLine()) {
			String tmp = sc.nextLine(); //more file processing
			String[] array = tmp.split("\\s+");
			Person p = new Person(attr, array, a);
			if (!classvalues.containsKey(p.classVal))
				classvalues.put(p.classVal, 1);
			else {
				classvalues.put(p.classVal, classvalues.get(p.classVal)+1);
			}
			ppl.add(p);
		}
		sc.close();
		ArrayList<Person> testppl = new ArrayList<Person>();
		if (args.length == 3) {
			Scanner testset = new Scanner(new File(args[2]));
			while(testset.hasNextLine()) {
				String tmp = testset.nextLine();
				String[] array = tmp.split("\\s+");
				Person p = new Person(attr, array, a);
				testppl.add(p);
			}
		}
		ArrayList<String> treevals = new ArrayList<String>(); 
		for (Map.Entry<String, Integer> entry: classvalues.entrySet()) {
			treevals.add(entry.getKey());
		}
		Collections.reverse(treevals);
		ArrayList<Person> pplcopy = new ArrayList<Person>();
		for (int i = 0; i < ppl.size(); i++) {
			pplcopy.add(ppl.get(i));
		}
		ArrayList<Rules> descisionList = new ArrayList<Rules>();
		ArrayList<Person> removethese = new ArrayList<Person>();
		for (int i = 0; i < treevals.size()-1; i++) { //runt he algorithm to learn the rules
			while(true) {
				Rules learned = LearnRule(pplcopy, treevals.get(i), atrpairs, treevals.size());//learn rules
				if (!descisionList.isEmpty()) { //breaking condition
					if (learned.toString().equals(descisionList.get(descisionList.size()-1).toString())) break;
				}
				descisionList.add(learned);//add rules to the list
				Iterator<Person> iter = pplcopy.iterator();
				HashSet<String> cvs = new HashSet<String>();//keep used pair, remove so they dont get redone
				ArrayList<Pair> rmatrpairs = new ArrayList<Pair>();
				while (iter.hasNext()) {
					Person p = iter.next();
					if (ruletrue(p, learned))
						removethese.add(p);
				}
				pplcopy.removeAll(removethese);
				removethese.clear();

				for (Rules r: descisionList) {
					ArrayList<Conjuncts> conj = r.getRuleList();
					for (int h = 0; h < conj.size(); h++) {
						Pair temppair = new Pair(conj.get(i).attr,conj.get(i).attrval);
						rmatrpairs.add(temppair);
					}
				}
				atrpairs.removeAll(rmatrpairs);
				for (Person q: pplcopy)
					cvs.add(q.classVal);
				if (cvs.size() <= i+1) break;
			}
		}
		//traverse arraylist of rules, and print out rules, evaluate accuracy
		for (int i = 0; i < descisionList.size(); i++)
			System.out.println("r"+(i+1)+": "+descisionList.get(i).toString());
		System.out.println("default = "+treevals.get(treevals.size()-1));
		System.out.println(evaluateRule(ppl, descisionList, treevals.get(treevals.size()-1), "training"));
		if (args.length == 3)
			System.out.println(evaluateRule(testppl, descisionList, treevals.get(treevals.size()-1), "testset"));

	}
	public static ConjunctPair conLaplace (ArrayList<Person> p, Rules r, double k, String eqop, final String attr) {
		//method for continuous lapplace, sorts values and return split with max laplace value
		Rules copy = new Rules(r.GetClassValue());
		double laplacetemp = 0.0;
		ArrayList<Conjuncts> temp = r.getRuleList();
		ArrayList<ConjunctPair> cp = new ArrayList<ConjunctPair>();
		for (int i = 0; i < temp.size(); i++) {
			copy.addRule(temp.get(i));
		}
		for (int i = 0; i < p.size(); i++) {
			Collections.sort(p, new Comparator<Person>() {
				public int compare(Person p1, Person p2) {
					double v1 = Double.parseDouble(p1.getattr(attr));
					double v2 = Double.parseDouble(p2.getattr(attr));
					if (v1 > v2) 
						return 1;
					else if (v1 == v2)
						return 0;
					else return -1;
				}
			});

			ArrayList<Double> lparray = new ArrayList<Double>();
			for (int l = 0; l < p.size()-1; l++) {
				double nc = 0;
				double n = 0;
				double splitval = (Double.parseDouble(p.get(l).getattr(attr)) + Double.parseDouble(p.get(l+1).getattr(attr)))/2.0;
				for (int w = 0; w < p.size(); w++) {
					if (eqop.equals("<=")) {
						if (Double.parseDouble(p.get(l).getattr(attr)) <= splitval) {
							n++;

						} if (Double.parseDouble(p.get(l).getattr(attr)) <= splitval && p.get(l).classVal.equals(r.GetClassValue())) {
							nc++;
						}
					} else {
						if (Double.parseDouble(p.get(l).getattr(attr)) > splitval) {
							n++;
						} if (Double.parseDouble(p.get(l).getattr(attr)) > splitval && p.get(l).classVal.equals(r.GetClassValue())) {
							nc++;
						}
					}
				}
				Conjuncts tmp = new Conjuncts(attr, splitval+"", eqop);		
				laplacetemp = (nc+1)/(n+k);
				n = 0;
				nc= 0;
				ConjunctPair tempcp = new ConjunctPair(tmp, laplacetemp);
				cp.add(tempcp);
			}
		}
		Collections.sort(cp, new Comparator<ConjunctPair>() {
			public int compare(ConjunctPair p1, ConjunctPair p2) {
				double v1 = p1.getLaplace();
				double v2 = p2.getLaplace();
				if (v1 > v2) 
					return 1;
				else if (v1 == v2)
					return 0;
				else return -1;
			}
		});
		cp.toString();
		if (cp.size() > 0)
			return cp.get(cp.size()-1);
		else
			return null;

	}

	public static double Laplace(ArrayList<Person> p, Rules r, double k, String eqop) {
		//laplace for non continuos value
		double nc = 0;
		double n = 0;
		Rules copy = new Rules(r.GetClassValue());
		double laplacetemp = 0.0;
		ArrayList<Conjuncts> temp = r.getRuleList();
		for (int i = 0; i < temp.size(); i++) {
			copy.addRule(temp.get(i));
		}
		for (int i = 0; i < p.size(); i++) {
			for (int j = 0; j < r.size(); j++) {
				nc = 0;
				n = 0;
				final String attr = r.getRule(j).getattr();
				if (p.get(i).continuous(r.getRule(j).getattr())) {
					Collections.sort(p, new Comparator<Person>() {
						public int compare(Person p1, Person p2) {
							int v1 = Integer.parseInt(p1.getattr(attr));
							int v2 = Integer.parseInt(p2.getattr(attr));
							if (v1 > v2) 
								return 1;
							else if (v1 == v2)
								return 0;
							else return -1;
						}
					});
					ArrayList<Double> lparray = new ArrayList<Double>();
					for (int l = 0; l < p.size()-1; l++) {
						double splitval = (Double.parseDouble(p.get(l).getattr(attr)) + Double.parseDouble(p.get(l+1).getattr(attr)))/2.0;
						for (int w = 0; w < p.size(); w++) {
							if (eqop.equals("<=")) {
								System.out.println(Double.parseDouble(p.get(l).getattr(attr)));
								System.out.println(splitval);
								if (Double.parseDouble(p.get(l).getattr(attr)) <= splitval) {
									n++;
								} if (Double.parseDouble(p.get(l).getattr(attr)) <= splitval && p.get(l).classVal.equals(r.GetClassValue())) {
									nc++;
								}
							} else {
								if (Double.parseDouble(p.get(l).getattr(attr)) > splitval) {
									n++;
								} if (Double.parseDouble(p.get(l).getattr(attr)) > splitval && p.get(l).classVal.equals(r.GetClassValue())) {
									nc++;
								}
							}
						}
						laplacetemp = (nc+1)/(n+k);
						lparray.add(laplacetemp);
					}
					Collections.sort(lparray);
					return lparray.get(lparray.size()-1);
				} 

				else {
					if (p.get(i).getattr(r.getRule(j).getattr()).equals(r.getRule(j).attrval))
						n++;
					if (p.get(i).getattr(r.getRule(j).getattr()).equals(r.getRule(j).attrval) && p.get(i).classVal.equals(r.GetClassValue())) {
						nc++;
					}
				}
			}
			laplacetemp = (nc+1)/(n+k);
		}	
		return laplacetemp;
	}

	public static Rules LearnRule ( ArrayList<Person> p, String cv, ArrayList<Pair> pairs, double k) {
		//method for learning ghe rule, iterates through ppl and attribute value pairs to make best rules
		Rules r = new Rules(cv);
		Rules rcopy = r.returnClone();
		Rules rorig = r.returnClone();
		double maxLaplace = 0.0;
		double currentmax = .000000000001;
		Conjuncts bestconj;
		ArrayList<Laplaceval> laplaces = new ArrayList<Laplaceval>();
		HashSet<String> used = new HashSet<String>(); 
		for (int i = 0; i < p.size(); i++) {
			for (int j = 0; j < pairs.size()-k; j++) {
				if (p.get(i).continuous(pairs.get(j).getatr())) {
					ConjunctPair conjuc1 = conLaplace(p, rcopy, k, "<=", pairs.get(j).getatr());
					ConjunctPair conjuc2 = conLaplace(p, rcopy, k, ">", pairs.get(j).getatr());
					if (conjuc1 == null || conjuc2 == null) break;
					if (conjuc1.getLaplace() > conjuc2.getLaplace()) {
						if (used.contains(pairs.get(j).getatr()))
							continue;
						rcopy.addRule(conjuc1.getConjunct());
						Laplaceval l = new Laplaceval(rcopy, conjuc1.getLaplace());
						laplaces.add(l);
						rcopy = r.returnClone();
						used.add(pairs.get(j).getatr());
						laplaces.add(l);
						if  (maxLaplace < currentmax) break;
						currentmax =  maxLaplace;
						maxLaplace = laplaces.get(laplaces.size()-1).getLaplace();
						break;
					} else {
						if (used.contains(pairs.get(j).getatr()))
							continue;
						rcopy.addRule(conjuc2.getConjunct());
						Laplaceval l = new Laplaceval(rcopy, conjuc1.getLaplace());
						laplaces.add(l);
						rcopy = r.returnClone();
						if  (maxLaplace < currentmax) break;
						currentmax =  maxLaplace;
						maxLaplace = laplaces.get(laplaces.size()-1).getLaplace();
						break;
					}
				}
				else {
					if (used.contains(pairs.get(j).getatr()))
						continue;
					bestconj = new Conjuncts(pairs.get(j).getatr(), pairs.get(j).getval(), "=");
					rcopy.addRule(bestconj);
					double temp = Laplace(p, rcopy, k, "=");
					Laplaceval l = new Laplaceval(rcopy, temp);
					used.add(pairs.get(j).getatr());
					laplaces.add(l);
					//rcopy = r.returnClone();
					if  (maxLaplace < currentmax) break;
					currentmax =  maxLaplace;
					maxLaplace = laplaces.get(laplaces.size()-1).getLaplace();
				}
			}
		}
		Collections.sort(laplaces, new Comparator<Laplaceval>() {
			public int compare(Laplaceval one, Laplaceval two) {
				double first = one.getLaplace();
				double second = two.getLaplace();
				if (first == second) 
					return 0;
				else if (first > second)
					return 1;
				else
					return -1;
			}
		});
		r = laplaces.get(laplaces.size()-1).getRule();


		//}
		return r;
	}

	public static boolean ruletrue (Person p, Rules r) {
		//evaluates if a rule is true
		boolean b = true;
		if (!p.classVal.equals(r.GetClassValue()))
			return false;
		else {
			ArrayList<Conjuncts> list = r.getRuleList();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).total.contains(">")) {
					double one = Double.parseDouble(list.get(i).attrval);
					double two = Double.parseDouble(p.getattr(list.get(i).attr));
					if (two <= one) {
						b = false;
						break;
					}

				} else if (list.get(i).getattrval().contains("<=")) {
					double one = Double.parseDouble(list.get(i).attrval);
					double two = Double.parseDouble(p.getattr(list.get(i).attr));
					if (two > one) {
						b = false;
						break;
					}
				}
				if (!list.get(i).attrval.equals(p.getattr(list.get(i).attr))) {
					b = false;
					break;
				}
			}
		}
		return b;
	}
	public static  String Predict(Person p, ArrayList<Rules> r, String defaultval) {
		//returns values that will be predicted for a record based on the rules
		String pred = defaultval;
		for (int j =0; j < r.size(); j++) {
			boolean b = true;
			ArrayList<Conjuncts> list = r.get(j).getRuleList();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).total.contains(">")) {
					double one = Double.parseDouble(list.get(i).attrval);
					double two = Double.parseDouble(p.getattr(list.get(i).attr));
					if (two <= one) {
						b = false;
						break;
					}
				} else if (list.get(i).total.contains("<=")) {
					double one = Double.parseDouble(list.get(i).attrval);
					double two = Double.parseDouble(p.getattr(list.get(i).attr));
					if (two > one) {
						b = false;
						break;
					}
				}
				else {
					if (!list.get(i).attrval.equals(p.getattr(list.get(i).attr))) {
						b = false;
						break;
					}
				}
			}
			if (b) {
				return r.get(j).GetClassValue();
			} 

		}
		return pred;
	}
	public static String evaluateRule(ArrayList<Person> p, ArrayList<Rules> r, String defaultval, String t) {
		//prints out accuracy
		double total = p.size();
		String end = "";
		double correct = 0;
		for (Person a: p) {
			if (a.getclassVal().equals(Predict(a, r, defaultval)))
				correct++;
		}
		double percent = (correct/total)*100.0;
		if (t.equals("training"))
			end = "The Algorithm predicts the class value for the training set with "+percent+"% accuracy";
		else 
			end = "The Algorithm predicts the class value for the test set with "+percent+"% accuracy";
		return end;
	}



}
