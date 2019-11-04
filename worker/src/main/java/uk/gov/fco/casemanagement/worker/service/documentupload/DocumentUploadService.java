package uk.gov.fco.casemanagement.worker.service.documentupload;

import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Service
@Slf4j
public class DocumentUploadService {

    /**
     * Download file from Document Upload service and convert to Base64 String.
     *
     * @param location The URL of the file to download.
     * @return The Base64 encoded content of the file at <code>location</code>.
     * @throws DocumentUploadServiceException When there is an error downloading the file content.
     */
    public String getFileAsBase64(URL location) throws DocumentUploadServiceException {
        try (InputStream in = location.openStream()) {
            byte[] content = IOUtils.toByteArray(in);
            return Base64.getEncoder().encodeToString(content);
        } catch (IOException e) {
            throw new DocumentUploadServiceException(e);
        }
    }
}
