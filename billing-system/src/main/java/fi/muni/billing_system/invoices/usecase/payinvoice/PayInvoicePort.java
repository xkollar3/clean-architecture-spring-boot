package fi.muni.billing_system.invoices.usecase.payinvoice;

import java.util.Optional;
import java.util.UUID;

import fi.muni.billing_system.invoices.model.Invoice;

public interface PayInvoicePort {

  public Optional<Invoice> getInvoice(UUID invoiceId);

  public PayInvoiceResult payInvoice(UUID invoiceId);

  public Invoice save(Invoice invoice);

  public static record PayInvoiceResult(String paymentId) {
  }
}
