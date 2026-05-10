package fi.muni.billing_system.invoices.usecase.viewinvoices;

import java.util.List;
import java.util.UUID;

import fi.muni.billing_system.invoices.model.Invoice;

public interface ViewInvoicesPort {

  public List<Invoice> getInvoices(UUID customerId);
}
