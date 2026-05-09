package fi.muni.billing_system.invoices.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.invoices.model.Invoice;
import fi.muni.billing_system.invoices.usecase.payinvoice.PayInvoicePort;
import fi.muni.billing_system.invoices.usecase.payinvoice.PayInvoiceUseCase;
import fi.muni.billing_system.invoices.usecase.payinvoice.PaymentGatewayPort;

@ExtendWith(MockitoExtension.class)
class PayInvoiceUseCaseTest {

  @Mock
  private PayInvoicePort payInvoicePort;

  @Mock
  private PaymentGatewayPort paymentGatewayPort;

  @InjectMocks
  private PayInvoiceUseCase useCase;

  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final String STRIPE_CUSTOMER_ID = "cus_mock_123";
  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final BigDecimal AMOUNT = new BigDecimal("29.99");
  private static final LocalDate BILLING_DATE = LocalDate.of(2026, 5, 1);
  private static final String PAYMENT_INTENT_ID = "pi_abc123";

  @Test
  void execute_paysInvoiceAndSaves() {
    var invoice = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, SUBSCRIPTION_ID, AMOUNT, BILLING_DATE);
    UUID invoiceId = invoice.getId();

    when(payInvoicePort.getInvoice(invoiceId)).thenReturn(Optional.of(invoice));
    when(paymentGatewayPort.createPaymentIntent(STRIPE_CUSTOMER_ID, invoiceId, AMOUNT))
        .thenReturn(PAYMENT_INTENT_ID);

    useCase.execute(invoiceId);

    var captor = ArgumentCaptor.forClass(Invoice.class);
    verify(payInvoicePort).save(captor.capture());
    assertThat(captor.getValue().getPaymentId()).isEqualTo(PAYMENT_INTENT_ID);
  }

  @Test
  void execute_invoiceNotFound_throws() {
    UUID invoiceId = UUID.randomUUID();
    when(payInvoicePort.getInvoice(invoiceId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(invoiceId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("does not exist");

    verify(paymentGatewayPort, never()).createPaymentIntent(any(), any(), any());
    verify(payInvoicePort, never()).save(any());
  }

  @Test
  void execute_invoiceAlreadyPaid_throws() {
    var invoice = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, SUBSCRIPTION_ID, AMOUNT, BILLING_DATE);
    invoice.payInvoice("pi_existing");
    UUID invoiceId = invoice.getId();

    when(payInvoicePort.getInvoice(invoiceId)).thenReturn(Optional.of(invoice));
    when(paymentGatewayPort.createPaymentIntent(STRIPE_CUSTOMER_ID, invoiceId, AMOUNT))
        .thenReturn(PAYMENT_INTENT_ID);

    assertThatThrownBy(() -> useCase.execute(invoiceId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already been paid");
  }
}
