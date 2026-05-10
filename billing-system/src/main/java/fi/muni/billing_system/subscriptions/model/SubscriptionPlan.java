package fi.muni.billing_system.subscriptions.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.data.domain.AbstractAggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscriptionPlan extends AbstractAggregateRoot<SubscriptionPlan> {

  private final Long periodDays = 30l;

  private final UUID id;

  private final UUID customerId;
  private Plan plan;

  private final Instant subscribedAt;
  private LocalDate nextBillingDate;
  private LocalDate currentPeriodEnd;

  private Instant cancelledAt;

  public SubscriptionPlan(UUID customerId, Plan plan) {
    this.id = UUID.randomUUID();
    this.customerId = customerId;
    this.plan = plan;
    this.subscribedAt = Instant.now();
    this.nextBillingDate = LocalDate.now();
    this.currentPeriodEnd = LocalDate.now().plus(periodDays, ChronoUnit.DAYS);

    registerEvent(new SubscribedToPlan(id, customerId, this.nextBillingDate, plan.getPlanPriceEur()));
  }

  public void cancelPlan() {
    if (this.cancelledAt != null) {
      throw new IllegalStateException("Plan with id: " + id + " is already cancelled.");
    }

    this.cancelledAt = Instant.now();

    registerEvent(new PlanCancelled(id));
  }

  public void upgradePlan(Plan plan) {
    if (this.getCancelledAt() != null) {
      throw new IllegalStateException("Plan with id: " + id + " is cancelled, create a new plan instead of upgrading.");
    }

    if (this.getPlan().equals(plan)) {
      throw new IllegalStateException("Subscription is already on plan: " + plan);
    }

    this.plan = plan;
    this.nextBillingDate = LocalDate.now();
    this.currentPeriodEnd = LocalDate.now().plus(periodDays, ChronoUnit.DAYS);

    registerEvent(new PlanUpgraded(id, customerId, this.nextBillingDate, plan.getPlanPriceEur()));
  }

  public void renewSubscription() {
    if (this.getCancelledAt() != null) {
      throw new IllegalStateException("Plan with id: " + id + " is already cancelled.");
    }

    this.nextBillingDate = LocalDate.now();
    this.currentPeriodEnd = LocalDate.now().plus(periodDays, ChronoUnit.DAYS);

    registerEvent(new PlanRenewed(id, customerId, this.nextBillingDate, plan.getPlanPriceEur()));
  }
}
