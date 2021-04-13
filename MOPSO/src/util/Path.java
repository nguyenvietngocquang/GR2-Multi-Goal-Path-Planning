package util;

import algorithm.PSO;

public class Path {
	public int n; // number of line segment
	public double pointy[];
	public Point points[];
	public double distance;
	double R;

	public Path(int number, double R, double[] pointy, Point[] points) {
		this.n = number;
		this.pointy = pointy;
		this.points = points;
		this.R = R;
		this.distance = 0;
		for (int i = 0; i < points.length - 1; i++) {
			distance += Math.hypot(points[i + 1].x - points[i].x, points[i + 1].y - points[i].y);
		}
	}

	public void distance() {
		this.distance = 0;
		for (int i = 0; i < points.length - 1; i++) {
			this.distance += Math.hypot(points[i + 1].x - points[i].x, points[i + 1].y - points[i].y);
		}
	}

	public void print() {
		for (int i = 0; i != n; i++) {
			points[i].printPoint();
		}
	}

	public Path(int number) {
		n = number;
		this.pointy = new double[n];
		this.points = new Point[n];
	}

	public static Point convertPointToPoint(double pointy, double pointx, Point start, Point end) {
//		double temp1, temp2, AB;
//		double sinp, cosp, x, y;
//		temp1 = end.x - start.x;
//		temp2 = end.y - start.y;
//		AB = Math.hypot(temp1, temp2);
//		cosp = (temp1 / AB) * Math.cos(Math.toRadians(angle)) - (temp2 / AB) * Math.sin(Math.toRadians(angle));
//		sinp = (temp1 / AB) * Math.sin(Math.toRadians(angle)) + (temp2 / AB) * Math.cos(Math.toRadians(angle));
//		x = start.x + R * cosp;
//		y = start.y + R * sinp;
//		Point p = new Point(x, y);
//		return p;
		double x, y, temp1, temp2, phi;
		temp1 = end.x - start.x;
		temp2 = end.y - start.y;
		phi = Math.atan(temp2 / temp1);
		x = Math.cos(phi) * pointx - Math.sin(phi) * pointy + start.x;
		y = Math.sin(phi) * pointx + Math.cos(phi) * pointy + start.y;
		return new Point(x, y);
	}

	public double pathSmooth() {
		double a, b, c1, c2, smooth;
		double sum = 0;
		for (int i = 0; i < points.length - 2; i++) {
			a = Math.hypot(points[i + 1].x - points[i].x, points[i + 1].y - points[i].y);
			b = Math.hypot(points[i + 2].x - points[i + 1].x, points[i + 2].y - points[i + 1].y);
			c1 = (points[i + 1].x - points[i].x) * (points[i + 2].x - points[i + 1].x);
			c2 = (points[i + 1].y - points[i].y) * (points[i + 2].y - points[i + 1].y);
			double temp = (c1 + c2) / (a * b);
			if (temp >= 1)
				sum += Math.PI;
			else if (temp >= -1)
				sum += Math.PI - Math.acos(temp);
		}
		smooth = sum / (points.length - 2);
		return smooth;
	}

	// Su dung dinh li cosin trong tam giac
//	public double pathSmooth() {
//		double temp = 0;
//		double[] ang = new double[n];
//		double a, b, c;
//		for (int i = 0; i < n; i++) {
//			if (i == 0) {
//				a = Math.pow(points[0].x - PSO.startPoint.x, 2) + Math.pow(points[0].y - PSO.startPoint.y, 2);
//				b = Math.pow(points[1].x - points[0].x, 2) + Math.pow(points[1].y - points[0].y, 2);
//				c = Math.pow(PSO.startPoint.x - points[1].x, 2) + Math.pow(PSO.startPoint.y - points[1].y, 2);
//				ang[0] = Math.toDegrees(Math.acos((a + b - c) / Math.sqrt(4 * a * b)));
//				if (a * b == 0) {
//					System.out.println("ERROR with " + a + " " + b);
//					PSO.startPoint.printPoint();
//					points[0].printPoint();
//					points[1].printPoint();
//
//				}
//
//				if (ang[0] != ang[0]) {
//					if ((a + b - c) / Math.sqrt(4 * a * b) < -1) {
//						ang[0] = Math.toDegrees(Math.acos(-1));
//					} else if ((a + b - c) / Math.sqrt(4 * a * b) > -1) {
//						ang[0] = Math.toDegrees(Math.acos(1));
//					}
//				}
//			} else if (i == n - 1) {
//				a = Math.pow(points[n - 1].x - points[n - 2].x, 2) + Math.pow(points[n - 1].y - points[n - 2].y, 2);
//				b = Math.pow(points[n - 1].x - PSO.endPoint.y, 2) + Math.pow(points[n - 1].y - PSO.endPoint.y, 2);
//				c = Math.pow(points[n - 2].x - PSO.endPoint.x, 2) + Math.pow(points[n - 2].y - PSO.endPoint.y, 2);
//				ang[n - 1] = Math.toDegrees(Math.acos((a + b - c) / Math.sqrt(4 * a * b)));
//				if (a * b == 0) {
//					System.out.println("ERROR with " + a + " " + b);
//					this.print();
//					points[n - 2].printPoint();
//					points[n - 1].printPoint();
//					PSO.endPoint.printPoint();
//				}
//				if (ang[n - 1] != ang[n - 1]) {
//					if ((a + b - c) / Math.sqrt(4 * a * b) < -1) {
//						ang[n - 1] = Math.toDegrees(Math.acos(-1));
//					}
//					if ((a + b - c) / Math.sqrt(4 * a * b) > 1) {
//						ang[n - 1] = Math.toDegrees(Math.acos(1));
//					}
//				}
//			} else {
//				a = Math.pow(points[i - 1].x - points[i].x, 2) + Math.pow(points[i - 1].y - points[i].y, 2);
//				b = Math.pow(points[i].x - points[i + 1].x, 2) + Math.pow(points[i].y - points[i + 1].y, 2);
//				c = Math.pow(points[i - 1].x - points[i + 1].x, 2) + Math.pow(points[i - 1].y - points[i + 1].y, 2);
//				ang[i] = Math.toDegrees(Math.acos((a + b - c) / Math.sqrt(4 * a * b)));
//				if (a * b == 0) {
//					System.out.println("ERROR with " + a + " " + b);
//					this.print();
//					points[i - 1].printPoint();
//					points[i].printPoint();
//					points[i + 1].printPoint();
//				}
//				if (ang[i] != ang[i]) {
//					if ((a + b - c) / Math.sqrt(4 * a * b) < -1) {
//						ang[i] = Math.toDegrees(Math.acos(-1));
//					} else if ((a + b - c) / Math.sqrt(4 * a * b) > 1) {
//						ang[i] = Math.toDegrees(Math.acos(1));
//					}
//				}
//			}
//		}
//		for (int i = 0; i < n; i++) {
//			temp += ang[i];
//		}
//		temp = temp / n;
//		return 180 - temp;
//	}

	// Neu o ngoai canh AB thi tich vo huong AS va AB < 0, tuong tu
	public static double p2sDistance(Point p1, Point p2, Point S) {
		Point p1S = new Point(S.x - p1.x, S.y - p1.y);
		Point p2S = new Point(S.x - p2.x, S.y - p2.y);
		Point p1p2 = new Point(p2.x - p1.x, p2.y - p1.y);
		if (p1S.x * p1p2.x + p1S.y * p1p2.y <= 0) {
			return p1.distanceFrom(S);
		} else if (-p1p2.x * p2S.x + -p1p2.y * p2S.y <= 0) {
			return p2.distanceFrom(S);
		} else {
			// |SH| = |AS.AB|/|AB|
			return Math.abs(p1S.x * p1p2.x + p1S.y * p1p2.y) / Math.sqrt(p1p2.x * p1p2.x + p1p2.y * p1p2.y);
		}
	}

	public double pathSafety(Graph g) {
		double[] dis = new double[n + 1];
		double d, safety = 0;

		// distance from a line segment to an obstacle vertice
		for (int i = 0; i <= n; i++) {
			dis[i] = 9999;
			for (int j = 0; j != g.obstacleNumber; j++) {
				for (int k = 0; k != g.obstacles[j].cornerNumber; k++) {
					if (i == 0) {
						d = p2sDistance(PSO.startPoint, points[0], g.obstacles[j].points[k]);
					} else if (i == n) {
						d = p2sDistance(PSO.endPoint, points[n - 1], g.obstacles[j].points[k]);
					} else {
						d = p2sDistance(points[i], points[i - 1], g.obstacles[j].points[k]);
					}
					if (d < dis[i]) {
						dis[i] = d;
					}
				}
			}
		}
		safety = dis[0];
		for (int i = 1; i <= n; i++) {
			if (safety > dis[i]) {
				safety = dis[i];
			}
		}

		for (int i = 0; i < g.obstacleNumber; i++) {
			for (int j = 0; j < g.obstacles[i].cornerNumber; j++) {
				if (j == g.obstacles[i].cornerNumber - 1) {
					for (int k = 0; k < n; k++) {
						d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], points[k]);
						if (d < safety) {
							safety = d;
						}
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], PSO.startPoint);
					if (d < safety) {
						safety = d;
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[0], PSO.endPoint);
					if (d < safety) {
						safety = d;
					}
				} else {
					for (int k = 0; k < n; k++) {
						d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], points[k]);
						if (d < safety)
							safety = d;
					}
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], PSO.startPoint);
					if (d < safety)
						safety = d;
					d = p2sDistance(g.obstacles[i].points[j], g.obstacles[i].points[j + 1], PSO.endPoint);
					if (d < safety)
						safety = d;
				}
			}
		}

		return Math.exp(-safety);
	}

	public boolean collision(Graph g, Point endPoint) {
		for (int i = 0; i < n - 1; i++) {
			if (g.isIntersectLine(points[i], points[i + 1])) {
				return true;
			}
		}
		return g.isIntersectLine(points[n - 1], endPoint);
	}

	public boolean compare(Path that) {
		if (this.points[0] == null) {
			return false;
		} else if (that.points[0] == null) {
			return true;
		} else if (this.distance <= that.distance) {
			return true;
		}
		return false;
	}

}