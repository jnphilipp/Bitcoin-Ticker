import java.io.BufferedReader;
		import java.io.DataOutputStream;
		import java.io.InputStreamReader;
		import java.net.HttpURLConnection;
		import java.net.URL;
		 
import javax.net.ssl.HttpsURLConnection;
public class Main {

	/**
	 * @param args
	 */
	private final String USER_AGENT = "Mozilla/28.0"; // defined useragent
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
				Main http = new Main();
				http.sendGet();
			}
		 String currency = "USD";
			// HTTP GET request
			private void sendGet() throws Exception {
		 while(true){
			 int timetosleep =600 ;//just the time to sleep that is later *10ed
				String url = "https://api.bitcoinaverage.com/ticker/"+currency; //sets the url
		 
				URL obj = new URL(url); // defines it as an url 
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		 int i = 0;
				// optional default is GET
				con.setRequestMethod("GET"); // Get or Post
		 
				//add request header
				con.setRequestProperty("User-Agent", USER_AGENT); // sets properties
		 
				int responseCode = con.getResponseCode(); // get the responce code as an int
		 
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				        //for readline
				String inputLine;// defines the string we later use for the each line
				StringBuffer response = new StringBuffer();
		 String price = "";
				while ((inputLine = in.readLine()) != null) { // while there is stuff to grab
					//response.append(inputLine);
					i++;//add to I 
					if(i==5){price = inputLine;} //when on the 5th line set price to that lnce
					
				}
				
				System.out.println(price);
				String id = price.replace("\"last\":", "")+"$"; // manipulate the strings
				Process p = Runtime.getRuntime().exec(new String[]{"/usr/bin/notify-send","-t","100","The Price Of Bitcoin Is...",id.replace(",","")});//makes the alert
				
		 Thread.sleep(timetosleep*1000);//sleep
				//print result
				//System.out.println(response.toString());
		 }
			}
		 

	{}

}
