package fi.muni.billing_system.subscriptions.usecase.subscribetoplan;

import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscribeToPlanUseCase {

  private final SubscribeToPlanPort customerSubscriptionsPort;

  public UUID execute(UUID customerId, Plan plan) {
    if (customerSubscriptionsPort.isCustomerAlreadySubscribedToPlan(customerId, plan)) {
      throw new IllegalStateException("Customer: " + customerId + " is already subscribed to: " + plan);
    }

    var subscription = new SubscriptionPlan(customerId, plan);

    SubscriptionPlan saved = customerSubscriptionsPort.save(subscription);

    return saved.getId();
  }
}
