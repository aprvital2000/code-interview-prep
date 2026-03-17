package my.interview.practice.sysdes;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterTest {

    @Test
    void testFixedWindowRateLimiter() {
        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(2, 5L);
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
    }

    @Test
    void testSlidingWindowLogRateLimiter() {
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(2, 5L);
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
    }

    @Test
    void testSlidingWindowCounterRateLimiter() {
        SlidingWindowCounterRateLimiter rateLimiter = new SlidingWindowCounterRateLimiter(2, 5L);
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
    }

    @Test
    void testTokenBucketRateLimiter() {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(2, 5L);
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
        IO.println(rateLimiter.allow());
    }

    @Test
    void testLeakyBucketRateLimiter() {
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(2, 5L);
        IO.println(rateLimiter.submit(() -> IO.println("hello")));
        IO.println(rateLimiter.submit(() -> IO.println("hello")));
        IO.println(rateLimiter.submit(() -> IO.println("hello")));
        IO.println(rateLimiter.submit(() -> IO.println("hello")));
    }

    /**
     * 1. Fixed Window Counter
     * The simplest approach. Divide time into fixed windows (e.g., 1-minute buckets) and count requests per window.
     * When the count exceeds the limit, reject requests until the next window starts.
     * Tradeoff: Vulnerable to burst traffic at window boundaries —
     * a client can make 2x the allowed requests in a short span by hammering the end of one window and the start of the next.
     */
    static class FixedWindowRateLimiter {
        private final int maxRequests;
        private final long windowDurationMs;
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private long windowStart = System.currentTimeMillis();

        public FixedWindowRateLimiter(int maxRequests, long windowDurationMs) {
            this.maxRequests = maxRequests;
            this.windowDurationMs = windowDurationMs;
        }

        public synchronized boolean allow() {
            long now = System.currentTimeMillis();

            if (now - windowStart >= windowDurationMs) {
                windowStart = now;
                requestCount.set(0);
            }

            if (requestCount.get() < maxRequests) {
                requestCount.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    /**
     * 2. Sliding Window Log
     * Stores a timestamp for every request. To check if a new request is allowed, purge timestamps older than the window and check how many remain.
     * Tradeoff: Precise, but memory-intensive — every request occupies a log entry, which is costly at high traffic volumes.
     */
    static class SlidingWindowLogRateLimiter {
        private final int maxRequests;
        private final long windowDurationMs;
        private final Deque<Long> requestLog = new ArrayDeque<>();

        public SlidingWindowLogRateLimiter(int maxRequests, long windowDurationMs) {
            this.maxRequests = maxRequests;
            this.windowDurationMs = windowDurationMs;
        }

        public synchronized boolean allow() {
            long now = System.currentTimeMillis();
            long windowStart = now - windowDurationMs;

            // Remove timestamps outside the window
            while (!requestLog.isEmpty() && requestLog.peekFirst() <= windowStart) {
                requestLog.pollFirst();
            }

            if (requestLog.size() < maxRequests) {
                requestLog.addLast(now);
                return true;
            }
            return false;
        }
    }

    /**
     * 3. Sliding Window Counter
     * A memory-efficient hybrid. It blends the current window's count with a weighted portion of the previous window's count to smooth out boundary bursts.
     * Tradeoff: An approximation — not perfectly accurate, but far more memory-efficient than the log approach while avoiding the boundary burst problem of fixed windows.
     */
    static class SlidingWindowCounterRateLimiter {
        private final int maxRequests;
        private final long windowDurationMs;
        private long currentWindowStart;
        private int currentWindowCount;
        private int previousWindowCount;

        public SlidingWindowCounterRateLimiter(int maxRequests, long windowDurationMs) {
            this.maxRequests = maxRequests;
            this.windowDurationMs = windowDurationMs;
            this.currentWindowStart = System.currentTimeMillis();
        }

        public synchronized boolean allow() {
            long now = System.currentTimeMillis();
            long elapsed = now - currentWindowStart;

            if (elapsed >= windowDurationMs * 2) {
                // Both windows are stale
                previousWindowCount = 0;
                currentWindowCount = 0;
                currentWindowStart = now;
            } else if (elapsed >= windowDurationMs) {
                // Slide the window forward
                previousWindowCount = currentWindowCount;
                currentWindowCount = 0;
                currentWindowStart += windowDurationMs;
                elapsed = now - currentWindowStart;
            }

            // Weight previous window's count by how far we are into the current window
            double previousWeight = 1.0 - ((double) elapsed / windowDurationMs);
            double weightedCount = previousWeight * previousWindowCount + currentWindowCount;

            if (weightedCount < maxRequests) {
                currentWindowCount++;
                return true;
            }
            return false;
        }
    }

    /**
     * 4. Token Bucket
     * A bucket holds up to capacity tokens. Tokens are added at a fixed refillRate.
     * Each request consumes one token. If the bucket is empty, the request is rejected.
     * Tradeoff: Naturally handles bursting — a client that hasn't made requests recently will have accumulated tokens and can burst up to the bucket capacity.
     * This is used by AWS, Stripe, and many others.
     */
    static class TokenBucketRateLimiter {
        private final int capacity;
        private final double refillRatePerMs; // tokens per millisecond
        private double tokens;
        private long lastRefillTime;

        public TokenBucketRateLimiter(int capacity, double refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerMs = refillRatePerSecond / 1000.0;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean allow() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            double tokensToAdd = (now - lastRefillTime) * refillRatePerMs;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    /**
     * 5. Leaky Bucket
     * Requests enter a queue (the "bucket") and are processed at a fixed rate.
     * Incoming requests that overflow the bucket are rejected.
     * Unlike token bucket, output is always smooth — there are no bursts on the processing side.
     * Tradeoff: Great for smoothing traffic to downstream services,
     * but it adds latency for queued requests. If your downstream system can't handle any burstiness, this is the right choice.
     */
    static class LeakyBucketRateLimiter {
        private final int capacity;
        private final long leakIntervalMs; // ms between processed requests
        private final BlockingQueue<Runnable> bucket;
        private final ScheduledExecutorService scheduler;

        public LeakyBucketRateLimiter(int capacity, double leakRatePerSecond) {
            this.capacity = capacity;
            this.leakIntervalMs = (long) (1000.0 / leakRatePerSecond);
            this.bucket = new LinkedBlockingQueue<>(capacity);
            this.scheduler = Executors.newSingleThreadScheduledExecutor();

            // Drain one request at a fixed rate
            scheduler.scheduleAtFixedRate(() -> {
                Runnable task = bucket.poll();
                if (task != null)
                    task.run();
            }, 0, leakIntervalMs, TimeUnit.MILLISECONDS);
        }

        public boolean submit(Runnable task) {
            if (bucket.size() < capacity) {
                bucket.offer(task);
                return true; // accepted, will be processed at leak rate
            }
            return false; // bucket overflow, rejected
        }

        public void shutdown() {
            scheduler.shutdown();
        }
    }
}
