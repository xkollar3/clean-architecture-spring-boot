package fi.muni.billing_system.invoices.usecase.issueinvoice;

import java.util.UUID;

public interface GetCustomerPort {

  public String getStripeCustomerIdPort(UUID customerId);
}
