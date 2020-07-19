/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.endpoints.staticfile;

import at.itopen.simplerest.conversion.Conversion;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class Dynamic {

    String[] extension;

    /**
     *
     * @param extension
     */
    public Dynamic(String... extension) {
        this.extension = extension;
    }

    /**
     *
     * @return
     */
    public String[] getExtension() {
        return extension;
    }

    /**
     *
     * @param conversion
     * @param UrlParameter
     * @param file
     */
    public abstract void call(Conversion conversion, Map<String, String> UrlParameter, DynamicFile file);

}
