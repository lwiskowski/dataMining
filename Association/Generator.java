import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class Generator {

	public static void main(String[] args) throws FileNotFoundException {
		//read in input from files, use command line args
		String readfile = args[0];
		Scanner kb = new Scanner(new File(readfile));
		ArrayList<attributes> attr = new ArrayList<attributes>(); 
		while (kb.hasNextLine()) {
			String temp = kb.nextLine();
			String[] array = temp.split(" ");
			attributes a = new attributes(array);
			attr.add(a); 
		}
		kb.close();
		String readfile1 = args[1];
		Scanner sc = new Scanner(new File(readfile1));
		ArrayList<Itemset> purchases = new ArrayList<Itemset>();
		while (sc.hasNextLine()) {
			String x = sc.nextLine();
			String[] temp = x.split(" ");
			Itemset t = new Itemset();
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("1")) {
					t.additem(attr.get(i).getItem());
				}
			}
			purchases.add(t);
		}
		ArrayList<Itemset> first = firstItemset(Double.parseDouble(args[2]), purchases);
		ArrayList<Itemset> all = generateItemSets(Double.parseDouble(args[2]), purchases, 1, attr.size());
		System.out.println();
		System.out.println();
		System.out.println("Rules");
		GenerateRule(all, purchases, Double.parseDouble(args[3]), attr.size());

	}
	//generate the first itemset, use to start of larger itemsets
	public static ArrayList<Itemset> firstItemset(double minsup, ArrayList<Itemset> list) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		ArrayList<Itemset> set = new ArrayList<Itemset>();
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getList().size(); j++) {
				if (hm.containsKey(list.get(i).getList().get(j))) {
					hm.put(list.get(i).getList().get(j), hm.get(list.get(i).getList().get(j))+1);
				} else {
					hm.put(list.get(i).getList().get(j), 1);
				}
			}
		}

		for (Entry<String, Integer> e : hm.entrySet()) {
			if ((double) e.getValue() / list.size() >= minsup) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getKey());
				Itemset plswork = new Itemset(temp);
				set.add(plswork);
			}
		}
		return set;

	}

	public static ArrayList<Itemset> kminusone(ArrayList<Itemset> list, int k) { //fk-1
		ArrayList<Itemset> temp = new ArrayList<Itemset>();
		for (int i = 0; i < list.size(); i++) {
			Collections.sort(list.get(i).getList());
			for (int j = 0; j < list.size(); j++) {
				Itemset t = new Itemset();
				boolean a = true;
				for (int q = 0; q < k-1; q++) {
					if (!list.get(i).getList().get(q).equals(list.get(j).getList().get(q))) {
						a = false;
						break;
					} else {
						t.additem(list.get(i).getList().get(q));
					}
				}
				if(a) {
					if (list.get(i).getList().get(list.get(i).getList().size()-1).equals(list.get(j).getList().get(list.get(i).getList().size()-1)))
						break;
					t.additem(list.get(i).getList().get(list.get(i).getList().size()-1));
					t.additem(list.get(j).getList().get(list.get(i).getList().size()-1));
					Collections.sort(t.values);
					if (!temp.contains(t) )
						temp.add(t);
				}
			}
		}
		return temp;
	}
	public static double support(Itemset is, ArrayList<Itemset> list) { //calculate the support for each
		double count = 0;
		for (int i = 0; i < list.size(); i++) {
			boolean b = true;
			for (int k = 0; k < is.getList().size(); k++) {
				if (!list.get(i).getList().contains(is.getList().get(k))) {
					b = false;
				}
			}
			if (b) count++;
		}
		return count/list.size();
	}
	public static ArrayList<Itemset> subsets(final Itemset is) { //find the subsets of a itemset to make sure they are both frequent
		ArrayList<Itemset> temp = new ArrayList<Itemset>();
		for (int i = 0; i < is.values.size(); i++) {
			ArrayList<String> listtmp = new ArrayList<String>();
			for (int j = 0; j < is.values.size(); j++) {
				listtmp.add(is.values.get(j));
			}
			listtmp.remove(i);
			Itemset itm = new Itemset(listtmp);
			temp.add(itm);
		}
		return temp;
	}

	public static ArrayList<Itemset> generateItemSets(double minsup, ArrayList<Itemset> list, int k, int maxsize) {
		int w = 1;
		double support = minsup;
		System.out.println("Item set size: "+k);

		ArrayList<Itemset> sizeone = firstItemset(minsup, list);
		System.out.println("Number of Item Sets generated: "+sizeone.size());
		System.out.println(sizeone.toString());
		ArrayList<ArrayList<Itemset>> allitemsets = new ArrayList<ArrayList<Itemset>>(); //get first itemsets
		allitemsets.add(sizeone);
		ArrayList<Itemset> pruned = new ArrayList<Itemset>();
		HashSet<Itemset> frequent = new HashSet<Itemset>();
		while(k < maxsize) { //set not empty
			ArrayList<Itemset> temp = kminusone(sizeone, k);
			if (temp.isEmpty()) break;
			if (temp.get(temp.size()-1).values.size() <= 1) break;
			System.out.println();
			System.out.println("Item set size: "+(k+1));
			System.out.println("Number of Item Sets generated: "+temp.size());
			System.out.println(temp.toString());
			allitemsets.add(temp);
			for (int i = 0; i < allitemsets.size(); i++) {
				int count = 0;
				for (int j = 0; j < allitemsets.get(i).size(); j++) {
					HashSet<Itemset> hs = new HashSet<Itemset>();
					hs.addAll(pruned);
					pruned.clear();
					pruned.addAll(hs);
					if (allitemsets.get(i).get(j).values.size() > 1) {
						ArrayList<Itemset> subset = subsets(allitemsets.get(i).get(j));
						boolean missing = true;
						if (k > 1) {
							for (int y = 0; y < subset.size(); y++) {
								if (!pruned.contains(subset.get(y))) {
									missing = false;
									break;
								}
							}
						}
						if (!missing) {
							continue;

						}
					}
					if (support(allitemsets.get(i).get(j), list) >= minsup) {
						count++;
						pruned.add(allitemsets.get(i).get(j));
						frequent.add(allitemsets.get(i).get(j));
					}



				}
			}
			System.out.println("Number of Item Sets generated after pruning: "+temp.size());
			sizeone = temp;
			k++;
		}
		HashSet<Itemset> hs = new HashSet<Itemset>();
		return pruned; //itemsets that passed pruning test
	}

	public static void GenerateRule(ArrayList<Itemset> frequent, ArrayList<Itemset> all, double min, int maxsize) { //grow rules starting from the frequent itemsets
		HashSet<Itemset> used = new HashSet<Itemset>();
		for (int i = 0; i < frequent.size()-1; i++) {
			HashSet<String> merge = new HashSet<String>();
			Itemset candidate = frequent.get(i);
			if (candidate.values.size() <= 1)
				continue;
			Itemset ant = new Itemset(candidate.values);
			for (int j = 0; j < candidate.values.size(); j++) {
				ArrayList<String> tg = new ArrayList<String>();
				tg.add(candidate.values.get(j));
				ant.values.remove(0);
				Itemset consequent = new Itemset(tg);
				merge.addAll(consequent.values);
				ArrayList<String> array = new ArrayList<String>();
				array.addAll(merge);
				Itemset combo = new Itemset(array);
				double confidence = support(frequent.get(i), all)/support(ant, all);//find confidence to see if rule passes
				confidence += .01;
				if (confidence >= min) {
					Rule r = new Rule(ant, consequent);
					System.out.println(r.toString());
				}
			}


		}
	}




}


