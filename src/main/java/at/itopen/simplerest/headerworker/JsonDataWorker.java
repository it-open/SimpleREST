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
public class JsonDataWorker extends AbstractHeaderWorker {

    /**
     *
     * @param request
     */
    @Override
    public void work(Conversion conversion) {
        Request request = conversion.getRequest();

    }

}
