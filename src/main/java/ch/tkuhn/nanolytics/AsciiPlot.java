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
				if (uri == null) continue;
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
				if (!attached && t.getLength() > l && !hasEnded(t, l)) {
					if (makeNewNode(t, previous, l)) {
						if (i > 0) plot[i-1] += "  ";
						URI uri = t.getUri(l);
						if (uri == null) {
							plot[i] += "  ";
						} else {
							for (int j = i-1 ; j >= 0 ; j--) {
								if (plot[j].endsWith(" ")) {
									plot[j] = plot[j].substring(0, plot[j].length()-1) + "|";
								}
							}
							if (multiRefs.get(l).contains(uri)) {
								plot[i] += " c";
							} else {
								plot[i] += " o";
							}
						}
					} else {
						String s = plot[i-1];
						while (s.endsWith(" ")) {
							s = s.replaceFirst(" ( *)$", "_$1");
						}
						plot[i-1] = s + "/ ";
						plot[i] += "  ";
						attached = true;
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
		if (previousTrail == null) {
			return true;
		} else {
			for (int j = level ; j < thisTrail.getLength() ; j++) {
				URI thisUri = thisTrail.getUri(j);
				URI prevUri = previousTrail.getUri(j);
				if (thisUri == null || prevUri == null) continue;
				if (thisUri == null || prevUri == null || !thisUri.equals(prevUri)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasEnded(ProvTrail thisTrail, int level) {
		for (int j = level ; j < thisTrail.getLength() ; j++) {
			if (thisTrail.getUri(j) != null) {
				return false;
			}
		}
		return true;
	}

}
