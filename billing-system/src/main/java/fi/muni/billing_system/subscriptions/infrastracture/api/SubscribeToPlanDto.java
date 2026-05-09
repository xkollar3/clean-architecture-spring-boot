package fi.muni.billing_system.subscriptions.infrastracture.api;

import java.util.UUID;

import fi.muni.billing_system.subscriptions.model.Plan;
import jakarta.validation.constraints.NotNull;

public record SubscribeToPlanDto(@NotNull UUID customerId, @NotNull Plan plan) {
}
