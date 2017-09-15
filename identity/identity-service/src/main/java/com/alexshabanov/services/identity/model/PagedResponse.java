package com.alexshabanov.services.identity.model;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: move to kvdao support
@ParametersAreNonnullByDefault
public final class PagedResponse<T> {
  private final String offsetToken;
  private final List<T> entities;

  private PagedResponse(String offsetToken, List<T> entities) {
    this.offsetToken = Objects.requireNonNull(offsetToken, "offsetToken");
    this.entities = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(entities, "entities")));
  }

  public static <T> PagedResponse<T> valueOf(String offsetToken, List<T> entities) {
    return new PagedResponse<>(offsetToken, entities);
  }

  public static <T> PagedResponse<T> empty() {
    return new PagedResponse<>("", Collections.emptyList());
  }

  public String getOffsetToken() {
    return offsetToken;
  }

  public List<T> getEntities() {
    return entities;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PagedResponse)) return false;

    PagedResponse<?> that = (PagedResponse<?>) o;

    return getOffsetToken().equals(that.getOffsetToken()) && getEntities().equals(that.getEntities());
  }

  @Override
  public int hashCode() {
    int result = getOffsetToken().hashCode();
    result = 31 * result + getEntities().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "PagedResponse{" +
        "offsetToken='" + getOffsetToken() + '\'' +
        ", entities=" + getEntities() +
        '}';
  }
}
