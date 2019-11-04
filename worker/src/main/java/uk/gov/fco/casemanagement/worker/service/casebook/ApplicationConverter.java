package uk.gov.fco.casemanagement.worker.service.casebook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Address;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Applicant;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Attachment;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadServiceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ApplicationConverter implements Converter<Form, NotarialApplication> {

    private DocumentUploadService documentUploadService;

    public ApplicationConverter(@NonNull DocumentUploadService documentUploadService) {
        this.documentUploadService = documentUploadService;
    }

    @Override
    public NotarialApplication convert(Form source) {

        Map<String, String> properties = source.getAnswers();

        Applicant applicant = convertApplicant(properties);
        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = convertApplication(properties);

        if (!properties.isEmpty()) {
            // Load any attachments and format any properties not mapped to CASEBOOK into description field.
            StringBuilder description = new StringBuilder();

            source.getQuestions().stream()
                    .filter(question -> question.getFields().stream()
                            .anyMatch(field -> properties.containsKey(field.getId())))
                    .forEach(question -> {
                        StringBuilder answers = new StringBuilder();
                        question.getFields().forEach(field -> {
                            if ("file".equals(field.getType())) {
                                try {
                                    String fileLocation = field.getAnswer();
                                    String fileName = null;
                                    String fileExtension = null;
                                    int i = fileLocation.lastIndexOf('.');
                                    int j = fileLocation.lastIndexOf('/');
                                    if (i > 0) {
                                        fileName = fileLocation.substring(j + 1, i);
                                        fileExtension = fileLocation.substring(i + 1);
                                    }
                                    String fileData = documentUploadService.getFileAsBase64(new URL(fileLocation));
                                    application.addAttachment(new Attachment(fileName, fileData, fileExtension));
                                } catch (DocumentUploadServiceException e) {
                                    throw new RuntimeException("Error getting file data for form", e);
                                } catch (MalformedURLException e) {
                                    log.warn("Invalid file URL provided in form data", e);
                                }
                            } else if (properties.containsKey(field.getId())) {
                                answers.append(field.getTitle())
                                        .append(": ")
                                        .append(field.getAnswer())
                                        .append("\n");
                            }
                        });
                        if (answers.length() > 0) {
                            description.append(question.getQuestion())
                                    .append("\n")
                                    .append(answers)
                                    .append("\n");
                        }
                    });

            application.setDescription(description.toString());
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
        StringBuilder value = new StringBuilder();
        for (String key : keys) {
            if (properties.containsKey(key)) {
                String answer = properties.remove(key);
                if (isNotBlank(answer)) {
                    if (value.length() > 0) {
                        value.append(" ");
                    }
                    value.append(answer);
                }
            }
        }
        if (value.length() > 0) {
            setter.call(value.toString());
        }
    }

    @FunctionalInterface
    interface SetterFunction {
        void call(String value);
    }
}
