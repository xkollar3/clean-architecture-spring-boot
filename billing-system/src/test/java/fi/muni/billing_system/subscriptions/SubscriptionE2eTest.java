package fi.muni.billing_system.subscriptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import fi.muni.billing_system.TestcontainersConfiguration;
import fi.muni.billing_system.subscriptions.infrastracture.adapters.SubscriptionPlanRepository;
import fi.muni.billing_system.subscriptions.infrastracture.schedulers.SubscriptionRenewalScheduler;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class SubscriptionE2eTest {

  @SpringBootApplication(scanBasePackages = "fi.muni.billing_system.subscriptions")
  static class TestApp {
  }

  @LocalServerPort
  int port;

  @Autowired
  SubscriptionPlanRepository repository;

  @Autowired
  SubscriptionRenewalScheduler scheduler;

  private RestClient restClient() {
    return RestClient.builder()
        .baseUrl("http://localhost:" + port + "/api/subscriptions")
        .build();
  }

  @Test
  void subscribe_returnsIdAndPersists() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "PLUS_TIER"))
        .retrieve()
        .body(UUID.class);

    assertThat(id).isNotNull();

    var entity = repository.findById(id).orElseThrow();
    assertThat(entity.getCustomerId()).isEqualTo(customerId);
    assertThat(entity.getPlan().name()).isEqualTo("PLUS_TIER");
    assertThat(entity.getCancelledAt()).isNull();
  }

  @Test
  void subscribe_duplicateThrows500() {
    var customerId = UUID.randomUUID();
    var body = Map.of("customerId", customerId, "plan", "PLUS_TIER");

    restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .body(UUID.class);

    try {
      restClient().post()
          .contentType(MediaType.APPLICATION_JSON)
          .body(body)
          .retrieve()
          .body(UUID.class);
      throw new AssertionError("Expected 500");
    } catch (HttpServerErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }
  }

  @Test
  void subscribe_nullFieldsReturns400() {
    try {
      restClient().post()
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of())
          .retrieve()
          .body(UUID.class);
      throw new AssertionError("Expected 400");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }
  }

  @Test
  void cancel_setsCancelledAt() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "MAX_TIER"))
        .retrieve()
        .body(UUID.class);

    restClient().delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();

    var entity = repository.findById(id).orElseThrow();
    assertThat(entity.getCancelledAt()).isNotNull();
  }

  @Test
  void cancel_alreadyCancelledThrows500() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "HARDCORE_MAX_TIER"))
        .retrieve()
        .body(UUID.class);

    restClient().delete()
        .uri("/{id}", id)
        .retrieve()
        .toBodilessEntity();

    try {
      restClient().delete()
          .uri("/{id}", id)
          .retrieve()
          .toBodilessEntity();
      throw new AssertionError("Expected 500");
    } catch (HttpServerErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }
  }

  @Test
  void upgrade_changesPlan() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "PLUS_TIER"))
        .retrieve()
        .body(UUID.class);

    restClient().put()
        .uri("/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("plan", "MAX_TIER"))
        .retrieve()
        .toBodilessEntity();

    var entity = repository.findById(id).orElseThrow();
    assertThat(entity.getPlan().name()).isEqualTo("MAX_TIER");
  }

  @Test
  void upgrade_samePlanThrows500() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "PLUS_TIER"))
        .retrieve()
        .body(UUID.class);

    try {
      restClient().put()
          .uri("/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of("plan", "PLUS_TIER"))
          .retrieve()
          .toBodilessEntity();
      throw new AssertionError("Expected 500");
    } catch (HttpServerErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(500));
    }
  }

  @Test
  void upgrade_nullPlanReturns400() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "PLUS_TIER"))
        .retrieve()
        .body(UUID.class);

    try {
      restClient().put()
          .uri("/{id}", id)
          .contentType(MediaType.APPLICATION_JSON)
          .body(Map.of())
          .retrieve()
          .toBodilessEntity();
      throw new AssertionError("Expected 400");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }
  }

  @Test
  void renewal_resetsExpiredSubscription() {
    var customerId = UUID.randomUUID();

    var id = restClient().post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Map.of("customerId", customerId, "plan", "MAX_TIER"))
        .retrieve()
        .body(UUID.class);

    var entity = repository.findById(id).orElseThrow();
    entity.setCurrentPeriodEnd(LocalDate.now().minusDays(1));
    entity.setNextBillingDate(LocalDate.now().minusDays(31));
    repository.save(entity);

    scheduler.renewExpiredSubscriptions();

    var renewed = repository.findById(id).orElseThrow();
    assertThat(renewed.getNextBillingDate()).isEqualTo(LocalDate.now());
    assertThat(renewed.getCurrentPeriodEnd()).isEqualTo(LocalDate.now().plus(30, ChronoUnit.DAYS));
  }
}
