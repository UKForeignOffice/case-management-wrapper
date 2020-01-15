package uk.gov.fco.casemanagement.api.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import uk.gov.fco.casemanagement.api.domain.FormSubmissionResult;
import uk.gov.fco.casemanagement.api.service.MessageQueueService;
import uk.gov.fco.casemanagement.api.service.MessageQueueTimeoutException;
import uk.gov.fco.casemanagement.common.domain.Form;

import java.util.concurrent.ForkJoinPool;

import static org.glassfish.jersey.internal.guava.Preconditions.checkNotNull;

@RestController
@RequestMapping("applications")
@Slf4j
@OpenAPIDefinition()
public class ApplicationController {

    private static final Long REQUEST_TIMEOUT = 30000L;

    private MessageQueueService messageQueueService;

    @Autowired
    public ApplicationController(MessageQueueService messageQueueService) {
        this.messageQueueService = checkNotNull(messageQueueService);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(
                    description = "New case created from form data.",
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    implementation = FormSubmissionResult.class))),
            @ApiResponse(
                    description = "Form data has been accepted but case creation is delayed. No need to resubmit.",
                    responseCode = "202")
    })
    @Operation(
            summary = "Submit form",
            description = "Returns the new case reference."
    )
    public DeferredResult<ResponseEntity<?>> submitForm(
            @Parameter(
                    name = "form",
                    description = "The form to submit",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    implementation = Form.class)))
            @RequestBody Form form) {
        log.debug("Submitting application form {}", form);

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(REQUEST_TIMEOUT);

        output.onTimeout(() -> {
            log.warn("Timeout waiting for response");
            output.setResult(ResponseEntity.accepted().build());
        });

        ForkJoinPool.commonPool().submit(() -> {
            log.trace("Processing create case in separate thread");
            try {
                String reference = messageQueueService.send(form);
                output.setResult(ResponseEntity.ok(new FormSubmissionResult(reference)));
            } catch (MessageQueueTimeoutException e) {
                log.warn("Message queue timeout waiting for response", e);
                output.setResult(ResponseEntity.accepted().build());
            } catch (Exception e) {
                log.error("Error submitting message to queue", e);
                output.setErrorResult(e);
            }
        });

        log.trace("Returning deferred result");
        return output;
    }
}
