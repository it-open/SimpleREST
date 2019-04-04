/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest;

/**
 *
 * @author roland
 */
public class TestJson {

    public TestJson() {
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * @return the dnum
     */
    public Double getDnum() {
        return dnum;
    }

    /**
     * @param dnum the dnum to set
     */
    public void setDnum(Double dnum) {
        this.dnum = dnum;
    }

    private String text;
    private Integer number;
    private Double dnum;

    public TestJson(String text, Integer number, Double dnum) {
        this.text = text;
        this.number = number;
        this.dnum = dnum;
    }

}
