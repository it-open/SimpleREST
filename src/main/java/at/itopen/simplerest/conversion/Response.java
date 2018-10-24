/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

/**
 *
 * @author roland
 */
public class Response {
    
    private HttpStatus status=HttpStatus.getByCode(404);
    private ContentType contentType=ContentType.JSON;
    private Object data=null;
    
    public void setStatus(HttpStatus status)
    {
        this.status=status;
    }

    public HttpStatus getStatus() {
        return status;
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
