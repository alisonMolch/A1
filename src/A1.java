import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JTable;



public class a1 {
	/**Main class runs our processor. It reads files from the designated path,
	 * pre-processes them, and can be used to run our unigram and bigram models.
	 * @param args
	 */
	private static String[] models = {"atheism", "autos", "graphics", "medicine", "motorcycles", "religion", "space"};
	private static String modelPath= "/Users/alisonmolchadsky/Documents/workspace/A1/data_corrected/classification task/";
	private static String modelPathSpellCheck = "/Users/alisonmolchadsky/Documents/workspace/A1/data_corrected/spell_checking_task/";
	private static HashMap<String, ArrayList<String>> confusion = tokensConfusion("/Users/alisonmolchadsky/Documents/workspace/A1/data_corrected/spell_checking_task/confusion_set.txt");
	
	public static void main(String[] args){
//		getFiles("religion");
		String result=getFiles("atheism"); 
		ArrayList<String> arr = makeArrayList(result);
		//System.out.println(arr.size());
		HashMap<String, Integer> x= unigramCounts(arr);
	
		//System.out.println(x);
		HashMap<Integer, Integer> coc = countsOfCountsUnigram(x);
		HashMap<String, Float> gtu = goodTuringUnigram(x, coc);
		HashMap<String, Float> usph= unigramSmoothedProbHashmap(gtu, arr);
		//System.out.println(usph);
		//System.out.println(unigramSmoothed("Reserve", usph));
		//System.out.println(unigramSmoothedCount("frowning", gtu));
		//System.out.println(x.get("<unk>"));
		//System.out.println(arr);
//		HashMap<String, Float> y = unigramProbHashmap(x);
		//System.out.println(y);
		//System.out.println(unigram("science", y));
		
		HashMap<String, HashMap<String, Integer>> bc = bigramCounts(arr);
		//System.out.println(bc);
		HashMap<Integer, Integer> cofcb = countOfCountBigram(bc);
		//System.out.println(cofcb);
		HashMap<String, HashMap<String, Float>> gtb =goodTuringBigram(bc, cofcb);
		HashMap<String, HashMap<String, Float>> bphs= bigramProbHashmapSmoothed(gtb);
		//HashMap<String, HashMap<String, Float>> z = bigramProbHashmap(bc, x);
		//System.out.println(bphs);
		
		//System.out.println(bigram("<unkb>", "<unkb>", z));
		//System.out.println(unigramRandomSentence("",arr));
		//System.out.println(sorted(arr));
		//System.out.println(unigram("aklZJBSDkla", x));
		//System.out.println(count2("science", "[b]", arr));
		//System.out.println(count("[b]", arr));
		//HashMap<String, LinkedHashMap<String, Float>> cum = cumProb(z);
		//System.out.println(cumProb(z));
		//System.out.println(nextWord("half", cum));
		//System.out.println(genSentenceBigram("My favorite sex position is",cum));
		ArrayList<String> testString =tokenizeTest("/Users/alisonmolchadsky/Documents/workspace/A1/data_corrected/classification task/test_for_classification/file_000.txt");
		//System.out.println(Math.log(0));
		//System.out.println(bigramSmoothed("horrible", "something", bphs));
		//System.out.println(perplexity(bphs, testString));
		//System.out.println(perplexityU(usph, testString));
		//System.out.println(findperplex("atheism", "file_029.txt"));
		//System.out.println(classification("file_200.txt"));
		//System.out.println(classificationU("file_200.txt"));
		//System.out.println(confusion);
		//System.out.println(correctSpell("atheism", "atheism_file1_modified.txt"));
		readToFile();
		
	}
	
	/** Takes a file path and retrieves all the files in that path as a string */
	static String getFiles(String model){
		File f = new File(modelPath+"/"+model+"/"+"train_docs");
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
	
	static String getFilesSpellCheck(String model){
		File f = new File(modelPathSpellCheck+"/"+model+"/"+"train_docs");
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		String result="";
		for (File x :files){
			try {
				byte[] encoded = Files.readAllBytes(Paths.get(x.getPath()));
				String strfile = new String(encoded, StandardCharsets.US_ASCII);
				result = result+strfile;
				
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
		}
		return result;
	}
	
	static String getContents(File x){
		String result="";
		try {
			result = result +readFile(x.getPath(), StandardCharsets.US_ASCII);
			
		} catch (Exception e) {
			e.printStackTrace();
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
				innerhash.put("<unkb>", 0);
				innerhash.put(arr.get(i+1),1);
				outerHash.put(arr.get(i), innerhash);
			}
		}
		return outerHash;
	}
	
	/**Given an arraylist of words, returns a hashmap of the counts of each word*/
	public static HashMap<String, Integer> unigramCounts(ArrayList<String> arr){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put("<unk>", 0);
		for (String x:arr){
			if(!result.containsKey(x)){
				result.put(x, 0);
				arr.set(arr.indexOf(x), "<unk>");
				result.put("<unk>", result.get("<unk>")+1);
				
			}
			else{
				result.put(x, result.get(x)+1);
			}
		}
		Iterator it = result.entrySet().iterator();
		while(it.hasNext()){
			HashMap.Entry pair = (HashMap.Entry)it.next();
			if (result.get(pair.getKey()).equals(0)){
				it.remove();
				result.remove(pair.getKey());
				
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
	public static HashMap<String, LinkedHashMap<String, Float>> cumProb(HashMap<String, HashMap<String, Float>> map){
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
		String next = "";
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
	
	/*Good turing counts for unigram, takes in the frequency unigram hashmap<string, integer>
	 * 
	 */
	
	public static HashMap<Integer, Integer> countsOfCountsUnigram(HashMap<String, Integer> map){
		HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
		for (Entry<String, Integer> x :map.entrySet()){
			if (x.getValue()<12){
				if (!h.containsKey(x.getValue())){
					h.put(x.getValue(), 1);
				}
				else{
					h.put(x.getValue(), h.get(x.getValue())+1);
				}
				
			}
		}
		return h;
	}
	
	public static HashMap<String, Float> goodTuringUnigram(HashMap<String, Integer>uni, HashMap<Integer, Integer> counts){
		HashMap<String, Float> h = new HashMap<String, Float>();
		for (Entry<String, Integer> x: uni.entrySet()){
			int c = x.getValue();
			int c1=c+1;
			if(counts.containsKey(c)){
				int nc= counts.get(c);
				if (counts.containsKey(c+1)){
					int nc1 = counts.get(c+1);
					float cnew=(float)c1*((float)nc1/(float)nc);
					h.put(x.getKey(), cnew);
				}
			}
			else{
				h.put(x.getKey(), (float) x.getValue());
			}
			
		}
		return h;
	}
	public static HashMap<String, Float> unigramSmoothedProbHashmap(HashMap<String, Float> map, ArrayList<String> arr){
		HashMap<String, Float> result = new HashMap<String, Float>();

		float sum = 0;
		for (Entry<String, Float> entry: map.entrySet()){
			float x = entry.getValue();
			sum = sum+x;
		}
		for (Entry<String, Float> z: map.entrySet()){
			float x = z.getValue();
			String y = z.getKey();
			result.put(y, (float)x/((float)sum));
					
		}
		return result;
		
		
	}
	
	
	public static Float unigramSmoothedCount(String x, HashMap<String, Float> count){
		return count.get(x);
	}
	
	public static HashMap<Integer, Integer> countOfCountBigram(HashMap<String, HashMap<String, Integer>> bigrams){
		HashMap<Integer, Integer> cofc = new HashMap<Integer, Integer>();
		int acc=0;
		for (Entry<String, HashMap<String, Integer>> el: bigrams.entrySet()){
			HashMap<String, Integer>innerHt = el.getValue();
			for (Entry<String, Integer> x:innerHt.entrySet()){
				if (x.getValue()<8 && x.getValue()>0){
					if (!cofc.containsKey(x.getValue())){
						cofc.put(x.getValue(), 1);
					}
					else{
						cofc.put(x.getValue(), cofc.get(x.getValue())+1);
					}
				}
				
			}
			cofc.put(0, bigrams.size()*bigrams.size()-acc);
		}
		return cofc;
	}
	
	public static HashMap<String, HashMap<String, Float>> goodTuringBigram(HashMap<String, HashMap<String, Integer>> bicounts, HashMap<Integer, Integer> cofcb){
		HashMap<String, HashMap<String, Float>> smoothed = new HashMap<String, HashMap<String, Float>>();

		for (Entry<String, HashMap<String, Integer>> el: bicounts.entrySet()){
			HashMap<String, Float> turing = new HashMap<String, Float>();
			HashMap<String, Integer>innerHM=el.getValue();
			for (Entry<String, Integer> x: innerHM.entrySet()){
				int c = x.getValue();
				if (cofcb.containsKey(c)){
					int c1 = c+1;
					float nc=cofcb.get(c);
					if (cofcb.containsKey(c1)){
						float nc1=cofcb.get(c1);
						float cnew = (float)c1*(nc1/nc);
						turing.put(x.getKey(), cnew);
					}
					else{
						turing.put(x.getKey(), (float)x.getValue());
					}
				}
				else{
					turing.put(x.getKey(), (float)x.getValue());
				}
				
			}
			
			smoothed.put(el.getKey(), turing);
		}
		return smoothed;
	}
	
	public static HashMap<String, HashMap<String, Float>> bigramProbHashmapSmoothed
	(HashMap<String, HashMap<String, Float>> map){
		HashMap<String, HashMap<String, Float>> outer = 
				new HashMap<String, HashMap<String, Float>>(); 
		int size = map.size();
		for (Entry<String, HashMap<String, Float>> entry : map.entrySet()){
			float count = 0;
			int countNotZero = -1; //because there is one count of 0;
			//System.out.println("here");
			float updatedZero = map.get(entry.getKey()).get("<unkb>");
			for (Entry<String, Float> innerEntry: map.get(entry.getKey()).entrySet()){
				Float x = map.get(entry.getKey()).get(innerEntry.getKey());
				count = x+count; 
				countNotZero=countNotZero+1;
			} 
			int countZero = size-countNotZero;
			//Integer countOuter = unigramCounts.get(entry.getKey());
			HashMap<String, Float> inner = new HashMap<String, Float>();
			for (Entry<String, Float> innerEntry: map.get(entry.getKey()).entrySet()){
				Float x = map.get(entry.getKey()).get(innerEntry.getKey());
				count =count+countZero*updatedZero;
				float prob = x/count;
				inner.put(innerEntry.getKey(), prob);
			}
			outer.put(entry.getKey(), inner);	
		}
		//System.out.println(outer);
		return outer;
	}
	

	public static float bigramSmoothed(String a, String b, HashMap<String, HashMap<String, Float>> map){
		if (!map.containsKey(b)){
			b="<unk>";
		}
		if (!map.containsKey(a)){
			a="<unk>";
		}
		
		try{
			return map.get(b).get(a);
		}
		catch (NullPointerException e){	
			a="<unkb>";
			return map.get(b).get(a);
		}

	}
	
	public static ArrayList<String> tokenizeTest(String path){
		File x = new File(path);
		return makeArrayList(getContents(x));
	}
	
	public static ArrayList<String> tokenizeSpell(String path){
		File x = new File(path);
		String result="";
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			String strfile = new String(encoded, StandardCharsets.US_ASCII);
			result = result+strfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> myList = new ArrayList<String>(Arrays.asList(result.split("\\s")));
		myList.removeAll(Arrays.asList("", null));
		return myList;
		
		
	}
	public static HashMap<String, ArrayList<String>> tokensConfusion(String path){
		File x = new File(path);
		HashMap<String, ArrayList<String>> confMap = new HashMap<String, ArrayList<String>>();
		try{
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			String strfile = new String(encoded, StandardCharsets.US_ASCII);
			ArrayList<String> y = makeArrayList(strfile);
			
			y.set(0, "went");
			int i = 0;
			while (i<y.size()){
				String pair = "";
				ArrayList<String> inner = new ArrayList<String>();
				if (i%2==0){
					pair = y.get(i+1);
					inner.add(pair);
				}
				if (i%2!=0){
					pair = y.get(i-1);
					inner.add(pair);
				}
				if (!confMap.containsKey(y.get(i))){
					confMap.put(y.get(i), inner);
				}
				else{
					confMap.get(y.get(i)).add(pair);
				}
				
				i++;
			}
		}
		catch (Exception e){
				
		}
		

	return confMap;

		
	}
	
	public static float perplexity(HashMap<String, HashMap<String, Float>> turBiProb, ArrayList<String> test){
		float sum=0;
		int i=1;
		float n = test.size();
		while(i<n){
			float x = bigramSmoothed(test.get(i), test.get(i-1), turBiProb);
			sum = sum -(float)(Math.log(x));
			
			
			
			i++;
		}
		
		return (float)Math.exp(sum/n);
	}
	
	public static float perplexityU(HashMap<String, Float> uniprobhash, ArrayList<String> test){
		float sum = 0;
		int i = 0;
		int n= test.size();
		while (i<n-1){
			String word = test.get(i);
			float prob = unigramSmoothed(word, uniprobhash);
			sum = sum - (float)Math.log(prob);
			i++;
		}
		return (float)Math.exp(sum/n);
	}
	
	public static Float unigramSmoothed(String x, HashMap<String, Float> uniprobhash){
		if (!uniprobhash.containsKey(x)){
			x = "<unk>";
		}
		try {
			return uniprobhash.get(x);
		}
		catch (NullPointerException e){
			return uniprobhash.get("<unk>");
		}
	}
	
	public static HashMap<String, HashMap<String, Float>> bigramProbHashmapSmoothed
	(HashMap<String, HashMap<String, Float>> map, HashMap<String, Integer> unigramCounts){
		HashMap<String, HashMap<String, Float>> outer = 
				new HashMap<String, HashMap<String, Float>>(); 
		for (Entry<String, HashMap<String, Float>> entry : map.entrySet()){
			float count = 0;
			for (Entry<String, Float> innerEntry: map.get(entry.getKey()).entrySet()){
				Float x = map.get(entry.getKey()).get(innerEntry.getKey());
				count = x+count; 
			} 
			HashMap<String, Float> inner = new HashMap<String, Float>();
			for (Entry<String, Float> innerEntry: map.get(entry.getKey()).entrySet()){
				Float x = map.get(entry.getKey()).get(innerEntry.getKey());
				float prob = x/count;
				inner.put(innerEntry.getKey(), prob);
			}
			outer.put(entry.getKey(), inner);	
		}

		return outer;
	}


	public static String classification(String file){
		ArrayList<String> teststr = tokenizeTest(modelPath+"/test_for_classification/"+file);
		Float min = (float)Integer.MAX_VALUE;
		String result = "medicine";
		for (String model:models){
			String resultstr=getFiles(model); 
			
			ArrayList<String> arr = makeArrayList(resultstr);
			HashMap<String, Integer> x= unigramCounts(arr);
			HashMap<String, HashMap<String, Integer>> bc = bigramCounts(arr);
			HashMap<Integer, Integer> cofcb = countOfCountBigram(bc);
			
			HashMap<String, HashMap<String, Float>> gtb =goodTuringBigram(bc, cofcb);
			
			HashMap<String, HashMap<String, Float>> bphs= bigramProbHashmapSmoothed(gtb);
			Float p =perplexity(bphs, teststr);
			if (p<min){
				result = model;
				min=perplexity(bphs,teststr);
			}
		}
		return result;
	}
	
	public static String classificationU(String file){
		ArrayList<String> teststr = tokenizeTest(modelPath+"/test_for_classification/"+file);
		Float min = (float)Integer.MAX_VALUE;
		String result = "medicine";
		for (String model:models){
			String resultstr=getFiles(model); 
			
			ArrayList<String> arr = makeArrayList(resultstr);
			HashMap<String, Integer> uc = unigramCounts(arr);
			HashMap<Integer, Integer> cofcu = countsOfCountsUnigram(uc);
			
			HashMap<String, Float> gtu =goodTuringUnigram(uc, cofcu);
			
			HashMap<String, Float> uphs= unigramSmoothedProbHashmap(gtu, arr);
			Float p =perplexityU(uphs, teststr);
			if (p<min){
				result = model;
				min=perplexityU(uphs,teststr);
			}
		}
		return result;
	}
	
	
	public static String correctSpell(String model, String testFile){
		//System.out.println(confusion);
		StringBuffer result  = new StringBuffer();
		String resultstr=getFilesSpellCheck(model); 
		ArrayList<String> arr = makeArrayList(resultstr);
		HashMap<String, Integer> x= unigramCounts(arr);
		HashMap<String, HashMap<String, Integer>> bc = bigramCounts(arr);
		HashMap<Integer, Integer> cofcb = countOfCountBigram(bc);
		HashMap<String, HashMap<String, Float>> gtb =goodTuringBigram(bc, cofcb);
		HashMap<String, HashMap<String, Float>> bphs= bigramProbHashmapSmoothed(gtb);
		ArrayList<String> test = tokenizeSpell(modelPathSpellCheck+"/"+model+"/test_modified_docs/"+testFile);
		//System.out.println(test);

		for (String word: test){
			String prev = "[b]";
			String next = "[b]";
			try{
				if(prev.equals(word)){
					prev = test.get((test.indexOf(word)-2));
				}
				prev = test.get((test.indexOf(word)-1));
			}
			
			catch (Exception e){
				//System.out.println("here");
				
			}
			try {
				next = test.get((test.indexOf(word)+1));
			}
			
			catch(Exception e){
				
			}
			if (confusion.containsKey(word)){
				String higher=word;
				Float higherProb = compareBigram(prev, next, word, bphs);
				for(String wordConf:confusion.get(word)){
					if (compareBigram(prev, next, wordConf, bphs)>higherProb){
						//System.out.println(wordConf);
						higherProb  = compareBigram(prev, next, wordConf, bphs);
						higher = wordConf;
					}
				}
				
				result.append(higher+" ");
				
			}
			else{
				result.append(word+" ");
			}
		}
		return result.toString();
	}
	/*
	 * Compares the the bigrams P(word1|word) and P(word2|word) and returns the word that gives the higher probability
	 */
	public static Float compareBigram(String prev, String next, String word, HashMap<String, HashMap<String, Float>> bigrams){
		//System.out.println(prev + " " + word + " " + next + " "+bigramSmoothed(word, prev, bigrams)*(bigramSmoothed(next, word, bigrams)));
		return bigramSmoothed(word, prev, bigrams)*(bigramSmoothed(next, word, bigrams));
	}
	
//	public static String correctSpell2(String model, String testFile){
//		System.out.println(confusion);
//		StringBuffer result  = new StringBuffer();
//		String resultstr=getFilesSpellCheck(model); 
//		ArrayList<String> arr = makeArrayList(resultstr);
//		HashMap<String, Integer> x= unigramCounts(arr);
//		HashMap<String, HashMap<String, Integer>> bc = bigramCounts(arr);
//		HashMap<Integer, Integer> cofcb = countOfCountBigram(bc);
//		HashMap<String, HashMap<String, Float>> gtb =goodTuringBigram(bc, cofcb);
//		HashMap<String, HashMap<String, Float>> bphs= bigramProbHashmapSmoothed(gtb);
//		ArrayList<String> test = tokenizeTest(modelPathSpellCheck+"/"+model+"/test_modified_docs/"+testFile);
//		return "";
//	}
	
	public static void readToFile(){
		
		for (String model: models){
			File f = new File(modelPathSpellCheck+"/"+model+"/"+"test_modified_docs/");
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
			for(File x: files){
				try{
					String name = x.getName();
					String corrected = correctSpell(model, name);
					FileWriter writer = new FileWriter(modelPathSpellCheck+"/" + model+"/test_docs/"+ name, true);
					writer.write(corrected);
					writer.close();
					
				}
				catch (Exception e){
					System.out.println("here");
				}
				
			}
		}
	}
	
	public static Float trigramGetProb(String x, String y, String z, HashMap<String, HashMap<String, HashMap<String, Float>>> triprobhash){
		return triprobhash.get(x).get(y).get(z);

	}
	
	/**Given an arraylist of words, returns a hashmap within a hashmap of the counts 
	 * of each combination of 2 words*/
	public static HashMap<String,HashMap<String, HashMap<String, Integer>>> trigramCounts(ArrayList<String> arr){
		HashMap<String, HashMap<String, HashMap<String, Integer>>> outerHash = 
				new HashMap<String,HashMap<String, HashMap<String, Integer>>>();
		for(int i=0; i<arr.size()-2; i++){
			if (outerHash.containsKey(arr.get(i))){
				HashMap<String,HashMap<String,Integer>>midhash = outerHash.get(arr.get(i));
				if (midhash.containsKey(arr.get(i+1))){
					HashMap<String,Integer> innerhash = midhash.get(arr.get(i+1));
					if (innerhash.containsKey(arr.get(i+2))){
						innerhash.put(arr.get(i+2), innerhash.get(arr.get(i+2))+1);	
					}
					else{
						innerhash.put(arr.get(i+2), 1);
					}
					midhash.put(arr.get(i), innerhash);
				}
				else{
					HashMap<String,Integer> innerhash = new HashMap<String,Integer>();
					innerhash.put(arr.get(i+2), 1);
					midhash.put(arr.get(i+1), innerhash);
				}
			}
			else{
				HashMap<String,Integer> innerhash = new HashMap<String, Integer>();
				HashMap<String,HashMap<String,Integer>> midhash = new HashMap<String, HashMap<String,Integer>>();
				innerhash.put(arr.get(i+2),1);
				midhash.put(arr.get(i+1),innerhash);
				outerHash.put(arr.get(i), midhash);
			}
		}
		return outerHash;
	}
	/**Given a hashmap of words and their counts, return a hashmap of 
			 * pairs of words and their probabilities using a trigram model*/
		public static HashMap<String, HashMap<String, HashMap<String, Float>>> trigramProbHashmap
			(HashMap<String, HashMap<String, HashMap<String, Integer>>> trigramCounts, HashMap<String, HashMap<String, Integer>> bigramCounts){
				HashMap<String, HashMap<String, HashMap<String, Float>>> outer = new HashMap<String, HashMap<String, HashMap<String, Float>>>(); 
			//	System.out.println("why");
				for (Entry<String, HashMap<String, HashMap<String, Integer>>> entry : trigramCounts.entrySet()){
					HashMap<String, HashMap<String,Float>> mid = new HashMap<String, HashMap<String,Float>>();
					//System.out.println("no error yet");
					for (Entry<String, HashMap<String, Integer>> midentry : trigramCounts.get(entry.getKey()).entrySet()){
						Integer countOuter = bigramCounts.get(entry.getKey()).get(midentry.getKey()); 
						//System.out.println("get first key  " + bigramCounts.get(entry.getKey()));
						//System.out.println("get next key  " + bigramCounts.get(entry.getKey()).get(midentry.getKey()));
						
						HashMap<String, Float> inner = new HashMap<String, Float>();
						//System.out.println("ok ok");
						for (Entry<String, Integer> innerEntry: trigramCounts.get(entry.getKey()).get(midentry.getKey()).entrySet()){
							Integer x = trigramCounts.get(entry.getKey()).get(midentry.getKey()).get(innerEntry.getKey());
							float prob = 0;
							try{
								prob = (float) x/ (float) countOuter;
							}
							catch (NullPointerException e){
								prob = 0;
							}
							inner.put(innerEntry.getKey(), prob);
							//inner.put("<unkb>", (float)0);
					//		System.out.println("wow");
						}
						mid.put(entry.getKey(), inner);	
					}
					outer.put(entry.getKey(), mid);	
				}
				return outer;
			}
		
		public static float perplexityTri(HashMap<String, HashMap<String, HashMap<String, Float>>> triProb, ArrayList<String> test){
			float sum=0;
			int i=2;
			float n = test.size();
			while(i<n){
				//System.out.println("here");
				float x= 1;
				try {
					x = trigramGetProb(test.get(i), test.get(i-1), test.get(i-2), triProb);
				}
				catch (NullPointerException e){
					
				}
				//System.out.println(x);
				//System.out.println(Math.log());
				sum = sum -(float)(Math.log(x));
				
				
				
				i++;
			}
			//System.out.println(1/n);
			//System.out.println(sum);
			//System.out.println(Math.exp(1/n));
			
			return (float)Math.exp(sum/n);
		}
	
	

}
	
		
	
