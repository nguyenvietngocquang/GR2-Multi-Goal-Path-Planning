package main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.TSP;
import gui.GUIRobotics;
import util.Point;

public class Test {
	public static void main(String[] args) throws IOException {
		// Doc du lieu dau vao
		LinkedList<Point> pointsToVisit = readPointData("dj38.tsp");

		try {
			TSP tsp = new TSP(pointsToVisit, 10000);
			// Hien thi ket qua
			showResult(pointsToVisit, tsp);
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

	public static LinkedList<Point> readPointData(String filename) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(filename));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		scan.nextDouble();
		double x = scan.nextDouble();
		double y = scan.nextDouble();
		pointsToVisit.addLast(new Point(x, y));

		double xMin = x, xMax = x;
		double yMin = y, yMax = y;

		scan.nextDouble();
		x = scan.nextDouble();
		while (x != -1) {
			y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			if (x < xMin)
				xMin = x;
			else if (x > xMax)
				xMax = x;
			if (y < yMin)
				yMin = y;
			else if (y > yMax)
				yMax = y;

			scan.nextDouble();
			x = scan.nextDouble();
		}
		scan.close();

		// Chuan hoa du lieu dau vao
		for (Point point : pointsToVisit) {
			point.x = Math.round((point.x - xMin) / (xMax - xMin) * 10000000) / 100000.0;
			point.y = Math.round((point.y - yMin) / (yMax - yMin) * 10000000) / 100000.0;
		}

		return pointsToVisit;
	}

	public static void showResult(LinkedList<Point> pointsToVisit, TSP tsp) {
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
		gui.generateEnvironment("no-obstacles.txt");

		// Ve do thi
		for (Point point : pointsToVisit) {
			gui.canvas.drawPoint(point, Color.BLACK);
		}
		LinkedList<Point> neurons = new LinkedList<Point>();
		for (int i = 0; i < tsp.inhibited.size(); i++) {
			neurons.add(tsp.inhibited.get(i));
		}
		gui.canvas.drawLines(tsp.path, neurons);
	}
}
