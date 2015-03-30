package ch.tkuhn.nanolytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProvPlot {

	private ProvNetwork provNetwork;

	public ProvPlot(ProvNetwork provNetwork) {
		this.provNetwork = provNetwork;
	}

	public String getAsciiPlot() {
		List<ProvTrail> provTrails = new ArrayList<>(provNetwork.getTrails());
		Collections.sort(provTrails);
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
					if (previous != null && t.getUri(p).equals(previous.getUri(p))) {
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
		String plotString = "";
		for (int i = 0 ; i < plot.length ; i++) {
			if (i % 2 == 0) {
				int p = i/2;
				plotString += plot[i] + " " + provNetwork.getVarNames().get(p) + "\n";
			} else {
				plotString += plot[i] + "\n";
			}
		}
		return plotString;
	}

}
