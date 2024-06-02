package com.edu.repositories;

import com.edu.models.entities.RouteParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutePartsRepository extends JpaRepository<RouteParts, Integer> {
}
