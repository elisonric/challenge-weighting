package br.com.challenge.weighing_management_service.repository;

import br.com.challenge.weighing_management_service.entity.GrainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrainTypeRepository extends JpaRepository<GrainType, Long> {
    Optional<GrainType> findByName(String name);
}