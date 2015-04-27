package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.trustyuri.TrustyUriUtils;

import org.nanopub.MalformedNanopubException;
import org.nanopub.MultiNanopubRdfHandler;
import org.nanopub.MultiNanopubRdfHandler.NanopubHandler;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.nanopub.extra.server.GetNanopub;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.memory.MemoryStore;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Run {

	@com.beust.jcommander.Parameter(description = "nanopubs", required = true)
	private List<String> nanopubRefs = new ArrayList<String>();

	@Parameter(names = "-v", description = "Provenance views to generate (comma seperated)")
	private String provViews;

	@Parameter(names = "-m", description = "Provenance metrics to generate (comma seperated)")
	private String provMetrics;

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
	private SailRepository sailRepo;
	private SailRepositoryConnection conn;

	public Run() {
	}

	private void init() throws IOException, OpenRDFException, MalformedNanopubException {
		for (String s : nanopubRefs) {
			if (s.matches("^https?://.*")) {
				nanopubs.add(new NanopubImpl(new URL(s)));
			} else if (TrustyUriUtils.isPotentialArtifactCode(s)) {
				nanopubs.add(GetNanopub.get(s));
			} else {
				MultiNanopubRdfHandler.process(new File(s), new NanopubHandler() {
					@Override
					public void handleNanopub(Nanopub np) {
						nanopubs.add(np);
					}
				});
			}
		}
		MemoryStore store = new MemoryStore();
		store.initialize();
		sailRepo = new SailRepository(store);
		conn = sailRepo.getConnection();
		for (Nanopub np : nanopubs) {
			conn.add(NanopubUtils.getStatements(np));
		}
	}

	public void run() throws IOException, OpenRDFException {
		if (provViews == null && provMetrics == null) {
			throw new RuntimeException("No provenance views nor metrics specified");
		}
		if (provViews != null) {
			for (String v : provViews.split(",")) {
				ProvNetwork n = new ProvNetwork(conn, v);
				AsciiPlot p = new AsciiPlot(n);
				System.out.println(p.getPlotString());
			}
		}
		if (provMetrics != null) {
			for (String m : provMetrics.split(",")) {
				ProvMetric p = new ProvMetric(conn, m);
				for (String v : p.getVarNames()) {
					Object metric = p.getMetric(v);
					if (metric == null) {
					} else if (metric instanceof Value) {
						System.out.println(v + ": " + ((Value) metric).stringValue());
					} else {
						System.out.println(v + ": " + metric);
					}
				}
			}
		}
	}

}
