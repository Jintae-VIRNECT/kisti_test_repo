package com.virnect.download.infra.file.donwload;

import java.io.IOException;

public interface FileDownloadService {
    byte[] fileDownload(String fileName) throws IOException;
}
