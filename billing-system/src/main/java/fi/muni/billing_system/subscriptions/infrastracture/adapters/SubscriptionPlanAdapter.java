package fi.muni.billing_system.subscriptions.infrastracture.adapters;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import fi.muni.billing_system.subscriptions.usecase.cancelplan.CancelCustomerPlanPort;
import fi.muni.billing_system.subscriptions.usecase.renewsubscription.RenewSubscriptionPort;
import fi.muni.billing_system.subscriptions.usecase.subscribetoplan.SubscribeToPlanPort;
import fi.muni.billing_system.subscriptions.usecase.upgradeplan.UpgradePlanPort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionPlanAdapter
    implements CancelCustomerPlanPort, RenewSubscriptionPort, SubscribeToPlanPort, UpgradePlanPort {

  private final SubscriptionPlanRepository repository;

  @Override
  public Optional<SubscriptionPlan> getSubscription(UUID id) {
    return repository.findById(id).map(SubscriptionPlanEntity::toDomain);
  }

  @Override
  public SubscriptionPlan save(SubscriptionPlan subscription) {
    SubscriptionPlanEntity entity = SubscriptionPlanEntity.fromDomain(subscription);
    SubscriptionPlanEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public boolean isCustomerAlreadySubscribedToPlan(UUID customerId, Plan plan) {
    return repository.existsByCustomerIdAndPlanAndCancelledAtIsNull(customerId, plan);
  }
}
