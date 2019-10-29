package uk.gov.fco.casemanagement.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import uk.gov.fco.casemanagement.api.service.MessageQueueService;
import uk.gov.fco.casemanagement.api.service.MessageQueueTimeoutException;
import uk.gov.fco.casemanagement.common.domain.Form;

import java.util.concurrent.ForkJoinPool;

import static org.glassfish.jersey.internal.guava.Preconditions.checkNotNull;

@RestController
@RequestMapping("applications")
@Slf4j
public class ApplicationController {

    private static final Long REQUEST_TIMEOUT = 30000L;

    private MessageQueueService messageQueueService;

    @Autowired
    public ApplicationController(MessageQueueService messageQueueService) {
        this.messageQueueService = checkNotNull(messageQueueService);
    }

    @PostMapping
    public DeferredResult<ResponseEntity<?>> submitForm(@RequestBody Form form) {
        log.debug("Submitting application form {}", form);

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(REQUEST_TIMEOUT);

        output.onTimeout(() -> {
            log.warn("Timeout waiting for response");
            output.setResult(ResponseEntity.accepted().build());
        });

        ForkJoinPool.commonPool().submit(() -> {
            log.trace("Processing create case in separate thread");
            try {
                String response = messageQueueService.send(form);
                output.setResult(ResponseEntity.ok(response));
            } catch (MessageQueueTimeoutException e) {
                log.warn("Message queue timeout waiting for response", e);
                output.setResult(ResponseEntity.accepted().build());
            }
        });

        log.trace("Returning deferred result");
        return output;
    }
}
