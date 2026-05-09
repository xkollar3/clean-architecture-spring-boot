package fi.muni.billing_system.invoices.usecase.issueinvoice;

import java.time.Instant;
import java.util.UUID;

import fi.muni.billing_system.invoices.model.Invoice;

public interface IssueInvoicePort {

  public boolean isAlreadyIssued(UUID subscriptionPlanId, UUID customerId, Instant billingDate);

  public Invoice save(Invoice invoice);
}
