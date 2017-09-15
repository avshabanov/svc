package com.alexshabanov.services.identity.model;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

// TODO: move to kvdao
@ParametersAreNonnullByDefault
public final class KeyedEntity<T> {
  private final String id;
  private final T entity;

  private KeyedEntity(String id, T entity) {
    this.id = Objects.requireNonNull(id, "id");
    this.entity = Objects.requireNonNull(entity, "entity");
  }

  public static <T> KeyedEntity<T> valueOf(String id, T entity) {
    return new KeyedEntity<>(id, entity);
  }

  public String getId() {
    return id;
  }

  public T getEntity() {
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KeyedEntity)) return false;

    KeyedEntity<?> that = (KeyedEntity<?>) o;

    return getId().equals(that.getId()) && getEntity().equals(that.getEntity());
  }

  @Override
  public int hashCode() {
    int result = getId().hashCode();
    result = 31 * result + getEntity().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "KeyedEntity{" +
        "id='" + getId() + '\'' +
        ", entity=" + getEntity() +
        '}';
  }
}
