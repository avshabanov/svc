package com.alexshabanov.services.identity.service;

import com.google.protobuf.Empty;
import com.truward.services.api.identity.v1.*;
import com.truward.services.identity.v1.IdentityService;

/**
 * Implementation of exposed identity service v. 1.
 */
public final class IdentityServiceImplV1 implements IdentityService {

  @Override
  public ListAccountsResponse listAccounts(ListAccountsRequest request) {
    return ListAccountsResponse.newBuilder()
        .addAccounts(Account.newBuilder()
            .setType("profile")
            .build())
        .build();
  }

  @Override
  public AccountProfile lookupAccounts(LookupAccountRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RegisterAccountResponse registerAccount(RegisterAccountRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AccountProfile updateAccount(UpdateAccountRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Empty deleteAccounts(DeleteAccountsRequest request) {
    throw new UnsupportedOperationException();
  }
}
