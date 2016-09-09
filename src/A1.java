import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



public class A1 {
	public static void main(String[] args){
		getFiles(args[0]); //try 0
		//ArrayList<String> arr = makeArrayList(filestr);
		//addtohash(arr);
	}
	
	static void getFiles(String path){
		File f = new File(path);
		System.out.println(f.exists());
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
	//	System.out.println(files);
		int y =0;
		for (File x :files){
			try {
				readFile(x.getPath(), StandardCharsets.US_ASCII);
				//y=y+1;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
		//System.out.println(y);
	}
	
	static String readFile(String path, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		// System.out.println(new String(encoded, encoding));
		String strfile = new String(encoded, encoding);
		System.out.println(strProcess(removeHeader(strfile)));
		return strProcess(removeHeader(strfile));
	}
	
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
	
	static String strProcess(String s){
		String result = s.replaceAll("[-+^:></\\()|#]", "");
		String re= result.replaceAll("[.?!]", " [b]");
		return re;
	}
	
	static HashMap<String,Integer> addtohash(ArrayList<String> arr){
		HashMap<String,Integer> wordcounts = new HashMap<String, Integer>();
		for(String word: arr){
			if (wordcounts.containsKey(word)){
				wordcounts.put(word,wordcounts.get(word)+1);
			}
			else{
				wordcounts.put(word,1);
			}
		}
		return wordcounts;
	}
	
	static float probability(String word, HashMap<String,Integer> wordcounts, int length){
		float count = wordcounts.get(word);
		return count/length;
	}
	
	public static ArrayList<String> makeArrayList(String st){
		ArrayList<String> myList = new ArrayList<String>(Arrays.asList(st.split("\\s")));
		myList.removeAll(Arrays.asList("", null));
		for (String x:myList){
			if (x.contains("@")){
				myList.removeAll(Arrays.asList(x, null));
			}
		}
		return myList;
	}
	

} 
