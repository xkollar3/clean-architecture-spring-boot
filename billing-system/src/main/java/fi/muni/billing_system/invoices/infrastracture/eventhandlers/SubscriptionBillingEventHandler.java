package fi.muni.billing_system.invoices.infrastracture.eventhandlers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import fi.muni.billing_system.invoices.usecase.issueinvoice.IssueInvoiceUseCase;
import fi.muni.billing_system.subscriptions.model.PlanRenewed;
import fi.muni.billing_system.subscriptions.model.PlanUpgraded;
import fi.muni.billing_system.subscriptions.model.SubscribedToPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO
@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionBillingEventHandler {

  private final IssueInvoiceUseCase issueInvoiceUseCase;

  @EventListener
  public void handle(SubscribedToPlan event) {
    log.info("Invoicing new subscription: {}", event.id());

    issueInvoiceUseCase.execute(event.id(), event.customerId(), event.billingDate(), event.planPriceEur());
  }

  @EventListener
  public void handle(PlanRenewed event) {
    log.info("Invoicing renewed subscription: {}", event.id());

    issueInvoiceUseCase.execute(event.id(), event.customerId(), event.billingDate(), event.planPriceEur());
  }

  @EventListener
  public void handle(PlanUpgraded event) {
    log.info("Invoicing upgraded subscription: {}", event.id());

    issueInvoiceUseCase.execute(event.id(), event.customerId(), event.billingDate(), event.planPriceEur());
  }
}
