package ch.tkuhn.nanolytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import com.google.common.collect.ImmutableList;

public class ProvNetwork {

	private List<ProvTrail> trails;
	private List<String> varNames;

	public ProvNetwork(RepositoryConnection conn, String queryName) throws IOException, OpenRDFException {
		init(conn, queryName);
	}

	private void init(RepositoryConnection conn, String queryName) throws IOException, OpenRDFException {
		trails = new ArrayList<>();
		Scanner scanner = new Scanner(NanolyticsUtils.getQueryFile("agent-prov"));
		String queryString = "";
		varNames = new ArrayList<>();
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
			BindingSet bs = result.next();
			List<URI> uris = new ArrayList<>();
			for (String n : varNames) {
				Value v = bs.getBinding(n).getValue();
				if (!(v instanceof URI)) {
					throw new RuntimeException("Not a URI: " + v);
				}
				uris.add((URI) v);
			}
			trails.add(new ProvTrail(uris));
		}
		Collections.sort(trails);
	}

	public List<ProvTrail> getTrails() {
		return ImmutableList.copyOf(trails);
	}

}
