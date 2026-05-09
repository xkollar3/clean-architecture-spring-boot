package fi.muni.billing_system.invoices.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Invoice {

  private final UUID id;

  private final UUID customerId;
  private final String stripeCustomerId;
  private final UUID subscriptionId;

  private final BigDecimal amount;

  private final Instant issuedAt;
  private final LocalDate billingDate;

  private String paymentId;

  public Invoice(UUID customerId, String stripeCustomerId, UUID subscriptionId, BigDecimal amount, LocalDate billingDate) {
    this.id = UUID.randomUUID();
    this.customerId = customerId;
    this.stripeCustomerId = stripeCustomerId;
    this.subscriptionId = subscriptionId;
    this.issuedAt = Instant.now();
    this.amount = amount;
    this.billingDate = billingDate;
  }

  public void payInvoice(String paymentId) {
    if (this.paymentId != null) {
      throw new IllegalStateException("Invoice has already been paid");
    }

    this.paymentId = paymentId;
  }
}
