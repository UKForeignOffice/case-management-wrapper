package uk.gov.fco.casemanagement.worker.service.casebook.domain;

public class Attachment {

    private String fileName;

    private String fileData;

    private String fileExtension;

    public Attachment(String fileName, String fileData, String fileExtension) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileData() {
        return fileData;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
