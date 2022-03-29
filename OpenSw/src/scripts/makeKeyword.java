package scripts;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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


public class makeKeyword {


	private String input_file;
	private String output_file = "./index.xml";
	
	public makeKeyword(String file) {
		this.input_file = file;
	}
	
	public File[] makeFileList(String path) {
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
	

	public void convertXml() {
		File[] files = makeFileList(input_file);

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

			ArrayList<String> strToExtrtKwrd = new ArrayList<String>();

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

			StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));

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
		
		System.out.println("3주차 실행완료");
	}
	
}
