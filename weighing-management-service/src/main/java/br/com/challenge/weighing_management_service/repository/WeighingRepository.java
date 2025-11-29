package br.com.challenge.weighing_management_service.repository;

import br.com.challenge.weighing_management_service.entity.Weighing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeighingRepository extends JpaRepository<Weighing, Long> {
    List<Weighing> findByTransactionId(Long transactionId);
    List<Weighing> findByScaleId(Long scaleId);

    @Query("SELECT w FROM Weighing w WHERE w.weighingTime BETWEEN :start AND :end")
    List<Weighing> findByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.branch.id = :branchId")
    List<Weighing> findByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.truck.id = :truckId")
    List<Weighing> findByTruckId(@Param("truckId") Long truckId);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.grainType.id = :grainTypeId")
    List<Weighing> findByGrainTypeId(@Param("grainTypeId") Long grainTypeId);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.branch.id = :branchId AND w.weighingTime BETWEEN :start AND :end")
    List<Weighing> findByBranchAndPeriod(@Param("branchId") Long branchId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.truck.id = :truckId AND w.weighingTime BETWEEN :start AND :end")
    List<Weighing> findByTruckAndPeriod(@Param("truckId") Long truckId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT w FROM Weighing w JOIN w.transaction t WHERE t.grainType.id = :grainTypeId AND w.weighingTime BETWEEN :start AND :end")
    List<Weighing> findByGrainTypeAndPeriod(@Param("grainTypeId") Long grainTypeId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}