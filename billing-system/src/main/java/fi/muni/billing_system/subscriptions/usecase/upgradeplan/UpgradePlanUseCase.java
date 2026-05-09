package fi.muni.billing_system.subscriptions.usecase.upgradeplan;

import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.muni.billing_system.subscriptions.model.Plan;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UpgradePlanUseCase {

  private final UpgradePlanPort upgradePlanPort;

  public void execute(UUID planId, Plan plan) {
    var subscription = upgradePlanPort.getSubscription(planId);

    if (subscription.isEmpty()) {
      throw new IllegalStateException("Cannot upgrade subscription: " + planId);
    }

    var sub = subscription.get();
    sub.upgradePlan(plan);

    upgradePlanPort.save(sub);
  }
}
