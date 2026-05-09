package fi.muni.billing_system.subscriptions.infrastracture.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import fi.muni.billing_system.subscriptions.usecase.cancelplan.CancelCustomerPlanUseCase;
import fi.muni.billing_system.subscriptions.usecase.subscribetoplan.SubscribeToPlanUseCase;
import fi.muni.billing_system.subscriptions.usecase.upgradeplan.UpgradePlanUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final SubscribeToPlanUseCase subscribeToPlanUseCase;
  private final CancelCustomerPlanUseCase cancelCustomerPlanUseCase;
  private final UpgradePlanUseCase upgradePlanUseCase;

  @PostMapping
  public UUID subscribeToPlan(@Valid @RequestBody SubscribeToPlanDto request) {
    return subscribeToPlanUseCase.execute(request.customerId(), request.plan());
  }

  @DeleteMapping("/{planId}")
  public void cancelPlan(@PathVariable("planId") UUID planId) {
    cancelCustomerPlanUseCase.execute(planId);
  }

  @PutMapping("/{planId}")
  public void upgradePlan(@PathVariable("planId") UUID planId, @Valid @RequestBody UpgradePlanDto request) {
    upgradePlanUseCase.execute(planId, request.plan());
  }
}
