import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;



public class A1 {
	/**Main class runs our processor. It reads files from the designated path,
	 * pre-processes them, and can be used to run our unigram and bigram models.
	 * @param args
	 */
	public static void main(String[] args){
		getFiles("Users/elizaweaver/Documents/workspace2110/NLPa1/data_corrected/"
				+ "classification task/atheism/train_docs");
		String result=getFiles("Users/elizaweaver/Documents/workspace2110/NLPa1/data_corrected/"
				+ "classification task/atheism/train_docs/"); 
		ArrayList<String> arr = makeArrayList(result);
		//System.out.println(arr.size());
		HashMap<String, Integer> x= unigramCounts(arr);
		//System.out.println(x);
		HashMap<String, Float> y = unigramProbHashmap(x);
		//System.out.println(y);
		//System.out.println(unigram("science", y));
		
		HashMap<String, HashMap<String, Integer>> bc = bigramCounts(arr);
		HashMap<String, HashMap<String, Float>> z = bigramProbHashmap(bc, x);
		//System.out.println(z);
		//System.out.println(bigram("[b]", "science", z));
		System.out.println(unigramRandomSentence("",arr));
		//System.out.println(sorted(arr));
		//System.out.println(unigram("aklZJBSDkla", x));
		//System.out.println(count2("science", "[b]", arr));
		//System.out.println(count("[b]", arr));
		//HashMap<String, LinkedHashMap<String, Float>> cum = cumProb(z);
		//System.out.println(cumProb(z));
		//System.out.println(nextWord("half", cum));
		//System.out.println(genSentenceBigram("My favorite sex position is",cum));
		
		
	}
	
	/** Takes a file path and retrieves all the files in that path as a string */
	static String getFiles(String path){
		File f = new File(path);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		String result="";
		for (File x :files){
			try {
				result = result +readFile(x.getPath(), StandardCharsets.US_ASCII);
				
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/**Takes a filepath and a set of characters and reads the files in that path. Returns a processed String
	 * of file content. 
	 */
	static String readFile(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		String strfile = new String(encoded, encoding);
		return strProcess(removeHeader(strfile));
	}
	
	/**Removes the email header of a file */
	static String removeHeader(String file){
		int stEmail = 0;
		if (file.contains("writes")){
			stEmail = file.indexOf("writes")+6;
		}
		else if (file.contains("wrote")){
			stEmail = file.indexOf("wrote")+5;
		}
		else{
			int atindex = file.indexOf('@');
			String email = file.substring(atindex, file.length());
			int endEmail = email.indexOf(" ");
			return email.substring(endEmail+1,email.length());
		}
		return file.substring(stEmail,file.length());
	} 
	
	/**Processes a string by removing extra characters and replacing all punctuation with one common pattern*/
	static String strProcess(String s){
		String result = s.replaceAll("[~$%'-+^:\"></\\()|#]", "");
		String re= result.replaceAll("[.?!]", "[b]");
		re= re.replace(". ", " [b]");
		return re;
	}
	

	/**Given a string, makes an Arraylist where each word is a separate element */
	public static ArrayList<String> makeArrayList(String st){
		ArrayList<String> myList = new ArrayList<String>(Arrays.asList(st.split("\\s")));
		myList.removeAll(Arrays.asList("", null));
		Iterator<String> iter = myList.iterator();
		while (iter.hasNext()){
			String r = iter.next();
			if (r.contains("@")){
				iter.remove();
			}
		}
		System.out.println(myList);
		return myList;
	}

	/**Given an arraylist of words, returns a hashmap within a hashmap of the counts 
	 * of each combination of 2 words*/
	public static HashMap<String, HashMap<String, Integer>> bigramCounts(ArrayList<String> arr){
		HashMap<String, HashMap<String, Integer>> outerHash = 
				new HashMap<String,HashMap<String, Integer>>();
		for(int i=0; i<arr.size()-2; i++){
			if (outerHash.containsKey(arr.get(i))){
				HashMap<String,Integer>innerhash = outerHash.get(arr.get(i));
				if (innerhash.containsKey(arr.get(i+1))){
					innerhash.put(arr.get(i+1), innerhash.get(arr.get(i+1))+1);	
				}
				else{
					innerhash.put(arr.get(i+1), 1);
				}
			}
			else{
				HashMap<String,Integer> innerhash = new HashMap<String, Integer>();
				innerhash.put(arr.get(i+1),1);
				outerHash.put(arr.get(i), innerhash);
			}
		}
		return outerHash;
	}
	
	/**Given an arraylist of words, returns a hashmap of the counts of each word*/
	public static HashMap<String, Integer> unigramCounts(ArrayList<String> arr){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (String x:arr){
			if(!result.containsKey(x)){
				result.put(x, 1);
			}
			else{
				result.put(x, result.get(x)+1);
			}
		}
		return result;
	}
	
	/**Given a hashmap of words and their counts, return a hashmap of 
	 * pairs of words and their probabilities using a bigram model*/
	public static HashMap<String, HashMap<String, Float>> bigramProbHashmap
	(HashMap<String, HashMap<String, Integer>> map, HashMap<String, Integer> unigramCounts){
		HashMap<String, HashMap<String, Float>> outer = 
				new HashMap<String, HashMap<String, Float>>(); 
		for (Entry<String, HashMap<String, Integer>> entry : map.entrySet()){
			
			Integer countOuter = unigramCounts.get(entry.getKey());
			HashMap<String, Float> inner = new HashMap<String, Float>();
			for (Entry<String, Integer> innerEntry: map.get(entry.getKey()).entrySet()){
				Integer x = map.get(entry.getKey()).get(innerEntry.getKey());
				float prob = (float) x/ (float) countOuter;
				inner.put(innerEntry.getKey(), prob);
			}
			outer.put(entry.getKey(), inner);	
		}
		return outer;
	}
	
	/**Gives the bigram probability of string a given string b */
	public static float bigram(String a, String b, HashMap<String, HashMap<String, Float>> map){
		try{
			return map.get(b).get(a);
		}
		catch (NullPointerException e){	
		}
		return (float) 0;
	}
	
	/**Given a hashmap of words and counts, returns a hashmap of 
	 * words and probabilities using a unigram model.*/
	public static HashMap<String, Float> unigramProbHashmap(HashMap<String, Integer> map){
		HashMap<String, Float> result = new HashMap<String, Float>();
		Integer sum = 0;
		for (Entry<String, Integer> entry: map.entrySet()){
			Integer x = entry.getValue();
			sum = sum+x;
		}
		for (Entry<String, Integer> z: map.entrySet()){
			Integer x = z.getValue();
			String y = z.getKey();
			result.put(y, (float)x/(float) sum);
			
		}
		return result;
	}
	
	/** Given a hashmap of probabilities and a word, returns the probability
	 * of that word */
	public static float unigram(String x, HashMap<String, Float> map){
		return map.get(x);
	}
	
	/** Returns a random sentence using the unigram model*/
	public static StringBuffer unigramRandomSentence(String sentence, ArrayList<String> arr){
		boolean flag = true;
		StringBuffer result= new StringBuffer();
		if (!sentence.equals("")){
			result= result.append(sentence+ " ");
		}
		while(flag){
			double rand = Math.random();
			int i = arr.size();
			int x = (int) (rand*i);
			if (arr.get(x).equals("[b][b][b]")){
				result = result.append("...");
			}
			if (arr.get(x).equals("[b]")){
				flag = false;
				result = result.append(".");
			}
			else{
				result = result.append(arr.get(x)+" ");
			}	
		}
		return result;
	}
	
	/** Returns a hashtable of words and linked hashtables of words and their cumulative
	 * probabilities, which is used in sentence generation*/
	public static HashMap<String, LinkedHashMap<String, Float>> 
	cumProb(HashMap<String, HashMap<String, Float>> map){
		HashMap<String, LinkedHashMap<String, Float>> outer = 
				new HashMap<String, LinkedHashMap<String, Float>>();
		for (Entry <String,HashMap<String, Float>> m :map.entrySet()){
			float sum = 0;
			HashMap<String, Float> innerOg = map.get(m.getKey());
			LinkedHashMap<String, Float> innerNew = new LinkedHashMap<String, Float>();
			for (Entry <String, Float> n: innerOg.entrySet()){
				Float prob = innerOg.get(n.getKey());
				sum=sum+prob;
				innerNew.put(n.getKey(), sum);
			}
			outer.put(m.getKey(), innerNew);
			
		}
		return outer;
	}
	
	/** Given a word, uses the cumulative probabilities from 
	 * the hashamp to return the next word*/
	public static String nextWord(String prev, HashMap<String, LinkedHashMap<String, Float>> map){
		double random = Math.random();
		//System.out.println(random);
		String next = "";
		//System.out.println(prev);
		LinkedHashMap<String, Float> lm =  map.get(prev);
		for (Entry<String, Float> x :lm.entrySet()){
			 if (random<x.getValue()){
				 next=x.getKey();
				 return next;
			 }
		}
		return next;
	}
	
	/** Generates a sentence using the bigram model*/
	public static String genSentenceBigram(String sentence, HashMap<String, 
			LinkedHashMap<String, Float>> cumBiprobz){
		StringBuffer result = new StringBuffer();
		String word = lastWord(sentence);
		boolean flag=true;
		if (!sentence.equals("[b]")){
			result.append(sentence+ " ");
		}
		while (flag){
			word = nextWord(word, cumBiprobz);
			if (word.equals("[b][b][b]")){
				result = result.append("...");
			}
			if (word.equals("[b]")){
				result.append(".");
				flag=false;
			}
			else{
				result.append(word + " ");
			}
		}
		return result.toString();
	}
	
	/** Returns the last word in a sentence*/
	public static String lastWord(String sentence){
		int x = sentence.lastIndexOf(" ");
		if (x==-1){
			return sentence;
		}
		else{
			return sentence.substring(x+1, sentence.length());
		}
	}
}
	
	
} 
	

