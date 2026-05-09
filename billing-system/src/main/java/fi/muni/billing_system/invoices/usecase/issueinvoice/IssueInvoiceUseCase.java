package fi.muni.billing_system.invoices.usecase.issueinvoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.muni.billing_system.invoices.model.Invoice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IssueInvoiceUseCase {

  private final IssueInvoicePort issueInvoicePort;

  public UUID execute(UUID subscriptionPlanId, UUID customerId, Instant billingDate, BigDecimal amount) {
    if (issueInvoicePort.isAlreadyIssued(subscriptionPlanId, customerId, billingDate)) {
      throw new IllegalStateException(
          "Invoice for plan: " + subscriptionPlanId + " on date: " + billingDate + " has already been issued");
    }

    var invoice = new Invoice(subscriptionPlanId, customerId, amount, billingDate);

    var saved = issueInvoicePort.save(invoice);

    return saved.getId();
  }
}
