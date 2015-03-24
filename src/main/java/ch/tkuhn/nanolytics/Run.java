package ch.tkuhn.nanolytics;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.trustyuri.TrustyUriUtils;

import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.nanopub.extra.server.GetNanopub;
import org.openrdf.rio.RDFFormat;

public class Run {

	public static void main(String[] args) throws Exception {
		List<Nanopub> nanopubs = new ArrayList<>();
		for (String s : args) {
			if (s.matches("^https?://.*")) {
				nanopubs.add(new NanopubImpl(new URL(s)));
			} else if (TrustyUriUtils.isPotentialArtifactCode(s)) {
				nanopubs.add(GetNanopub.get(s));
			} else {
				nanopubs.add(new NanopubImpl(new File(s)));
			}
		}
		Run obj = new Run(nanopubs);
		obj.run();
	}

	private List<Nanopub> nanopubs = new ArrayList<>();

	public Run(List<Nanopub> nanopubs) {
		this.nanopubs.addAll(nanopubs);
	}

	public void run() throws Exception {
		for (Nanopub np : nanopubs) {
			NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
		}
	}

}
