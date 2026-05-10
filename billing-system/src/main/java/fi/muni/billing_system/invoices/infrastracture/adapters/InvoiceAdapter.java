package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import fi.muni.billing_system.invoices.infrastracture.persistence.InvoiceEntity;
import fi.muni.billing_system.invoices.infrastracture.persistence.InvoiceRepository;
import fi.muni.billing_system.invoices.model.Invoice;
import fi.muni.billing_system.invoices.usecase.issueinvoice.IssueInvoicePort;
import fi.muni.billing_system.invoices.usecase.payinvoice.PayInvoicePort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InvoiceAdapter implements IssueInvoicePort, PayInvoicePort {

  private final InvoiceRepository repository;

  @Override
  public boolean isAlreadyIssued(UUID subscriptionPlanId, UUID customerId, LocalDate billingDate) {
    return repository.existsBySubscriptionIdAndCustomerIdAndBillingDate(subscriptionPlanId, customerId, billingDate);
  }

  @Override
  public Invoice save(Invoice invoice) {
    InvoiceEntity entity = InvoiceEntity.fromDomain(invoice);
    InvoiceEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<Invoice> getInvoice(UUID invoiceId) {
    return repository.findById(invoiceId).map(InvoiceEntity::toDomain);
  }
}
