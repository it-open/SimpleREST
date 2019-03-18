/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.client;

/**
 *
 * @author roland
 */
public class WrappedResponse<T> {

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the generationMsSeconds
     */
    public double getGenerationMsSeconds() {
        return generationMsSeconds;
    }

    /**
     * @param generationMsSeconds the generationMsSeconds to set
     */
    public void setGenerationMsSeconds(double generationMsSeconds) {
        this.generationMsSeconds = generationMsSeconds;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    private int code;
    private String message;
    private String info;
    private double generationMsSeconds;
    private T data;

    public WrappedResponse() {
    }

}
