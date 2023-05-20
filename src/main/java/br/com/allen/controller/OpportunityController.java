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

import java.util.Date;
import java.util.List;

@Path("/api/opportunity")
@Authenticated
public class OpportunityController {

    @Inject
    OpportunityService opportunityService;

    @Inject
    JsonWebToken jsonWebToken;

    @GET
    @Path("/report")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response generateReport() {
        try {
            return Response
                    .ok(opportunityService.generateCSVOpportunityReport(),
                            MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = "
                            + new Date() + "--oportunidade.csv")
                    .build();
        } catch (ServerErrorException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/data")
    @RolesAllowed({"user", "manager"})
    public List<OpportunityDTO> generateReportData() {
        return opportunityService.generateOpportunityData();
    }
}
