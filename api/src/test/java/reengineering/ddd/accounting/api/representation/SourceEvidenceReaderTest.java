package reengineering.ddd.accounting.api.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reengineering.ddd.accounting.description.basic.Amount;
import reengineering.ddd.accounting.description.basic.Currency;
import reengineering.ddd.accounting.description.basic.Ref;
import reengineering.ddd.accounting.description.SalesSettlementDescription;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SourceEvidenceReaderTest {
    private SourceEvidenceReader reader;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void before() {
        reader = new SourceEvidenceJsonReader(mapper);
    }

    @Test
    public void should_read_sales_settlement_from_json() {
        SourceEvidenceRequest<?> request = reader.read("{\"type\":\"sales-settlement\",\"order\":{\"id\":\"ORD-001\"},\"total\":{\"value\":1000.00,\"currency\":\"CNY\"},\"account\":{\"id\":\"CASH-001\"},\"details\":[{\"amount\":{\"value\":1000.00,\"currency\":\"CNY\"}}]}\n").get();

        SalesSettlementDescription description = (SalesSettlementDescription) request.description();

        assertEquals(new Ref<>("ORD-001"), description.order());
        assertEquals(new Amount(new BigDecimal("1000.00"), Currency.CNY), description.total());
        assertEquals(new Ref<>("CASH-001"), description.account());

        List<SalesSettlementDescription.Detail> details = description.details();
        assertEquals(1, details.size());
        assertEquals(new Amount(new BigDecimal("1000.00"), Currency.CNY), details.get(0).getAmount());
    }
}
