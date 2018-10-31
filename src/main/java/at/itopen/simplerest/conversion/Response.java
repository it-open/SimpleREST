/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author roland
 */
public class Response {
    
    private HttpStatus status=HttpStatus.getByCode(404);
    private ContentType contentType=ContentType.JSON;
    private Object data=null;
    private final List<Cookie> cookies=new ArrayList<>();
    
    /**
     *
     * @param status
     */
    public void setStatus(HttpStatus status)
    {
        this.status=status;
    }

    /**
     *
     * @return
     */
    public HttpStatus getStatus() {
        return status;
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
    public String getCookieString()
    {
        StringBuilder sb=new StringBuilder();
        cookies.forEach((cookie) -> {
            if (sb.length()>0)
                sb.append("; ");
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
    public boolean hasData()
    {
        return data!=null;
    }
    
    
    
    
    
}
