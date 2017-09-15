package com.alexshabanov.services.identity.service.dao.support;

import com.alexshabanov.services.identity.dao.model.Idb1;
import com.alexshabanov.services.identity.model.KeyedEntity;
import com.alexshabanov.services.identity.model.PagedResponse;
import com.alexshabanov.services.identity.service.dao.IdentityDao;
import com.truward.kvdao.xodus.KeyUtil;
import com.truward.semantic.id.IdCodec;
import com.truward.semantic.id.SemanticIdCodec;
import com.truward.semantic.id.exception.IdParsingException;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.env.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.*;

import static com.truward.kvdao.xodus.ProtoEntity.entryToProto;
import static com.truward.kvdao.xodus.ProtoEntity.protoToEntry;
import static jetbrains.exodus.bindings.StringBinding.entryToString;
import static jetbrains.exodus.bindings.StringBinding.stringToEntry;

/**
 * TBD
 */
@ParametersAreNonnullByDefault
public final class DefaultIdentityDao implements IdentityDao {
  private static final String USER_ACCOUNT_STORE_NAME = "user-account";
  private static final String USER_ALIAS_STORE_NAME = "user-alias";

  /**
   * User account encoder, "acc1" stands for Account ver. 1
   */
  private static final IdCodec USER_ACCOUNT_CODEC = SemanticIdCodec.forPrefixNames("acc1");

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Environment environment;
  private final DefaultIdentityDao.Stores stores;
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

  public DefaultIdentityDao(Environment environment, Random keyRandom) {
    this.environment = Objects.requireNonNull(environment, "environment");
    this.keyRandom = Objects.requireNonNull(keyRandom, "keyRandom");
    this.stores = environment.computeInTransaction(tx -> new DefaultIdentityDao.Stores(environment, tx));
  }

  public DefaultIdentityDao(Environment environment) {
    this(environment, new SecureRandom());
  }

  public Environment getEnvironment() {
    return this.environment;
  }

  @Override
  public String createRecord(Transaction tx, Idb1.UserRecord newRecord) {
    final String newId = USER_ACCOUNT_CODEC.encodeRandomBytes(this.keyRandom, KeyUtil.DEFAULT_KEY_BYTES_SIZE);
    final ByteIterable idKey = KeyUtil.semanticIdAsKey(USER_ACCOUNT_CODEC, newId);

    this.stores.userAccount.put(tx, idKey, protoToEntry(newRecord));
    if (newRecord.hasProfile()) {
      for (final Idb1.Contact contact : newRecord.getProfile().getContactsList()) {
        final ByteIterable contactKey = toContactKey(contact);
        if (!this.stores.userAlias.add(tx, contactKey, idKey)) {
          throw new IllegalArgumentException("Someone already added a profile referring to contact=" +
              contact.getValue());
        }
      }
    }

    if (!this.stores.userAlias.add(tx, toAliasKey("username", newRecord.getUsername()), idKey)) {
      throw new IllegalArgumentException("Someone already added a profile with username=" + newRecord.getUsername());
    }

    return newId;
  }

  @Override
  public KeyedEntity<Idb1.UserRecord> getRecordByAlias(Transaction tx, String alias) {
    final ByteIterable userIdBytes = this.stores.userAlias.get(tx, toAliasKey("alias", alias));
    if (userIdBytes == null) {
      throw new EmptyResultDataAccessException(1);
    }

    final String userId = entryToString(userIdBytes);
    try {
      return KeyedEntity.valueOf(userId, getRecordById(tx, userId));
    } catch (EmptyResultDataAccessException e) {
      throw new DataIntegrityViolationException("Missing user record corresponding to alias=" + alias, e);
    }
  }

  @Override
  public Idb1.UserRecord getRecordById(Transaction tx, String userId) {
    final ByteIterable recordBody = this.stores.userAccount.get(
        tx,
        new ArrayByteIterable(USER_ACCOUNT_CODEC.decodeBytes(userId)));
    if (recordBody == null) {
      throw new EmptyResultDataAccessException(1);
    }

    return entryToProto(recordBody, Idb1.UserRecord.getDefaultInstance());
  }

  @Override
  public PagedResponse<KeyedEntity<Idb1.UserRecord>> getRecords(Transaction tx, String offsetToken, int limit) {
    if (limit < 0 || limit > MAX_LIMIT) {
      throw new IllegalArgumentException("limit");
    }

    if (limit == 0) {
      return PagedResponse.valueOf(offsetToken, Collections.emptyList());
    }

    final List<KeyedEntity<Idb1.UserRecord>> records = new ArrayList<>();
    String nextOffsetToken = "";
    try (final Cursor cursor = this.stores.userAccount.openCursor(tx)) {
      // locate next record by transforming offset token to user ID
      if (StringUtils.hasText(offsetToken)) {
        final ByteIterable userIdKey;
        try {
          userIdKey = new ArrayByteIterable(USER_ACCOUNT_CODEC.decodeBytes(offsetToken));
        } catch (IdParsingException e) {
          throw new IllegalArgumentException("Invalid offsetToken=" + offsetToken, e);
        }

        // rare case: all keys deleted since last run
        if (cursor.getSearchKeyRange(userIdKey) == null) {
          return PagedResponse.empty();
        }
      }

      do {
        records.add(
            KeyedEntity.valueOf(
                KeyUtil.keyAsSemanticId(USER_ACCOUNT_CODEC, cursor.getKey()),
                entryToProto(cursor.getValue(), Idb1.UserRecord.getDefaultInstance())));

        // once we added sufficient number of records, get next offset token and leave this loop
        if (records.size() >= limit) {
          if (cursor.getNext()) {
            nextOffsetToken = KeyUtil.keyAsSemanticId(USER_ACCOUNT_CODEC, cursor.getKey());
          }
          break;
        }
      } while (cursor.getNext());
    }

    return PagedResponse.valueOf(nextOffsetToken, records);
  }

  //
  // Private
  //

  private static ByteIterable toContactKey(Idb1.Contact contact) {
    return toAliasKey("contact value", contact.getValue());
  }

  private static ByteIterable toAliasKey(String propertyName, String value) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException("Invalid " + propertyName);
    }

    // TODO: more checks for contact value
    return stringToEntry(value);
  }
}
