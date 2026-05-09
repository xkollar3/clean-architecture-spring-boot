package fi.muni.billing_system.subscriptions.usecase.renewsubscription;

import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RenewSubscriptionUseCase {

  public final RenewSubscriptionPort renewSubscriptionPort;

  public void execute(UUID subscriptionId) {
    var optSubscription = renewSubscriptionPort.getSubscription(subscriptionId);

    if (optSubscription.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot renew subscription with id: " + subscriptionId + " because it does not exist.");
    }

    optSubscription.get().renewSubscription();

    renewSubscriptionPort.save(optSubscription.get());
  }
}
