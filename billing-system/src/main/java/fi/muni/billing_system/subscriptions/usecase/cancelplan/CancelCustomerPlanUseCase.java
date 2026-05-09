package fi.muni.billing_system.subscriptions.usecase.cancelplan;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelCustomerPlanUseCase {

  private final CancelCustomerPlanPort cancelCustomerPlanPort;

  public void execute(UUID planId) {
    Optional<SubscriptionPlan> subscription = cancelCustomerPlanPort.getSubscription(planId);

    if (subscription.isEmpty()) {
      throw new IllegalStateException(
          "Cannot cancel susbcription plan with id: " + planId + " because it does not exist.");
    }

    subscription.get().cancelPlan();

    cancelCustomerPlanPort.save(subscription.get());
  }
}
