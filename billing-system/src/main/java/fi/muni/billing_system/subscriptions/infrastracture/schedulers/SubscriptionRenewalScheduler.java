package fi.muni.billing_system.subscriptions.infrastracture.schedulers;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.muni.billing_system.subscriptions.infrastracture.adapters.SubscriptionPlanRepository;
import fi.muni.billing_system.subscriptions.usecase.renewsubscription.RenewSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionRenewalScheduler {

  private final SubscriptionPlanRepository repository;
  private final RenewSubscriptionUseCase renewSubscriptionUseCase;

  @Scheduled(cron = "0 0 0 * * *")
  public void renewExpiredSubscriptions() {
    var dueSubscriptions = repository
        .findAllByCurrentPeriodEndLessThanEqualAndCancelledAtIsNull(LocalDate.now());

    log.info("Found {} subscriptions due for renewal", dueSubscriptions.size());

    for (var entity : dueSubscriptions) {
      try {
        renewSubscriptionUseCase.execute(entity.getId());
        log.info("Renewed subscription {}", entity.getId());
      } catch (Exception e) {
        log.error("Failed to renew subscription {}", entity.getId(), e);
      }
    }
  }
}
