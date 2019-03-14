/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import java.util.List;

/**
 *
 * @author roland
 */
public class MessageRequest<T> {

    String messageid;

    List<String> guarantorServiceIds;

    T data;

}
