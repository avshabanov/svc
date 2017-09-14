package com.alexshabanov.services.identity.service;

import com.google.protobuf.Empty;
import com.truward.semantic.id.IdCodec;
import com.truward.semantic.id.SemanticIdCodec;
import com.truward.services.api.identity.v1.*;
import com.truward.services.identity.v1.IdentityService;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.StoreConfig;
import jetbrains.exodus.env.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Implementation of exposed identity service v. 1.
 */
@ParametersAreNonnullByDefault
public final class IdentityServiceImplV1 implements IdentityService {

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

@ParametersAreNonnullByDefault
final class IdentityDao {

  /**
   * Key size, used to generate smaller IDs in an environment that allows global contention control.
   */
  private static final int START_KEY_SIZE = 5;

  private static final String USER_ACCOUNT_STORE_NAME = "user-account";
  private static final String USER_ALIAS_STORE_NAME = "user-alias";

  /**
   * User account encoder, "acc1" stands for Account ver. 1
   */
  private static final IdCodec USER_ACCOUNT_CODEC = SemanticIdCodec.forPrefixNames("acc1");

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Environment environment;
  private final Stores stores;
  private final Random keyRandom;

  private static final class Stores {
    final Store userAccount;
    final Store userAlias;

    Stores(Environment environment, Transaction tx) {
      // bytesFromSemanticId(item.id) -> item
      this.userAccount = environment.openStore(USER_ACCOUNT_STORE_NAME, StoreConfig.WITHOUT_DUPLICATES, tx);

      // ExternalId -> item.id(semantic ID)
      this.userAlias = environment.openStore(USER_ALIAS_STORE_NAME, StoreConfig.WITHOUT_DUPLICATES, tx);
    }
  }

  public IdentityDao(Environment environment, Random keyRandom) {
    this.environment = Objects.requireNonNull(environment, "environment");
    this.keyRandom = Objects.requireNonNull(keyRandom, "keyRandom");
    this.stores = environment.computeInTransaction(tx -> new Stores(environment, tx));
  }

  public IdentityDao(Environment environment) {
    this(environment, new SecureRandom());
  }

  public Environment getEnvironment() {
    return environment;
  }


}
