package br.com.allen.controller;

import br.com.allen.dto.OpportunityDTO;
import br.com.allen.service.OpportunityService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Path("/api/opportunity")
@Authenticated
public class OpportunityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityController.class);


    @Inject
    OpportunityService opportunityService;

    @Inject
    JsonWebToken jsonWebToken;

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generateReport() {
        try {
            LOGGER.info("Recebida solicitação de geração de relatório.");
            Response.ResponseBuilder responseBuilder = Response.ok(opportunityService.generateCSVOpportunityReport(),
                            MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = " + new Date() + "--oportunidade.csv");
            LOGGER.info("Relatório gerado com sucesso.");
            return responseBuilder.build();
        } catch (ServerErrorException e) {
            LOGGER.error("Erro durante a geração do relatório.", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/data")
    @RolesAllowed({"user", "manager"})
    public List<OpportunityDTO> generateReportData() {
        LOGGER.info("Recebida solicitação de geração de dados de oportunidade.");
        List<OpportunityDTO> opportunityData = opportunityService.generateOpportunityData();
        LOGGER.info("Dados de oportunidade gerados com sucesso.");
        return opportunityData;
    }
}
