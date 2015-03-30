package ch.tkuhn.nanolytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsciiPlot {

	private ProvNetwork provNetwork;
	private List<ProvTrail> provTrails;
	private String plotString;

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
			makePlot();
		}
		return plotString;
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
				int p = i/2;
				if (!attached && t.getLength() > p) {
					boolean doAttach = false;
					if (previous != null) {
						doAttach = true;
						for (int j = p ; j < t.getLength() ; j++) {
							if (!t.getUri(j).equals(previous.getUri(j))) {
								doAttach = false;
								break;
							}
						}
					}
					if (doAttach) {
						String s = plot[i-1];
						while (s.endsWith(" ")) {
							s = s.replaceFirst(" ( *)$", "_$1");
						}
						plot[i-1] = s + "/ ";
						plot[i] += "  ";
						attached = true;
					} else {
						if (i > 0) plot[i-1] += " |";
						plot[i] += " o";
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
				plotString += plot[i] + " " + provNetwork.getVarNames().get(p) + "\n";
			} else {
				plotString += plot[i] + "\n";
			}
		}
	}

}
