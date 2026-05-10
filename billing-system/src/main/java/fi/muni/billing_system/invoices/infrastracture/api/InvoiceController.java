package fi.muni.billing_system.invoices.infrastracture.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.muni.billing_system.invoices.model.Invoice;
import fi.muni.billing_system.invoices.usecase.payinvoice.PayInvoiceUseCase;
import fi.muni.billing_system.invoices.usecase.viewinvoices.ViewInvoiceUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

  private final PayInvoiceUseCase payInvoiceUseCase;
  private final ViewInvoiceUseCase viewInvoiceUseCase;

  @PostMapping("/{invoiceId}/pay")
  public void payInvoice(@PathVariable("invoiceId") UUID invoiceId) {
    payInvoiceUseCase.execute(invoiceId);
  }

  @GetMapping("/{customerId}")
  public List<Invoice> viewInvoices(@PathVariable("customerId") UUID customerId) {
    return viewInvoiceUseCase.execute(customerId);
  }
}
