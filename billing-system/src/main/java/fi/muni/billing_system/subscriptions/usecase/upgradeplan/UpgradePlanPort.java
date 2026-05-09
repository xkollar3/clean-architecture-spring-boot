package fi.muni.billing_system.subscriptions.usecase.upgradeplan;

import java.util.Optional;
import java.util.UUID;

import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;

public interface UpgradePlanPort {

  public Optional<SubscriptionPlan> getSubscription(UUID id);

  public SubscriptionPlan save(SubscriptionPlan subscriptionPlan);
}
