package uk.gov.fco.casemanagement.worker.service.documentupload;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class DocumentUploadServiceTest {

    private DocumentUploadService documentUploadService;

    @Before
    public void setup() {
        documentUploadService = new DocumentUploadService();
    }

    @Test
    public void shouldReturnFileContentAsBase64String() throws Exception {
        final String content = "Hi there, this is a test.. 🤪";

        Path tempFile = Files.createTempFile(DocumentUploadServiceTest.class.getName(), ".txt");
        Files.write(tempFile, content.getBytes());

        String encodedContent = documentUploadService.getFileAsBase64(tempFile.toUri().toURL());

        assertThat(encodedContent, notNullValue());
        assertThat(new String(Base64.getDecoder().decode(encodedContent)), equalTo(content));
    }

    @Test(expected = DocumentUploadServiceException.class)
    public void shouldThrowIfIOException() throws Exception {
        Path tempFile = Paths.get(UUID.randomUUID().toString());
        documentUploadService.getFileAsBase64(tempFile.toUri().toURL());
    }
}
