package graph;

import java.util.LinkedList;

import algorithm.AStar;
import util.Graph;
import util.Line;
import util.Point;

public class MakeWeightedGraph {
	public double[][] weights;

	public MakeWeightedGraph(Graph myGraph, LinkedList<Point> visitPoints, LinkedList<Point> midPoints,
			LinkedList<Line> lines) {
		int size = visitPoints.size();
		this.weights = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				AStar findPath = new AStar(myGraph, midPoints, lines, visitPoints.get(i), visitPoints.get(j));
				weights[i][j] = findPath.length;
			}
		}
	}

}
