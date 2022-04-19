package scripts;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

public class searcher {

	private String searchQuestion;
	private String input_file;
	private ArrayList<String> keywords;
	private HashMap<String, String> indexPost;
	private ArrayList<String> titleArr;
	private ArrayList<String> bodyArr;

	public searcher(String q, String path) {
		this.searchQuestion = q;
		this.input_file = path;
	}

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

	public void search() {
		this.readPost();
		this.getTitles();

		ArrayList<String> keyWords = new ArrayList<String>();
		ArrayList<Integer> keyWordsCount = new ArrayList<Integer>();

		KeywordExtractor ke = new KeywordExtractor();

		KeywordList kl = ke.extractKeyword(searchQuestion, true);

		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);
			keyWords.add(kwrd.getString());
			keyWordsCount.add(kwrd.getCnt());
		}

		ArrayList<Double> weights = new ArrayList<Double>();

		for (int i = 0; i < keyWords.size(); i++) {

			Integer dfx = 0;
			for (int k = 0; k < bodyArr.size(); k++) {
				if (checkDfx(parseKeyword(bodyArr.get(k)), keyWords.get(i))) {
					dfx += 1;
				}
			}

			weights.add(tf(keyWords.get(i), searchQuestion, keyWordsCount.get(i), dfx));
		}

		this.calcSim(keyWords.toArray(new String[keyWords.size()]), weights.toArray(new Double[weights.size()]));

		return;

	}

	private ArrayList<String> parseKeyword(String body) {
		StringTokenizer parsedst = new StringTokenizer(body, "#");
		ArrayList<String> keyWords = new ArrayList<String>();
		while (parsedst.hasMoreTokens()) {
			keyWords.add(parsedst.nextToken().split(":")[0]);
		}

		return keyWords;
	}

	private boolean checkDfx(ArrayList<String> arr, String keyword) {

		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).equals(keyword)) {
//					System.out.println("arr : " + arr.get(i) + "," + "keyword:" + keyword );
				return true;
			}
		}

		return false;
	}

	private void getTitles() {
		titleArr = new ArrayList<String>();
		bodyArr = new ArrayList<String>();
		File[] files = makeFileList("./index.xml");
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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void calcSim(String[] keywords, Double[] keywordWeights) {

		ArrayList<Double> weights = new ArrayList<Double>();
		for (int i = 0; i < titleArr.size(); i++) {
			weights.add(0.0);
		}

		ArrayList<Double> results = new ArrayList<Double>();
		for (int i = 0; i < keywords.length; i++) {
			int count = 0;
			String values = this.indexPost.get(keywords[i]);
			if (values == null) {

			} else {
				StringTokenizer parsedst = new StringTokenizer(values, " ");

				while (parsedst.hasMoreTokens()) {
					String token = parsedst.nextToken();
					Double value = Double.parseDouble(token.substring(3, token.length() - 1));

					weights.set(count, weights.get(count) + (value * keywordWeights[i]));
					count += 1;
				}
			}

		}

		ArrayList<String> selectedDocs = new ArrayList<String>();

		for (int i = 0; i < weights.size(); i++) {
			if (weights.get(i) > 0) {
				selectedDocs.add(titleArr.get(i));
			}
		}

		if (selectedDocs.size() == 0) {
			System.out.println("검색된 문서가 없습니다");
		} else {
			String result = "검색된 문서";
			for (int i = 0; i < selectedDocs.size(); i++) {
				if (i >= 3) {
					break;
				} else {
					result += "[" + selectedDocs.get(i) + "] ";
				}
			}
			System.out.println(result);
		}

	}

	private Double InnerProduct() {

		return 0.0;

	}

	private Double tf(String x, String body, Integer tfxy, Integer dfx) {
		Double wxy;
		// 단어 가 몇개의 문서에서 등장하는지
		wxy = tfxy * (Math.log(1 / (dfx)));

		if (wxy == 0.0) {
			return 1.0;
		}

		// 소수점 2자리 까지만

		return Math.round(wxy * 100) / 100.0;
	}

	@SuppressWarnings("unchecked")
	private void readPost() {
		try {

			FileInputStream fileStream = new FileInputStream(input_file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

			Object object;

			object = objectInputStream.readObject();

			objectInputStream.close();

			this.indexPost = (HashMap<String, String>) object;

			Iterator<String> it = this.indexPost.keySet().iterator();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EOFException eof) {
			// TODO: handle exception
			System.out.println("EOF Exception occur");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}