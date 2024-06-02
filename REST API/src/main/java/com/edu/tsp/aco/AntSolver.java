package com.edu.tsp.aco;

import com.edu.tsp.TSPSolver;

import java.util.ArrayList;
import java.util.Random;

public class AntSolver extends TSPSolver {
    private int start;
    private final double[][] weights;
    private final boolean[] visited;
    private final int[] choices;

    private int next(double[] weight) {
        int pos = 0;
        double sum = 0;

        for (int j = 0; j < nVertices; ++j) {
            if (!visited[j]) {
                choices[pos++] = j;
                sum += weight[j];
            }
        }

        Random random = new Random();
        double rnd = random.nextDouble(sum);

        pos = 0;

        while (rnd > 0) {
            rnd -= weight[choices[pos++]];
        }

        return choices[pos - 1];
    }

    public AntSolver(double[][] weights, boolean[] visited, int[] choices) {
        this.weights = weights;
        this.visited = visited;
        this.choices = choices;
    }

    @Override
    public void solve(double[][] adjMat) {
        nVertices = adjMat.length;

        solution = new ArrayList<>();
        len = 0;

        solution.add(start);
        visited[start] = true;

        for (int i = 1; i < nVertices; ++i) {
            int from = solution.get(i - 1);
            int to = next(weights[from]);

            solution.add(to);
            len += adjMat[from][to];
            visited[to] = true;
        }

        int finish = solution.get(nVertices - 1);

        solution.add(start);
        len += adjMat[finish][start];
    }

    public void setStart(int start) {
        this.start = start;
    }
}
