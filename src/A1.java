import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;



public class A1 {
	public static void main(String[] args){
		getFiles("/Users/rentaluser/Documents/workspace/AS1/data_corrected/classification task/atheism/train_docs"); //try 0
		String result=getFiles("/Users/rentaluser/Documents/workspace/AS1/data_corrected/classification task/atheism/train_docs/"); 
		ArrayList<String> arr = makeArrayList(result);
		//System.out.println(arr.size());
		//HashMap<String, Float> x= probHash(arr);
		//System.out.println(unigram("aklZJBSDkla", x));
		HashMap<String, HashMap<String, Float>> y= bigramHash2(arr);
		System.out.println(bigram("science","[b]", y));
		//System.out.println(count2("science", "[b]", arr));
		//System.out.println(count("[b]", arr));
		
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
		String result = s.replaceAll("[-+^:\"></\\()|#]", "");
		String re= result.replaceAll("[.?!]", "[b]");
		re= re.replace(". ", " [b]");
		return re;
	}
	
	/**Takes an arraylist of words (strings) and returns a hashmap of the probabilities associated with each word*/
	static HashMap<String,Float> probHash(ArrayList<String> arr){
		HashMap<String,Float> wordcounts = new HashMap<String, Float>();
		for(String word: arr){
			if (!wordcounts.containsKey(word.toLowerCase())){
				wordcounts.put(word.toLowerCase(),(float) count(word,arr)/arr.size());
			}
		}

		System.out.println(wordcounts);
		return wordcounts;
	}
	
/**Given a word, counts the number of occurrences of that word in the arraylist */
	public static int count(String a, ArrayList<String> arrlst){
		int i=0;
		//System.out.println("here");
		for (String s:arrlst){
			if (s.equals(a)){
				i++;
			}
		}
		return i;
	}
	
	/**Given a word, returns the probability of that word */
	static float unigram(String word, HashMap<String,Float> wordcounts){
		if (wordcounts.get(word)!=null){
			return wordcounts.get(word);
		}
		return 0;
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

	/**Given an arraylist of words, returns a hashmap of words and the probability of that word combined with each other word*/
	public static HashMap<String, HashMap<String, Float>> bigramHash(ArrayList<String> arr){
		
		HashMap<String, HashMap<String, Float>> outerHash = new HashMap<String, HashMap<String, Float>>();
		
		for(String x: arr){
			System.out.println("here");
			HashMap<String, Float> innerHash = new HashMap<String, Float>();
			if (!outerHash.containsKey(x)){
				System.out.println("here1");
				for (String y:arr){
					System.out.println("here3");
					if (!innerHash.containsKey(y)){
						innerHash.put(y, bigramProb(y,x, arr));	
					}
				}
				outerHash.put(x, innerHash);	
			}	
		}
		return outerHash;
	}
	
/*	public static HashMap<String, HashMap<String, Float>> bigramHash3(ArrayList<String> arr){
		
		HashMap<String, Word> outerHash = new HashMap<String, Word>();
		for(int i=0; i<arr.size(); i++){
			if (!outerHash.containsKey(arr.get(i))){
			//how do I create a new name for each word..
				Word hi = new Word(arr.get(i)); //consider a new class for each word, adv disadv compared to hashmap?
				hi.count +=1;
				
			
			
		}
	} */
	
	/**Another way of doing the function above?*/
	public static HashMap<String, HashMap<String, Float>> bigramHash2(ArrayList<String> arr){
		
		HashMap<String, HashMap<String, Float>> outerHash = new HashMap<String, HashMap<String, Float>>();
		int count=0; //this is counting the total words in arr, not what we want
		for(String x: arr){
			System.out.println(count);
			HashMap<String, Float> innerHash = new HashMap<String, Float>();
			if (!outerHash.containsKey(x)){
				System.out.println("here2");
					String y = arr.get(count+1); //i think this is generally right, is there something we can do to not call count
					if (!innerHash.containsKey(y)){
						System.out.println("here3");
						innerHash.put(y, bigramProb(y,x, arr));	
					}
			
				outerHash.put(x, innerHash);	
			}
			count=count+1;
		}
		return outerHash;
	}
	
	/** Given two words and an arraylist of words, return the probability of that combination of words*/
	public static float bigramProb(String a, String b, ArrayList<String> arr){
		
		return (float) count2(a,b, arr)/count(b, arr);
	}
	
	/** Takes two words and returns the number of occurences of those two words */
	public static int count2(String a, String b, ArrayList<String> arrlst){
		int count=0;
		for (int i=0; i<arrlst.size()-1; i++){
			
			if (arrlst.get(i).toLowerCase().equals(a.toLowerCase())){
				if (arrlst.get(i+1).toLowerCase().equals(b.toLowerCase())){
					
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**given two strings and a hashtable of probabilities, return the probability of that combo of strings*/
	public static float bigram(String a, String b, HashMap<String, HashMap<String, Float>> hash){
		System.out.println("here2");
		try{
			return hash.get(b).get(a);
		}
		catch (NullPointerException exc){
		
		}
		return (float) 0;
	}
	

} 


