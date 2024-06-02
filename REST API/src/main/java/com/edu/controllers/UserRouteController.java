package com.edu.controllers;

import com.edu.models.AddRouteRequest;
import com.edu.models.entities.DeliveryPoint;
import com.edu.models.entities.RouteParts;
import com.edu.models.entities.User;
import com.edu.models.entities.UserRoute;
import com.edu.repositories.DeliveryPointRepository;
import com.edu.repositories.RoutePartsRepository;
import com.edu.repositories.UserRepository;
import com.edu.repositories.UserRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userRoute")
public class UserRouteController {
    private final UserRouteRepository userRouteRepository;
    private final RoutePartsRepository routePartsRepository;

    private final UserRepository userRepository;
    private final DeliveryPointRepository deliveryPointRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addRoute(@RequestBody AddRouteRequest request){
        User user = userRepository.findById(request.userId()).orElseThrow();

        Instant utcInstant = Instant.now().atZone(ZoneOffset.UTC).toInstant();
        Date createdAt = Date.from(utcInstant);

        UserRoute userRoute = UserRoute.builder()
                .user(user)
                .createdAt(createdAt)
                .routeLength(request.routeLength())
                .build();

        userRouteRepository.save(userRoute);

        for (int i = 0; i < request.routePoints().size(); ++i) {
            DeliveryPoint deliveryPoint = deliveryPointRepository.findById(request.routePoints().get(i)).orElseThrow();

            RouteParts routeParts = RouteParts.builder()
                    .userRoute(userRoute)
                    .deliveryPoint(deliveryPoint)
                    .position(i)
                    .build();

            routePartsRepository.save(routeParts);
        }

        Map<String, Object> result = Map.of("routeId", userRoute.getId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<List<Map<String, Object>>> getUserRoutes(@PathVariable(value = "id") int id) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<UserRoute> routes = userRepository.findById(id).orElseThrow().getUserRoutes();

        for (var route : routes) {
            List<RouteParts> parts = route.getRouteParts();

            List<Integer> routePoints = new ArrayList<>();

            for (var part : parts) {
                routePoints.add(part.getDeliveryPoint().getId());
            }

            result.add(Map.of(
                    "id", route.getId(),
                    "routePoints", routePoints,
                    "createdAt", route.getCreatedAt(),
                    "routeLength", route.getRouteLength())
            );
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> removeRoute(@PathVariable(value = "id") int id) {
        UserRoute route = userRouteRepository.findById(id).orElseThrow();
        List<RouteParts> routeParts = route.getRouteParts();

        for (var part : routeParts) {
            routePartsRepository.delete(part);
        }

        userRouteRepository.delete(route);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
