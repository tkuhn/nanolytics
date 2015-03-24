package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.trustyuri.TrustyUriUtils;

import org.nanopub.MalformedNanopubException;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.nanopub.extra.server.GetNanopub;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFFormat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Run {

	@com.beust.jcommander.Parameter(description = "nanopubs", required = true)
	private List<String> nanopubIdentifiers = new ArrayList<String>();

	public static void main(String[] args) throws IOException, OpenRDFException, MalformedNanopubException {
		Run obj = new Run();
		JCommander jc = new JCommander(obj);
		try {
			jc.parse(args);
		} catch (ParameterException ex) {
			jc.usage();
			System.exit(1);
		}
		try {
			obj.init();
			obj.run();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private List<Nanopub> nanopubs = new ArrayList<>();

	public Run() {
	}

	private void init() throws IOException, OpenRDFException, MalformedNanopubException {
		for (String s : nanopubIdentifiers) {
			if (s.matches("^https?://.*")) {
				nanopubs.add(new NanopubImpl(new URL(s)));
			} else if (TrustyUriUtils.isPotentialArtifactCode(s)) {
				nanopubs.add(GetNanopub.get(s));
			} else {
				nanopubs.add(new NanopubImpl(new File(s)));
			}
		}
	}

	public void run() throws IOException, OpenRDFException {
		for (Nanopub np : nanopubs) {
			NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
		}
	}

}
