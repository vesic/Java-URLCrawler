import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class URLCrawler {

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Basic usage: java URLCrawler url file depth\n"
					+ "e.g. java URLCrawler http://example.com output.txt 10");
			System.exit(0);
		}
		
		final String url = args[0];
		final String fileName = args[1];
		final int depth = Integer.parseInt(args[2]);
		
		new Thread(new Runnable() {
			public void run() {
				crawler(url, depth, fileName);				
			}
		}).start();
	}

	public static void crawler(String startingURL, int depth, String fileName) {
		List<String> pendingURLs = new ArrayList<>();
		List<String> traversedURLs = new ArrayList<>();
		
		try (PrintWriter pWriter = new PrintWriter(new FileWriter(fileName))) {
			pendingURLs.add(startingURL);
			while (!pendingURLs.isEmpty() && traversedURLs.size() <= depth) {
				String urlString = pendingURLs.remove(0);
				if (!traversedURLs.contains(urlString)) {
					traversedURLs.add(urlString);
					System.out.println("Crawling => " + urlString);
					pWriter.println(urlString);
					for (String s : getSubURLs(urlString)) {
						if (!traversedURLs.contains(s))
							pendingURLs.add(s);
					}
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getSubURLs(String urlString) throws IOException {
		List<String> list = new ArrayList<>();
		URL url = new URL(urlString);
		Scanner input = new Scanner(url.openStream());
		int current = 0;
		
		while (input.hasNext()) {
			String line = input.nextLine();
			current = line.indexOf("http:", current);
			while (current > 0) {
				int endIndex = line.indexOf("\"", current);
				if (endIndex > 0) {
					list.add(line.substring(current, endIndex));
					current = line.indexOf("http:", endIndex);
				} else current = -1;
			}
		}

		return list;
	}
}