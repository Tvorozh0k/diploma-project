package com.edu.tsp.opt;

import com.edu.tsp.TSPSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс KOptSolver для решения задачи коммивояжера
 * алгоритмом k-opt
 */
public class KOptSolver extends TSPSolver {
    /**
     * Тип используемого алгоритма. Возможные значения:
     * <p>
     * - 2-opt (Алгоритм 2-оптимизации)
     * <p>
     * - 2.5-opt (Алгоритм 2.5-оптимизации)
     * <p>
     * - 3-opt (Алгоритм 3-оптимизации)
     */
    private final String type;

    /**
     * Матрица смежности
     */
    private double[][] adjMat;

    /**
     * Префикс длины решения solution <p>
     * pref[i] = длина пути от 0 до i (0 -> 1 -> ... -> i), pref[0] = 0
     */
    private double[] pref;

    /**
     * Суффикс длины обратного пути solution^R <p>
     * suf[i] = длина обратного пути от 0 до i (0 -> (n - 1) -> ... -> i), suf[n] = 0
     */
    private double[] suf;

    /**
     * Алгоритм 2-opt <p>
     * Первоначальное решение: 0 A (i, i + 1) B (j, j + 1) C 0
     * <p>
     * A - путь от 0 до i <p>
     * B - путь от (i + 1) до j <p>
     * C - путь от (j + 1) до 0 <p>
     * <p>
     * Возможны 2 случая: <p>
     * <p>
     * 1) 0 A (i, j) B^R (i + 1, j + 1) C 0 <p>
     * 2) 0 C^R (j + 1, i + 1) B (j, i) A^R 0 <p>
     * <p>
     * A^R - путь от i до 0 <p>
     * B^R - путь от j до (i + 1) <p>
     * C^R - путь от 0 до (j + 1)
     */
    private boolean twoOpt() {
        for (int i = 0; i < nVertices - 2; ++i) {
            for (int j = i + 2; j < nVertices; ++j) {
                // A = 0 -> 1 -> ... -> (i - 1) -> i
                double newLen1 = pref[i];

                // A^R = i -> (i - 1) -> ... -> 1 -> 0
                double newLen2 = suf[0] - suf[i];

                // i -> j
                newLen1 += adjMat[solution.get(i)][solution.get(j)];

                // j -> i
                newLen2 += adjMat[solution.get(j)][solution.get(i)];

                // B^R = j -> (j - 1) -> ... -> (i + 2) -> (i + 1)
                newLen1 += suf[i + 1] - suf[j];

                // B = (i + 1) -> (i + 2) -> ... -> (j - 1) -> j
                newLen2 += pref[j] - pref[i + 1];

                // (i + 1) -> (j + 1)
                newLen1 += adjMat[solution.get(i + 1)][solution.get(j + 1)];

                // (j + 1) -> (i + 1)
                newLen2 += adjMat[solution.get(j + 1)][solution.get(i + 1)];

                // C = (j + 1) -> (j + 2) -> ... -> (n - 1) -> 0
                newLen1 += pref[nVertices] - pref[j + 1];

                // C^R = 0 -> (n - 1) -> ... -> (j + 2) -> (j + 1)
                newLen2 += suf[j + 1];

                if (Math.min(newLen1, newLen2) < len) {
                    Collections.reverse(solution.subList(i + 1, j + 1));

                    if (newLen1 <= newLen2) {
                        len = newLen1;
                    } else {
                        Collections.reverse(solution.subList(1, nVertices));
                        len = newLen2;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Алгоритм 2.5-opt <p>
     * Первоначальное решение: 0 A (i, i + 1) (i + 1, i + 2) B (j, j + 1) C 0
     * <p>
     * A - путь от 0 до i <p>
     * B - путь от (i + 2) до j <p>
     * C - путь от (j + 1) до 0 <p>
     * <p>
     * Возможны 2 случая: <p>
     * <p>
     * 1) 0 A (i, i + 2) B (j, i + 1) (i + 1, j + 1) C 0 <p>
     * 2) 0 C^R (j + 1, i + 1) (i + 1, j) B^R (i + 2, i) A^R 0 <p>
     * <p>
     * A^R - путь от i до 0 <p>
     * B^R - путь от j до (i + 2) <p>
     * C^R - путь от 0 до (j + 1)
     */
    private boolean twoHalfOpt() {
        for (int i = 2; i < nVertices - 2; ++i) {
            double newLen1 = pref[nVertices];
            double newLen2 = suf[0];

            // 0 -> 1 -> ... -> i -> (i + 1) -> ... -> (n - 1) -> 0
            newLen1 -= adjMat[solution.get(0)][solution.get(1)];
            newLen1 -= adjMat[solution.get(nVertices - 1)][solution.get(0)];
            newLen1 -= adjMat[solution.get(i)][solution.get(i + 1)];

            // 0 -> (i + 1) -> (i + 2) -> ... -> (n - 1) -> 1 -> 2 -> ... -> i -> 0
            newLen1 += adjMat[solution.get(0)][solution.get(i + 1)];
            newLen1 += adjMat[solution.get(nVertices - 1)][solution.get(1)];
            newLen1 += adjMat[solution.get(i)][solution.get(0)];

            // 0 -> (n - 1) -> ... -> (i + 1) -> i -> ... -> 1 -> 0
            newLen2 -= adjMat[solution.get(1)][solution.get(0)];
            newLen2 -= adjMat[solution.get(0)][solution.get(nVertices - 1)];
            newLen2 -= adjMat[solution.get(i + 1)][solution.get(i)];

            // 0 -> i -> ... -> 2 -> 1 -> (n - 1) -> ... -> (i + 2) -> (i + 1) -> 0
            newLen2 += adjMat[solution.get(i + 1)][solution.get(0)];
            newLen2 += adjMat[solution.get(1)][solution.get(nVertices - 1)];
            newLen2 += adjMat[solution.get(0)][solution.get(i)];

            if (Math.min(newLen1, newLen2) < len) {
                Collections.rotate(solution.subList(1, nVertices), -i);

                if (newLen1 <= newLen2) {
                    len = newLen1;
                } else {
                    Collections.reverse(solution.subList(1, nVertices));
                    len = newLen2;
                }

                return true;
            }
        }

        for (int i = 0; i < nVertices - 1; ++i) {
            for (int h = 3; h < nVertices - 1; ++h) {
                int j = (i + h) % nVertices;

                double newLen1 = pref[nVertices];
                double newLen2 = suf[0];

                // 0 -> 1 -> ... -> i -> (i + 1) -> (i + 2) -> ... -> j -> (j + 1) -> ... -> (n - 1) -> 0
                newLen1 -= adjMat[solution.get(i)][solution.get(i + 1)];
                newLen1 -= adjMat[solution.get(i + 1)][solution.get(i + 2)];
                newLen1 -= adjMat[solution.get(j)][solution.get(j + 1)];

                // 0 -> 1 -> ... -> i -> (i + 2) -> ... -> j -> (i + 1) -> (j + 1) -> ... -> (n - 1) -> 0
                newLen1 += adjMat[solution.get(i)][solution.get(i + 2)];
                newLen1 += adjMat[solution.get(j)][solution.get(i + 1)];
                newLen1 += adjMat[solution.get(i + 1)][solution.get(j + 1)];

                // 0 -> (n - 1) -> ... -> (j + 1) -> j -> ... -> (i + 2) -> (i + 1) -> i -> ... -> 1 -> 0
                newLen2 -= adjMat[solution.get(i + 1)][solution.get(i)];
                newLen2 -= adjMat[solution.get(i + 2)][solution.get(i + 1)];
                newLen2 -= adjMat[solution.get(j + 1)][solution.get(j)];

                // 0 -> (n - 1) -> ... -> (j + 1) -> (i + 1) -> j -> ... -> (i + 2) -> i -> ... -> 1 -> 0
                newLen2 += adjMat[solution.get(i + 2)][solution.get(i)];
                newLen2 += adjMat[solution.get(i + 1)][solution.get(j)];
                newLen2 += adjMat[solution.get(j + 1)][solution.get(i + 1)];

                if (Math.min(newLen1, newLen2) < len) {
                    int pos1 = Math.min(i + 1, j + 1);
                    int pos2 = Math.max(j + 1, i + 2);
                    int shift = (i < j) ? -1 : 1;

                    Collections.rotate(solution.subList(pos1, pos2), shift);

                    if (newLen1 <= newLen2) {
                        len = newLen1;
                    } else {
                        Collections.reverse(solution.subList(1, nVertices));
                        len = newLen2;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Алгоритм 3-opt (возможны 8 случаев)
     */
    private boolean threeOpt() {
        for (int i = 0; i < nVertices - 2; ++i)
            for (int j = i + 1; j < nVertices - 1; ++j)
                for (int k = j + 1; k < nVertices; ++k)
                {
                    double newLen = len;

                    // ... -> x -> (x + 1) -> ...

                    newLen -= adjMat[solution.get(i)][solution.get(i + 1)];
                    newLen -= adjMat[solution.get(j)][solution.get(j + 1)];
                    newLen -= adjMat[solution.get(k)][solution.get(k + 1)];

                    // i -> j + 1 -> ... -> k -> i + 1 -> ... -> j -> k + 1 -> ...
                    newLen += adjMat[solution.get(i)][solution.get(j + 1)];
                    newLen += adjMat[solution.get(k)][solution.get(i + 1)];
                    newLen += adjMat[solution.get(j)][solution.get(k + 1)];

                    if (newLen < len)
                    {
                        Collections.rotate(solution.subList(i + 1, k + 1), i - j);
                        len = newLen;

                        return true;
                    }
                }

        return false;
    }

    public KOptSolver(String type, List<Integer> solution, double len) {
        this.type = type;

        this.solution = new ArrayList<>(solution);
        this.len = len;
    }

    @Override
    public void solve(double[][] adjMat) {
        this.nVertices = adjMat.length;
        this.adjMat = adjMat;

        boolean isImproved = true;

        this.pref = new double[nVertices + 1];
        this.suf = new double[nVertices + 1];

        while(isImproved) {
            for (int i = 1; i <= nVertices; ++i) {
                pref[i] = pref[i - 1] + adjMat[solution.get(i - 1)][solution.get(i)];
            }

            for (int i = nVertices - 1; i >= 0; --i) {
                suf[i] = suf[i + 1] + adjMat[solution.get(i + 1)][solution.get(i)];
            }

            switch (type) {
                case "2-opt" -> isImproved = twoOpt();
                case "2.5-opt" -> isImproved = twoHalfOpt();
                case "3-opt" -> isImproved = threeOpt();
            }
        }
    }
}
