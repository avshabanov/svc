package com.alexshabanov.services.identity.service;

import com.alexshabanov.services.identity.service.dao.IdentityDao;
import com.google.protobuf.Empty;
import com.truward.services.api.identity.v1.*;
import com.truward.services.identity.v1.IdentityService;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Implementation of exposed identity service v. 1.
 */
@ParametersAreNonnullByDefault
public final class IdentityServiceImplV1 implements IdentityService {
  private final TimeService timeService;
  private final IdentityDao identityDao;

  public IdentityServiceImplV1(TimeService timeService, IdentityDao identityDao) {
    this.timeService = timeService;
    this.identityDao = identityDao;
  }

  @Override
  public ListAccountsResponse listAccounts(ListAccountsRequest request) {
    return ListAccountsResponse.newBuilder()
        .addAccounts(Account.newBuilder()
            .setType(AccountType.PERSON)
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

