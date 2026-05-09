package fi.muni.billing_system.subscriptions.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.subscriptions.model.Plan;
import fi.muni.billing_system.subscriptions.model.SubscriptionPlan;
import fi.muni.billing_system.subscriptions.usecase.subscribetoplan.SubscribeToPlanPort;
import fi.muni.billing_system.subscriptions.usecase.subscribetoplan.SubscribeToPlanUseCase;

@ExtendWith(MockitoExtension.class)
class SubscribeToPlanUseCaseTest {

  @Mock
  private SubscribeToPlanPort port;

  @InjectMocks
  private SubscribeToPlanUseCase useCase;

  private static final UUID CUSTOMER_ID = UUID.randomUUID();

  @Test
  void execute_createsSubscriptionAndReturnsId() {
    when(port.isCustomerAlreadySubscribedToPlan(CUSTOMER_ID, Plan.PLUS_TIER)).thenReturn(false);
    when(port.save(any(SubscriptionPlan.class))).thenAnswer(inv -> inv.getArgument(0));

    UUID result = useCase.execute(CUSTOMER_ID, Plan.PLUS_TIER);

    assertThat(result).isNotNull();

    var captor = ArgumentCaptor.forClass(SubscriptionPlan.class);
    verify(port).save(captor.capture());
    assertThat(captor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(captor.getValue().getPlan()).isEqualTo(Plan.PLUS_TIER);
  }

  @Test
  void execute_customerAlreadySubscribed_throws() {
    when(port.isCustomerAlreadySubscribedToPlan(CUSTOMER_ID, Plan.PLUS_TIER)).thenReturn(true);

    assertThatThrownBy(() -> useCase.execute(CUSTOMER_ID, Plan.PLUS_TIER))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already subscribed");

    verify(port, never()).save(any());
  }
}
