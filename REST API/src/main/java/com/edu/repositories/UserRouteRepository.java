package com.edu.repositories;

import com.edu.models.entities.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Integer> {
}
