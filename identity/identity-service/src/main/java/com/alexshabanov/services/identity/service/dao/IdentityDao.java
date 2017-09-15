package com.alexshabanov.services.identity.service.dao;

import com.alexshabanov.services.identity.dao.model.Idb1;
import com.alexshabanov.services.identity.model.KeyedEntity;
import com.alexshabanov.services.identity.model.PagedResponse;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Transaction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IdentityDao {
  int MAX_LIMIT = 32;

  Environment getEnvironment();

  String createRecord(Transaction tx, Idb1.UserRecord newRecord);

  KeyedEntity<Idb1.UserRecord> getRecordByAlias(Transaction tx, String alias);

  Idb1.UserRecord getRecordById(Transaction tx, String userId);

  PagedResponse<KeyedEntity<Idb1.UserRecord>> getRecords(Transaction tx, String offsetToken, int limit);
}
