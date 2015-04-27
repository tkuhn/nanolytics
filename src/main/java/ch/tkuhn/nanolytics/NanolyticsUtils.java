package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class NanolyticsUtils {

	private NanolyticsUtils() {}  // no instances allowed

	public static File getProvViewFile(String queryName) {
		return new File(Run.class.getClassLoader().getResource("provviews/" + queryName + ".sparql").getFile());
	}

	public static File getProvMetricFile(String queryName) {
		return new File(Run.class.getClassLoader().getResource("provmetrics/" + queryName + ".sparql").getFile());
	}

	public static String getQuery(File queryFile, List<String> collectVarNames) throws IOException {
		String queryString = "";
		Scanner scanner = new Scanner(queryFile);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.toLowerCase().startsWith("select ")) {
				for (String token : line.split("(\\s|\\)|\\()+")) {
					if (token.startsWith("?")) {
						collectVarNames.add(token.substring(1));
					}
				}
			}
			queryString += line + "\n";
		}
		scanner.close();
		return queryString;
	}

}
