/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

/**
 *
 * @author roland
 */
public class MessageStatus {

    private boolean finished;
    private boolean working;
    private String messageid;

    /**
     *
     */
    public MessageStatus() {
    }

    /**
     *
     * @param finished
     * @param working
     * @param messageid
     */
    public MessageStatus(boolean finished, boolean working, String messageid) {
        this.finished = finished;
        this.working = working;
        this.messageid = messageid;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * @return the working
     */
    public boolean isWorking() {
        return working;
    }

    /**
     * @param working the working to set
     */
    public void setWorking(boolean working) {
        this.working = working;
    }

    /**
     *
     * @return
     */
    public String getMessageid() {
        return messageid;
    }

    /**
     *
     * @param messageid
     */
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

}
