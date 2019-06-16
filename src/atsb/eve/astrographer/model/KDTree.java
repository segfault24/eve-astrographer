package atsb.eve.astrographer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class KDTree {

	private KDNode root;

	public KDTree(Collection<SolarSystem> systems) {
		root = stepX(new ArrayList<SolarSystem>(systems));
	}

	private KDNode stepX(List<SolarSystem> l) {
		if (l.isEmpty()) {
			return null;
		}
		KDNode node = new KDNode();
		List<SolarSystem> n = sortByX(l);
		int median = l.size() / 2;
		node.data = n.get(median);
		node.left = stepY(n.subList(0, median));
		node.right = stepY(n.subList(median + 1, n.size()));
		return node;
	}

	private KDNode stepY(List<SolarSystem> l) {
		if (l.isEmpty()) {
			return null;
		}
		KDNode node = new KDNode();
		List<SolarSystem> n = sortByZ(l);
		int median = l.size() / 2;
		node.data = n.get(median);
		node.left = stepX(n.subList(0, median));
		node.right = stepX(n.subList(median + 1, n.size()));
		return node;
	}

	public SolarSystem nearest(double x, double z) {
		return nearestX(x, z, root, Double.MAX_VALUE, null).data;
	}

	private KDNode nearestX(double x, double z, KDNode n, double best, KDNode bestNode) {
		double d = distXZ(x, z, n.data);
		if (d < best) {
			best = d;
			bestNode = n;
		}
		if (x < n.data.getPosition().getX() && n.left != null) {
			return nearestZ(x, z, n.left, best, bestNode);
		} else if (x > n.data.getPosition().getX() && n.right != null) {
			return nearestZ(x, z, n.right, best, bestNode);
		} else {
			return bestNode;
		}
	}

	private KDNode nearestZ(double x, double z, KDNode n, double best, KDNode bestNode) {
		double d = distXZ(x, z, n.data);
		if (d < best) {
			best = d;
			bestNode = n;
		}
		if (z < n.data.getPosition().getZ() && n.left != null) {
			return nearestX(x, z, n.left, best, bestNode);
		} else if (z > n.data.getPosition().getZ() && n.right != null) {
			return nearestX(x, z, n.right, best, bestNode);
		} else {
			return bestNode;
		}
	}

	private double distXZ(double x, double z, SolarSystem s) {
		return Math.pow(x - s.getPosition().getX(), 2) + Math.pow(z - s.getPosition().getZ(), 2);
	}

	public List<SolarSystem> range(double x, double z, double r) {
		return null;
	}

	public List<SolarSystem> range(SolarSystem s, double r) {
		return range(s.getPosition().getX(), s.getPosition().getZ(), r);
	}

	public void print() {
		print(root, 0);
	}

	private void print(KDNode n, int depth) {
		if (n == null) {
			return;
		}
		for (int i = 0; i < depth; i++) {
			System.out.print("-");
		}
		System.out.println(n.data.getName() + "  " + n.data.getPosition().getX() + "  " + n.data.getPosition().getZ());
		print(n.left, depth + 1);
		print(n.right, depth + 1);
	}

	private List<SolarSystem> sortByX(List<SolarSystem> l) {
		ArrayList<SolarSystem> n = new ArrayList<SolarSystem>(l);
		n.sort(new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem a, SolarSystem b) {
				double ax = a.getPosition().getX();
				double bx = b.getPosition().getX();
				if (ax < bx) {
					return -1;
				} else if (ax > bx) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return n;
	}

	private List<SolarSystem> sortByZ(List<SolarSystem> l) {
		ArrayList<SolarSystem> n = new ArrayList<SolarSystem>(l);
		n.sort(new Comparator<SolarSystem>() {
			@Override
			public int compare(SolarSystem a, SolarSystem b) {
				double az = a.getPosition().getZ();
				double bz = b.getPosition().getZ();
				if (az < bz) {
					return -1;
				} else if (az > bz) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return n;
	}

	private class KDNode {
		public SolarSystem data;
		public KDNode left;
		public KDNode right;
	}
}
