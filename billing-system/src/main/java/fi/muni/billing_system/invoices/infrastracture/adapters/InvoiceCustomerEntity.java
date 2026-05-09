package fi.muni.billing_system.invoices.infrastracture.adapters;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCustomerEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String stripeCustomerId;
}
