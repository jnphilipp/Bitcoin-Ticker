import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;


public class Main {
	public static final String ICON_PATH = "/tmp/BitCoinIconForBitCoinTicker.png";
	public static final Map<String, String> CURRENCIES;

	static {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("AUD", "A$");
		map.put("BRL", "R$");
		map.put("CAD", "C$");
		map.put("CHF", "SFr.");
		map.put("CNY", "¥");
		map.put("EUR", "€");
		map.put("GBP", "£");
		map.put("ILS", "₪");
		map.put("JPY", "¥");
		map.put("NOK", "kr");
		map.put("NZD", "$");
		map.put("PLN", "zł");
		map.put("RUB", "руб");
		map.put("SEK", "kr");
		map.put("SGD", "$");
		map.put("TRY", "₤");
		map.put("USD", "$");
		map.put("ZAR", "S");
		CURRENCIES = Collections.unmodifiableMap(map);
	}

	private final String USER_AGENT = "Mozilla/6.0";
	public int currentprice;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String currency = "USD";
		if ( args.length == 1 )
			if ( Main.CURRENCIES.containsKey(args[0].toUpperCase()) )
				currency = args[0].toUpperCase();

		System.out.println("currency: " + currency);

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
		http.sendGet(currency);
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
	private void sendGet(String currency) throws Exception {
		
		
		while (true) {
			int timetosleep = 20;
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
			float realaverage = 0;
			StringBuffer response = new StringBuffer();
			String price = "";
			while ((inputLine = in.readLine()) != null) {
				// response.append(inputLine);
				i++;
				Pattern p = Pattern.compile("last\\\":\\s(.+),$");
				Matcher m = p.matcher(inputLine);
				if ( m.find() )
					price = m.group(1);

				p = Pattern.compile("24h_avg\\\":\\s(.+),$");
				m = p.matcher(inputLine);
				if ( m.find() ) {
					realaverage = Float.parseFloat(m.group(1));
				}
			}
			in.close();

			System.out.println("price: " + price);
			System.out.println("avg: " + realaverage);

			float y = Float.parseFloat(price);
			if(y-realaverage > 0)
				this.sendNotify("The Bitcoin price is up from the daily average  at " + price + " " + Main.CURRENCIES.get(currency) + ".");
			if(y-realaverage < 0)
				this.sendNotify("The Bitcoin price is down from the daily average at " + price + " " + Main.CURRENCIES.get(currency) + ".");
			if(y-realaverage == 0)
				this.sendNotify("The Bitcoin price is at the daily average at " + price + " " + Main.CURRENCIES.get(currency) + ".");

			Thread.sleep(timetosleep*60000);
		}
	}

	private void sendNotify(String text) throws IOException {
		Runtime.getRuntime().exec(new String[] {"/usr/bin/notify-send", "-i", Main.ICON_PATH, text});
	}

}