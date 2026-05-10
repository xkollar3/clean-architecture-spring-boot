package fi.muni.billing_system.subscriptions.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubscribedToPlan(UUID id, UUID customerId, LocalDate billingDate, BigDecimal planPriceEur) {

}
