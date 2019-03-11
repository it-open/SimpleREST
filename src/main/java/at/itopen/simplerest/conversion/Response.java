/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public class Response {

    private HttpStatus status = HttpStatus.getByCode(404);
    private String statusMessage = null;
    private ContentType contentType = ContentType.JSON;
    private Map<String, String> headerData = new HashMap<>();
    private Object data = null;
    private final List<Cookie> cookies = new ArrayList<>();
    private boolean wrapJson = true;
    private boolean convertStringToJson = true;

    /**
     *
     * @param status
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     *
     * @param status
     */
    public void setStatus(HttpStatus status, String message) {
        this.status = status;
        this.statusMessage = message;
    }

    /**
     *
     * @return
     */
    public HttpStatus getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     *
     * @param wrapJson
     */
    public void setWrapJson(boolean wrapJson) {
        this.wrapJson = wrapJson;
    }

    /**
     *
     * @return
     */
    public boolean isWrapJson() {
        return wrapJson;
    }

    /**
     *
     * @param convertStringToJson
     */
    public void setConvertStringToJson(boolean convertStringToJson) {
        this.convertStringToJson = convertStringToJson;
    }

    /**
     *
     * @return
     */
    public boolean isConvertStringToJson() {
        return convertStringToJson;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setHeader(String key, String value) {
        headerData.put(key, value);
    }

    /**
     *
     * @return
     */
    public Map<String, String> getHeaderData() {
        return headerData;
    }

    /**
     *
     * @return
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     *
     * @return
     */
    public String getCookieString() {
        StringBuilder sb = new StringBuilder();
        cookies.forEach((cookie) -> {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(cookie.getName()).append("=").append(cookie.getValue());
        });
        return sb.toString();
    }

    /**
     *
     * @param contentType
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
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
    public ContentType guessandSetContentTypefromData() {
        if (data instanceof byte[]) {
            contentType = ContentType.fromByteArray((byte[]) data);
        }
        return contentType;
    }

    /**
     *
     * @param name
     * @return
     */
    public ContentType guessandSetContentTypefromDataOrName(String name) {
        return guessandSetContentTypefromData(guessContentTypefromName(name));
    }

    /**
     *
     * @param name
     * @return
     */
    public ContentType guessContentTypefromName(String name) {
        ContentType erg = ContentType.OTHER;
        if (name.contains(".")) {
            erg = ContentType.fromFileName(name);
        } else {
            erg = ContentType.fromFileExtension(name);
        }
        return erg;
    }

    /**
     *
     * @param defaultContentType
     * @return
     */
    public ContentType guessandSetContentTypefromData(ContentType defaultContentType) {
        if (data instanceof byte[]) {
            contentType = ContentType.fromByteArray((byte[]) data);
        }
        if (contentType.equals(ContentType.OTHER)) {
            contentType = defaultContentType;
        }
        return contentType;
    }

    /**
     *
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public Object getData() {
        return data;
    }

    /**
     *
     * @return
     */
    public boolean hasData() {
        return data != null;
    }

}
