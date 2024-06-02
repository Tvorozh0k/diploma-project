package com.edu.controllers;

import com.edu.tsp.TSPSolver;
import com.edu.tsp.TspData;
import com.edu.tsp.aco.ACOSolver;
import com.edu.tsp.heldkarp.HeldKarpSolver;
import com.edu.tsp.nn.NNSolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tsp")
public class TspController {
    @PostMapping("/getSolution")
    public ResponseEntity<?> getSolution(@RequestBody TspData info) {
        double[][] graph = info.distMatrix();
        int startPoint = info.startPoint();
        String option = info.option();

        if (option.equals("auto")) {
            if (graph.length <= 15) {
                option = "held_karp";
            } else if (graph.length > 50) {
                option = "ant_algorithm";
            } else {
                option = "ant_opt_algorithm";
            }
        }

        NNSolver alg1 = new NNSolver();
        alg1.solve(graph);

        Map<String, Object> paramsAnt = Map.of(
                "type", "MMAS",
                "alpha", 1.0,
                "beta", 3.5,
                "rho", 0.02,
                "a", 0.5 / graph.length,
                "tau0", 5.0 / alg1.getLen(),
                "nAnts", graph.length,
                "maxIter", 750,
                "localSearchType", "None"
        );

        Map<String, Object> paramsAntOpt = Map.of(
                "type", "MMAS",
                "alpha", 1.0,
                "beta", 3.5,
                "rho", 0.2,
                "a", 0.5 / graph.length,
                "tau0", 5.0 / alg1.getLen(),
                "nAnts", Math.min(10, graph.length),
                "maxIter", 250,
                "localSearchType", "2.5-opt"
        );

        TSPSolver solver = switch (option) {
            case "held_karp" -> new HeldKarpSolver();
            case "aco_algorithm" -> new ACOSolver(paramsAnt);
            case "aco_opt_algorithm" -> new ACOSolver(paramsAntOpt);
            default -> null;
        };

        if (solver != null) {
            solver.solve(graph);
            List<Integer> solution = solver.getSolution();

            List<Integer> subSolution = new ArrayList<>(solution.subList(0, solution.size() - 1));

            Collections.rotate(subSolution, -subSolution.indexOf(startPoint));

            subSolution.add(startPoint);

            Map<String, Object> result = Map.of(
                    "solution", subSolution,
                    "len", solver.getLen()
            );

            return new ResponseEntity<>(result, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
