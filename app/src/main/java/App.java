

import java.util.ArrayList;
import java.util.function.Consumer;

import com.mins01.java.PickupKeywords.*;


/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args){
		
		
		Consumer<Object> print = new Consumer<Object>() {
			public void accept(Object x) {
				System.out.println(x);
			}
		};

		
		//========================
		System.out.println("START");
		//========================		
		PickupKeywords pk = new PickupKeywords();
		pk.init();
		ArrayList<TextInfo> texts = null;
		ArrayList<WordInfo> words = null;
		GetAppInfoByPackagename gaibp = new GetAppInfoByPackagename();
		gaibp.init();
		String url = "http://domeggook.com/main/item/itemList.php?sfc=ttl&sf=ttl&sw=%C1%F6%BF%EC%B0%B3";
		
//		System.out.println("====================");
//		url = "https://www.amazon.com/s?k=speaker&ref=nb_sb_noss_1";
//
//		String html = pk.getHTML(url);
//		Files.write(Paths.get("c:/temp/xx.txt"), html.getBytes());
//		return;
		
		
		System.out.println("====================");
		System.out.println("url : "+url);
		try{
			pk.setUrl(url);
			texts= pk.getTexts(); //get texts
			words= pk.getWords(texts); // generate words from texts
			words.subList(0, Math.min(10, words.size())).forEach(print);	
			texts.forEach(print);	

		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
		// System.exit(0);
		System.out.println("====================");
		
		url = "https://www.amazon.com/s?k=speaker&ref=nb_sb_noss_1";
		System.out.println("url : "+url);
		try{
			pk.setUrl(url);
//			pk.getTags().forEach(print);
//			System.exit(0);

			texts= pk.getTexts(); //get texts
			words= pk.getWords(texts); // generate words from texts
			words.subList(0, Math.min(10, words.size())).forEach(print);	
			System.out.println(">> setting .numericWeight = 0.1 <<");
			System.out.println(">> setting .wordToLowerCase = true <<");

			pk.numericWeight = 0.1; //숫자 가중치 0으로 설정
			pk.wordToLowerCase = true; //단어를 무조건 소문자로 처리함

			words= pk.getWords(texts); // generate words from texts
			words.subList(0, Math.min(10, words.size())).forEach(print);
			pk.numericWeight = 1; //숫자 가중치 1로 되돌림
			pk.wordToLowerCase = !pk.wordToLowerCase; //되돌림

		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
//		System.exit(0);
		System.out.println("====================");
		
		url = "https://www.amazon.com/s?k=speaker&ref=nb_sb_noss_1";
		System.out.println("url : "+url);
		try{
			pk.numericWeight = 0; //숫자 가중치 0으로 설정

			pk.setUrl(url);
			texts= pk.getTexts(); //get texts

		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
		
		pk.numericWeight = 1; //숫자 가중치 1로 되돌림
		pk.wordToLowerCase = !pk.wordToLowerCase; //되돌림
		
		System.out.println("====================");
		url = "http://aldkjlkjasdlja.asdjkljasd/main/";
		System.out.println("url : "+url);
		try{
			pk.setUrl(url);
			texts= pk.getTexts(); //get texts
			words= pk.getWords(texts); // generate words from texts
			words.subList(0, 10).forEach(print);	
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
		System.out.println("====================");
		url = "https://www.google.com/asd";
		System.out.println("url : "+url);
		try{
			pk.setUrl(url);
			texts= pk.getTexts(); //get texts
			words= pk.getWords(texts); // generate words from texts
			words.subList(0, 10).forEach(print);	
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
		
		
		//========================
		System.out.println("====================");
		String pkname = "com.mins01.othello001";
		System.out.println("packagename : "+pkname);
		try{
			gaibp.setPackagename(pkname);
			texts= gaibp.getTexts(); //get texts

			words= gaibp.getWords(texts); // generate words from texts
			words.subList(0, 10).forEach(print);
			
//			texts.forEach(print);
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("ERROR");
		}
		//========================
		System.out.println("====================");
		System.out.println("END");
		//========================
		
	}
}
