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
public class MessageAnswer {

    private int position;
    private boolean finished;
    private boolean working;
    private boolean queued;

    /**
     *
     */
    public MessageAnswer() {
    }

    /**
     *
     * @param position
     * @param finished
     * @param working
     * @param queued
     */
    public MessageAnswer(int position, boolean finished, boolean working, boolean queued) {
        this.position = position;
        this.finished = finished;
        this.working = working;
        this.queued = queued;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
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
     * @return the queued
     */
    public boolean isQueued() {
        return queued;
    }

    /**
     * @param queued the queued to set
     */
    public void setQueued(boolean queued) {
        this.queued = queued;
    }

}
