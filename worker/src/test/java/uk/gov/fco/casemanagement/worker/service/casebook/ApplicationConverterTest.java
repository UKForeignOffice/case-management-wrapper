package uk.gov.fco.casemanagement.worker.service.casebook;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.fco.casemanagement.common.domain.Fees;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.*;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.field.Field;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationConverterTest {

    @InjectMocks
    private ApplicationConverter applicationConverter;

    @Mock
    private DocumentUploadService documentUploadService;

    @Mock
    private CasebookService casebookService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldConvertApplicantAddress() {

        final String companyName = "companyName";
        final String country = "country";
        final String district = "district";
        final String flatNumber = "flatNumber";
        final String houseNumber = "houseNumber";
        final String postcode = "postcode";
        final String premises = "premises";
        final String region = "region";
        final String street = "street";
        final String town = "town";

        Form form = new FormBuilder()
                .withQuestion("companyName", companyName)
                .withQuestion("country", country)
                .withQuestion("district", district)
                .withQuestion("flatNumber", flatNumber)
                .withQuestion("houseNumber", houseNumber)
                .withQuestion("postcode", postcode)
                .withQuestion("premises", premises)
                .withQuestion("region", region)
                .withQuestion("street", street)
                .withQuestion("town", town)
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Applicant applicant = notarialApplication.getApplicant();

        assertThat(applicant, notNullValue());

        Address address = applicant.getAddress();

        assertThat(address.getCompanyName(), equalTo(companyName));
        assertThat(address.getCountry(), equalTo(country));
        assertThat(address.getDistrict(), equalTo(district));
        assertThat(address.getFlatNumber(), equalTo(flatNumber));
        assertThat(address.getHouseNumber(), equalTo(houseNumber));
        assertThat(address.getPostcode(), equalTo(postcode));
        assertThat(address.getPremises(), equalTo(premises));
        assertThat(address.getRegion(), equalTo(region));
        assertThat(address.getStreet(), equalTo(street));
        assertThat(address.getTown(), equalTo(town));
    }

    @Test
    public void shouldConvertApplicant() {

        final String cityOfBirth = "cityOfBirth";
        final String countryOfBirth = "countryOfBirth";
        final String dateOfBirth = "1980-10-01";
        final String emailAddress = "emailAddress";
        final String ethnicity = "ethnicity";
        final String eveningTelephone = "eveningTelephone";
        final String firstName = "firstName";
        final String middleName = "middleName";
        final String language = "language";
        final String mobileTelephone = "mobileTelephone";
        final String nationality = "nationality";
        final String primaryTelephone = "primaryTelephone";
        final String reference = "reference";
        final String secondNationality = "secondNationality";
        final String lastName = "lastName";
        final String title = "title";

        Form form = new FormBuilder()
                .withQuestion("cityOfBirth", cityOfBirth)
                .withQuestion("countryOfBirth", countryOfBirth)
                .withQuestion(new QuestionBuilder()
                        .withField("dateOfBirth", "dateOfBirth", "date", dateOfBirth)
                        .build())
                .withQuestion("emailAddress", emailAddress)
                .withQuestion("ethnicity", ethnicity)
                .withQuestion("eveningTelephone", eveningTelephone)
                .withQuestion("firstName", firstName)
                .withQuestion("middleName", middleName)
                .withQuestion("language", language)
                .withQuestion("mobileTelephone", mobileTelephone)
                .withQuestion("nationality", nationality)
                .withQuestion("primaryTelephone", primaryTelephone)
                .withQuestion("reference", reference)
                .withQuestion("secondNationality", secondNationality)
                .withQuestion("lastName", lastName)
                .withQuestion("title", title)
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Applicant applicant = notarialApplication.getApplicant();

        assertThat(applicant, notNullValue());
        assertThat(applicant.getCityOfBirth(), equalTo(cityOfBirth));
        assertThat(applicant.getCountryOfBirth(), equalTo(countryOfBirth));
        assertThat(applicant.getDateOfBirth(), equalTo("01/10/1980"));
        assertThat(applicant.getEmail(), equalTo(emailAddress));
        assertThat(applicant.getEthnicity(), equalTo(ethnicity));
        assertThat(applicant.getEveningTelephone(), equalTo(eveningTelephone));
        assertThat(applicant.getForenames(), equalTo(firstName + " " + middleName));
        assertThat(applicant.getLanguage(), equalTo(language));
        assertThat(applicant.getMobileTelephone(), equalTo(mobileTelephone));
        assertThat(applicant.getNationality(), equalTo(nationality));
        assertThat(applicant.getPrimaryTelephone(), equalTo(primaryTelephone));
        assertThat(applicant.getReference(), equalTo(reference));
        assertThat(applicant.getSecondNationality(), equalTo(secondNationality));
        assertThat(applicant.getSurname(), equalTo(lastName));
        assertThat(applicant.getTitle(), equalTo(title));
    }

    @Test
    public void shouldConvertApplication() {

        final String caseType = "caseType";
        final String customerInsightConsent = "customerInsightConsent";
        final String marriageCategory = "marriageCategory";
        final String post = "post";
        final String reasonForBeingOverseas = "reasonForBeingOverseas";

        Form form = new FormBuilder()
                .withQuestion("caseType", caseType)
                .withQuestion("customerInsightConsent", customerInsightConsent)
                .withQuestion("marriageCategory", marriageCategory)
                .withQuestion("post", post)
                .withQuestion("reasonForBeingOverseas", reasonForBeingOverseas)
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getReasonForBeingOverseas(), equalTo(reasonForBeingOverseas));
        assertThat(application.getCaseType(), equalTo(caseType));
        assertThat(application.getCustomerInsightConsent(), equalTo(customerInsightConsent));
        assertThat(application.getMarriageCategory(), equalTo(marriageCategory));
        assertThat(application.getPost(), equalTo(post));
    }

    @Test
    public void shouldConvertAdditionalQuestionsAsDescription() {

        final String question = "What is your partner's name?";
        final String firstNameLabel = "First name";
        final String firstNameAnswer = "Gerald";
        final String lastNameLabel = "Last name";
        final String lastNameAnswer = "Smith";

        Form form = new FormBuilder()
                .withQuestion(new QuestionBuilder()
                        .withQuestion(question)
                        .withField(firstNameLabel, "partnersFirstName", firstNameAnswer)
                        .withField(lastNameLabel, "partnersLastName", lastNameAnswer)
                        .build())
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());

        String description = application.getDescription();

        assertThat(description, equalTo(
                question + "\n" +
                        firstNameLabel + ": " + firstNameAnswer + "\n" +
                        lastNameLabel + ": " + lastNameAnswer + "\n\n"
        ));
    }

    @Test
    public void shouldConvertFilesAsAttachments() throws Exception {
        final URL fileLocation = new URL("http://example.org/file.pdf");
        final String fileData = "VGhpcyBpcyBhIGZpbGUgYXR0YWNobWVudA==";

        when(documentUploadService.getFileAsBase64(ArgumentMatchers.eq(fileLocation)))
                .thenReturn(fileData);

        Form form = new FormBuilder()
                .withQuestion(new QuestionBuilder()
                        .withField("File", "file", "file", fileLocation.toString())
                        .build())
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());

        List<Attachment> attachments = application.getAttachments();

        assertThat(attachments, notNullValue());
        assertThat(attachments.size(), is(1));

        Attachment attachment = attachments.get(0);

        assertThat(attachment, notNullValue());
        assertThat(attachment.getFileName(), equalTo("file.pdf"));
        assertThat(attachment.getFileExtension(), equalTo("pdf"));
        assertThat(attachment.getFileData(), equalTo(fileData));
    }

    @Test
    public void shouldConvertFeesAsDescription() {

        final String paymentReference = "FCO-12345";
        final String total = "0.50";

        Form form = new FormBuilder()
                .withFees(new Fees(paymentReference, new BigDecimal("50")))
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());

        String description = application.getDescription();

        assertThat(description, equalTo(
                "\n" +
                        "Amount paid: £" + total + "\n" +
                        "Payment reference: " + paymentReference
        ));
    }

    @Test
    public void shouldConvertFeeServices() {

        final String partnerName = "partnerName";
        final String feeServiceName = "feeServiceName";

        Form form = new FormBuilder()
                .withQuestion("partnerName", partnerName)
                .withFees(new FeesBuilder("ref")
                    .withFeeDetail(feeServiceName, BigDecimal.TEN)
                    .build())
                .build();

        when(casebookService.getFeeServices(eq(ImmutableList.of(feeServiceName)))).thenReturn(ImmutableList.of(
            new FeeServiceBuilder()
                .withName(feeServiceName)
                .withField("thailandAffirmationPartnersName")
                .build()
        ));

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getFeeServices(), notNullValue());
        assertThat(application.getFeeServices().size(), is(1));

        FeeService feeService = application.getFeeServices().get(0);

        assertThat(feeService.getName(), equalTo(feeServiceName));

        assertFieldEquals(feeService.getFields(), "thailandAffirmationPartnersName", partnerName);
    }

    @Test
    public void shouldConvertExpression() {

        Form form = new FormBuilder()
                .withFees(new FeesBuilder("ref").build())
                .build();

        when(casebookService.getFeeServices(any())).thenReturn(ImmutableList.of(
                new FeeServiceBuilder()
                        .withName("name")
                        .withField("notarialConsentMethodOfContact")
                        .build()
        ));

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getFeeServices(), notNullValue());
        assertThat(application.getFeeServices().size(), is(1));

        FeeService feeService = application.getFeeServices().get(0);

        assertFieldEquals(feeService.getFields(), "notarialConsentMethodOfContact", "Not set");
    }

    @Test
    public void shouldConvertBooleanField() {

        Form form = new FormBuilder()
                .withQuestion("declaration", "true")
                .withFees(new FeesBuilder("ref").build())
                .build();

        when(casebookService.getFeeServices(any())).thenReturn(ImmutableList.of(
                new FeeServiceBuilder()
                        .withName("name")
                        .withBooleanField("notarialDeclaration")
                        .build()
        ));

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getFeeServices(), notNullValue());
        assertThat(application.getFeeServices().size(), is(1));

        FeeService feeService = application.getFeeServices().get(0);

        assertFieldEquals(feeService.getFields(), "notarialDeclaration", Boolean.TRUE);
    }

    @Test
    public void shouldRemovePropertyUsingExpression() {

        Form form = new FormBuilder()
                .withQuestion("declaration", "Yes")
                .withFees(new FeesBuilder("ref").build())
                .build();

        when(casebookService.getFeeServices(any())).thenReturn(ImmutableList.of(
                new FeeServiceBuilder()
                        .withName("name")
                        .withField("notarialDeclaration")
                        .build()
        ));

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getDescription(), equalTo("\nAmount paid: £0.00\nPayment reference: ref"));
    }

    @Test
    public void shouldRemoveProperty() {

        Form form = new FormBuilder()
                .withQuestion("forenames", "Test")
                .withQuestion("forDescription", "Yes")
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getDescription(), equalTo("null\nforDescription: Yes\n\n"));
    }

    @Test
    public void shouldDefaultToMappingFielfDirectlyToProperty() {

        Form form = new FormBuilder()
                .withQuestion("somethingUnmapped", "Test")
                .withFees(new FeesBuilder("ref").build())
                .build();

        when(casebookService.getFeeServices(any())).thenReturn(ImmutableList.of(
                new FeeServiceBuilder()
                        .withName("name")
                        .withField("somethingUnmapped")
                        .build()
        ));

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();
        FeeService feeService = application.getFeeServices().get(0);

        assertFieldEquals(feeService.getFields(), "somethingUnmapped", "Test");
    }

    private void assertFieldEquals(List<Field> fields, String fieldName, Object value) {
        Optional<Field> possibleField = fields.stream()
                .filter(f -> f.getFieldName().equals(fieldName))
                .findFirst();

        if (possibleField.isPresent()) {
            assertThat(possibleField.get().getValue(), equalTo(value));
        } else {
            fail("No field found named " + fieldName);
        }
    }
}
