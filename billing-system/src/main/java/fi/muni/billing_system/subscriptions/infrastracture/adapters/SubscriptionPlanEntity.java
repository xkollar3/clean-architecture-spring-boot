package fi.muni.billing_system.subscriptions.infrastracture.adapters;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanEntity {

  @Id
  private UUID id;

  @Column(nullable = false)
  private UUID customerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Plan plan;

  @Column(nullable = false)
  private Instant subscribedAt;

  private LocalDate nextBillingDate;

  private LocalDate currentPeriodEnd;

  private Instant cancelledAt;

  public static SubscriptionPlanEntity fromDomain(SubscriptionPlan subscription) {
    SubscriptionPlanEntity entity = new SubscriptionPlanEntity();
    entity.setId(subscription.getId());
    entity.setCustomerId(subscription.getCustomerId());
    entity.setPlan(subscription.getPlan());
    entity.setSubscribedAt(subscription.getSubscribedAt());
    entity.setNextBillingDate(subscription.getNextBillingDate());
    entity.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
    entity.setCancelledAt(subscription.getCancelledAt());
    return entity;
  }

  public SubscriptionPlan toDomain() {
    return new SubscriptionPlan(
        this.id,
        this.customerId,
        this.plan,
        this.subscribedAt,
        this.nextBillingDate,
        this.currentPeriodEnd,
        this.cancelledAt);
  }
}
