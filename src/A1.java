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
	public static void main(String[] args){
		getFiles("/Users/rentaluser/Documents/workspace/AS1/data_corrected/classification task/atheism/train_docs"); //try 0
		String result=getFiles("/Users/rentaluser/Documents/workspace/AS1/data_corrected/classification task/atheism/train_docs/"); 
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
	
	/** Takes a file path and returns the path as a string */
	static String getFiles(String path){
		File f = new File(path);
		//System.out.println(f.exists());
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
	//	System.out.println(files);
		//int y =0;
		String result="";
		for (File x :files){
			try {
				result = result +readFile(x.getPath(), StandardCharsets.US_ASCII);
				//y=y+1;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
		//System.out.println(result);
		//makeArrayList(result);
		return result;
		//System.out.println(y);
	}
	
	/**Takes a filepath and a set of characters and reads the files in that path. Returns a processed String
	 * of file content 
	 */
	static String readFile(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		// System.out.println(new String(encoded, encoding));
		String strfile = new String(encoded, encoding);
		//System.out.println(strProcess(removeHeader(strfile)));
		return strProcess(removeHeader(strfile));
		//return strProcess(removeHeader(strfile));
	}
	
	/**Removes the email header of a file */
	static String removeHeader(String file){
		int stEmail = 0;
		if (file.contains("writes")){
		//	System.out.println("stEmail");
			stEmail = file.indexOf("writes")+6;
//			System.out.println("EMAIL");
			//System.out.println(stEmail);
		}
		else if (file.contains("wrote")){
			//System.out.println("CASEEED");
//			System.out.println("EMAILEMAIL");
			stEmail = file.indexOf("wrote")+5;
		}
		else{
			int atindex = file.indexOf('@');
//			System.out.println("atindex is    " + atindex);
			String email = file.substring(atindex, file.length());
			int endEmail = email.indexOf(" ");
//			System.out.println("endemail is    " + endEmail);
//	
//			System.out.println("EMAILEMAILEMAIL");
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
	

	/**Given a string, makes an arraylist where each word is a separate element */
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

	public static HashMap<String, HashMap<String, Integer>> bigramCounts(ArrayList<String> arr){
		HashMap<String, HashMap<String, Integer>> outerHash = new HashMap<String,HashMap<String, Integer>>();
		for(int i=0; i<arr.size()-2; i++){
			if (outerHash.containsKey(arr.get(i))){
				
				HashMap<String,Integer>innerhash = outerHash.get(arr.get(i));
				
				if (innerhash.containsKey(arr.get(i+1))){
//					try{
						//System.out.println(arr.get(i+1));
						String x = arr.get(i+1);
						innerhash.put(arr.get(i+1), innerhash.get(arr.get(i+1))+1);
					
					
//					catch (NullPointerException e){
//						
//					}
					
				}
				else{
					//System.out.println(arr.get(i+1));
					innerhash.put(arr.get(i+1), 1);
				}
				//System.out.println(innerhash);
			}
			else{
				//System.out.println(arr.get(i));
				HashMap<String,Integer> innerhash = new HashMap<String, Integer>();
				innerhash.put(arr.get(i+1),1);
				outerHash.put(arr.get(i), innerhash);
			}
			
		}
		return outerHash;
	}
	
//	public static HashMap<String, HashMap<String, Integer>> bigramCounts1(ArrayList<String> arr){
//		HashMap<String, HashMap<String, Integer>> outerHash = new HashMap<String,HashMap<String, Integer>>();
//		
//		
//	}
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
	
	
	public static HashMap<String, HashMap<String, Float>> bigramProbHashmap(HashMap<String, HashMap<String, Integer>> map, HashMap<String, Integer> unigramCounts){
		HashMap<String, HashMap<String, Float>> outer = new HashMap<String, HashMap<String, Float>>(); 
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
	
	/*gives the bigram probability of string a given string b
	 * 
	 */
	public static float bigram(String a, String b, HashMap<String, HashMap<String, Float>> map){
		try{
			return map.get(b).get(a);
		}
		catch (NullPointerException e){
			
		}
		return (float) 0;
	}
	
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
	
	public static float unigram(String x, HashMap<String, Float> map){
		return map.get(x);
	}
	

	public static ArrayList<String> sorted(ArrayList<String> arr){
		Collections.sort(arr);
		return arr;
	}
	
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
	
	public static HashMap<String, LinkedHashMap<String, Float>> cumProb(HashMap<String, HashMap<String, Float>> map){
		
		HashMap<String, LinkedHashMap<String, Float>> outer = new HashMap<String, LinkedHashMap<String, Float>>();
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
	
	public static String genSentenceBigram(String sentence, HashMap<String, LinkedHashMap<String, Float>> cumBiprobz){
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
	

