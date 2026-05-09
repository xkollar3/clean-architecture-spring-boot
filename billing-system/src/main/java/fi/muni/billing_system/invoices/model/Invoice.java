package fi.muni.billing_system.invoices.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Invoice {

  private final UUID id;

  private final UUID customerId;
  private final UUID subscriptionId;

  private final BigDecimal amount;

  private final Instant issuedAt;
  private final Instant billingDate;

  private String paymentId;

  public Invoice(UUID customerId, UUID subscriptionId, BigDecimal amount, Instant billingDate) {
    this.id = UUID.randomUUID();
    this.customerId = customerId;
    this.subscriptionId = subscriptionId;
    this.issuedAt = Instant.now();
    this.amount = amount;
    this.billingDate = billingDate;
  }

  public void payInvoice(String paymentId) {
    if (paymentId != null) {
      throw new IllegalStateException("Invoice has already been paid");
    }

    this.paymentId = paymentId;
  }
}
