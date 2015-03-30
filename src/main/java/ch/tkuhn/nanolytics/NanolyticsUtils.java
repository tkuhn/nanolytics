package ch.tkuhn.nanolytics;

import java.io.File;

public class NanolyticsUtils {

	private NanolyticsUtils() {}  // no instances allowed

	public static File getQueryFile(String queryName) {
		return new File(Run.class.getClassLoader().getResource("queries/" + queryName + ".sparql").getFile());
	}

}
