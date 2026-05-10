package fi.muni.billing_system.invoices.infrastracture.eventhandlers;

import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fi.muni.billing_system.invoices.usecase.issueinvoice.IssueInvoiceUseCase;
import fi.muni.billing_system.subscriptions.model.PlanRenewed;
import fi.muni.billing_system.subscriptions.model.PlanUpgraded;
import fi.muni.billing_system.subscriptions.model.SubscribedToPlan;

@ExtendWith(MockitoExtension.class)
class SubscriptionBillingEventHandlerTest {

  @Mock
  private IssueInvoiceUseCase issueInvoiceUseCase;

  @InjectMocks
  private SubscriptionBillingEventHandler handler;

  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final UUID CUSTOMER_ID = UUID.randomUUID();
  private static final LocalDate BILLING_DATE = LocalDate.of(2026, 5, 1);
  private static final BigDecimal AMOUNT = new BigDecimal("29.99");

  @Test
  void subscribedToPlan_passesCorrectCustomerIdAndSubscriptionId() {
    var event = new SubscribedToPlan(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);

    handler.handle(event);

    verify(issueInvoiceUseCase).execute(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);
  }

  @Test
  void planRenewed_passesCorrectCustomerIdAndSubscriptionId() {
    var event = new PlanRenewed(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);

    handler.handle(event);

    verify(issueInvoiceUseCase).execute(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);
  }

  @Test
  void planUpgraded_passesCorrectCustomerIdAndSubscriptionId() {
    var event = new PlanUpgraded(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);

    handler.handle(event);

    verify(issueInvoiceUseCase).execute(SUBSCRIPTION_ID, CUSTOMER_ID, BILLING_DATE, AMOUNT);
  }
}
