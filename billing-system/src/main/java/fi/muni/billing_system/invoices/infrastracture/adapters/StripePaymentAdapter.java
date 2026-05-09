package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import fi.muni.billing_system.invoices.usecase.payinvoice.PaymentGatewayPort;

@Component
public class StripePaymentAdapter implements PaymentGatewayPort {

  private final StripeClient stripeClient;

  public StripePaymentAdapter(@Value("${stripe.api-key}") String apiKey) {
    this.stripeClient = new StripeClient(apiKey);
  }

  @Override
  public String createPaymentIntent(String stripeCustomerId, UUID invoiceId, BigDecimal amount) {
    try {
      var params = PaymentIntentCreateParams.builder()
          .setCustomer(stripeCustomerId)
          .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
          .setCurrency("usd")
          .setAutomaticPaymentMethods(
              PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                  .setEnabled(true)
                  .build())
          .putMetadata("invoice_id", invoiceId.toString())
          .build();

      PaymentIntent paymentIntent = stripeClient.paymentIntents().create(params);
      return paymentIntent.getId();
    } catch (StripeException e) {
      throw new RuntimeException("Failed to create payment intent for invoice: " + invoiceId, e);
    }
  }
}
