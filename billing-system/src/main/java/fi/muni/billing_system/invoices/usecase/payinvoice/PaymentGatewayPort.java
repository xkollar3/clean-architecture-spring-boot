package fi.muni.billing_system.invoices.usecase.payinvoice;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGatewayPort {

  String createPaymentIntent(String stripeCustomerId, UUID invoiceId, BigDecimal amount);
}
