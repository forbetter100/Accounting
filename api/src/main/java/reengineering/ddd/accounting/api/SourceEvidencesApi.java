package reengineering.ddd.accounting.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import reengineering.ddd.accounting.api.representation.SourceEvidenceModel;
import reengineering.ddd.accounting.api.representation.SourceEvidenceReader;
import reengineering.ddd.accounting.api.representation.TransactionModel;
import reengineering.ddd.accounting.model.Account;
import reengineering.ddd.accounting.model.Customer;
import reengineering.ddd.accounting.model.SourceEvidence;
import reengineering.ddd.archtype.Many;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;

import static reengineering.ddd.accounting.api.ApiTemplates.accountTransactions;
import static reengineering.ddd.accounting.api.ApiTemplates.sourceEvidences;

public class SourceEvidencesApi {
    private Customer customer;

    @Inject
    private SourceEvidenceReader reader;

    public SourceEvidencesApi(Customer customer) {
        this.customer = customer;
    }

    @GET
    @Path("{evidence-id}")
    public SourceEvidenceModel findById(@PathParam("evidence-id") String id,
                                        @Context UriInfo info) {
        return customer.sourceEvidences().findByIdentity(id).map(evidence -> SourceEvidenceModel.of(customer, evidence, info))
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @GET
    public CollectionModel<SourceEvidenceModel> findAll(@Context UriInfo info, @DefaultValue("0") @QueryParam("page") int page) {
        return new Pagination<>(customer.sourceEvidences().findAll(), 40).page(page,
                evidence -> SourceEvidenceModel.simple(customer, evidence, info),
                p -> sourceEvidences(info).queryParam("page", p).build(customer.getIdentity()));
    }

    /**
     * 注by wj: http rpc风格会在customer api暴露一个add api，但restful的风格，是在资源本身去暴露api
     * 也就是说：一致性是以聚合根为中心设计的模型驱动，而api是以内部资源被暴露的风格设计的，这其实也不冲突：
     * 因为我们暴露的资源不是技术性的，而是业务上明确存在的实体，只是这些实体被聚合根这个实体管控着而已，而不代表这些实体需要对外隐瞒。
     * 并且，这个具体的内部资源，其实在uri上已经表达了对象网，因此并不是暴露全局对象。
     */
    @POST
    public Response create(String json, @Context UriInfo info) {
        SourceEvidence evidence = customer.add(reader.read(json)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_ACCEPTABLE)).description());
        return Response.created(ApiTemplates.sourceEvidence(info).build(customer.getIdentity(), evidence.getIdentity())).build();
    }
}
