package com.foodhub.foodhub.Repository;

import com.foodhub.foodhub.Entity.DeliveryBoyApplication;
import com.foodhub.foodhub.Enum.DeliveryBoyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryBoyApplicationRepository extends JpaRepository<DeliveryBoyApplication, Long> {

    List<DeliveryBoyApplication> findByStatus(DeliveryBoyStatus status);
    @Query("""
        SELECT d
        FROM DeliveryBoyApplication d
        WHERE d.status = :status
        AND d.available = true""")
    List<DeliveryBoyApplication> findAvailableDeliveryBoys(@Param("status") DeliveryBoyStatus status);
}