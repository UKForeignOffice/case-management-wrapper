package uk.gov.fco.casemanagement.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import uk.gov.fco.casemanagement.api.service.MessageQueueService;
import uk.gov.fco.casemanagement.api.service.MessageQueueTimeoutException;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import static org.glassfish.jersey.internal.guava.Preconditions.checkNotNull;

@RestController
@RequestMapping("cases")
public class CaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

    private static final Long REQUEST_TIMEOUT = 30000L;

    private MessageQueueService messageQueueService;

    @Autowired
    public CaseController(MessageQueueService messageQueueService) {
        this.messageQueueService = checkNotNull(messageQueueService);
    }

    @PostMapping
    public DeferredResult<ResponseEntity<?>> createCase(@RequestBody Map<?, ?> id) {
        LOGGER.debug("Creating case");

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(REQUEST_TIMEOUT);

        output.onTimeout(() -> {
            LOGGER.warn("Timeout waiting for response");
            output.setResult(ResponseEntity.accepted().build());
        });

        ForkJoinPool.commonPool().submit(() -> {
            LOGGER.trace("Processing create case in separate thread");
            try {
                String response = messageQueueService.send("12345");
                output.setResult(ResponseEntity.ok(response));
            } catch (MessageQueueTimeoutException e) {
                LOGGER.warn("Message queue timeout waiting for response", e);
                output.setResult(ResponseEntity.accepted().build());
            }
        });

        LOGGER.trace("Returning deferred result");
        return output;
    }
}
