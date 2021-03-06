package uk.gov.fco.casemanagement.worker.service.casebook;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import uk.gov.fco.casemanagement.common.domain.FeeDetail;
import uk.gov.fco.casemanagement.common.domain.Fees;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.*;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.field.Field;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class ApplicationConverter implements Converter<Form, NotarialApplication> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

    ApplicationConverter(@NonNull CasebookService casebookService, @NonNull DocumentUploadService documentUploadService) {
        this.casebookService = casebookService;
        this.documentUploadService = documentUploadService;
    }

    @Override
    public @NonNull NotarialApplication convert(Form source) {

        AccessAwareMap<String, Object> properties = new AccessAwareMap<>(source.getAnswers());

        NotarialApplication notarialApplication = new NotarialApplication();
        setProperties(properties, notarialApplication);

        uk.gov.fco.casemanagement.worker.service.casebook.domain.Application application = notarialApplication.getApplication();

        applyFeeServices(properties, application, source.getFees());

        StringBuilder description = new StringBuilder();

        if (!properties.isEmpty()) {
            // Load any attachments and format any properties not mapped to CASEBOOK into description field.
            source.getQuestions().stream()
                    .filter(question -> question.getFields().stream()
                            .anyMatch(field -> properties.containsKey(field.getKey()) && !properties.wasAccessed(field.getKey())))
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
                            } else if (properties.containsKey(field.getKey())) {
                                if (!field.getTitle().equals(question.getQuestion())) {
                                    answers.append(field.getTitle())
                                            .append(": ");
                                }
                                answers.append(field.getAnswer())
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
                    .append(CURRENCY_FORMAT.format(fees.getTotal().divide(ONE_HUNDRED, 2, RoundingMode.CEILING)))
                    .append("\nPayment reference: ")
                    .append(fees.getPaymentReference());
        }

        application.setDescription(description.toString());

        return notarialApplication;
    }

    private void applyFeeServices(Map<String, Object> properties, Application application, Fees fees) {
        if (fees == null) {
            return;
        }

        List<FeeService> feeServices = casebookService.getFeeServices(
                fees.getDetails().stream()
                        .map(FeeDetail::getDescription)
                        .collect(toList()));

        application.setSummary(fees.getDetails().stream()
                .map(FeeDetail::getDescription)
                .reduce((s, s2) -> s + ", " + s2)
                .orElse(null));

        application.setFeeServices(feeServices);

        for (FeeService feeService : feeServices) {
            for (Iterator<Field> iterator = feeService.getFields().iterator(); iterator.hasNext(); ) {
                Field field = iterator.next();
                if (FIELD_PROPERTIES.containsKey(field.getFieldName())) {
                    String expression = FIELD_PROPERTIES.getProperty(field.getFieldName());
                    if (expression != null) {
                        String value = formatValue(properties, expression);
                        if (value != null) {
                            field.setValue(value);
                        }
                    }
                } else {
                    String value = formatValue(properties, field.getFieldName());
                    if (value != null) {
                        field.setValue(value);
                    }
                }
                // Will cause a validation error if we send null values to CASEBOOK
                if (field.getValue() == null) {
                    iterator.remove();
                }
            }
        }
    }

    private void setProperties(Map<String, Object> properties, NotarialApplication notarialApplication) {
        BeanWrapper wrappedApplication = new BeanWrapperImpl(notarialApplication);

        for (String property : APPLICATION_PROPERTIES.stringPropertyNames()) {
            String expression = APPLICATION_PROPERTIES.getProperty(property);
            if (expression != null) {
                String value = formatValue(properties, expression);
                if (value != null) {
                    wrappedApplication.setPropertyValue(property, value);
                }
            }
        }
    }

    private String formatValue(Map<String, Object> properties, String expression) {
        if (expression.startsWith("#{")) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression.substring(2, expression.length() - 1));
            Object value = exp.getValue(properties);
            if (value != null) {
                return value.toString();
            }
            return null;
        }

        String[] keys = expression.split(",");
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            String normalisedKey = key.toLowerCase();
            if (properties.containsKey(normalisedKey)) {
                Object value = properties.get(normalisedKey);
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
