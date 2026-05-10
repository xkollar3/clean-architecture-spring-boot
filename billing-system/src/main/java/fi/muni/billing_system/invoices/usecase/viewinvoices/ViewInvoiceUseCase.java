package fi.muni.billing_system.invoices.usecase.viewinvoices;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fi.muni.billing_system.invoices.model.Invoice;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewInvoiceUseCase {

  private final ViewInvoicesPort viewInvoiceAdapter;

  public List<Invoice> execute(UUID customerId) {
    return viewInvoiceAdapter.getInvoices(customerId);
  }
}
