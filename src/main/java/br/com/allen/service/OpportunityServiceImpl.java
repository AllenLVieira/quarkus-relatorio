package br.com.allen.service;

import br.com.allen.dto.OpportunityDTO;
import br.com.allen.dto.ProposalDTO;
import br.com.allen.dto.QuotationDTO;
import br.com.allen.entity.OpportunityEntity;
import br.com.allen.entity.QuotationEntity;
import br.com.allen.repository.OpportunityRepository;
import br.com.allen.repository.QuotationRepository;
import br.com.allen.utils.CSVUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OpportunityServiceImpl implements OpportunityService {
    @Inject
    QuotationRepository quotationRepository;
    @Inject
    OpportunityRepository opportunityRepository;

    @Override
    public void buildOpportunity(ProposalDTO proposal) {
        List<QuotationEntity> quotationEntities = quotationRepository.findAll().list();
        Collections.reverse(quotationEntities);
        OpportunityEntity opportunity = createOpportunity(proposal, quotationEntities);
        opportunityRepository.persist(opportunity);
    }

    @Override
    @Transactional
    public void saveQuotation(QuotationDTO quotation) {
        QuotationEntity createQuotation = new QuotationEntity();
        createQuotation.setDate(new Date());
        createQuotation.setCurrencyPrice(quotation.getCurrencyPrice());
        quotationRepository.persist(createQuotation);
    }

    @Override
    public List<OpportunityDTO> generateOpportunityData() {
        return null;
    }

    @Override
    public ByteArrayInputStream generateCSVOpportunityReport() {
        List<OpportunityDTO> opportunityList = mapOpportunityEntitiesToDTOs(opportunityRepository.findAll().list());
        return CSVUtils.opportunitiesToCSV(opportunityList);
    }

    private OpportunityEntity createOpportunity(ProposalDTO proposal, List<QuotationEntity> quotationEntities) {
        OpportunityEntity opportunity = new OpportunityEntity();
        opportunity.setDate(new Date());
        opportunity.setProposalId(proposal.getProposalId());
        opportunity.setCustomer(proposal.getCustomer());
        opportunity.setPriceTonne(proposal.getPriceTonne());
        if (!quotationEntities.isEmpty()) {
            opportunity.setLastDollarQuotation(quotationEntities.get(0).getCurrencyPrice());
        }
        return opportunity;
    }

    private OpportunityDTO mapOpportunityEntityToDTO(OpportunityEntity opportunityEntity) {
        return OpportunityDTO.builder()
                .proposalId(opportunityEntity.getProposalId())
                .priceTonne(opportunityEntity.getPriceTonne())
                .customer(opportunityEntity.getCustomer())
                .lastDollarQuotation(opportunityEntity.getLastDollarQuotation())
                .build();
    }

    private List<OpportunityDTO> mapOpportunityEntitiesToDTOs(List<OpportunityEntity> opportunityEntities) {
        return opportunityEntities.stream()
                .map(this::mapOpportunityEntityToDTO)
                .collect(Collectors.toList());
    }
}
