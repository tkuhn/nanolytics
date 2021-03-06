package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		File queryFile = NanolyticsUtils.getProvViewFile(queryName);
		varNames = new ArrayList<>();
		String queryString = NanolyticsUtils.getQuery(queryFile, varNames);
		varNames = ImmutableList.copyOf(varNames);
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		while (result.hasNext()) {
			BindingSet bs = result.next();
			List<URI> uris = new ArrayList<>();
			for (String n : varNames) {
				if (bs.hasBinding(n)) {
					Value v = bs.getBinding(n).getValue();
					if (!(v instanceof URI)) {
						throw new RuntimeException("Not a URI: " + v);
					}
					uris.add((URI) v);
				} else {
					uris.add(null);
				}
			}
			trails.add(new ProvTrail(uris));
		}
		trails = ImmutableList.copyOf(trails);
	}

	public List<ProvTrail> getTrails() {
		return trails;
	}

	public List<String> getVarNames() {
		return varNames;
	}

	public int getTrailLength() {
		return varNames.size();
	}

}
