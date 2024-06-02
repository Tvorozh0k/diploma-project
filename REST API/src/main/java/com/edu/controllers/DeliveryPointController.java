package com.edu.controllers;

import com.edu.models.entities.DeliveryPoint;
import com.edu.repositories.DeliveryPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class DeliveryPointController {
    private final DeliveryPointRepository deliveryPointRepository;

    @PostMapping("/add")
    public ResponseEntity<?> createDeliveryPoint(@RequestBody DeliveryPoint deliveryPoint) {
        try {
            deliveryPointRepository.save(deliveryPoint);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<DeliveryPoint>> getAll() {
        return new ResponseEntity<>(deliveryPointRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DeliveryPoint> getDeliveryPointById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(deliveryPointRepository.findById(id).orElse(new DeliveryPoint()), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDeliveryPoint(@RequestBody DeliveryPoint deliveryPointInfo) {
        DeliveryPoint deliveryPoint = deliveryPointRepository.findById(deliveryPointInfo.getId()).orElse(null);

        if (deliveryPoint == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        deliveryPoint.setName(deliveryPointInfo.getName());
        deliveryPoint.setAddress(deliveryPointInfo.getAddress());
        deliveryPoint.setPhoneNumber(deliveryPointInfo.getPhoneNumber());
        deliveryPoint.setLatitude(deliveryPointInfo.getLatitude());
        deliveryPoint.setLongitude(deliveryPointInfo.getLongitude());

        deliveryPointRepository.save(deliveryPoint);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}