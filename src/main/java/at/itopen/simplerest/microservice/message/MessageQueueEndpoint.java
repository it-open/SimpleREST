/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import at.itopen.simplerest.Json;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.microservice.client.LoadBalancedRestClient;
import at.itopen.simplerest.microservice.loadbalancer.LoadBalancer;
import at.itopen.simplerest.path.RestEndpoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.istack.internal.logging.Logger;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

/**
 *
 * @author roland
 * @param <T>
 */
public abstract class MessageQueueEndpoint<T> extends RestEndpoint {

    private Class genericType = null;
    private MessageRequest<T> aktWorking = null;
    private final LoadBalancer loadBalancer;
    private final Queue<MessageRequest<T>> messagequeue = new LinkedList<>();

    public MessageQueueEndpoint(String endpointName, LoadBalancer loadBalancer) {
        super(endpointName);
        this.loadBalancer = loadBalancer;
        Type sooper = getClass().getGenericSuperclass();
        genericType = (Class) ((ParameterizedType) sooper).getActualTypeArguments()[0];
        new Thread("MessageQueue:" + this.getClass().getName()) {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        if (messagequeue.isEmpty()) {
                            Thread.sleep(10);
                        } else {
                            aktWorking = messagequeue.poll();
                            signal(aktWorking, true, false);
                            work(aktWorking.data);
                            signal(aktWorking, true, true);
                            aktWorking = null;
                        }
                    } catch (Exception e) {
                        Logger.getLogger(this.getClass()).log(Level.SEVERE, "MessageQueue", e);
                    }
                }
            }
        }.start();
    }

    private void signal(MessageRequest<T> message, boolean processing, boolean finished) {
        LoadBalancedRestClient lbrc = loadBalancer.RestClient("/loadbalancer/guarantor", RestClient.REST_METHOD.POST);
        lbrc.setJson(new MessageStatus(finished, processing, message.messageid));
        for (String guarantorid : message.guarantorServiceIds) {
            lbrc.toDistinctServiceFireAndForget(guarantorid, false);
        }
    }

    @Override
    public void Call(Conversion conversion, Map<String, String> UrlParameter) {

        if ("POST".equals(conversion.getRequest().getMethod())) {
            MessageRequest<T> mrequest = null;
            T data;
            if (conversion.getRequest().getContentData() != null) {
                mrequest = (MessageRequest<T>) Json.fromString(conversion.getRequest().getContentData(), new TypeReference<MessageRequest<T>>() {
                });
                data = mrequest.data;
            }
            if (mrequest != null) {
                messagequeue.add(mrequest);
                conversion.getResponse().setData(new MessageAnswer(messagequeue.size(), false, false, true));
            }
        }
        if ("GET".equals(conversion.getRequest().getMethod())) {
            String id = conversion.getRequest().getParam("id");
            MessageRequest<T> search = null;
            int pos = 0;
            for (MessageRequest<T> item : (MessageRequest<T>[]) messagequeue.toArray()) {
                if (item.messageid.equals(id)) {
                    if (search == null) {
                        pos++;
                        search = item;
                    }
                }
            }
            if (search != null) {
                conversion.getResponse().setData(new MessageAnswer(pos, false, isWorking(id), true));
            } else {
                conversion.getResponse().setData(new MessageAnswer(-1, true, true, true));
            }
        }
    }

    private boolean isWorking(String id) {
        if (aktWorking == null) {
            return false;
        }
        return (aktWorking.messageid.equals(id));
    }

    public abstract void work(T data);

    @Override
    protected boolean checkEndpoint(Conversion conversion) {
        if ("POST".equals(conversion.getRequest().getMethod())) {
            return super.checkEndpoint(conversion); //To change body of generated methods, choose Tools | Templates.
        }
        if ("GET".equals(conversion.getRequest().getMethod())) {
            return super.checkEndpoint(conversion); //To change body of generated methods, choose Tools | Templates.
        }
        if ("DELETE".equals(conversion.getRequest().getMethod())) {
            return super.checkEndpoint(conversion); //To change body of generated methods, choose Tools | Templates.
        }
        return false;
    }

}
