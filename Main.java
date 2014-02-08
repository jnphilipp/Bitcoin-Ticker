import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;


public class Main {

	/**
	 * @param args
	 */
	public static final String ICON_PATH = "/tmp/BitCoinIconForBitCoinTicker.png";
	private final String USER_AGENT = "Mozilla/6.0";
	public int currentprice;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main http = new Main();
		BufferedImage image =null;
		File f = new File("/tmp/BitCoinIconForBitCoinTicker.png");
		if(f.exists()) {System.out.println("User has logo file"); }else{
        try{
 System.out.println("Downloading File");
            URL url =new URL("http://i.imgur.com/6bNY1sH.png");
            // read the url
           image = ImageIO.read(url);
           ImageIO.write(image, "png",new File(Main.ICON_PATH));
        }catch(IOException e){
            e.printStackTrace();
       System.out.println("Something went wrong, you likely the file is gone :(");
        }
		}
		// // System.out.println("Testing 1 - Send Http GET request");
		// http.sendGet();
		
		// System.out.println("\nTesting 2 - Send Http POST request");
		http.sendGet();
	//	File f = new File("/home/Home/Downloads/6bN1sH.png");
		//if(f.exists()) {System.out.println("User has logo file"); }else{
			//System.out.println("Not found");
			//Process p = Runtime
			//		.getRuntime()
				//	.exec(new String[] {"sudo wget http://i.imgur.com/6bNY1sH.png -P /home/Home/Downloads/"});
		//	System.out.println("Download compleat");
		//}

	}

	// HTTP GET request
	private void sendGet() throws Exception {
		
		
		while (true) {
			int timetosleep = 20;
			String currency = "USD";
			String url = "https://api.bitcoinaverage.com/ticker/"+currency;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int i = 0;
			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
		
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			int realaverage = 0;
			StringBuffer response = new StringBuffer();
			String price = "";
			while ((inputLine = in.readLine()) != null) {
				// response.append(inputLine);
				i++;
				Pattern p = Pattern.compile("last\\\":\\s(.+),$");
				Matcher m = p.matcher(inputLine);
				if ( m.find() )
					price = m.group(1);

				if (i == 2) {
					String avg = inputLine;
					System.out.println(avg.replace("\"24h_avg\":", ""));
					String avgfixed = avg.replace("\"24h_avg\":", "");
					String avgfixed2 = avgfixed.replace(",", "");
					String avgfixed3 = avgfixed2.replace(" ", "");
					int ave = Integer.parseInt(avgfixed3.replace(".",""));
				    realaverage = ave / 100;
				}
			}
			in.close();

			

	System.out.println(realaverage);

			float y = Float.parseFloat(price);
			if(y-realaverage > 0)
				this.sendNotify("The Price Of Bitcoin Is Up from the daily average  at " + price + "$");
			if(y-realaverage < 0)
				this.sendNotify("The Price Of Bitcoin Is Down from the daily average at " + price + "$");
			if(y-realaverage == 0)
				this.sendNotify("The Price Of Bitcoin Is at the daily average at " + price + "$");

			Thread.sleep(timetosleep*60000);
		}
	}

	private void sendNotify(String text) throws IOException {
		Runtime.getRuntime().exec(new String[] {"/usr/bin/notify-send", "-i", Main.ICON_PATH, text});
	}

}