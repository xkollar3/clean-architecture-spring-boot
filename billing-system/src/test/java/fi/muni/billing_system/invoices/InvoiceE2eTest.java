package fi.muni.billing_system.invoices;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import fi.muni.billing_system.TestcontainersConfiguration;
import fi.muni.billing_system.invoices.infrastracture.persistence.InvoiceRepository;
import fi.muni.billing_system.invoices.usecase.payinvoice.PaymentGatewayPort;
import fi.muni.billing_system.subscriptions.model.PlanRenewed;
import fi.muni.billing_system.subscriptions.model.PlanUpgraded;
import fi.muni.billing_system.subscriptions.model.SubscribedToPlan;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class InvoiceE2eTest {

  @SpringBootApplication(scanBasePackages = "fi.muni.billing_system.invoices")
  static class TestApp {

    @Bean
    PaymentGatewayPort paymentGatewayPort() {
      return (stripeCustomerId, invoiceId, amount) -> "pi_mock_" + invoiceId;
    }
  }

  @LocalServerPort
  int port;

  @Autowired
  InvoiceRepository repository;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  private RestClient restClient() {
    return RestClient.builder()
        .baseUrl("http://localhost:" + port + "/api/invoices")
        .build();
  }

  @Test
  void subscribedToPlan_createsInvoice() {
    var id = UUID.randomUUID();
    var billingDate = LocalDate.now();
    var amount = new BigDecimal("20.00");

    eventPublisher.publishEvent(new SubscribedToPlan(id, id, billingDate, amount));

    var invoices = repository.findAll().stream()
        .filter(e -> e.getSubscriptionId().equals(id))
        .toList();
    assertThat(invoices).hasSize(1);

    var invoice = invoices.getFirst();
    assertThat(invoice.getCustomerId()).isEqualTo(id);
    assertThat(invoice.getAmount()).isEqualByComparingTo(amount);
    assertThat(invoice.getBillingDate()).isEqualTo(billingDate);
    assertThat(invoice.getPaymentId()).isNull();
  }

  @Test
  void planRenewed_createsInvoice() {
    var id = UUID.randomUUID();
    var billingDate = LocalDate.now();
    var amount = new BigDecimal("100.00");

    eventPublisher.publishEvent(new PlanRenewed(id, id, billingDate, amount));

    var invoices = repository.findAll().stream()
        .filter(e -> e.getSubscriptionId().equals(id))
        .toList();
    assertThat(invoices).hasSize(1);
    assertThat(invoices.getFirst().getAmount()).isEqualByComparingTo(amount);
  }

  @Test
  void planUpgraded_createsInvoice() {
    var id = UUID.randomUUID();
    var billingDate = LocalDate.now();
    var amount = new BigDecimal("200.00");

    eventPublisher.publishEvent(new PlanUpgraded(id, id, billingDate, amount));

    var invoices = repository.findAll().stream()
        .filter(e -> e.getSubscriptionId().equals(id))
        .toList();
    assertThat(invoices).hasSize(1);
    assertThat(invoices.getFirst().getAmount()).isEqualByComparingTo(amount);
  }

  @Test
  void duplicateInvoice_isRejected() {
    var id = UUID.randomUUID();
    var billingDate = LocalDate.now();
    var amount = new BigDecimal("20.00");

    eventPublisher.publishEvent(new SubscribedToPlan(id, id, billingDate, amount));

    try {
      eventPublisher.publishEvent(new SubscribedToPlan(id, id, billingDate, amount));
      throw new AssertionError("Expected IllegalStateException for duplicate invoice");
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).contains("already been issued");
    }
  }

  @Test
  void payInvoice_setsPaymentId() {
    var id = UUID.randomUUID();
    eventPublisher.publishEvent(new SubscribedToPlan(id, id, LocalDate.now(), new BigDecimal("20.00")));

    var invoiceEntity = repository.findAll().stream()
        .filter(e -> e.getSubscriptionId().equals(id))
        .findFirst()
        .orElseThrow();

    restClient().post()
        .uri("/{invoiceId}/pay", invoiceEntity.getId())
        .retrieve()
        .toBodilessEntity();

    var paid = repository.findById(invoiceEntity.getId()).orElseThrow();
    assertThat(paid.getPaymentId()).isEqualTo("pi_mock_" + invoiceEntity.getId());
  }

  @Test
  void payInvoice_nonExistent_throws500() {
    var fakeId = UUID.randomUUID();

    try {
      restClient().post()
          .uri("/{invoiceId}/pay", fakeId)
          .retrieve()
          .toBodilessEntity();
      throw new AssertionError("Expected 500");
    } catch (HttpServerErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }
  }

  @Test
  void payInvoice_alreadyPaid_throws500() {
    var id = UUID.randomUUID();
    eventPublisher.publishEvent(new SubscribedToPlan(id, id, LocalDate.now(), new BigDecimal("20.00")));

    var invoiceEntity = repository.findAll().stream()
        .filter(e -> e.getSubscriptionId().equals(id))
        .findFirst()
        .orElseThrow();

    restClient().post()
        .uri("/{invoiceId}/pay", invoiceEntity.getId())
        .retrieve()
        .toBodilessEntity();

    try {
      restClient().post()
          .uri("/{invoiceId}/pay", invoiceEntity.getId())
          .retrieve()
          .toBodilessEntity();
      throw new AssertionError("Expected 500");
    } catch (HttpServerErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }
  }
}
