package com.virnect.message.application.mail;

import javax.activation.DataSource;
import java.io.*;

/**
 * Project: PF-Message
 * DATE: 2020-08-06
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ByteArrayDataSource  implements DataSource {
    byte[] bytes;
    String contentType;
    String name;

    ByteArrayDataSource(byte[] bytes, String contentType, String name) {
        this.bytes = bytes;
        if(contentType == null)
            this.contentType = "application/octet-stream";
        else
            this.contentType = contentType;
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes,0,bytes.length - 2);
    }

    public String getName() {
        return name;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new FileNotFoundException();
    }
}