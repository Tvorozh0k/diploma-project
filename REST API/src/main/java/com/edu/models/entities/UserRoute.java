package com.edu.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "[UserRoute]")
@NoArgsConstructor
@AllArgsConstructor
public class UserRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserRouteID", nullable = false)
    private int id;

    @Column(name = "CreatedAt", columnDefinition="DATETIME", nullable = false)
    private Date createdAt;

    @Column(name = "RouteLength", nullable = false)
    private double routeLength;

    @OneToMany(mappedBy = "userRoute")
    @JsonIgnore
    private List<RouteParts> routeParts;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
}
