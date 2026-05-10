package fi.muni.billing_system.invoices.infrastracture.persistence;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {

  boolean existsBySubscriptionIdAndCustomerIdAndBillingDate(UUID subscriptionId, UUID customerId,
      LocalDate billingDate);
}
