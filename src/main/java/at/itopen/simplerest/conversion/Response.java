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
    
    public void setStatus(HttpStatus status)
    {
        this.status=status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
    
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
    
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public ContentType getContentType() {
        return contentType;
    }
    
    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
    
    public boolean hasData()
    {
        return data!=null;
    }
    
    
    
    
    
}
