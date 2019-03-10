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
public class RestClient {

    /**
     *
     * @return
     */
    public static RestBuilder builder() {
        return new RestBuilder();
    }
}
