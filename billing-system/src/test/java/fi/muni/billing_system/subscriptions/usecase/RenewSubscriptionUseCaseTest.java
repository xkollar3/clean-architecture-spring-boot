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
import fi.muni.billing_system.subscriptions.usecase.renewsubscription.RenewSubscriptionPort;
import fi.muni.billing_system.subscriptions.usecase.renewsubscription.RenewSubscriptionUseCase;

@ExtendWith(MockitoExtension.class)
class RenewSubscriptionUseCaseTest {

  @Mock
  private RenewSubscriptionPort port;

  @InjectMocks
  private RenewSubscriptionUseCase useCase;

  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();

  @Test
  void execute_renewsAndSavesSubscription() {
    var subscription = new SubscriptionPlan(UUID.randomUUID(), Plan.HARDCORE_MAX_TIER);
    when(port.getSubscription(SUBSCRIPTION_ID)).thenReturn(Optional.of(subscription));

    useCase.execute(SUBSCRIPTION_ID);

    verify(port).save(subscription);
  }

  @Test
  void execute_subscriptionNotFound_throws() {
    when(port.getSubscription(SUBSCRIPTION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(SUBSCRIPTION_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("does not exist");

    verify(port, never()).save(any());
  }
}
