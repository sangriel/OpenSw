package scripts;
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


public class indexer {

	private String input_file;
	private String output_file = "./index.post";
	
	
	
	
	
	public indexer(String path) { 
		this.input_file = path;
		
	}
	
	private File[] makeFileList(String path) {
		File dir = new File(path);
		if (dir.isFile()) { 
			File[] f = new File[1];
			f[0] = dir;
			return f;
		}
		else { 
			return dir.listFiles();
		}
		
	}
	
	
	
	
	public void makexml() {
		
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
//					    System.out.println(e);
						titles.add(e.text());
					}
					for (org.jsoup.nodes.Element e : xml.select("body")) {
//					    System.out.println(e);
						bodies.add(e.text());
					}

					break;
				}

			}

			ArrayList<String> strToExtrtKwrd = new ArrayList<String>();

			// init KeywordExtractor
			KeywordExtractor ke = new KeywordExtractor();
			for (int i = 0; i < bodies.size(); i++) {
				KeywordList kl = ke.extractKeyword(bodies.get(i), true);
				String temp = "";

				for (int j = 0; j < kl.size(); j++) {
					Keyword kwrd = kl.get(j);
					temp += kwrd.getString() + ":" + kwrd.getCnt() + "#";
				}
				strToExtrtKwrd.add(temp);

			}

			DocumentBuilder docBuilder;

			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element docs = doc.createElement("docs");
			doc.appendChild(docs);

			for (int i = 0; i < strToExtrtKwrd.size(); i++) {
				Element docId = doc.createElement("doc");
				docs.appendChild(docId);
				docId.setAttribute("id", Integer.toString(i));

				Element titleElement = doc.createElement("title");

				docId.appendChild(titleElement);
				titleElement.appendChild(doc.createTextNode(titles.get(i)));

				Element contentElement = doc.createElement("body");

				docId.appendChild(contentElement);
				contentElement.appendChild(doc.createTextNode(strToExtrtKwrd.get(i)));

			}

			TransformerFactory trans = TransformerFactory.newInstance();

			Transformer transformer = trans.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new FileOutputStream(new File(input_file)));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// + indent space의 칸 수 조정 옵션

			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(source, result);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		catch (TransformerConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	

	public void makePost() {
		File[] files = makeFileList(input_file);
		
		ArrayList<String> bodyArr = new ArrayList<String>();
		ArrayList<String> titleArr = new ArrayList<String>();

		try {

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.getName().contains("index.xml")) {
					org.jsoup.nodes.Document xml;
					xml = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
					
					for (org.jsoup.nodes.Element e : xml.select("title")) {
						titleArr.add(e.text());
					}
					for (org.jsoup.nodes.Element e : xml.select("body")) {
						bodyArr.add(e.text());
					}
				}
			}
			
			
			
			
			FileOutputStream fileStream = new FileOutputStream(output_file);
			
			
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
			
			HashMap<String, String> temp = new HashMap<String, String>();

			
			
			
			for (int i = 0 ; i < bodyArr.size(); i++) { 
				
				ArrayList<String> keyWords = new ArrayList<String>();
				keyWords = parseKeyword(bodyArr.get(i));
				
				for (int j = 0 ; j < keyWords.size(); j++) { 
					Integer dfx = 0; 
					for (int k = 0 ; k < bodyArr.size() ; k++) { 
						if (checkDfx(parseKeyword(bodyArr.get(k)),keyWords.get(j))) { 
							dfx += 1;
						}
					}
					
					String tempvalue = "";
					
					for ( int k = 0 ; k < bodyArr.size() ; k++) { 
						tempvalue += "["+Integer.toString(k) + "," + Double.toString(tf(keyWords.get(j),bodyArr.get(k),dfx)) + "] ";
					
					}
					
					temp.put(keyWords.get(j),tempvalue);
					
				}
				
				objectOutputStream.writeObject(temp);
				
			}
			
			objectOutputStream.writeObject(null);
			objectOutputStream.close();
			
			System.out.println("make post end");
			
//			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> parseKeyword(String body) { 
		StringTokenizer parsedst = new StringTokenizer(body, "#");
		ArrayList<String> keyWords = new ArrayList<String>();
		while(parsedst.hasMoreTokens()){
			keyWords.add(parsedst.nextToken().split(":")[0]);
		}
		
		return keyWords;
	}
	
	private boolean checkDfx(ArrayList<String> arr, String keyword) { 
		
		for (int i = 0 ; i < arr.size() ; i++) {
			if (arr.get(i).equals(keyword)) { 
				System.out.println("arr : " + arr.get(i) + "," + "keyword:" + keyword );
				return true;
			}
		}
		
		
		return false;
	}
	
	public void readPost() {
		try {
			
			FileInputStream fileStream = new FileInputStream(output_file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

			Object object;

			object = objectInputStream.readObject();

			objectInputStream.close();

			System.out.println("읽어온 객체 타입" + object.getClass());

			@SuppressWarnings("unchecked")
			HashMap<String, String> hashMap = (HashMap<String, String>) object;
			Iterator<String> it = hashMap.keySet().iterator();

			while (it.hasNext()) {
				String key = it.next();
				String value = (String) hashMap.get(key);
				System.out.println(key + " -> " + value);

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (EOFException eof) {
            // TODO: handle exception
            System.out.println("EOF Exception occur");
         }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Double tf(String x, String body, Integer dfx) {
		Double wxy;
		StringTokenizer parsedst = new StringTokenizer(body, "#");
		ArrayList<String> pstr = new ArrayList<String>();
		while (parsedst.hasMoreTokens()) {
			pstr.add(parsedst.nextToken());
		}

		Integer tfxy = 0;

		for (int i = 0; i < pstr.size(); i++) {
			if (pstr.get(i).contains(x)) {
				String[] split = pstr.get(i).split(":");
				tfxy = Integer.parseInt(split[1]);
				break;
			}
		}

		wxy = tfxy * (Math.log(5 / (dfx)));

		// 소수점 2자리 까지만


		return Math.round(wxy * 100) / 100.0;

	}
	
	
}
