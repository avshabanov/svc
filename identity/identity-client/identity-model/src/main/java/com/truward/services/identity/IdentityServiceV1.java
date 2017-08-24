package com.truward.services.identity;

/**
 * Identity service interface.
 */
public interface IdentityServiceV1 {

  IdentityV1.ListAccountsResponse listAccounts(
      IdentityV1.ListAccountsRequest request);

  IdentityV1.LookupAccountResponse lookupAccounts(
      IdentityV1.LookupAccountRequest request);
}
