package br.com.challenge.weighing_management_service.repository;

import br.com.challenge.weighing_management_service.entity.Scale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScaleRepository extends JpaRepository<Scale, Long> {
    Optional<Scale> findByCode(String code);
    List<Scale> findByBranchId(Long branchId);
    List<Scale> findByActive(Boolean active);
}