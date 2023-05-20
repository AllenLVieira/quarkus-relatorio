package br.com.allen.service;

import br.com.allen.dto.OpportunityDTO;
import br.com.allen.dto.ProposalDTO;
import br.com.allen.dto.QuotationDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.ByteArrayInputStream;
import java.util.List;

@ApplicationScoped
public interface OpportunityService {

    void buildOpportunity(ProposalDTO proposal);

    void saveQuotation(QuotationDTO quotation);

    List<OpportunityDTO> generateOpportunityData();

    ByteArrayInputStream generateCSVOpportunityReport();
}
