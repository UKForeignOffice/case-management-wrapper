package uk.gov.fco.casemanagement.worker.service.casebook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.converter.Converter;
import uk.gov.fco.casemanagement.common.domain.Fees;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.*;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ApplicationConverter implements Converter<Form, NotarialApplication> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.UK);

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private static final Properties APPLICATION_PROPERTIES = new Properties() {{
        try {
            load(ApplicationConverter.class.getResourceAsStream("/mappings/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }};

    private static final Properties FIELD_PROPERTIES = new Properties() {{
        try {
            load(ApplicationConverter.class.getResourceAsStream("/mappings/fields.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }};

    private CasebookService casebookService;

    private DocumentUploadService documentUploadService;

    public ApplicationConverter(@NonNull CasebookService casebookService, @NonNull DocumentUploadService documentUploadService) {
        this.casebookService = casebookService;
        this.documentUploadService = documentUploadService;
    }

    @Override
    public @NonNull NotarialApplication convert(Form source) {

        Map<String, Object> properties = source.getAnswers();

        NotarialApplication notarialApplication = new NotarialApplication();
        setProperties(properties, notarialApplication);

        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = notarialApplication.getApplication();

        applyFeeServices(properties, application);

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
                    .append(CURRENCY_FORMAT.format(fees.getTotal().divide(ONE_HUNDRED)))
                    .append("\nPayment reference: ")
                    .append(fees.getPaymentReference());
        }

        application.setDescription(description.toString());

        return notarialApplication;
    }

    private void applyFeeServices(Map<String, Object> properties, Application application) {

        List<FeeService> feeServices = casebookService.getFeeServices(application.getPost(), application.getCaseType(),
                application.getSummary());

        application.setFeeServices(feeServices);

        for (FeeService feeService : feeServices) {
            for (Field field : feeService.getFields()) {
                if (FIELD_PROPERTIES.containsKey(field.getFieldName())) {
                    String[] values = FIELD_PROPERTIES.getProperty(field.getFieldName(), "").split(",");
                    String value = formatAndRemoveValue(properties, values);
                    field.setValue(value);
                }
            }
        }
    }

    private void setProperties(Map<String, Object> properties, NotarialApplication notarialApplication) {
        BeanWrapper wrappedApplication = new BeanWrapperImpl(notarialApplication);

        for (String property : APPLICATION_PROPERTIES.stringPropertyNames()) {
            String[] valueKeys = APPLICATION_PROPERTIES.getProperty(property, "").split(",");
            String value = formatAndRemoveValue(properties, valueKeys);

            if (value.length() > 0) {
                wrappedApplication.setPropertyValue(property, value);
            }
        }
    }

    private String formatAndRemoveValue(Map<String, Object> properties, String[] keys) {
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            String normalisedKey = key.toLowerCase();
            if (properties.containsKey(normalisedKey)) {
                Object value = properties.remove(normalisedKey);
                String answer;
                if (value instanceof Date) {
                    answer = DATE_FORMAT.format((Date) value);
                } else {
                    answer = value.toString();
                }
                if (isNotBlank(answer)) {
                    if (builder.length() > 0) {
                        builder.append(" ");
                    }
                    builder.append(answer);
                }
            }
        }
        return builder.length() > 0 ? builder.toString() : null;
    }
}
