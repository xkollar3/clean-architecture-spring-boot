package fi.muni.billing_system.subscriptions.infrastracture.api;

import java.util.UUID;

import fi.muni.billing_system.subscriptions.model.Plan;

public record SubscribeToPlanDto(UUID customerId, Plan plan) {
}
