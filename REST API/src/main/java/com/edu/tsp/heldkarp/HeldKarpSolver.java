package com.edu.tsp.heldkarp;

import com.edu.tsp.TSPSolver;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс HeldKarpSolver для решения задачи коммивояжера
 * алгоритмом динамического программирования
 */
public class HeldKarpSolver extends TSPSolver {
    /**
     * dp[i][j] = длина кратчайшего пути из
     * вершины (n - 1) до вершины j, проходящего
     * через вершины множества i только 1 раз
     */
    private double[][] dp;

    /**
     * Массив родителей (запоминаем переходы
     * в динамике для восстановления решения)
     */
    private Pair[][] p;

    /**
     * Матрица смежности графа
     */
    private double[][] adjMat;

    private double calc(int i, int j) {
        if (dp[i][j] == Double.POSITIVE_INFINITY) {
            int two = 1;

            for (int k = 0; k < nVertices - 1; ++k, two <<= 1) {
                if ((i & two) != 0) {
                    double len = calc(i - two, k) + adjMat[k][j];

                    if (len < dp[i][j]) {
                        dp[i][j] = len;

                        p[i][j].setI(i - two);
                        p[i][j].setJ(k);
                    }
                }
            }
        }

        return dp[i][j];
    }

    @Override
    public void solve(double[][] adjMat) {
        nVertices = adjMat.length;
        len = Double.POSITIVE_INFINITY;

        solution = new ArrayList<>();

        // Этап инициализации

        int n = (1 << (nVertices - 1)) - 1;
        int m = nVertices - 1;

        dp = new double[n][m];
        p = new Pair[n][m];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                dp[i][j] = Double.POSITIVE_INFINITY;
                p[i][j] = new Pair(-1, -1);
            }
        }

        this.adjMat = adjMat;

        for (int j = 0; j < m; ++j) {
            dp[0][j] = adjMat[nVertices - 1][j];
        }

        // Этап решения (находим длину решения)

        int two = 1;
        Pair pair = new Pair(-1, -1);

        for (int i = 0; i < nVertices - 1; ++i, two <<= 1) {
            double len = calc(n - two, i) + adjMat[i][nVertices - 1];

            if (len < this.len) {
                this.len = len;

                pair.setI(n - two);
                pair.setJ(i);
            }
        }

        // Этап решения (восстанавливаем решение)

        solution.add(nVertices - 1);

        while (pair.getJ() != -1) {
            int i = pair.getI();
            int j = pair.getJ();

            solution.add(j);

            pair.setI(p[i][j].getI());
            pair.setJ(p[i][j].getJ());
        }

        solution.add(nVertices - 1);
        Collections.reverse(solution);
    }

    /**
     * Вспомогательный класс пара
     */
    @Setter
    @Getter
    private static class Pair {
        /**
         * Индекс строки
         */
        private int i;

        /**
         * Индекс столбца
         */
        private int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", i, j);
        }
    }
}
