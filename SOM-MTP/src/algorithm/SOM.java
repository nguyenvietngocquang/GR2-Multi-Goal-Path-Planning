package algorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import graph.GraphDivision;
import graph.MakeWeightedGraph;
import util.Graph;
import util.Neuron;
import util.Point;
import util.Ring;

public class SOM {

	public Ring ring;
	public Graph graph;
	public GraphDivision graphDivision;
	public MakeWeightedGraph makeGraph;
	public LinkedList<Point> visit = new LinkedList<Point>();
	public LinkedList<Neuron> inhibited = new LinkedList<Neuron>();
	public LinkedList<Point> path = new LinkedList<Point>();
	public double pathLength = 0;
	int epochs;
	double learningRate = 0.6;
	final double maxError = 0.001; // Maximal allowable error
	int n; // Number of nodes
	double G; // The gain parameter
	int m; // Number of neighbors

	Random rd = new Random();

	public SOM(Graph graph, LinkedList<Point> visit, int epochs) throws IOException {
		this.graph = graph;
		this.visit = visit;
		this.epochs = epochs;
		this.n = (int) (visit.size() * 3);
		this.G = 0.06 + 12.41 * n;
		this.m = (int) (0.2 * n);

		long start = System.currentTimeMillis();
		createRingOfNeurons();
		long time = System.currentTimeMillis() - start;
		System.out.println("- Init:\t\t" + time / 1000F + "s");

		start = System.currentTimeMillis();
		somAlgorithm();
		time = System.currentTimeMillis() - start;
		System.out.println("- SOM:\t\t" + time / 1000F + "s");

		start = System.currentTimeMillis();
		getPath();
		time = System.currentTimeMillis() - start;
		System.out.println("- Path:\t\t" + time / 1000F + "s");
	}

	public void createRingOfNeurons() {
		this.ring = new Ring();

		// Trung binh toa do x, y cua cac neuron
		double meanX = 0, meanY = 0;
		Neuron[] neurons = new Neuron[n];
		for (int i = 0; i < n; i++) {
			Neuron neuron;
			do {
				neuron = new Neuron((int) (rd.nextDouble() * 10000) / 100.0, (int) (rd.nextDouble() * 10000) / 100.0);
			} while (neuron.isIntersectGraph(graph));
			meanX += neuron.x;
			meanY += neuron.y;
			neurons[i] = neuron;
		}
		meanX /= n;
		meanY /= n;

		// Tinh cac goc qua arctan
		double[] angles = new double[n];
		for (int i = 0; i < n; i++) {
			angles[i] = Math.atan2(neurons[i].y - meanY, neurons[i].x - meanX);
		}
		double[] temp = angles.clone();
		// Sap xep theo goc
		Arrays.sort(angles);

		int position = indexOf(temp, angles[0]);
		Neuron neuron = new Neuron(neurons[position].x, neurons[position].y);
		ring.rightInsert(neuron, null);

		for (int i = 1; i < n; i++) {
			position = indexOf(temp, angles[i]);
			neuron = new Neuron(neurons[position].x, neurons[position].y);
			ring.rightInsert(neuron, ring.neurons.get(i - 1));
		}

	}

	public void somAlgorithm() {
		for (int epoch = 0; epoch < epochs; epoch++) {
			double error = 0;
			// Update the learning rate
			double rate = learningRate * Math.exp(-1.0 * epoch / epochs);

			// A set of inhibited neurons
			this.inhibited = new LinkedList<Neuron>();

			// A random permutation of visit points
			Collections.shuffle(visit);

			this.graphDivision = new GraphDivision(graph, visit, ring.neurons);

			for (int i = 0; i < visit.size(); i++) {
				// Find the smallest Euclidean distance
				int index = 0;
				Point currentVisit = visit.get(i);
				while (inhibited.contains(ring.get(index))) {
					index++;
				}
				double minDistance = currentVisit.distanceFrom(ring.get(index));
				int position = index;
				for (int j = 0; j < ring.size(); j++) {
					if (!inhibited.contains(ring.get(j))) {
						double distance = currentVisit.distanceFrom(ring.get(j));
						if (distance < minDistance) {
							minDistance = distance;
							position = j;
						}
					}
				}
				inhibited.add(ring.get(position));

				// Update the winner neuron weight
				error = Math.max(error,
						updateWeight(ring.get(position), ring.get(position), currentVisit, rate, epoch));

				// Update the neighbor neuron weight
				Neuron prevNeuron = ring.get(position);
				Neuron nextNeuron = ring.get(position);
				for (int j = 0; j < m; j++) {
					prevNeuron = prevNeuron.prev;
					nextNeuron = nextNeuron.next;
					error = Math.max(error, updateWeight(ring.get(position), prevNeuron, currentVisit, rate, epoch));
					error = Math.max(error, updateWeight(ring.get(position), nextNeuron, currentVisit, rate, epoch));
				}

				// Update graph division
				this.graphDivision = new GraphDivision(graph, visit, ring.neurons);
			}

			if (error <= maxError)
				break;
		}
	}

	public void getPath() throws IOException {
		// Get the list of winning neurons
		int[] index = new int[visit.size()];
		int flag = 0;
		for (Neuron neuron : inhibited) {
			index[flag] = ring.indexOf(neuron);
			flag++;
		}
		Arrays.sort(index);

		// Get path
		File f = new File("neurons.txt");
		FileWriter fw = new FileWriter(f);
		fw.write("List of neurons:\n");

		flag--;
		for (int i = 0; i < flag; i++) {
			Neuron neuron = ring.get(index[i]);
			fw.write("(" + neuron.x + ", " + neuron.y + ")\n");
			AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, neuron,
					ring.get(index[i + 1]));
			pathLength += findPath.length;
			for (int j = 0; j < findPath.path.size() - 1; j++) {
				path.add(findPath.path.get(j));
			}
		}
		Neuron neuron = ring.get(index[flag]);
		fw.write("(" + neuron.x + ", " + neuron.y + ")\n");
		AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, neuron, ring.get(index[0]));
		pathLength += findPath.length;
		for (int j = 0; j < findPath.path.size() - 1; j++) {
			path.add(findPath.path.get(j));
		}
		fw.write("-1");
		fw.close();

		// Save path
		f = new File("path.txt");
		fw = new FileWriter(f);
		fw.write("Path to follow:\n");
		for (Point point : path) {
			fw.write("(" + point.x + ", " + point.y + ")\n");
		}
		fw.write("-1");
		fw.close();
	}

	public int indexOf(double arr[], double value) {
		for (int i = 0; i < arr.length; i++) {
			if (value == arr[i])
				return i;
		}
		return -1;
	}

	// Compute neighborhood function
	public double neighborhoodFunction(int epoch, Neuron winnerNeuron, Neuron currentNeuron) {
		double radius = 4 * Math.exp(-1.0 * epoch / epochs);
		double distance = winnerNeuron.distanceFrom(currentNeuron);
		return Math.exp(-Math.pow(distance, 2) / (2 * Math.pow(radius, 2)));
	}

	// Update the neuron weight
	public double updateWeight(Neuron winnerNeuron, Neuron currentNeuron, Point visitPoint, double rate, int epoch) {
		AStar findPath = new AStar(graph, graphDivision.midPoints, graphDivision.MAKLINK, currentNeuron, visitPoint);
		// Distance to move
		double move = rate * neighborhoodFunction(epoch, winnerNeuron, currentNeuron) * findPath.length;
		double temp = move;

		int pathCount = findPath.path.size() - 1;
		int i = 0;
		while (i < pathCount) {
			Point Point1 = findPath.path.get(i);
			Point Point2 = findPath.path.get(i + 1);
			double length = Point1.distanceFrom(Point2);
			if (length > move) {
				// Update the coordinates
				double theta = Math.atan2(Point2.y - Point1.y, Point2.x - Point1.x);
				currentNeuron.x = Point1.x + move * Math.cos(theta);
				currentNeuron.y = Point1.y + move * Math.sin(theta);
				break;
			}
			move -= length;
			i++;
		}

		return temp;
	}

}
