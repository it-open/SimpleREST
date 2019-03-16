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
public class Cookie {

    private final String name;
    private final String value;
    private int maxseconds;

    /**
     *
     * @param name
     * @param value
     */
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.maxseconds = 5 * 60;
    }

    /**
     *
     * @param name
     * @param value
     * @param maxseconds
     */
    public Cookie(String name, String value, int maxseconds) {
        this.name = name;
        this.value = value;
        this.maxseconds = maxseconds;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @return
     */
    public int getMaxseconds() {
        return maxseconds;
    }

}
