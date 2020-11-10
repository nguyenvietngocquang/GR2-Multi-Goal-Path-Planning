package main;

import java.awt.Color;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

import algorithm.SOM;
import gui.GUIRobotics;
import util.Graph;
import util.Neuron;
import util.Point;

public class Main {
	public static void main(String[] args) throws IOException {
		// Tao moi truong
		GUIRobotics gui = new GUIRobotics(500, 110, 11);
		gui.generateEnvironment("obstacles.txt");

		// Doc du lieu dau vao
		Graph graph = new Graph("obstacles.txt");
		Scanner scan = new Scanner(new File("visit.txt"));
		LinkedList<Point> pointsToVisit = new LinkedList<Point>();
		double x = scan.nextDouble();
		while (x != -1) {
			double y = scan.nextDouble();
			pointsToVisit.addLast(new Point(x, y));
			x = scan.nextDouble();
		}
		scan.close();

		try {
			SOM som = new SOM(graph, pointsToVisit, 10);

			// Ve do thi
			long start = System.currentTimeMillis();
			for (Point point : pointsToVisit) {
				gui.canvas.drawPoint(point, Color.GREEN);
			}
			for (Neuron neuron : som.inhibited) {
				gui.canvas.drawPoint(neuron, Color.ORANGE);
			}
			for (int i = 0; i < som.path.size() - 1; i++) {
				gui.canvas.drawLine(som.path.get(i), som.path.get(i + 1), Color.BLACK);
			}
			gui.canvas.drawLine(som.path.getLast(), som.path.getFirst(), Color.BLACK);
			long time = System.currentTimeMillis() - start;
			System.out.println("- GUI:\t\t" + time / 1000F + "s");

			System.out.println("Length:\t" + som.pathLength);

		} catch (Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
		}
		System.out.println("End!");
	}
}
