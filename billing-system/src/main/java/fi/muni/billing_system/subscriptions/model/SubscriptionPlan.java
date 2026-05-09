package fi.muni.billing_system.subscriptions.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Getter;

@Getter
public class SubscriptionPlan {

  private final Long periodDays = 30l;

  private final UUID id;

  private final UUID customerId;
  private Plan plan;

  private final Instant subscribedAt;
  private Instant nextBillingDate;
  private Instant currentPeriodEnd;

  private Instant cancelledAt;

  public SubscriptionPlan(UUID customerId, Plan plan) {
    this.id = UUID.randomUUID();
    this.customerId = customerId;
    this.plan = plan;
    this.subscribedAt = Instant.now();
    this.nextBillingDate = subscribedAt;
    this.currentPeriodEnd = this.subscribedAt.plus(periodDays, ChronoUnit.DAYS);
  }

  public void cancelPlan() {
    if (this.cancelledAt != null) {
      throw new IllegalStateException("Plan with id: " + id + " is already cancelled.");
    }

    this.cancelledAt = Instant.now();
    this.nextBillingDate = null;
  }

  public void upgradePlan(Plan plan) {
    if (this.getCancelledAt() != null) {
      throw new IllegalStateException("Plan with id: " + id + " is cancelled, create a new plan instead of upgrading.");
    }

    if (this.getPlan().equals(plan)) {
      throw new IllegalStateException("Subscription is already on plan: " + plan);
    }

    this.plan = plan;
    this.nextBillingDate = Instant.now();
    this.currentPeriodEnd = this.subscribedAt.plus(periodDays, ChronoUnit.DAYS);
  }
}
