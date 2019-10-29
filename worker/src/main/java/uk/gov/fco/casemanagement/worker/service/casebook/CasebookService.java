package uk.gov.fco.casemanagement.worker.service.casebook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;

import java.util.UUID;

@Service
@Slf4j
public class CasebookService {

    public String createCase(@NonNull Form form) {
        log.debug("Creating case from {}", form);

        NotarialApplication notarialApplication = new ApplicationConverter().convert(form);

        // Send to CASEBOOK

        // TODO: return casebook reference
        return UUID.randomUUID().toString();
    }
}
