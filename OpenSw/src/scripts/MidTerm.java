package scripts;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MidTerm {

	private String input_file;
	private String question;
	private ArrayList<String> questionExtracted = new ArrayList<String>();
	
	

	private File[] makeFileList(String path) {
		File dir = new File(path);
		if (dir.isFile()) {
			File[] f = new File[1];
			f[0] = dir;
			return f;
		} else {
			return dir.listFiles();
		}

	}

	public MidTerm(String path,String question) {
		this.input_file = path;
		this.question = question;
		questionExtracted = extractor(question);
		
		System.out.println("size" + questionExtracted.size());
	for ( int i = 0 ; i < questionExtracted.size(); i++) { 
	}
		showSnippet();
		
	}
	
	
	private ArrayList<String> extractor(String body) { 
		
		
		ArrayList<String> result = new ArrayList<String>();
		
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(body, true);

		
		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);
			result.add(kwrd.getString());
		}
		
		
		
		return result;
		
	}

	private void showSnippet() {

		File[] files = makeFileList("./collection.xml");

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

		try {
			ArrayList<String> titles = new ArrayList<String>();
			ArrayList<String> bodies = new ArrayList<String>();

			for (int i = 0; i < files.length; i++) {
				File file = files[i];

				if (file.getName().contains("collection.xml")) {
					org.jsoup.nodes.Document xml;

					xml = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());

					for (org.jsoup.nodes.Element e : xml.select("title")) {
						titles.add(e.text());
					}
					for (org.jsoup.nodes.Element e : xml.select("body")) {
						bodies.add(e.text());
					}
					break;
				}

			}
			
			String results = "";
			
			for ( int i = 0 ; i < bodies.size() ; i++) { 
				
				int highestmatchpoint = 0;
				String snippet = "";
		
				int head = 0; 
				int tail = 30;
				while( bodies.get(i).length() > tail) { 
					if ((tail % 200) == 0) { 
							System.out.println("찾는중입니다...");
					}
					String parsed = bodies.get(i).substring(head, tail);
					ArrayList<String> extracted = this.extractor(parsed);
					int matchpoint = 0;
					for (int j = 0 ; j < extracted.size(); j ++) { 
						for (int q = 0 ; q < questionExtracted.size();q++) {
							if (extracted.get(j).contains(this.questionExtracted.get(q))) { 
								matchpoint += 1;
							}
						}
					}
					
					if (matchpoint > highestmatchpoint) { 
						highestmatchpoint = matchpoint;
						snippet = parsed;
					}
					head += 1;
					tail += 1;
				}//문서 제목,스니펫, 점수 
				
				if (highestmatchpoint != 0) { 
					results += "\n" + titles.get(i) + "," + snippet + "," + highestmatchpoint + "점";
//					System.out.println(titles.get(i) + "," + snippet + "," + highestmatchpoint + "점");
				}
			
			}
			
			System.out.println(results);
			
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return;
	}

}
