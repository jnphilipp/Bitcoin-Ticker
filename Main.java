import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;


public class Main {
	public static final String ICON_PATH = "/tmp/BitCoinIconForBitCoinTicker.png";
	public static final String ICON_SOURCE = "http://i.imgur.com/6bNY1sH.png";
	public static final Map<String, String> CURRENCIES;
	private static final String USER_AGENT = "Mozilla/6.0";

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

	private String currency;
	private int timeToSleep = 1800;

	public Main(String currency, int timeToSleep) {
		this.currency = currency;
		this.timeToSleep = timeToSleep;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String currency = "USD";
		int timeToSleep = 1800;
		if ( args.length == 1 )
			if ( Main.CURRENCIES.containsKey(args[0].toUpperCase()) )
				currency = args[0].toUpperCase();

		Iterator<String> it = Arrays.asList(args).iterator();
		while ( it.hasNext() ) {
			String arg = it.next();
			if ( Main.CURRENCIES.containsKey(arg.toUpperCase()) )
				currency = arg.toUpperCase();
			else if ( arg.equals("-time-to-sleep") )
				timeToSleep = Integer.parseInt(it.next());
		}

		System.out.println("currency: " + currency);
		System.out.println("time to sleep: " + timeToSleep);
		if ( new File(Main.ICON_PATH).exists())
			System.out.println("icon exists");
		else
			Main.downloadIcon();

		Main main = new Main(currency, timeToSleep);
		main.mainLoop();
	}

	/**
	 * Downloads the Bitcoin icon for notification bubble.
	 */
	private static void downloadIcon() {
		System.out.println("downloading icon");
		BufferedImage image = null;

		try {
			URL url = new URL(Main.ICON_SOURCE);
			image = ImageIO.read(url);
			ImageIO.write(image, "png", new File(Main.ICON_PATH));
		}
		catch ( IOException e ) {
			System.err.println("Something went wrong while downloading the icon :(");
			e.printStackTrace();
		}
	}

	/**
	 * Main loop.
	 */
	private void mainLoop() {
		float[] price = new float[2];

		while (true) {
			try {
				price = this.getNewPrice();
				System.out.println("price: " + price[0]);
				System.out.println("avg: " + price[1]);

				if ( price[0] - price[1] > 0 )
					this.sendNotify("The Bitcoin price is up from the daily average  at %.2f %s.", price[0]);
				else if ( price[0] - price[1] < 0 )
					this.sendNotify("The Bitcoin price is down from the daily average at %.2f %s.", price[0]);
				else
					this.sendNotify("The Bitcoin price is at the daily average at %.2f %s.", price[0]);

				Thread.sleep(this.timeToSleep * 1000);
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the new price and average.
	 * @return two dimensional array, first field price, second field average
	 * @throws Exception
	 */
	private float[] getNewPrice() throws Exception {
		URL url = new URL("https://api.bitcoinaverage.com/ticker/" + this.currency);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine, price = "", avg = "";
		while ( (inputLine = in.readLine()) != null ) {
			Pattern p = Pattern.compile("last\\\":\\s(.+),$");
			Matcher m = p.matcher(inputLine);
			if ( m.find() )
				price = m.group(1);

			p = Pattern.compile("24h_avg\\\":\\s(.+),$");
			m = p.matcher(inputLine);
			if ( m.find() )
				avg = m.group(1);
		}
		in.close();

		return new float[] {Float.parseFloat(price), Float.parseFloat(avg)};
	}

	/**
	 * Sends a notification bubble.
	 * @param text text
	 * @param price price
	 * @throws IOException
	 */
	private void sendNotify(String text, float price) throws IOException {
		Runtime.getRuntime().exec(new String[] {"/usr/bin/notify-send", "-i", Main.ICON_PATH, String.format(text, price, Main.CURRENCIES.get(this.currency))});
	}
}