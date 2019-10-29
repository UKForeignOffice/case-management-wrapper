package uk.gov.fco.casemanagement.worker.service.casebook.domain;

public class Attachment {

    private String fileName;

    private byte[] fileData;

    private String fileExtension;

    public Attachment(String fileName, byte[] fileData, String fileExtension) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
