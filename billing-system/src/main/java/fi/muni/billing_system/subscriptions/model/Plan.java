package fi.muni.billing_system.subscriptions.model;

import java.math.BigDecimal;

import lombok.Getter;

/**
 * Plan is modeled as an enum, in reality we would have a module for managing
 * plans
 *
 * Plan prices are in euros to keep it simple
 */
@Getter
public enum Plan {

  PLUS_TIER(BigDecimal.valueOf(20)),
  MAX_TIER(BigDecimal.valueOf(100)),
  HARDCORE_MAX_TIER(BigDecimal.valueOf(200));

  private final BigDecimal planPriceEur;

  private Plan(BigDecimal planPrice) {
    this.planPriceEur = planPrice;
  }

}
