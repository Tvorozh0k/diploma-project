package com.edu.tsp.aco;

import com.edu.tsp.TSPSolver;
import com.edu.tsp.opt.KOptSolver;

import java.util.*;

/**
 * Класс ACOSolver для решения задачи коммивояжера
 * муравьиным алгоритмом
 */
public class ACOSolver extends TSPSolver {
    /**
     * Тип используемого алгоритма. Возможные значения:
     * <p>
     * - AS (Ant System - муравьиная система)
     * <p>
     * - EAS (Elitist Ant System - элитарная муравьиная система)
     * <p>
     * - ASRank (Rank-based Ant System - ранговая муравьиная система)
     * <p>
     * - MMAS (MAX-MIN Ant System - максиминная муравьиная система)
     */
    private final String type;

    /**
     * Степень значимости числа феромонов при подсчете
     * вероятности муравья попасть из вершины i в вершину j
     */
    private final double alpha;

    /**
     * Степень значимости веса ребра графа при подсчете
     * вероятности муравья попасть из вершины i в вершину j
     */
    private final double beta;

    /**
     * Коэффициент испарения феромонов (0 < &rho < 1)
     */
    private final double rho;

    /**
     * Изначальное число феромонов на всех ребрах графа
     */
    private final double tau0;

    /**
     * Число муравьев
     */
    private final int nAnts;

    /**
     * Число итераций алгоритма
     */
    private final int maxIter;

    /**
     * В случае EAS, w - вес, оставляемый лучшим туром,
     * найденным муравьями
     * <p>
     * В случае ASRank, w - число лучших туров, учитываемых
     * при переподсчете феромонной матрицы
     */
    private int w = 0;

    /**
     * В случае MMAS, a - отношение tauMin к tauMax:
     * <p>
     * {@code tauMin = a * tauMax}
     */
    private double a = 0;

    /**
     * В случае MMAS, localSearchType - тип применяемого алгоритма локального поиска
     * <p>
     * Возможные значения:
     * <p>
     * - None (Нет алгоритма локального поиска)
     * <p>
     * - 2-opt (Алгоритм 2-оптимизации)
     * <p>
     * - 2.5-opt (Алгоритм 2.5-оптимизации)
     * <p>
     * - 3-opt (Алгоритм 3-оптимизации)
     */
    private String localSearchType = "None";

    /**
     * Матрица смежности
     */
    private double[][] adjMat;

    /**
     * Феромонная матрица
     */
    private double[][] tau;

    /**
     * Матрица привлекательности ребер графа
     * <p>
     * {@code etaBeta[i][j] = Math.pow(adjMat[i][j], -beta)}
     */
    private double[][] etaBeta;

    /**
     * Матрица значимости ребер графа
     * <p>
     * {@code weights[i][j] = Math.pow(tau[i][j], alpha) * etaBeta[i][j]}
     */
    private double[][] weights;

    /**
     * Массив муравьев
     */
    private AntSolver[] ants;

    /**
     * Массив посещенных муравьями вершин
     */
    private boolean[] visited;

    /**
     * Массив не посещенных муравьями вершин
     */
    private int[] choices;

    /**
     * Массив вершин
     * <p>
     * {@code vertices = {0, 1, 2, ..., nVertices - 1}}
     */
    private List<Integer> vertices;

    /**
     * Алгоритм элитарной муравьиной системы
     */
    private void elitistAntSystem() {
        for (int it = 0; it < maxIter; ++it) {
            // Обновляем матрицу значимостей ребер графа
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    weights[i][j] = Math.pow(tau[i][j], alpha) * etaBeta[i][j];
                }
            }

            Collections.shuffle(vertices);

            // Каждый муравей находит свое решение
            for (int i = 0; i < nAnts; ++i) {
                ants[i].setStart(vertices.get(i));

                // i-ый муравей решает задачу коммивояжера
                ants[i].solve(adjMat);
                Arrays.fill(visited, false);

                // Обновляем глобальное решение, если нашлось решение получше
                if (ants[i].getLen() < len) {
                    solution = ants[i].getSolution();
                    len = ants[i].getLen();
                }
            }

            // Феромоны испаряются
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    tau[i][j] *= (1 - rho);
                }
            }

            // Число феромонов увеличилось на пути муравьев
            for (int i = 0; i < nAnts; ++i) {
                ArrayList<Integer> solution = ants[i].getSolution();
                double weight = 1.0 / ants[i].getLen();

                for (int j = 0; j < nVertices; ++j) {
                    tau[solution.get(j)][solution.get(j + 1)] += weight;
                }
            }

            // В случае элитарной муравьиной системы
            if (type.equals("EAS")) {
                double weight = w / len;

                for (int j = 0; j < nVertices; ++j) {
                    tau[solution.get(j)][solution.get(j + 1)] += weight;
                }
            }
        }
    }

    /**
     * Алгоритм ранговой муравьиной системы
     */
    private void rankBasedAntSystem() {
        for (int it = 0; it < maxIter; ++it) {
            // Обновляем матрицу значимостей ребер графа
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    weights[i][j] = Math.pow(tau[i][j], alpha) * etaBeta[i][j];
                }
            }

            Collections.shuffle(vertices);

            // Каждый муравей находит свое решение
            for (int i = 0; i < nAnts; ++i) {
                ants[i].setStart(vertices.get(i));

                // i-ый муравей решает задачу коммивояжера
                ants[i].solve(adjMat);
                Arrays.fill(visited, false);

                // Обновляем глобальное решение, если нашлось решение получше
                if (ants[i].getLen() < len) {
                    solution = ants[i].getSolution();
                    len = ants[i].getLen();
                }
            }

            // Феромоны испаряются
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    tau[i][j] *= (1 - rho);
                }
            }

            // Сортируем муравьев по длинам найденных ими решений
            Arrays.sort(ants, Comparator.comparingDouble(TSPSolver::getLen));

            // Только (w - 1) лучшее решение влияют на матрицу феромонов
            for (int i = 0; i < w - 1; ++i) {
                ArrayList<Integer> solution = ants[i].getSolution();
                double weight = (w - i - 1.0) / ants[i].getLen();

                for (int j = 0; j < nVertices; ++j) {
                    tau[solution.get(j)][solution.get(j + 1)] += weight;
                }
            }

            // Лучшее решение также влияет на матрицу феромонов
            double weight = (double) w / len;

            for (int j = 0; j < nVertices; ++j) {
                tau[solution.get(j)][solution.get(j + 1)] += weight;
            }
        }
    }

    /**
     * Алгоритм максиминной муравьиной системы
     */
    private void maxMinAntSystem() {
        // tauMax - максимально возможное число феромонов на ребре графа
        double tauMax;

        // tauMin - минимально возможное число феромонов на ребре графа
        double tauMin;

        for (int it = 0; it < maxIter; ++it) {
            // Обновляем матрицу значимостей ребер графа
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    weights[i][j] = Math.pow(tau[i][j], alpha) * etaBeta[i][j];
                }
            }

            Collections.shuffle(vertices);

            // Каждый муравей находит свое решение
            for (int i = 0; i < nAnts; ++i) {
                ants[i].setStart(vertices.get(i));

                // i-ый муравей решает задачу коммивояжера
                ants[i].solve(adjMat);
                Arrays.fill(visited, false);

                // Обновляем глобальное решение, если нашлось решение получше
                if (ants[i].getLen() < len) {
                    solution = ants[i].getSolution();
                    len = ants[i].getLen();
                }
            }

            // Если имеется алгоритм локальной поиска
            if (!localSearchType.equals("None")) {
                for (int i = 0; i < nAnts; ++i) {
                    KOptSolver alg = new KOptSolver(localSearchType, ants[i].getSolution(), ants[i].getLen());

                    // Применяем алгоритм локального поиска к решению муравья
                    alg.solve(adjMat);

                    // Обновляем глобальное решение, если нашлось решение получше
                    if (alg.getLen() < len) {
                        solution = alg.getSolution();
                        len = alg.getLen();
                    }
                }
            }

            // Обновляем границы значений феромонов [tauMin, tauMax]
            tauMax = 1.0 / rho / len;
            tauMin = a * tauMax;

            // Феромоны испаряются
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < nVertices; ++j) {
                    tau[i][j] = Math.max(tauMin, tau[i][j] * (1 - rho));
                }
            }

            // Обновляем феромоны только на лучшем найденном туре
            double weight = 1.0 / len;

            for (int j = 0; j < nVertices; ++j) {
                int u = solution.get(j);
                int v = solution.get(j + 1);
                tau[u][v] = Math.min(tauMax, tau[u][v] + weight);
            }
        }
    }

    /**
     * Конструктор
     * @param params - параметры алгоритма в виде словаря
     */
    public ACOSolver(Map<String, Object> params) {
        // Тип алгоритма
        type = (String) params.get("type");

        // Базовые вещественные параметры алгоритма
        alpha = (double) params.get("alpha");
        beta = (double) params.get("beta");
        rho = (double) params.get("rho");
        tau0 = (double) params.get("tau0");

        // Базовые целочисленные параметры алгоритма
        nAnts = (int) params.get("nAnts");
        maxIter = (int) params.get("maxIter");

        // Параметры в зависимости от типа алгоритма
        switch (type) {
            case "EAS", "ASRank" -> w = (int) params.get("w");
            case "MMAS" -> {
                a = (double) params.get("a");
                localSearchType = (String) params.get("localSearchType");
            }
        }
    }

    /**
     * Метод, решающий задачу коммивояжера
     * муравьиным алгоритмом для заданного графа
     * @param adjMat - матрица смежности графа
     */
    @Override
    public void solve(double[][] adjMat) {
        nVertices = adjMat.length;

        // Изначально, решения нет, длина - бесконечность
        len = Double.POSITIVE_INFINITY;

        this.adjMat = adjMat;

        tau = new double[nVertices][nVertices];
        etaBeta = new double[nVertices][nVertices];
        weights = new double[nVertices][nVertices];

        // Инициализация матрицы феромонов
        for (int i = 0; i < nVertices; ++i) {
            Arrays.fill(tau[i], tau0);
        }

        // Инициализация матрицы значимости ребер графа
        for (int i = 0; i < nVertices; ++i) {
            for (int j = 0; j < nVertices; ++j) {
                etaBeta[i][j] = Math.pow(adjMat[i][j], -beta);
            }
        }

        ants = new AntSolver[nVertices];

        visited = new boolean[nVertices];
        choices = new int[nVertices];

        // Инициализация массива муравьев
        for (int i = 0; i < nVertices; ++i) {
            ants[i] = new AntSolver(weights, visited, choices);
        }

        vertices = new ArrayList<>();

        for (int i = 0; i < nVertices; ++i) {
            vertices.add(i);
        }

        // Вызов муравьиного алгоритма в зависимости от типа
        switch (type) {
            case "AS", "EAS" -> elitistAntSystem();
            case "ASRank" -> rankBasedAntSystem();
            case "MMAS" -> maxMinAntSystem();
        }
    }
}
