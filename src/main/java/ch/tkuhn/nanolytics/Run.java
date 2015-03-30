package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.trustyuri.TrustyUriUtils;

import org.nanopub.MalformedNanopubException;
import org.nanopub.MultiNanopubRdfHandler;
import org.nanopub.MultiNanopubRdfHandler.NanopubHandler;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.nanopub.extra.server.GetNanopub;
import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.sail.memory.MemoryStore;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Run {

	@com.beust.jcommander.Parameter(description = "nanopubs", required = true)
	private List<String> nanopubRefs = new ArrayList<String>();

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
		Scanner scanner = new Scanner(getQueryFile("agent-prov"));
		String queryString = "";
		List<String> varNames = new ArrayList<>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.toLowerCase().startsWith("select ")) {
				for (String token : line.split("\\s+")) {
					if (token.startsWith("?")) {
						varNames.add(token.substring(1));
					}
				}
			}
			queryString += line + "\n";
		}
		scanner.close();
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		while (result.hasNext()) {
			System.err.println("---");
			BindingSet bs = result.next();
			for (String n : varNames) {
				System.err.println(n + " " + bs.getBinding(n));
			}
		}
	}

	private static File getQueryFile(String dataset) {
		return new File(Run.class.getClassLoader().getResource("queries/" + dataset + ".sparql").getFile());
	}

}
