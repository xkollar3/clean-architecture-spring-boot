package fi.muni.billing_system.invoices.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class InvoiceTest {

  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final String STRIPE_CUSTOMER_ID = "cus_mock_123";
  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final BigDecimal AMOUNT = new BigDecimal("29.99");
  private static final LocalDate BILLING_DATE = LocalDate.of(2026, 5, 1);

  @Test
  void createInvoice_setsFieldsCorrectly() {
    var invoice = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, SUBSCRIPTION_ID, AMOUNT, BILLING_DATE);

    assertThat(invoice.getId()).isNotNull();
    assertThat(invoice.getCustomerId()).isEqualTo(CUSTOMER_ID);
    assertThat(invoice.getSubscriptionId()).isEqualTo(SUBSCRIPTION_ID);
    assertThat(invoice.getAmount()).isEqualTo(AMOUNT);
    assertThat(invoice.getBillingDate()).isEqualTo(BILLING_DATE);
    assertThat(invoice.getIssuedAt()).isNotNull();
    assertThat(invoice.getIssuedAt()).isBetween(Instant.now().minusSeconds(5), Instant.now());
    assertThat(invoice.getPaymentId()).isNull();
  }

  @Test
  void payInvoice_setsPaymentId() {
    var invoice = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, SUBSCRIPTION_ID, AMOUNT, BILLING_DATE);
    String paymentId = "pay_abc123";

    invoice.payInvoice(paymentId);

    assertThat(invoice.getPaymentId()).isEqualTo(paymentId);
  }

  @Test
  void payInvoice_alreadyPaid_throws() {
    var invoice = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, SUBSCRIPTION_ID, AMOUNT, BILLING_DATE);
    invoice.payInvoice("pay_abc123");

    assertThatThrownBy(() -> invoice.payInvoice("pay_xyz789"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already been paid");
  }
}
