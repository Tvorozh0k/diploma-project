package com.edu.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "[RouteParts]")
@NoArgsConstructor
@AllArgsConstructor
public class RouteParts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoutePartsID", nullable = false)
    private int id;

    @Column(name = "Position", nullable = false)
    private int position;

    @ManyToOne
    @JoinColumn(name = "DeliveryPointID", nullable = false)
    private DeliveryPoint deliveryPoint;

    @ManyToOne
    @JoinColumn(name = "UserRouteID", nullable = false)
    private UserRoute userRoute;
}
