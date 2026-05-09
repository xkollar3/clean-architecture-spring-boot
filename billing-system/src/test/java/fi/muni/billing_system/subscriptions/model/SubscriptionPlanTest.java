package fi.muni.billing_system.subscriptions.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SubscriptionPlanTest {

  private static final UUID CUSTOMER_ID = UUID.randomUUID();

  @Test
  void createSubscription_setsFieldsCorrectly() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.PLUS_TIER);

    assertThat(plan.getId()).isNotNull();
    assertThat(plan.getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(plan.getPlan()).isEqualTo(Plan.PLUS_TIER);
    assertThat(plan.getSubscribedAt()).isNotNull();
    assertThat(plan.getNextBillingDate()).isEqualTo(LocalDate.now());
    assertThat(plan.getCurrentPeriodEnd()).isEqualTo(LocalDate.now().plus(30, ChronoUnit.DAYS));
    assertThat(plan.getCancelledAt()).isNull();
  }

  // --- cancelPlan ---

  @Test
  void cancelPlan_setsTimestamp() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.MAX_TIER);
    Instant before = Instant.now();

    plan.cancelPlan();

    assertThat(plan.getCancelledAt()).isNotNull();
    assertThat(plan.getCancelledAt()).isBetween(before, Instant.now());
  }

  @Test
  void cancelPlan_alreadyCancelled_throws() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.MAX_TIER);
    plan.cancelPlan();

    assertThatThrownBy(plan::cancelPlan)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already cancelled");
  }

  // --- upgradePlan ---

  @Test
  void upgradePlan_changesPlanAndResetsBillingPeriod() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.PLUS_TIER);

    plan.upgradePlan(Plan.MAX_TIER);

    assertThat(plan.getPlan()).isEqualTo(Plan.MAX_TIER);
    assertThat(plan.getNextBillingDate()).isEqualTo(LocalDate.now());
    assertThat(plan.getCurrentPeriodEnd()).isEqualTo(LocalDate.now().plus(30, ChronoUnit.DAYS));
  }

  @Test
  void upgradePlan_toSamePlan_throws() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.PLUS_TIER);

    assertThatThrownBy(() -> plan.upgradePlan(Plan.PLUS_TIER))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already on plan");
  }

  @Test
  void upgradePlan_cancelledSubscription_throws() {
    var plan = new SubscriptionPlan(CUSTOMER_ID, Plan.PLUS_TIER);
    plan.cancelPlan();

    assertThatThrownBy(() -> plan.upgradePlan(Plan.MAX_TIER))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cancelled");
  }

  // --- renewSubscription ---

  @Test
  void renewSubscription_resetsBillingPeriod() {
    var subscription = new SubscriptionPlan(CUSTOMER_ID, Plan.HARDCORE_MAX_TIER);

    subscription.renewSubscription();

    assertThat(subscription.getNextBillingDate()).isEqualTo(LocalDate.now());
    assertThat(subscription.getCurrentPeriodEnd()).isEqualTo(LocalDate.now().plus(30, ChronoUnit.DAYS));
  }

  @Test
  void renewSubscription_cancelledSubscription_throws() {
    var subscription = new SubscriptionPlan(CUSTOMER_ID, Plan.HARDCORE_MAX_TIER);
    subscription.cancelPlan();

    assertThatThrownBy(subscription::renewSubscription)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already cancelled");
  }
}
