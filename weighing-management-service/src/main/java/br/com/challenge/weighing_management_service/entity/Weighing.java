package br.com.challenge.weighing_management_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weighings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weighing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransportTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scale_id", nullable = false)
    private Scale scale;

    @Column(name = "gross_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossWeight;

    @Column(name = "tare_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal tareWeight;

    @Column(name = "net_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal netWeight;

    @Column(name = "load_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal loadCost;

    @Column(name = "weighing_time", nullable = false)
    private LocalDateTime weighingTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (weighingTime == null) {
            weighingTime = LocalDateTime.now();
        }
        if (netWeight == null && grossWeight != null && tareWeight != null) {
            netWeight = grossWeight.subtract(tareWeight);
        }
    }
}