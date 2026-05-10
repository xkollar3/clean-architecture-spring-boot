package fi.muni.billing_system.invoices.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.invoices.model.Invoice;
import fi.muni.billing_system.invoices.usecase.viewinvoices.ViewInvoiceUseCase;
import fi.muni.billing_system.invoices.usecase.viewinvoices.ViewInvoicesPort;

@ExtendWith(MockitoExtension.class)
class ViewInvoiceUseCaseTest {

  @Mock
  private ViewInvoicesPort viewInvoicesPort;

  @InjectMocks
  private ViewInvoiceUseCase useCase;

  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final String STRIPE_CUSTOMER_ID = "cus_mock_123";
  private static final BigDecimal AMOUNT = new BigDecimal("29.99");
  private static final LocalDate BILLING_DATE = LocalDate.of(2026, 5, 1);

  @Test
  void execute_returnsInvoicesForCustomer() {
    var invoice1 = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, UUID.randomUUID(), AMOUNT, BILLING_DATE);
    var invoice2 = new Invoice(CUSTOMER_ID, STRIPE_CUSTOMER_ID, UUID.randomUUID(), new BigDecimal("49.99"),
        BILLING_DATE.plusMonths(1));

    when(viewInvoicesPort.getInvoices(CUSTOMER_ID)).thenReturn(List.of(invoice1, invoice2));

    List<Invoice> result = useCase.execute(CUSTOMER_ID);

    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(invoice1, invoice2);
  }

  @Test
  void execute_noInvoices_returnsEmptyList() {
    when(viewInvoicesPort.getInvoices(CUSTOMER_ID)).thenReturn(List.of());

    List<Invoice> result = useCase.execute(CUSTOMER_ID);

    assertThat(result).isEmpty();
  }
}
