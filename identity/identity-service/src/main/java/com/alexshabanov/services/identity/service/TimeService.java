package com.alexshabanov.services.identity.service;

import com.google.protobuf.Timestamp;

public interface TimeService {

  TimeService INSTANCE = () -> {
    long millis = System.currentTimeMillis();
    return Timestamp.newBuilder()
        .setSeconds(millis / 1000)
        .setNanos((int) ((millis % 1000) * 1000000))
        .build();
  };

  Timestamp now();
}
