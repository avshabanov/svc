package com.alexshabanov.services.identity.model.error;

import com.truward.brikar.error.RestErrors;

/**
 * Custom error producer.
 */
public final class IdentityRestErrors extends RestErrors {

  public static final String SOURCE = "IdentityService";

  @Override
  protected String getSource() {
    return SOURCE;
  }
}
