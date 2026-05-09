package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import fi.muni.billing_system.invoices.model.Invoice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

  @Id
  private UUID id;

  @Column(nullable = false)
  private UUID customerId;

  @Column(nullable = false)
  private String stripeCustomerId;

  @Column(nullable = false)
  private UUID subscriptionId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private Instant issuedAt;

  @Column(nullable = false)
  private LocalDate billingDate;

  private String paymentId;

  public static InvoiceEntity fromDomain(Invoice invoice) {
    InvoiceEntity entity = new InvoiceEntity();
    entity.setId(invoice.getId());
    entity.setCustomerId(invoice.getCustomerId());
    entity.setStripeCustomerId(invoice.getStripeCustomerId());
    entity.setSubscriptionId(invoice.getSubscriptionId());
    entity.setAmount(invoice.getAmount());
    entity.setIssuedAt(invoice.getIssuedAt());
    entity.setBillingDate(invoice.getBillingDate());
    entity.setPaymentId(invoice.getPaymentId());
    return entity;
  }

  public Invoice toDomain() {
    return new Invoice(
        this.id,
        this.customerId,
        this.stripeCustomerId,
        this.subscriptionId,
        this.amount,
        this.issuedAt,
        this.billingDate,
        this.paymentId);
  }
}
