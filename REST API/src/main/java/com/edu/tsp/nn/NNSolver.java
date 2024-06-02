package com.edu.tsp.nn;

import com.edu.tsp.TSPSolver;

import java.util.ArrayList;
import java.util.Random;

public class NNSolver extends TSPSolver {
    @Override
    public void solve(double[][] adjMat) {
        nVertices = adjMat.length;
        boolean[] visited = new boolean[nVertices];

        solution = new ArrayList<>();
        len = 0;

        // Генерируем стартовую вершину
        Random random = new Random();
        int cur = random.nextInt(nVertices);

        solution.add(cur); // Добавляем стартовую вершину в решение
        visited[cur] = true; // Стартовая вершина посещена (уже есть в решении)

        for (int i = 1; i < nVertices; ++i) {
            int next = cur;
            double dist = Double.POSITIVE_INFINITY;

            for (int j = 0; j < nVertices; ++j) {
                if (!visited[j] && adjMat[cur][j] < dist) {
                    next = j;
                    dist = adjMat[cur][j];
                }
            }

            solution.add(next);
            visited[next] = true;
            len += dist;

            cur = next;
        }

        int start = solution.get(0);
        int finish = solution.get(nVertices - 1);

        solution.add(start);
        len += adjMat[finish][start];
    }
}
