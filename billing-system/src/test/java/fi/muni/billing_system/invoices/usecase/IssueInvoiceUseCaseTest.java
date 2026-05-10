package fi.muni.billing_system.invoices.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.invoices.model.Invoice;
import fi.muni.billing_system.invoices.usecase.issueinvoice.IssueInvoicePort;
import fi.muni.billing_system.invoices.usecase.issueinvoice.IssueInvoiceUseCase;
import fi.muni.billing_system.invoices.usecase.issueinvoice.GetCustomerPort;

@ExtendWith(MockitoExtension.class)
class IssueInvoiceUseCaseTest {

  @Mock
  private IssueInvoicePort port;

  @Mock
  private GetCustomerPort stripeCustomerPort;

  @InjectMocks
  private IssueInvoiceUseCase useCase;

  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final String STRIPE_CUSTOMER_ID = "cus_mock_123";
  private static final BigDecimal AMOUNT = new BigDecimal("29.99");
  private static final LocalDate BILLING_DATE = LocalDate.of(2026, 5, 1);

  @Test
  void execute_createsInvoiceAndReturnsId() {
    when(port.isAlreadyIssued(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE)).thenReturn(false);
    when(stripeCustomerPort.getStripeCustomerIdPort(CUSTOMER_ID)).thenReturn(STRIPE_CUSTOMER_ID);
    when(port.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

    UUID result = useCase.execute(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);

    assertThat(result).isNotNull();

    var captor = ArgumentCaptor.forClass(Invoice.class);
    verify(port).save(captor.capture());
    assertThat(captor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(captor.getValue().getSubscriptionId()).isEqualTo(SUBSCRIPTION_ID);
    assertThat(captor.getValue().getAmount()).isEqualTo(AMOUNT);
    assertThat(captor.getValue().getBillingDate()).isEqualTo(BILLING_DATE);
  }

  @Test
  void execute_alreadyIssued_throws() {
    when(port.isAlreadyIssued(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE)).thenReturn(true);

    assertThatThrownBy(() -> useCase.execute(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already been issued");

    verify(port, never()).save(any());
  }
}
