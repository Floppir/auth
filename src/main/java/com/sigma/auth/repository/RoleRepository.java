package com.sigma.auth.repository;

import java.util.Optional;

import com.sigma.auth.models.ERole;
import com.sigma.auth.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
