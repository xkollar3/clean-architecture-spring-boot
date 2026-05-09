package fi.muni.billing_system.subscriptions.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import fi.muni.billing_system.subscriptions.usecase.upgradeplan.UpgradePlanPort;
import fi.muni.billing_system.subscriptions.usecase.upgradeplan.UpgradePlanUseCase;

@ExtendWith(MockitoExtension.class)
class UpgradePlanUseCaseTest {

  @Mock
  private UpgradePlanPort port;

  @InjectMocks
  private UpgradePlanUseCase useCase;

  private static final UUID PLAN_ID = UUID.randomUUID();

  @Test
  void execute_upgradesAndSavesSubscription() {
    var subscription = new SubscriptionPlan(UUID.randomUUID(), Plan.PLUS_TIER);
    when(port.getSubscription(PLAN_ID)).thenReturn(Optional.of(subscription));

    useCase.execute(PLAN_ID, Plan.MAX_TIER);

    assertThat(subscription.getPlan()).isEqualTo(Plan.MAX_TIER);
    verify(port).save(subscription);
  }

  @Test
  void execute_subscriptionNotFound_throws() {
    when(port.getSubscription(PLAN_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(PLAN_ID, Plan.MAX_TIER))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot upgrade");

    verify(port, never()).save(any());
  }
}
