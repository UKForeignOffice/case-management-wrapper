package uk.gov.fco.casemanagement.worker.service.casebook;

import org.springframework.core.convert.converter.Converter;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Address;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Applicant;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;

import java.time.Instant;
import java.util.Map;

public class ApplicationConverter implements Converter<Form, NotarialApplication> {

    @Override
    public NotarialApplication convert(Form source) {

        Map<String, String> properties = source.getAnswers();

        Applicant applicant = convertApplicant(properties);
        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = convertApplication(properties);

        if (!properties.isEmpty()) {
            // files load, convert to bytes and set

            // format into description anything else
//            source.getQuestions().forEach(question -> {
//
//            });
        }

        return new NotarialApplication(
                Instant.now(), // TODO: should come from Application
                applicant,
                application
        );
    }

    private uk.gov.fco.casemanagement.worker.service.casebook.domain.Application convertApplication(Map<String, String> properties) {
        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = new uk.gov.fco.casemanagement.worker.service.casebook.domain.Application();
        setAndRemoveValue(properties, application::setCasetype, "caseType");
        setAndRemoveValue(properties, application::setCustomerInsightConsent, "customerInsightConsent");
        setAndRemoveValue(properties, application::setMarriageCategory, "marriageCategory");
        setAndRemoveValue(properties, application::setPost, "post");
        setAndRemoveValue(properties, application::setReasonForBeingOverseas, "reasonForBeingOverseas");
        setAndRemoveValue(properties, application::setSummary, "summary");

        return application;
    }

    private Applicant convertApplicant(Map<String, String> properties) {
        Address address = new Address();
        setAndRemoveValue(properties, address::setCompanyName, "companyName");
        setAndRemoveValue(properties, address::setCountry, "country");
        setAndRemoveValue(properties, address::setDistrict, "district");
        setAndRemoveValue(properties, address::setFlatNumber, "flatNumber");
        setAndRemoveValue(properties, address::setHouseNumber, "houseNumber");
        setAndRemoveValue(properties, address::setPostcode, "postcode");
        setAndRemoveValue(properties, address::setPremises, "premises");
        setAndRemoveValue(properties, address::setRegion, "region");
        setAndRemoveValue(properties, address::setStreet, "street");
        setAndRemoveValue(properties, address::setTown, "town");

        Applicant applicant = new Applicant();
        setAndRemoveValue(properties, applicant::setCityOfBirth, "cityOfBirth");
        setAndRemoveValue(properties, applicant::setCountryOfBirth, "countryOfBirth");
        setAndRemoveValue(properties, applicant::setDateOfBirth, "dateOfBirth");
        setAndRemoveValue(properties, applicant::setEmail, "emailAddress");
        setAndRemoveValue(properties, applicant::setEthnicity, "ethnicity");
        setAndRemoveValue(properties, applicant::setEveningTelephone, "eveningTelephone");
        setAndRemoveValue(properties, applicant::setForenames, "firstName", "middleName");
        setAndRemoveValue(properties, applicant::setLanguage, "language");
        setAndRemoveValue(properties, applicant::setMobileTelephone, "mobileTelephone");
        setAndRemoveValue(properties, applicant::setNationality, "nationality");
        setAndRemoveValue(properties, applicant::setPrimaryTelephone, "primaryTelephone");
        setAndRemoveValue(properties, applicant::setReference, "reference");
        setAndRemoveValue(properties, applicant::setSecondNationality, "secondNationality");
        setAndRemoveValue(properties, applicant::setSurname, "lastName");
        setAndRemoveValue(properties, applicant::setTitle, "title");

        applicant.setAddress(address);

        return applicant;
    }

    private void setAndRemoveValue(Map<String, String> properties, SetterFunction setter, String... keys) {
        for (String key : keys) {
            if (properties.containsKey(key)) {
                setter.call(properties.remove(key));
                break;
            }
        }
    }

    @FunctionalInterface
    interface SetterFunction {
        void call(String value);
    }
}
