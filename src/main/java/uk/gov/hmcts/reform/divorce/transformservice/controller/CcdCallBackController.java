package uk.gov.hmcts.reform.divorce.transformservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.divorce.notifications.service.EmailService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.transformservice.CCDCallbackResponse;
import uk.gov.hmcts.reform.divorce.transformservice.service.UpdateService;

import java.util.ArrayList;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(path = "/caseprogression")
@Api(value = "Transformation API", consumes = "application/json", produces = "application/json")
public class CcdCallBackController {

    @Autowired
    private UpdateService updateService;
    @Autowired
    private EmailService emailService;

    @PostMapping(path = "/petition-issued", consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate and add a pdf of the petition to the case")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A pdf of the petition has been generated and added to the case",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
        })
    public ResponseEntity<CCDCallbackResponse> addPdf(
        @RequestHeader(value = "Authorization", required = false) String authorizationToken,
        @RequestBody @ApiParam("CaseData") CreateEvent caseDetailsRequest) {

        CoreCaseData coreCaseData = updateService.addPdf(caseDetailsRequest, authorizationToken);
        return ResponseEntity.ok(new CCDCallbackResponse(coreCaseData, new ArrayList<>(), new ArrayList<>()));
    }

    @PostMapping(path = "/petition-rejected",
        consumes = MediaType.APPLICATION_JSON,
        produces = MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generate and dispatch a notification email to the petition when the application is rejected")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "A pdf of the petition has been generated and added to the case",
            response = CCDCallbackResponse.class),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    public ResponseEntity<CCDCallbackResponse> petitionIssued(
        @RequestHeader(value = "Authorization", required = false) String authorizationToken,
        @RequestBody @ApiParam("CaseData") CreateEvent caseDetailsRequest) {

        String petitionerEmail = caseDetailsRequest.getCaseDetails().getCaseData().getD8PetitionerEmail();

        if (StringUtils.isNotBlank(petitionerEmail)) {
            emailService.sendRejectionNotificationEmail(petitionerEmail);
        }

        CoreCaseData coreCaseData = updateService.addPdf(caseDetailsRequest, authorizationToken);
        return ResponseEntity.ok(new CCDCallbackResponse(coreCaseData, new ArrayList<>(), new ArrayList<>()));
    }
}
