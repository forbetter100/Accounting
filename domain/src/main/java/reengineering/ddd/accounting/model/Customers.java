package reengineering.ddd.accounting.model;

import java.util.Optional;

/**
 * 这玩意本质就是customer这个聚合根的repository，只是换了个更加业务的名字
 */
public interface Customers {
    Optional<Customer> findById(String id);
}
