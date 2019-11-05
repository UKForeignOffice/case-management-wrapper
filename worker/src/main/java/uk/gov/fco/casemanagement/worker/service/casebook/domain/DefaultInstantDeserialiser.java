package uk.gov.fco.casemanagement.worker.service.casebook.domain;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DefaultInstantDeserialiser extends InstantDeserializer<Instant> {

    public DefaultInstantDeserialiser() {
        super(
                Instant.class, DateTimeFormatter.ISO_INSTANT,
                Instant::from,
                a -> Instant.ofEpochMilli(a.value),
                a -> Instant.ofEpochSecond(a.integer, a.fraction),
                null,
                true);
    }
}
