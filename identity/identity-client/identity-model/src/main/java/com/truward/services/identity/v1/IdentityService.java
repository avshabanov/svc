package com.truward.services.identity.v1;

import com.google.protobuf.Empty;
import com.truward.services.api.identity.v1.*;

/**
 * Identity service interface.
 */
public interface IdentityService {

  ListAccountsResponse listAccounts(ListAccountsRequest request);

  AccountProfile lookupAccounts(LookupAccountRequest request);

  RegisterAccountResponse registerAccount(RegisterAccountRequest request);

  AccountProfile updateAccount(UpdateAccountRequest request);

  Empty deleteAccounts(DeleteAccountsRequest request);
}
