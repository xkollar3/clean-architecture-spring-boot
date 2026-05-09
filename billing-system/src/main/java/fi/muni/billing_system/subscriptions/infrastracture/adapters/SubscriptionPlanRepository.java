package fi.muni.billing_system.subscriptions.infrastracture.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.muni.billing_system.subscriptions.model.Plan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity, UUID> {

  boolean existsByCustomerIdAndPlanAndCancelledAtIsNull(UUID customerId, Plan plan);

  List<SubscriptionPlanEntity> findAllByCurrentPeriodEndLessThanEqualAndCancelledAtIsNull(LocalDate date);
}
