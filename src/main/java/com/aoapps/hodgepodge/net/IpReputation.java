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

import com.aoapps.collections.AoCollections;
import com.aoapps.lang.exception.NotImplementedException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tracks IP reputation for use by any number of services.
 *
 * <p>The lifecycle:</p>
 * <ol>
 * <li>{@link IpReputation#init(java.net.InetAddress)}, returns {@link Connection} - establishes a new context for
 *   tracking one specific service interaction.</li>
 * <li>Connection:
 *   <ol type="a">
 *     <li>{@link Connection#accept()}</li>
 *     <li>{@link Connection#abort()}</li>
 *     <li>{@link Connection#started()}</li>
 *     <li>{@link Connection#fail()}</li>
 *     <li>{@link Connection#success()}</li>
 *   </ol>
 *  </li>
 *  <li>Bad score decays once per hour.</li>
 *  <li>Blocking is done at a bad score of {@link IpReputation#REJECT_BAD_SCORE}, limiting attempts
 *    (both serial and concurrent).</li>
 *  <li>Bad score is capped to {@link IpReputation#MAX_BAD_SCORE} for a maximum of a
 *    {@link IpReputation#UNBLOCK_HOURS}-hour block.</li>
 * </ol>
 *
 * <p>Future ideas (in no particular order):</p>
 * <ol>
 * <li>Sortable columns in simple aoadmin-only web interface to view current statistics for the VNC tracker.</li>
 * <li>Using hard-coded values; only make configurable when first needed.</li>
 * <li>Concurrency through ConcurrentMap and AtomicInteger.</li>
 * <li>Evict older records, with lower "bad" scores, when a fixed number of records are reached.
 *   (to limited maximum heap usage).</li>
 * <li>Be able to remove blocks from the aoadmin interface.</li>
 * <li>Track "Good" scores, too.</li>
 * <li>Add a "boost" method, which would add some "Good" reputation from an external source, such
 *   as successful website login or access to remote console control panels.
 *   Have to consider consequences of this - don't want a bad guy with access to boost themselves unlimited service
 *   password attempts.  Could limit to one boost per time period or session.
 *   However, the per-attempt delays might be sufficient to mitigate this already.
 *   Boost an be weaker than Connection.success(), and only apply to the /32 or /128.</li>
 * <li>Track good/bad scores per different network ranges, both IPv4 and IPv6 supported:
 *   <ol type="a">
 *     <li>For IPv4, tracking /32 through /24, at every level (/32, /31, …, /25, /24).</li>
 *     <li>For IPv6, tracking /128 through /32, at every 4th level (/128, /124, …, /36, /32).
 *       Exact details to be determined later.  Could be every 4th from /128 to /64, then others like /56, /48</li>
 *     <li>Scores are added and removed across all the levels.</li>
 *     <li>For allow/deny decision, check tightest range first (/32 or /128): if (bad - good) &gt; deny_threshold, deny.
 *         If (bad - good) &lt; allow_threshold, allow.  Otherwise check the next level up.
 *         When allow levels checked and no threshold, would be UNCERTAIN (Link enum).</li>
 *      <li>At all prefixes, bad scores would have a maximum value where the decay would not block more than 24 hours.</li>
 *      <li>At all prefixes, good scores would have a maximum value where their reputation can persist longer, maybe a month?</li>
 *      <li>But scores for increasingly broader network ranges should have caps and decay such that the reputation effects do not last as long.</li>
 *      <li>Maybe the positive score decays more quickly by prefix.  So a positive /32 could last up to 32 days, while
 *        the positive /24 could last much less time.  Basically, either cap the max good score by prefix (preferred),
 *        or decay more quickly by prefix.</li>
 *      <li>Bound the downside of the difference.  One good login could result in multiple bad attempts: 30 days reputation,
 *        slower decay, +2 good versus +1 bad, all multiple to the number of attempts that could be made.  But, the
 *        number of attempts would remain bounded.  A bad guy with one good login could use that to magnify their
 *        attempts.  However, like the "boost" idea, the per-attempt delays might be sufficient to mitigate this already.</li>
 *   </ol>
 * </li>
 * <li>Support IPv6 (including mapped IPv4 being converted to IPv4 internally)</li>
 * <li>Support persistence.  If storing in local filesystem, consider consequences of Tomcat parallel deployment.
 *   More enterprisey solution would be a reputation service that centralizes across multiple services, but this
 *   would begin to overlap the existing, and more complicated, <a href="https://aoindustries.com/aoserv/ipreputation/">AOServ IP Reputation</a>
 *   system that blocks at edge routers.</li>
 * <li>Bad scores decay more quickly, maybe once per hour and in a quantity scaled by (33-prefix) (/32 would decay one
 *   per hour, /31 would decay two per hour, /24 would decay 9 per hour /24 8 per hour).</li>
 * <li>Good scores decay more slowly, maybe once per 10 hours or similar.</li>
 * <li>Use this feature for other Java endpoints, such as aoserv-daemon, aoserv-master, and NOC RMI.</li>
 * <li>When background clean-up is triggered, clean the immediate record being processed (if any), then delegate
 *   background clean-up to the provided task executor.  API users would have to pass-in the executor, which could
 *   simply by {@link Thread#Thread(java.lang.Runnable) Thread::new} or could be
 *   <a href="https://oss.aoapps.com/concurrent/apidocs/com.aoapps.concurrent/com/aoapps/concurrent/Executors.html#getUnbounded()">an
 *   unbouded executor from ao-concurrent</a>.</li>
 * <li>Beyond reputation, add rate-limits per-host, and per-network prefix ranges.  These rate limits would even affect
 *   those successfully performing the operations.  The rate limit allowed could be affected by the currently effective good/bad scores.</li>
 * <li>Use the reputation system for login forms and servlet filter for public facing sites.
 *   In particular, could integrate well with the rate limiting idea.</li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public class IpReputation {

  private static final Logger logger = Logger.getLogger(IpReputation.class.getName());

  /**
   * The number of milliseconds between background cleanup passes.
   */
  private static final long BACKGROUND_CLEANUP_MILLIS = TimeUnit.MINUTES.toMillis(1);

  /**
   * The number of milliseconds between background cleanup logging.
   * Will log {@link Level#FINE} at this interval, and {@link Level#FINER} on intermediate cleanups.
   */
  private static final long BACKGROUND_CLEANUP_LOG_MILLIS = TimeUnit.HOURS.toMillis(1);

  /**
   * The number of hours at which will be unblocked.
   */
  private static final int UNBLOCK_HOURS = 24;

  /**
   * The number of milliseconds at which will be unblocked.
   */
  private static final long UNBLOCK_MILLIS = TimeUnit.HOURS.toMillis(UNBLOCK_HOURS);

  /**
   * The bad score at which attempts will be rejected.
   * Given the temporary +1 during {@link Connection#accept()}, it follows that concurrent in-flight login attempts
   * per-IP are also limited to this value.
   */
  private static final int REJECT_BAD_SCORE = 10;

  /**
   * The number of milliseconds between each decay of {@link Record#lastDecayTime}.
   */
  private static final long BAD_DECAY_MILLIS = TimeUnit.HOURS.toMillis(1);

  /**
   * The maximum bad score.  This is computed so a block lasts {@link IpReputation#UNBLOCK_HOURS} hours maximum.
   */
  static final long MAX_BAD_SCORE = REJECT_BAD_SCORE - 1 + (UNBLOCK_MILLIS / BAD_DECAY_MILLIS);

  static {
    int expected = (REJECT_BAD_SCORE - 1) + UNBLOCK_HOURS;
    assert MAX_BAD_SCORE == expected : "Expecting to be (" + REJECT_BAD_SCORE + " - 1) + " + UNBLOCK_HOURS + " = " + expected + ", got " + MAX_BAD_SCORE;
  }

  /**
   * Normalize IPv4 mapped on IPv6 to IPv4.
   */
  private static InetAddress normalize(InetAddress address) {
    Objects.requireNonNull(address);
    if (address instanceof Inet6Address) {
      throw new NotImplementedException("Future: Implement IPv6, including checking and normalizing IPv4-mapped");
    }
    return address;
  }

  private static class Record {

    /**
     * The effective last time this record had its decay processed, as a multiple of
     * {@link IpReputation#BAD_DECAY_MILLIS} from previous time decayed.
     *
     * @see IpReputation#getSystemTimeAndAdjustForPast()
     */
    private long lastDecayTime;

    /**
     * The current bad score, when reduced back to {@code 0} this record may be cleaned from
     * {@link IpReputation#records}.  Always less than or equal to {@link IpReputation#MAX_BAD_SCORE}.
     */
    private int badScore;

    /**
     * @param currentTime See {@link IpReputation#getSystemTimeAndAdjustForPast()}
     */
    private Record(long currentTime) {
      this.lastDecayTime = currentTime;
    }

    private void doDecay(long currentTime) {
      assert currentTime >= lastDecayTime : "When system time moves backwards, lastDecayTime should have been offset to match";
      assert badScore >= 0;
      // Cast to (int) is safe since badScore is int and >= 0
      int badScoreDecrement = (int) Math.min(badScore, (currentTime - lastDecayTime) / BAD_DECAY_MILLIS);
      assert badScoreDecrement >= 0;
      badScore -= badScoreDecrement;
      lastDecayTime += badScoreDecrement * BAD_DECAY_MILLIS;
      assert lastDecayTime <= currentTime;
    }
  }

  private final Map<InetAddress, Record> records = new LinkedHashMap<>(); // LinkedHashMap to maintain insertion order for logging and statistics

  /**
   * Locking on {@link IpReputation#records} for now, until possible future high concurrency implementation.
   */
  private final Object lock = records;

  /**
   * The last time the background decay cleanup was performed or {@link Long#MIN_VALUE} when not yet
   * performed.
   *
   * @see IpReputation#getSystemTimeAndAdjustForPast()
   */
  private long lastBackgroundCleanup = Long.MIN_VALUE;

  /**
   * The last time the background decay cleanup logging was performed at {@link Level#FINE} or {@link Long#MIN_VALUE}
   * when not yet performed.
   *
   * @see IpReputation#getSystemTimeAndAdjustForPast()
   */
  private long lastBackgroundCleanupFineLogging = Long.MIN_VALUE;

  private final String logPrefix;

  private final LongSupplier timeSource;

  /**
   * @param timeSource  Use the provided time source, useful for testing.
   */
  public IpReputation(String logPrefix, LongSupplier timeSource) {
    this.logPrefix = Objects.toString(logPrefix, "");
    this.timeSource = Objects.requireNonNull(timeSource);
  }

  /**
   * Defaults to {@link System#currentTimeMillis()}.
   */
  public IpReputation(String logPrefix) {
    this(logPrefix, System::currentTimeMillis);
  }

  /**
   * The last time returned from {@link IpReputation#getSystemTimeAndAdjustForPast()}.
   */
  private long lastSystemTime = Long.MIN_VALUE;

  /**
   * Gets the current system time.  If in the past compared to the last time accessed, will adjust all times used for
   * the hourly decay and all timestamps stored in all records.
   */
  private long getSystemTimeAndAdjustForPast() {
    synchronized (lock) {
      long currentTime = timeSource.getAsLong();
      if (currentTime < lastSystemTime) {
        long diff = lastSystemTime - currentTime;
        logger.info(() -> logPrefix + "System time moved back by " + diff + " ms");
        if (lastBackgroundCleanup != Long.MIN_VALUE) {
          // Not worrying about potential overflows in extreme cases
          lastBackgroundCleanup -= diff;
        }
        if (lastBackgroundCleanupFineLogging != Long.MIN_VALUE) {
          // Not worrying about potential overflows in extreme cases
          lastBackgroundCleanupFineLogging -= diff;
        }
        for (Record record : records.values()) {
          // Not worrying about potential overflows in extreme cases
          record.lastDecayTime -= diff;
        }
      }
      lastSystemTime = currentTime;
      return currentTime;
    }
  }

  private void doBackgroundCleanupIfTime() {
    Level logLevel = null;
    StringBuilder logMe = null;
    synchronized (lock) {
      long currentTime = getSystemTimeAndAdjustForPast();
      if (lastBackgroundCleanup == Long.MIN_VALUE || (currentTime - BACKGROUND_CLEANUP_MILLIS) >= lastBackgroundCleanup) {
        if (lastBackgroundCleanupFineLogging == Long.MIN_VALUE || (currentTime - BACKGROUND_CLEANUP_LOG_MILLIS) >= lastBackgroundCleanupFineLogging) {
          logLevel = Level.FINE;
        } else {
          logLevel = Level.FINER;
        }
        int recordsRemoved = 0;
        Iterator<Record> recordIter = records.values().iterator();
        while (recordIter.hasNext()) {
          Record record = recordIter.next();
          record.doDecay(currentTime);
          if (record.badScore <= 0) {
            recordIter.remove();
            recordsRemoved++;
          }
        }
        // Set time here, logging failing should not cause immediate redo of background cleanup
        lastBackgroundCleanup = currentTime;
        if (logger.isLoggable(logLevel)) {
          if (logLevel == Level.FINE) {
            // Set time first so logs don't get spammed if failure during logging
            lastBackgroundCleanupFineLogging = currentTime;
          }
          String nl = System.lineSeparator();
          logMe = new StringBuilder();
          logMe.append(logPrefix).append("Background clean-up completed.").append(nl)
              .append("Removed ").append(recordsRemoved).append(recordsRemoved == 1 ? " record" : " records").append(nl)
              .append("Current records:");
          for (Map.Entry<InetAddress, Record> entry : records.entrySet()) {
            logMe.append(nl).append("  ").append(entry.getKey())
                .append(": badScore = ").append(entry.getValue().badScore);
          }
        }
      }
    }
    if (logMe != null) {
      assert logLevel != null;
      logger.log(logLevel, logMe.toString());
    }
  }

  /**
   * Gets a snapshot of the current reputation records with a badScore &gt; 0.
   *
   * @return Unmodifiable snapshot map from address to bad score in insertion order.
   */
  public Map<InetAddress, Integer> getRecordsSnapshot() {
    synchronized (lock) {
      doBackgroundCleanupIfTime();
      // Java 19: LinkedHashMap.newLinkedHashMap
      Map<InetAddress, Integer> snapshot = AoCollections.newLinkedHashMap(records.size());
      records.forEach((address, record) -> {
        if (record.badScore > 0) {
          snapshot.put(address, record.badScore);
        }
      });
      return Collections.unmodifiableMap(snapshot);
    }
  }

  public Connection init(InetAddress address) {
    doBackgroundCleanupIfTime();
    return new Connection(this, address);
  }

  public static class Connection {

    private enum State {
      NEW,
      REJECTED,
      ACCEPTED,
      STARTED,
      ABORT,
      FAIL,
      SUCCESS
    }

    private final IpReputation ipReputation;
    private final InetAddress address;
    private State state;
    private Record acceptRecord;
    private boolean didAcceptBadIncrement;

    private Connection(IpReputation ipReputation, InetAddress address) {
      this.ipReputation = ipReputation;
      this.address = normalize(address);
      this.state = State.NEW;
    }

    public enum AcceptResult {
      ACCEPT,
      REJECT;
      // Future might have RATE_LIMIT
    }

    /**
     * This is to be called as early as possible at connection time, ideally before SSL overhead.
     * May only be called after {@link IpReputation#init(java.net.InetAddress)}.
     *
     * <p>Whether accepted or rejected, always adds one to "bad" score, but not past the cap.
     * This is how the score can accumulate up to the {@link IpReputation#MAX_BAD_SCORE} value.
     * Tracks if bad score incremented, so later stages can conditionally decrement.
     * This protects from concurrent bursts, and allows fail-fast before SSL overhead.</p>
     *
     * @return  The action to take.  When {@link AcceptResult#REJECT}, caller is recommended
     *          to exit/close immediately (but possibly in async task/thread).
     */
    public AcceptResult accept() {
      synchronized (ipReputation.lock) {
        if (state != State.NEW) {
          throw new IllegalStateException("accept can only be called on a new connection: state = " + state);
        }
        // Lookup the record, creating a new one if needed
        long currentTime = ipReputation.getSystemTimeAndAdjustForPast();
        Record record = ipReputation.records.get(address);
        if (record == null) {
          record = new Record(currentTime);
          ipReputation.records.put(address, record);
        } else {
          record.doDecay(currentTime);
        }
        // Check before increment, to allow REJECT_BAD_SCORE concurrent attempts inclusive
        final int badScoreForDecision = record.badScore;
        boolean isReject = badScoreForDecision >= REJECT_BAD_SCORE;
        // Always increment regardless of result
        if (record.badScore < MAX_BAD_SCORE) {
          didAcceptBadIncrement = true;
          record.badScore++;
        }
        if (isReject) {
          logger.fine(() -> ipReputation.logPrefix + "Rejecting " + address + " with badScore = " + badScoreForDecision);
          state = State.REJECTED;
          return AcceptResult.REJECT;
        } else {
          logger.finer(() -> ipReputation.logPrefix + "Accepting " + address + " with badScore = " + badScoreForDecision);
          acceptRecord = record;
          state = State.ACCEPTED;
          return AcceptResult.ACCEPT;
        }
      }
    }

    /**
     * Connection lost before operating starting.
     * May only be called after {@link Connection#accept()} returns {@link AcceptResult#ACCEPT}.
     *
     * <p>Example usage: our monitoring simply does SSL connection then disconnects, never speaks VNC protocol.</p>
     *
     * <p>When aborted, remove the one from "bad" if it was incremented during {@link Connection#accept()},
     * (Future:: When good implemented: do not do anything to "good").</p>
     */
    public void abort() {
      synchronized (ipReputation.lock) {
        if (state != State.ACCEPTED) {
          throw new IllegalStateException("abort can only be called after successful accept: state = " + state);
        }
        state = State.ABORT;
        // Note: If other cleanups have removed the record, it will have a badScore of zero
        if (didAcceptBadIncrement && acceptRecord.badScore > 0) {
          acceptRecord.badScore--;
        }
        logger.finer(() -> ipReputation.logPrefix + "Abort from " + address + " with badScore = " + acceptRecord.badScore);
      }
    }

    /**
     * Indicate the action has started so {@link Connection#abort()} may no longer be performed.
     * May only be called after {@link Connection#accept()} returns {@link AcceptResult#ACCEPT}.
     *
     * <p>Don't want abort possible after operations started, bad guy could potentially use abort and timing to
     * determine if password was good without having their "bad" score increased.</p>
     */
    public void started() {
      synchronized (ipReputation.lock) {
        if (state != State.ACCEPTED) {
          throw new IllegalStateException("started can only be called after successful accept: state = " + state);
        }
        logger.finer(() -> ipReputation.logPrefix + "Auth starting for " + address);
        state = State.STARTED;
        assert acceptRecord != null : "acceptRecord remains present for later fail/success accounting";
      }
    }

    /**
     * To be called when operations fails.  After calling this, the caller should delay then exit/close.
     * May only be called after {@link Connection#started()}.
     *
     * <p>Keeps the one "bad" score from {@link Connection#accept()} if it was not capped, otherwise attempts to increase by one up to cap.</p>
     *
     * <p>If the record for this address has been removed or replaced, increments the bad score on the new record (up to the cap).</p>
     */
    public void fail() {
      synchronized (ipReputation.lock) {
        if (state != State.STARTED) {
          throw new IllegalStateException("fail can only be called after started: state = " + state);
        }
        state = State.FAIL;
        // Lookup the record, creating a new one if needed
        long currentTime = ipReputation.getSystemTimeAndAdjustForPast();
        Record record = ipReputation.records.get(address);
        if (record == null) {
          record = new Record(currentTime);
          ipReputation.records.put(address, record);
        } else {
          // Decay now, which might make room for the badScore increase
          record.doDecay(currentTime);
        }
        // If there is a replacement record, increase its bad score up to cap
        // If is acceptRecord and was capped at accept, try to increase bad score again
        // If is a new record, try to increase bad score.
        if ((record != acceptRecord || !didAcceptBadIncrement) && record.badScore < MAX_BAD_SCORE) {
          record.badScore++;
        }
        final int badScoreFinal = record.badScore;
        logger.fine(() -> ipReputation.logPrefix + "Fail from " + address + " with badScore = " + badScoreFinal);
      }
    }

    /**
     * To be called when operation succeeds.  After calling this, the caller should allow the operation to continue.
     * May only be called after {@link Connection#started()}.
     *
     * <p>Remove the one from "bad" if it was incremented during {@link Connection#accept()}.</p>
     *
     * <p>Decrements one further "bad" to account for successful operation.
     * (Future:: When good implemented: do not do second decrement and instead increment "good").</p>
     */
    public void success() {
      synchronized (ipReputation.lock) {
        if (state != State.STARTED) {
          throw new IllegalStateException("success can only be called after started: state = " + state);
        }
        state = State.SUCCESS;
        // Note: Not looking for replacement record.  If success took a very long time, so-be-it.

        // Note: If other cleanups have removed the record, it will have a badScore of zero
        if (didAcceptBadIncrement && acceptRecord.badScore > 0) {
          acceptRecord.badScore--;
        }
        // Do an addition bad decrement to compensate for not having a good score yet.
        // Future:: Later, where there is a separate "good" score, do not do the additional decrement of bad here.
        if (acceptRecord.badScore > 0) {
          acceptRecord.badScore--;
        }
        final int badScoreFinal = acceptRecord.badScore;
        logger.info(() -> ipReputation.logPrefix + "Success from " + address + " with badScore = " + badScoreFinal);
      }
    }
  }
}
