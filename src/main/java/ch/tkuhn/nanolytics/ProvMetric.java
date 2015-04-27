package ch.tkuhn.nanolytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import com.google.common.collect.ImmutableList;

public class ProvMetric {

	private List<String> varNames;
	private Map<String,Object> metrics;

	public ProvMetric(RepositoryConnection conn, String queryName) throws IOException, OpenRDFException {
		init(conn, queryName);
	}

	private void init(RepositoryConnection conn, String queryName) throws IOException, OpenRDFException {
		metrics = new HashMap<String,Object>();
		File queryFile = NanolyticsUtils.getProvMetricFile(queryName);
		varNames = new ArrayList<>();
		String queryString = NanolyticsUtils.getQuery(queryFile, varNames);
		varNames = ImmutableList.copyOf(varNames);
		TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = query.evaluate();
		if (!result.hasNext()) {
			throw new RuntimeException("Metric calculation failed");
		}
		BindingSet bs = result.next();
		if (result.hasNext()) {
			throw new RuntimeException("Metric calculation led to multiple results");
		}
		for (String n : varNames) {
			if (bs.hasBinding(n)) {
				metrics.put(n, bs.getBinding(n).getValue());
			}
		}
	}

	public Object getMetric(String varName) {
		return metrics.get(varName);
	}

	public List<String> getVarNames() {
		return varNames;
	}

	public int getTrailLength() {
		return varNames.size();
	}

}
