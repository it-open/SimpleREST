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

    Map<String, String> parameter = new TreeMap<>();
    Map<String, String> pathParameter = new TreeMap<>();
    List<HttpStatus> returns = new ArrayList<>();
    private final String shortInfo;
    private String longInfo;
    private ContentType answerContentType;
    private Class in;
    private Class out;
    private boolean inlist = false;
    private boolean outlist = false;

    /**
     *
     * @param shortInfo
     */
    public EndpointDocumentation(String shortInfo) {
        this.shortInfo = shortInfo;
        this.answerContentType = ContentType.JSON;
        this.in = null;
        this.out = null;
        returns.add(HttpStatus.OK);
    }

    /**
     *
     * @param answerContentType
     * @return
     */
    public EndpointDocumentation setContentType(ContentType answerContentType) {
        this.answerContentType = answerContentType;
        return this;
    }

    /**
     *
     * @param shortInfo
     * @param answerContentType
     * @param in
     * @param out
     */
    public EndpointDocumentation(String shortInfo, ContentType answerContentType, Class in, Class out) {
        this.shortInfo = shortInfo;
        this.answerContentType = answerContentType;
        this.in = in;
        this.out = out;
    }

    /**
     *
     * @param httpStatus
     * @return
     */
    public EndpointDocumentation addReturns(HttpStatus httpStatus) {
        returns.add(httpStatus);
        return this;
    }

    /**
     *
     * @param longInfo
     * @return
     */
    public EndpointDocumentation setLongInfo(String longInfo) {
        this.longInfo = longInfo;
        return this;
    }

    /**
     *
     * @param out
     * @return
     */
    public EndpointDocumentation setOut(Class out) {
        this.out = out;
        return this;
    }

    /**
     *
     * @param in
     * @return
     */
    public EndpointDocumentation setIn(Class in) {
        this.in = in;
        return this;
    }

    /**
     *
     * @param out
     * @return
     */
    public EndpointDocumentation setOutList(Class out) {
        this.out = out;
        outlist = true;
        return this;
    }

    /**
     *
     * @param in
     * @return
     */
    public EndpointDocumentation setInList(Class in) {
        this.in = in;
        inlist = true;
        return this;
    }

    /**
     *
     * @return
     */
    public boolean isInlist() {
        return inlist;
    }

    /**
     *
     * @return
     */
    public boolean isOutlist() {
        return outlist;
    }

    /**
     *
     * @param inlist
     * @return
     */
    public EndpointDocumentation setInlist(boolean inlist) {
        this.inlist = inlist;
        return this;
    }

    /**
     *
     * @param outlist
     * @return
     */
    public EndpointDocumentation setOutlist(boolean outlist) {
        this.outlist = outlist;
        return this;
    }

    /**
     *
     * @param name
     * @param doc
     * @return
     */
    public EndpointDocumentation addParameter(String name, String doc) {
        parameter.put(name, doc);
        return this;
    }

    /**
     *
     * @param name
     * @param doc
     * @return
     */
    public EndpointDocumentation addPathParameter(String name, String doc) {
        pathParameter.put(name, doc);
        return this;
    }

    /**
     *
     * @param path
     * @param type
     * @return
     */
    public String toHTML(String path, String type) {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    public ContentType getAnswerContentType() {
        return answerContentType;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getPathParameter() {
        return pathParameter;
    }

    /**
     *
     * @return
     */
    public Class getIn() {
        return in;
    }

    /**
     *
     * @return
     */
    public String getLongInfo() {
        return longInfo;
    }

    /**
     *
     * @return
     */
    public Class getOut() {
        return out;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getParameter() {
        return parameter;
    }

    /**
     *
     * @return
     */
    public List<HttpStatus> getReturns() {
        return returns;
    }

    /**
     *
     * @return
     */
    public String getShortInfo() {
        return shortInfo;
    }

}
