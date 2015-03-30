package ch.tkuhn.nanolytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;

public class AsciiPlot {

	private ProvNetwork provNetwork;
	private List<ProvTrail> provTrails;
	private String plotString;
	private List<Set<URI>> refs, multiRefs;

	public AsciiPlot(ProvNetwork provNetwork) {
		this.provNetwork = provNetwork;
		init();
	}

	private void init() {
		provTrails = new ArrayList<>(provNetwork.getTrails());
		Collections.sort(provTrails);
	}

	public String getPlotString() {
		if (plotString == null) {
			preparePlot();
			makePlot();
		}
		return plotString;
	}

	private void preparePlot() {
		refs = new ArrayList<Set<URI>>();
		multiRefs = new ArrayList<Set<URI>>();
		for (int l = 0 ; l < provNetwork.getTrailLength() ; l++) {
			Set<URI> r = new HashSet<URI>();
			Set<URI> mr = new HashSet<URI>();
			refs.add(r);
			multiRefs.add(mr);
			ProvTrail previous = null;
			for (ProvTrail t : provTrails) {
				URI uri = t.getUri(l);
				if (makeNewNode(t, previous, l)) {
					if (r.contains(uri)) {
						mr.add(uri);
					} else {
						r.add(uri);
					}
				}
				previous = t;
			}
		}
	}

	private void makePlot() {
		String[] plot = new String[provNetwork.getTrailLength()*2 - 1];
		for (int i = 0 ; i < plot.length ; i++) {
			plot[i] = "";
		}
		ProvTrail previous = null;
		for (ProvTrail t : provTrails) {
			boolean attached = false;
			for (int i = 0 ; i < plot.length ; i = i+2) {
				int l = i/2;
				if (!attached && t.getLength() > l) {
					if (!makeNewNode(t, previous, l)) {
						String s = plot[i-1];
						while (s.endsWith(" ")) {
							s = s.replaceFirst(" ( *)$", "_$1");
						}
						plot[i-1] = s + "/ ";
						plot[i] += "  ";
						attached = true;
					} else {
						if (i > 0) plot[i-1] += " |";
						URI uri = t.getUri(l);
						if (multiRefs.get(l).contains(uri)) {
							plot[i] += " c";
						} else {
							plot[i] += " o";
						}
					}
				} else {
					if (i > 0) plot[i-1] += "  ";
					plot[i] += "  ";
				}
			}
			previous = t;
		}
		plotString = "";
		for (int i = 0 ; i < plot.length ; i++) {
			if (i % 2 == 0) {
				int p = i/2;
				plotString += plot[i] + "  " + refs.get(p).size() + " " + provNetwork.getVarNames().get(p) + "\n";
			} else {
				plotString += plot[i] + "\n";
			}
		}
	}

	private boolean makeNewNode(ProvTrail thisTrail, ProvTrail previousTrail, int level) {
		boolean makeNewNode = true;
		if (previousTrail != null) {
			makeNewNode = false;
			for (int j = level ; j < thisTrail.getLength() ; j++) {
				if (!thisTrail.getUri(j).equals(previousTrail.getUri(j))) {
					makeNewNode = true;
					break;
				}
			}
		}
		return makeNewNode;
	}

}
