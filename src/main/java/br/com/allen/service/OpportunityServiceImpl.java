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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OpportunityServiceImpl implements OpportunityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityServiceImpl.class);

    @Inject
    QuotationRepository quotationRepository;
    @Inject
    OpportunityRepository opportunityRepository;

    @Override
    public void buildOpportunity(ProposalDTO proposal) {
        LOGGER.info("Iniciando construção de oportunidade.");
        List<QuotationEntity> quotationEntities = quotationRepository.findAll().list();
        Collections.reverse(quotationEntities);
        OpportunityEntity opportunity = createOpportunity(proposal, quotationEntities);
        opportunityRepository.persist(opportunity);
        LOGGER.info("Oportunidade construída e persistida com sucesso.");
    }

    @Override
    @Transactional
    public void saveQuotation(QuotationDTO quotation) {
        LOGGER.info("Salvando cotação.");
        QuotationEntity createQuotation = new QuotationEntity();
        createQuotation.setDate(new Date());
        createQuotation.setCurrencyPrice(quotation.getCurrencyPrice());
        quotationRepository.persist(createQuotation);
        LOGGER.info("Cotação salva com sucesso.");
    }

    @Override
    public List<OpportunityDTO> generateOpportunityData() {
        LOGGER.info("Gerando dados de oportunidade.");
        List<OpportunityDTO> opportunities = new ArrayList<>();

        opportunityRepository
                .findAll()
                .stream()
                .forEach(item -> opportunities.add(OpportunityDTO.builder()
                        .proposalId(item.getProposalId())
                        .customer(item.getCustomer())
                        .priceTonne(item.getPriceTonne())
                        .lastDollarQuotation(item.getLastDollarQuotation())
                        .build()));
        LOGGER.info("Dados de oportunidade gerados com sucesso.");
        return opportunities;
    }

    @Override
    public ByteArrayInputStream generateCSVOpportunityReport() {
        LOGGER.info("Gerando relatório de oportunidades em CSV.");
        List<OpportunityDTO> opportunityList = mapOpportunityEntitiesToDTOs(opportunityRepository.findAll().list());
        ByteArrayInputStream csvReport = CSVUtils.opportunitiesToCSV(opportunityList);
        LOGGER.info("Relatório de oportunidades em CSV gerado com sucesso.");
        return csvReport;
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
