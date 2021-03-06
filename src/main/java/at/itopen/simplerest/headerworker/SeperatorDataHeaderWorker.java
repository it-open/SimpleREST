/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.headerworker;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.Request;

/**
 *
 * @author roland
 */
public class SeperatorDataHeaderWorker extends AbstractHeaderWorker {

    String section;
    String seperator;

    /**
     *
     * @param section
     * @param seperator
     */
    public SeperatorDataHeaderWorker(String section, String seperator) {
        this.section = section;
        this.seperator = seperator;
    }

    /**
     *
     * @param conversion
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();
        String value = request.getHeaders().get(section);
        String[] values = value.split(seperator);
        if (values.length > 1) {
            request.getHeaders().remove(section);
            for (String val1 : values) {
                request.getHeaders().add(section, val1.trim());
            }
        }

    }

}
