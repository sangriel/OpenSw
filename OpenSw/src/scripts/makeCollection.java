package scripts;
import java.io.File;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class makeCollection {


	private String data_path;
	private String output_file = "./collection.xml";
	
	public makeCollection(String path) { 
		this.data_path = path;

		
	}
	
	public File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	
	
	public void makeXml() {
		File[] files = makeFileList(data_path);
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder docBuilder;

			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element docs = doc.createElement("docs");
			doc.appendChild(docs);

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				System.out.println("filenames" + file.getName());
				if (file.getName().contains(".html")) {
					org.jsoup.nodes.Document html;

					html = Jsoup.parse(file, "UTF-8");

					String titleData = html.title();
					String bodyData = html.body().text();

					Element docId = doc.createElement("doc");
					docs.appendChild(docId);
					docId.setAttribute("id", Integer.toString(i));

					Element titleElement = doc.createElement("title");

					docId.appendChild(titleElement);
					titleElement.appendChild(doc.createTextNode(titleData));

					Element contentElement = doc.createElement("body");

					docId.appendChild(contentElement);
					contentElement.appendChild(doc.createTextNode(bodyData));

				}

			}

			TransformerFactory trans = TransformerFactory.newInstance();

			Transformer transformer = trans.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));

			transformer.transform(source, result);
			
			System.out.println(" 실행 완료");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
			
			
	
	
}
