package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.muni.billing_system.invoices.usecase.issueinvoice.GetCustomerPort;

@Component
public class StripeCustomerAdapter implements GetCustomerPort {

  private final String stripeCustomerId;

  public StripeCustomerAdapter(@Value("${stripe.customer-id}") String stripeCustomerId) {
    this.stripeCustomerId = stripeCustomerId;
  }

  // for demo not to gain more scope we return customer id from config
  // keep the param only to be faithful to a realistic interface
  @Override
  public String getStripeCustomerIdPort(UUID customerId) {
    return stripeCustomerId;
  }
}
