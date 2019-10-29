package uk.gov.fco.casemanagement.common.domain;

import java.util.HashMap;
import java.util.Locale;

public class LocalisedString extends HashMap<Locale, String> {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public String value() {
        return value(DEFAULT_LOCALE);
    }

    public String value(Locale locale) {
        if (containsKey(locale)) {
            return get(locale);
        }
        return get(DEFAULT_LOCALE);
    }
}
