/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.entity.ContentType;

/**
 *
 * @author roland
 */
public class RestFile {

    byte[] data;
    ContentType contentType;
    String name;

    /**
     *
     * @param data
     * @param contentType
     * @param name
     */
    public RestFile(byte[] data, ContentType contentType, String name) {
        this.data = data;
        this.contentType = contentType;
        this.name = name;
    }

    /**
     *
     * @param file
     * @param contentType
     * @param name
     */
    public RestFile(File file, ContentType contentType, String name) {
        try {
            this.data = Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            Logger.getLogger(RestFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.contentType = contentType;
        this.name = name;
    }

    /**
     *
     * @param stream
     * @param contentType
     * @param name
     */
    public RestFile(InputStream stream, ContentType contentType, String name) {
        try {
            this.data = readAllBytes(stream);
        } catch (IOException ex) {
            Logger.getLogger(RestFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.contentType = contentType;
        this.name = name;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
                    outputStream.write(buf, 0, readLen);
                }

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) {
                inputStream.close();
            } else {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    exception.addSuppressed(e);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

}
