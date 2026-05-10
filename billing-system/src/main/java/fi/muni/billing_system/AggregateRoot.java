package fi.muni.billing_system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

  private final transient List<Object> domainEvents = new ArrayList<>();

  protected void registerEvent(Object event) {
    domainEvents.add(event);
  }

  public List<Object> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  public void clearDomainEvents() {
    domainEvents.clear();
  }
}
