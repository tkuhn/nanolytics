package ch.tkuhn.nanolytics;

import java.util.List;

import org.openrdf.model.URI;

public class ProvTrail implements Comparable<ProvTrail> {

	private URI[] uris;

	public ProvTrail(URI... uris) {
		this.uris = uris;
	}

	public ProvTrail(List<URI> uris) {
		this.uris = uris.toArray(new URI[uris.size()]);
	}

	public int getLength() {
		return uris.length;
	}

	public URI getUri(int position) {
		return uris[position];
	}

	@Override
	public int compareTo(ProvTrail other) {
		if (this.getLength() != other.getLength()) {
			return this.getLength() - other.getLength();
		}
		int i = this.getLength();
		while (i > 0) {
			i--;
			URI thisUri = this.getUri(i);
			URI otherUri = other.getUri(i);
			if (thisUri == null && otherUri == null) continue;
			if (thisUri == null) return 1;
			if (otherUri == null) return -1;
			int c = this.getUri(i).toString().compareTo(other.getUri(i).toString());
			if (c != 0) return c;
		}
		return 0;
	}

	@Override
	public String toString() {
		String s = "";
		for (URI uri : uris) {
			if (!s.isEmpty()) s += " ";
			s += uri.toString();
		}
		return s;
	}

}
