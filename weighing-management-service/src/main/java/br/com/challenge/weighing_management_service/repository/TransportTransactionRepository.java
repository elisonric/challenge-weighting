package br.com.challenge.weighing_management_service.repository;

import br.com.challenge.weighing_management_service.entity.TransportTransaction;
import br.com.challenge.weighing_management_service.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransportTransactionRepository extends JpaRepository<TransportTransaction, Long> {
    List<TransportTransaction> findByTruckId(Long truckId);
    List<TransportTransaction> findByBranchId(Long branchId);
    List<TransportTransaction> findByGrainTypeId(Long grainTypeId);
    List<TransportTransaction> findByStatus(TransactionStatus status);

    @Query("SELECT t FROM TransportTransaction t WHERE t.startTime BETWEEN :start AND :end")
    List<TransportTransaction> findByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM TransportTransaction t WHERE t.branch.id = :branchId AND t.startTime BETWEEN :start AND :end")
    List<TransportTransaction> findByBranchAndPeriod(@Param("branchId") Long branchId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}