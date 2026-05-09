package fi.muni.billing_system.subscriptions.usecase.subscribetoplan;

import java.util.UUID;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;

public interface SubscribeToPlanPort {

  public boolean isCustomerAlreadySubscribedToPlan(UUID customerId, Plan plan);

  public SubscriptionPlan save(SubscriptionPlan subscription);

}
