package com.edu.tsp;

import lombok.Getter;

import java.util.ArrayList;

public abstract class TSPSolver {
    @Getter
    protected ArrayList<Integer> solution;

    @Getter
    protected double len;

    protected int nVertices;

    public abstract void solve(double[][] adjMat);
}
