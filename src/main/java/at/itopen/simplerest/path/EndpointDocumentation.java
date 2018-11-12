package at.itopen.simplerest.path;

import at.itopen.simplerest.conversion.ContentType;
import at.itopen.simplerest.conversion.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author roland
 */
public class EndpointDocumentation {
    
    Map<String,String> parameter=new TreeMap<>();
    Map<String,String> pathParameter=new TreeMap<>();
    List<HttpStatus> returns=new ArrayList<>();
    private final String shortInfo;
    private String longInfo;
    private final ContentType answerContentType;
    private final Class in;
    private final Class out;
    
    
    public EndpointDocumentation(String shortInfo) {
        this.shortInfo = shortInfo;
        this.answerContentType = ContentType.JSON;
        this.in = null;
        this.out = null;
        returns.add(HttpStatus.OK);
    }

    public EndpointDocumentation(String shortInfo, ContentType answerContentType, Class in, Class out) {
        this.shortInfo = shortInfo;
        this.answerContentType = answerContentType;
        this.in = in;
        this.out = out;
    }
    
    public EndpointDocumentation addReturns(HttpStatus httpStatus)
    {
        returns.add(httpStatus);
        return this;
    }

    public EndpointDocumentation setLongInfo(String longInfo) {
        this.longInfo = longInfo;
        return this;
    }
    
    
    public EndpointDocumentation addParameter(String name,String doc)
    {
        parameter.put(name, doc);
        return this;
    }
    
    public EndpointDocumentation addPathParameter(String name,String doc)
    {
        pathParameter.put(name, doc);
        return this;
    }

    public String toHTML(String path, String Type) {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    public ContentType getAnswerContentType() {
        return answerContentType;
    }

    public Map<String, String> getPathParameter() {
        return pathParameter;
    }
    
    

    public Class getIn() {
        return in;
    }

    public String getLongInfo() {
        return longInfo;
    }

    public Class getOut() {
        return out;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public List<HttpStatus> getReturns() {
        return returns;
    }

    public String getShortInfo() {
        return shortInfo;
    }
    
    
    
    
    
}
