package my.interview.practice.sysdes;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Static Load Balancing Algorithms
 *      1. Round Robin Load Balancing Algorithm
 *      2. Weighted Round Robin Load Balancing Algorithm
 *      3. Source IP Hash Load Balancing Algorithm
 * Dynamic Load Balancing Algorithms
 *      1. Least Connection Method Load Balancing Algorithm
 *      2. Least Response Time Method Load Balancing Algorithm
 *      3. Resource-based Load Balancing Algorithm
 */
public class LoadBalancerTest {

    @ParameterizedTest
    @CsvSource(value = {"[1,2,3,4,5]"}, delimiter = ':')
    public void testRoundRobinLoadBalancer(@ConvertWith(IterableConverter.class) String[] servers) {
        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(Arrays
                .stream(servers)
                .toList());
        assertEquals("1", loadBalancer.getNextServer());
        assertEquals("2", loadBalancer.getNextServer());
        assertEquals("3", loadBalancer.getNextServer());
        assertEquals("4", loadBalancer.getNextServer());
        assertEquals("5", loadBalancer.getNextServer());
        assertEquals("1", loadBalancer.getNextServer());
    }

    /**
     * 1. Round Robin Load Balancing Algorithm
     * Round Robin is a simple static load balancing technique that distributes incoming requests to servers
     * in a fixed sequential or rotational order. It is commonly used due to its ease of implementation.
     * Requests are assigned to servers one by one in a circular manner.
     * Does not consider current server load, which may cause some servers to become overloaded.
     */
    static class RoundRobinLoadBalancer {
        private final List<String> servers;
        private final AtomicInteger currentIndex = new AtomicInteger(0);

        public RoundRobinLoadBalancer(List<String> servers) {
            if (servers == null || servers.isEmpty())
                throw new IllegalArgumentException("List empty");
            this.servers = servers;
        }

        public String getNextServer() {
            // Use modulus to wrap around the list, AtomicInteger handles thread safety
            int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());
            return servers.get(index);
        }
    }

    /**
     * 2. Weighted Round Robin Load Balancing Algorithm
     * Weighted Round Robin is a static load balancing technique similar to Round Robin,
     * but it distributes requests based on assigned weight values that represent each server’s capacity.
     * Servers with higher weights receive a larger share of requests.
     * Requests are distributed in a cyclic manner, proportional to each server’s weight.
     */

}
