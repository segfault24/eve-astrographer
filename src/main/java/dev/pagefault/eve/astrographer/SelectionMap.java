package dev.pagefault.eve.astrographer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectionMap {

	private List<SolarSystem> selected = new ArrayList<SolarSystem>();
	private HashMap<SolarSystem, Link> forward = new HashMap<SolarSystem, Link>();
	private HashMap<SolarSystem, Link> backward =  new HashMap<SolarSystem, Link>();

	public void setSelected(SolarSystem base) {
		if (!selected.contains(base)) {
			selected.add(base);
		}
	}

	public void addAll(SolarSystem base, List<SolarSystem> targets) {
		for (SolarSystem s : targets) {
			add(base, s);
		}
	}

	public void add(SolarSystem base, SolarSystem target) {
		setSelected(base);
		Link link = forward.get(base);
		if(link == null) {
			link = new Link();
			forward.put(base, link);
		}
		if (!link.links.contains(target)) {
			link.links.add(target);
		}
		link = backward.get(target);
		if (link == null) {
			link = new Link();
			backward.put(target, link);
		}
		if (!link.links.contains(base)) {
			link.links.add(base);
		}
	}

	public void removeAll(SolarSystem base) {
		selected.remove(base);
		Link link = forward.remove(base);
		if (link != null) {
			for (SolarSystem s : link.links) {
				Link backlink = backward.get(s);
				if (backlink != null) {
					backlink.links.remove(base);
					if (backlink.links.isEmpty()) {
						backward.remove(s);
					}
				}
			}
		}
	}

	public boolean isSelected(SolarSystem base) {
		return selected.contains(base);
	}

	public int getNumLinks(SolarSystem base) {
		Link l = forward.get(base);
		if (l != null) {
			return l.links.size();
		}
		return 0;
	}

	public int getNumBacklinks(SolarSystem target) {
		Link l = backward.get(target);
		if (l != null) {
			return l.links.size();
		}
		return 0;
	}

	private static class Link {
		ArrayList<SolarSystem> links = new ArrayList<SolarSystem>();
	}

}
