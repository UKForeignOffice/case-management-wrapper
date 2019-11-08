package uk.gov.fco.casemanagement.worker.service.casebook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import uk.gov.fco.casemanagement.common.domain.Fees;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Address;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Applicant;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.Attachment;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.NotarialApplication;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadServiceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ApplicationConverter implements Converter<Form, NotarialApplication> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.UK);

    private DocumentUploadService documentUploadService;

    public ApplicationConverter(@NonNull DocumentUploadService documentUploadService) {
        this.documentUploadService = documentUploadService;
    }

    @Override
    public NotarialApplication convert(Form source) {

        Map<String, Object> properties = source.getAnswers();

        Applicant applicant = convertApplicant(properties);
        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = convertApplication(properties);

        StringBuilder description = new StringBuilder();

        if (!properties.isEmpty()) {
            // Load any attachments and format any properties not mapped to CASEBOOK into description field.
            source.getQuestions().stream()
                    .filter(question -> question.getFields().stream()
                            .anyMatch(field -> properties.containsKey(field.getId())))
                    .forEach(question -> {
                        StringBuilder answers = new StringBuilder();
                        question.getFields().forEach(field -> {
                            if ("file".equals(field.getType())) {
                                try {
                                    String fileLocation = (String) field.getAnswer();
                                    String fileName = null;
                                    String fileExtension = null;
                                    int i = fileLocation.lastIndexOf('.');
                                    int j = fileLocation.lastIndexOf('/');
                                    if (i > 0) {
                                        fileName = fileLocation.substring(j + 1);
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
        }

        if (source.getFees() != null) {
            Fees fees = source.getFees();
            description
                    .append("\nAmount paid: ")
                    .append(CURRENCY_FORMAT.format(fees.getTotal().doubleValue()))
                    .append("\nPayment reference: ")
                    .append(fees.getPaymentReference());
        }

        application.setDescription(description.toString());

        return new NotarialApplication(
                applicant,
                application
        );
    }

    private uk.gov.fco.casemanagement.worker.service.casebook.domain.Application convertApplication(Map<String, Object> properties) {
        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = new uk.gov.fco.casemanagement.worker.service.casebook.domain.Application();
        setAndRemoveStringValue(properties, application::setCaseType, "casetype");
        setAndRemoveStringValue(properties, application::setCustomerInsightConsent, "customerinsightconsent");
        setAndRemoveStringValue(properties, application::setMarriageCategory, "marriagecategory");
        setAndRemoveStringValue(properties, application::setPost, "post");
        setAndRemoveStringValue(properties, application::setReasonForBeingOverseas, "reasonforbeingoverseas");
        setAndRemoveStringValue(properties, application::setSummary, "summary");

        return application;
    }

    private Applicant convertApplicant(Map<String, Object> properties) {
        Address address = new Address();
        setAndRemoveStringValue(properties, address::setCompanyName, "companyname");
        setAndRemoveStringValue(properties, address::setCountry, "country");
        setAndRemoveStringValue(properties, address::setDistrict, "district");
        setAndRemoveStringValue(properties, address::setFlatNumber, "flatnumber");
        setAndRemoveStringValue(properties, address::setHouseNumber, "housenumber");
        setAndRemoveStringValue(properties, address::setPostcode, "postcode");
        setAndRemoveStringValue(properties, address::setPremises, "premises");
        setAndRemoveStringValue(properties, address::setRegion, "region");
        setAndRemoveStringValue(properties, address::setStreet, "street");
        setAndRemoveStringValue(properties, address::setTown, "town");

        Applicant applicant = new Applicant();
        setAndRemoveStringValue(properties, applicant::setCityOfBirth, "cityofbirth");
        setAndRemoveStringValue(properties, applicant::setCountryOfBirth, "countryofbirth");
        setAndRemoveDateValue(properties, applicant::setDateOfBirth, "dateofbirth");
        setAndRemoveStringValue(properties, applicant::setEmail, "emailaddress");
        setAndRemoveStringValue(properties, applicant::setEthnicity, "ethnicity");
        setAndRemoveStringValue(properties, applicant::setEveningTelephone, "eveningtelephone");
        setAndRemoveStringValue(properties, applicant::setForenames, "firstname", "middlename", "forenames");
        setAndRemoveStringValue(properties, applicant::setLanguage, "language");
        setAndRemoveStringValue(properties, applicant::setMobileTelephone, "mobiletelephone");
        setAndRemoveStringValue(properties, applicant::setNationality, "nationality");
        setAndRemoveStringValue(properties, applicant::setPrimaryTelephone, "primarytelephone");
        setAndRemoveStringValue(properties, applicant::setReference, "reference");
        setAndRemoveStringValue(properties, applicant::setSecondNationality, "secondnationality");
        setAndRemoveStringValue(properties, applicant::setSurname, "lastname", "surname");
        setAndRemoveStringValue(properties, applicant::setTitle, "title");

        applicant.setAddress(address);

        return applicant;
    }

    private void setAndRemoveStringValue(Map<String, Object> properties, SetterFunction setter, String... keys) {
        StringBuilder value = new StringBuilder();
        for (String key : keys) {
            if (properties.containsKey(key)) {
                String answer = (String) properties.remove(key);
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

    private void setAndRemoveDateValue(Map<String, Object> properties, SetterFunction setter, String key) {
        if (properties.containsKey(key)) {
            Date answer = (Date) properties.remove(key);
            if (answer != null) {
                setter.call(DATE_FORMAT.format(answer));
            }
        }
    }

    @FunctionalInterface
    interface SetterFunction {
        void call(String value);
    }
}
