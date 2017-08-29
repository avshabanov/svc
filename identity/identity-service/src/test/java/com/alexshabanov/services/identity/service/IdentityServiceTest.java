package com.alexshabanov.services.identity.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Tests for identity service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/IdentityServiceTest-context.xml")
public final class IdentityServiceTest {

  @Test
  public void shouldPass() {
    assertEquals(1, 1);
  }
}
