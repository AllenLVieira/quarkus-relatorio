package br.com.allen.messaging;

import br.com.allen.dto.ProposalDTO;
import br.com.allen.dto.QuotationDTO;
import br.com.allen.service.OpportunityService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEvents.class);

    @Inject
    OpportunityService opportunityService;

    @Incoming("proposal")
    @Transactional
    public void receiveProposal(ProposalDTO proposal) {
        LOGGER.info("Recebendo proposta do tópico Kafka");
        opportunityService.buildOpportunity(proposal);
    }

    @Incoming("quotation")
    @Blocking
    public void receiveQuotation(QuotationDTO quotation) {
        LOGGER.info("Recebendo cotação do tópico Kafka");
        opportunityService.saveQuotation(quotation);
    }
}
