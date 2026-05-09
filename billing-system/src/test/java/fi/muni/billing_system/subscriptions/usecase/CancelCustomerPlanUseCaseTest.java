package fi.muni.billing_system.subscriptions.usecase;

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
import fi.muni.billing_system.subscriptions.usecase.cancelplan.CancelCustomerPlanPort;
import fi.muni.billing_system.subscriptions.usecase.cancelplan.CancelCustomerPlanUseCase;

@ExtendWith(MockitoExtension.class)
class CancelCustomerPlanUseCaseTest {

  @Mock
  private CancelCustomerPlanPort port;

  @InjectMocks
  private CancelCustomerPlanUseCase useCase;

  private static final UUID PLAN_ID = UUID.randomUUID();

  @Test
  void execute_cancelsAndSavesSubscription() {
    var subscription = new SubscriptionPlan(UUID.randomUUID(), Plan.MAX_TIER);
    when(port.getSubscription(PLAN_ID)).thenReturn(Optional.of(subscription));

    useCase.execute(PLAN_ID);

    verify(port).save(subscription);
    assertThatThrownBy(subscription::cancelPlan)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already cancelled");
  }

  @Test
  void execute_subscriptionNotFound_throws() {
    when(port.getSubscription(PLAN_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(PLAN_ID))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("does not exist");

    verify(port, never()).save(any());
  }
}
