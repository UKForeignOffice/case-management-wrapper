package uk.gov.fco.casemanagement.worker.service.casebook;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.fco.casemanagement.common.domain.Form;
import uk.gov.fco.casemanagement.worker.service.casebook.domain.*;
import uk.gov.fco.casemanagement.worker.service.documentupload.DocumentUploadService;

import java.net.URL;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationConverterTest {

    @InjectMocks
    private ApplicationConverter applicationConverter;

    @Mock
    private DocumentUploadService documentUploadService;

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
        final String dateOfBirth = "dateOfBirth";
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
                .withQuestion("dateOfBirth", dateOfBirth)
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
        assertThat(applicant.getDateOfBirth(), equalTo(dateOfBirth));
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
        final String summary = "summary";

        Form form = new FormBuilder()
                .withQuestion("caseType", caseType)
                .withQuestion("customerInsightConsent", customerInsightConsent)
                .withQuestion("marriageCategory", marriageCategory)
                .withQuestion("post", post)
                .withQuestion("reasonForBeingOverseas", reasonForBeingOverseas)
                .withQuestion("summary", summary)
                .build();

        NotarialApplication notarialApplication = applicationConverter.convert(form);
        Application application = notarialApplication.getApplication();

        assertThat(application, notNullValue());
        assertThat(application.getReasonForBeingOverseas(), equalTo(reasonForBeingOverseas));
        assertThat(application.getCasetype(), equalTo(caseType));
        assertThat(application.getCustomerInsightConsent(), equalTo(customerInsightConsent));
        assertThat(application.getMarriageCategory(), equalTo(marriageCategory));
        assertThat(application.getPost(), equalTo(post));
        assertThat(application.getSummary(), equalTo(summary));
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
        assertThat(attachment.getFileName(), equalTo("file"));
        assertThat(attachment.getFileExtension(), equalTo("pdf"));
        assertThat(attachment.getFileData(), equalTo(fileData));
    }
}
