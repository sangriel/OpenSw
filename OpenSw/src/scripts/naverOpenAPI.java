package scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class naverOpenAPI {
	
	String clientId = "NN8xIwFZMnC1s1SNwm3E";
	String clientSecret = "vaVL9u78OL";
	String searchWord = "";
	
	
	public naverOpenAPI(String q) {
		this.searchWord = q;
		
	}
	

	
	public void makeNetworkCall() { 
		try {
			
			
			String text = URLEncoder.encode(searchWord,"UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;
			
			URL url = new URL(apiURL);
			
		
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			
			
			int responseCode = con.getResponseCode();
			
			BufferedReader br;
			
			if (responseCode == 200) { 
				br = new BufferedReader( new InputStreamReader(con.getInputStream()));
			}
			else { 
				br = new BufferedReader( new InputStreamReader(con.getErrorStream()));
			}
			
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) { 
				response.append(inputLine);
			}
			br.close();
		
		
			parseJson(response.toString());
			
		
		
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return;
	}
	
	
	public void parseJson(String text) { 
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(text);
			JSONArray infoArray = (JSONArray) jsonObject.get("items");
			
			
			for (int i = 0 ; i < infoArray.size(); i++) { 
				System.out.println("=item_" + i + "=======================");
				JSONObject itemObject = (JSONObject) infoArray.get(i);
				
				System.out.println("title:\t" + itemObject.get("title"));
				System.out.println("subtitle:\t" + itemObject.get("subtitle"));
				System.out.println("director:\t" + itemObject.get("director"));
				System.out.println("actor:\t" + itemObject.get("actor"));
				System.out.println("userRating:\t" + itemObject.get("userRating") + "\n");
			}
			
			
			
			
			
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
