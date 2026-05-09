package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceCustomerRepository extends JpaRepository<InvoiceCustomerEntity, UUID> {
}
