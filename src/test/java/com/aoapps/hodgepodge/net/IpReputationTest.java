/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2026  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.junit.BeforeClass;
import org.junit.Test;

public class IpReputationTest {

  private static InetAddress TEST_IP_1;

  @BeforeClass
  public static void init() throws UnknownHostException {
    TEST_IP_1 = InetAddress.getByAddress(new byte[] {(byte) 192, (byte) 168, 1, 1});
  }

  @Test
  public void testMaxBadScore() {
    assertEquals("Expecting 10 - 1 + 24 = 33 for max bad score", 33, IpReputation.MAX_BAD_SCORE);
  }

  private static IpReputation createIpReputation(LongSupplier timeSource) {
    return new IpReputation(IpReputationTest.class.getName() + ": ", timeSource);
  }

  private static IpReputation createIpReputation() {
    long fixedTime = System.currentTimeMillis();
    return createIpReputation(() -> fixedTime);
  }

  private static IpReputation.Connection doAccept() {
    IpReputation ir = createIpReputation();
    IpReputation.Connection conn = ir.init(TEST_IP_1);
    // Do accept
    assertEquals(IpReputation.Connection.AcceptResult.ACCEPT, conn.accept());
    // These are not allowed after accept
    assertThrows(IllegalStateException.class, () -> conn.accept());
    // abort is allowed
    // started is allowed
    assertThrows(IllegalStateException.class, () -> conn.fail());
    assertThrows(IllegalStateException.class, () -> conn.success());
    return conn;
  }

  @Test
  public void testStatusOrderingForAbort() {
    IpReputation.Connection conn = doAccept();
    // Do abort
    conn.abort();
    // But once aborted, nothing else is allowed
    assertThrows(IllegalStateException.class, () -> conn.accept());
    assertThrows(IllegalStateException.class, () -> conn.abort());
    assertThrows(IllegalStateException.class, () -> conn.started());
    assertThrows(IllegalStateException.class, () -> conn.fail());
    assertThrows(IllegalStateException.class, () -> conn.success());
  }

  private static void doStarted(IpReputation.Connection conn) {
    // Do started
    conn.started();
    // These are not allowed after started
    assertThrows(IllegalStateException.class, () -> conn.accept());
    assertThrows(IllegalStateException.class, () -> conn.abort());
    assertThrows(IllegalStateException.class, () -> conn.started());
    // fail is allowed
    // success is allowed
  }

  @Test
  public void testStatusOrderingForFail() {
    IpReputation.Connection conn = doAccept();
    doStarted(conn);
    // Do fail
    conn.fail();
    // But once failed, nothing else is allowed
    assertThrows(IllegalStateException.class, () -> conn.accept());
    assertThrows(IllegalStateException.class, () -> conn.abort());
    assertThrows(IllegalStateException.class, () -> conn.started());
    assertThrows(IllegalStateException.class, () -> conn.fail());
    assertThrows(IllegalStateException.class, () -> conn.success());
  }

  @Test
  public void testStatusOrderingForSuccess() {
    IpReputation.Connection conn = doAccept();
    doStarted(conn);
    // Do success
    conn.success();
    // But once success, nothing else is allowed
    assertThrows(IllegalStateException.class, () -> conn.accept());
    assertThrows(IllegalStateException.class, () -> conn.abort());
    assertThrows(IllegalStateException.class, () -> conn.started());
    assertThrows(IllegalStateException.class, () -> conn.fail());
    assertThrows(IllegalStateException.class, () -> conn.success());
  }

  @Test
  public void testRejectOnConcurrentAccept() {
    IpReputation ir = createIpReputation();
    int numAllowed = 10;
    IpReputation.Connection[] conns = new IpReputation.Connection[numAllowed];
    for (int i = 0; i < numAllowed; i++) {
      conns[i] = ir.init(TEST_IP_1);
      assertEquals("First ones all accepted",
          IpReputation.Connection.AcceptResult.ACCEPT, conns[i].accept());
    }
    assertEquals("Next one rejected",
        IpReputation.Connection.AcceptResult.REJECT, ir.init(TEST_IP_1).accept());
    // But have to abort two (one for rejection and one more) to allow another connection
    conns[0].abort();
    conns[1].abort();
    assertEquals("After an abort, one slot should be freed and next one allowed",
        IpReputation.Connection.AcceptResult.ACCEPT, ir.init(TEST_IP_1).accept());
    assertEquals("But then the next one rejected",
        IpReputation.Connection.AcceptResult.REJECT, ir.init(TEST_IP_1).accept());
  }

  @Test
  public void testRepeatedFailuresThenAllowedOneHourLater() {
    AtomicLong testTime = new AtomicLong();
    IpReputation ir = createIpReputation(() -> testTime.get());
    int numAllowed = 10;
    IpReputation.Connection[] conns = new IpReputation.Connection[numAllowed];
    for (int i = 0; i < numAllowed; i++) {
      conns[i] = ir.init(TEST_IP_1);
      assertEquals("First ones all accepted",
          IpReputation.Connection.AcceptResult.ACCEPT, conns[i].accept());
      conns[i].started();
      conns[i].fail();
    }
    assertEquals("Next one rejected, which adds one to badScore",
        IpReputation.Connection.AcceptResult.REJECT, ir.init(TEST_IP_1).accept());

    testTime.addAndGet(TimeUnit.HOURS.toMillis(1));
    assertEquals("After an hour, next one still rejected, which adds one more to badScore",
        IpReputation.Connection.AcceptResult.REJECT, ir.init(TEST_IP_1).accept());

    testTime.addAndGet(TimeUnit.HOURS.toMillis(2) - 1);
    assertEquals("After just under two more hours, next one rejected, which adds one more to badScore",
        IpReputation.Connection.AcceptResult.REJECT, ir.init(TEST_IP_1).accept());

    testTime.addAndGet(TimeUnit.HOURS.toMillis(1) + 1);
    assertEquals("After just over one more hour, next one accepted",
        IpReputation.Connection.AcceptResult.ACCEPT, ir.init(TEST_IP_1).accept());
  }
}
