package reengineering.ddd.accounting.api.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import reengineering.ddd.accounting.api.ApiTemplates;
import reengineering.ddd.accounting.description.CustomerDescription;
import reengineering.ddd.accounting.model.Customer;

import javax.ws.rs.core.UriInfo;

import static reengineering.ddd.accounting.api.ApiTemplates.*;


public class CustomerModel extends RepresentationModel<CustomerModel> {
    @JsonProperty
    private String id;
    /**
     * 注by wj:可以看到，虽然表现层有自己的隔离考量，但在description也就是值对象这块，并没有重新考量
     */
    @JsonUnwrapped
    private CustomerDescription description;

    public CustomerModel(Customer customer, UriInfo info) {
        this.id = customer.getIdentity();
        /**
         * 注by wj:可以看到，description也就是值对象这块，是直接返回的，并非link的方式
         */
        this.description = customer.getDescription();
        add(Link.of(customer(info).build(customer.getIdentity()).getPath(), "self"));
        add(Link.of(sourceEvidences(info).build(customer.getIdentity()).getPath(), "source-evidences"));
        customer.accounts().findAll().forEach(a ->
                add(Link.of(accountTransactions(info).build(customer.getIdentity(), a.getIdentity()).getPath(), "account-" + a.getIdentity() + "-transactions")));
    }
}
