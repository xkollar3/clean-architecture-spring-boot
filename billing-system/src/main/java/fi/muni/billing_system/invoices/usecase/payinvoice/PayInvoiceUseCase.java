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
  private final PaymentGatewayPort paymentGatewayPort;

  public void execute(UUID invoiceId) {
    var invoice = payInvoicePort.getInvoice(invoiceId)
        .orElseThrow(
            () -> new IllegalStateException("Cannot pay invoice: " + invoiceId + " because it does not exist."));

    var paymentIntentId = paymentGatewayPort.createPaymentIntent(invoice.getStripeCustomerId(), invoice.getId(),
        invoice.getAmount());

    invoice.payInvoice(paymentIntentId);

    payInvoicePort.save(invoice);
  }
}
