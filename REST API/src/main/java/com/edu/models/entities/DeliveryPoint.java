package com.edu.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "DeliveryPoint")
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeliveryPointID", nullable = false)
    private int id;

    @Column(name = "[Name]", length = 100, nullable = false)
    private String name;

    @Column(name = "[Address]", length = 100, nullable = false)
    private String address;

    @Column(name = "PhoneNumber", length = 20, nullable = false)
    private String phoneNumber ;

    @Column(name = "Latitude", nullable = false)
    private double latitude;

    @Column(name = "Longitude", nullable = false)
    private double longitude;

    @OneToMany(mappedBy = "deliveryPoint")
    @JsonIgnore
    private List<RouteParts> routeParts;
}
