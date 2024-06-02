package com.edu.models;

import java.util.List;

public record AddRouteRequest(int userId, List<Integer> routePoints, double routeLength) {
}
