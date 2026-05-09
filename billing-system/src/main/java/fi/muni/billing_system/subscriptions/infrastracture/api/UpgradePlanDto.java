package fi.muni.billing_system.subscriptions.infrastracture.api;

import fi.muni.billing_system.subscriptions.model.Plan;
import jakarta.validation.constraints.NotNull;

public record UpgradePlanDto(@NotNull Plan plan) {

}
