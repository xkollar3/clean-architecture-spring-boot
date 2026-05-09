package fi.muni.billing_system.invoices.usecase.payinvoice;

import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PayInvoiceUseCase {

  private final PayInvoicePort payInvoicePort;

  public UUID execute(UUID invoiceId) {
    var optInvoice = payInvoicePort.getInvoice(invoiceId);
    if (optInvoice.isEmpty()) {
      throw new IllegalStateException("Cannot pay invoice: " + invoiceId + " because it does not exist.");
    }

    var result = payInvoicePort.payInvoice(invoiceId);

    optInvoice.get().payInvoice(result.paymentId());

    return payInvoicePort.save(optInvoice.get()).getId();
  }
}
