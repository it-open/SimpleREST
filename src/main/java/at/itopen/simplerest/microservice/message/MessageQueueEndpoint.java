/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.message;

import at.itopen.simplerest.JsonHelper;
import at.itopen.simplerest.client.RestClient;
import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.microservice.client.LoadBalancedRestClient;
import at.itopen.simplerest.microservice.loadbalancer.LoadBalancer;
import at.itopen.simplerest.path.RestEndpoint;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roland
 * @param <T> Type
 */
public abstract class MessageQueueEndpoint<T> extends RestEndpoint {

    // private Class genericType = null;
    private MessageRequest<T> aktWorking = null;
    private final LoadBalancer loadBalancer;
    private final Queue<MessageRequest<T>> messagequeue = new LinkedList<>();

    /**
     *
     * @param endpointName
     * @param loadBalancer
     */
    public MessageQueueEndpoint(String endpointName, LoadBalancer loadBalancer) {
        super(endpointName);
        this.loadBalancer = loadBalancer;
        Type sooper = getClass().getGenericSuperclass();
        //   genericType = (Class) ((ParameterizedType) sooper).getActualTypeArguments()[0];
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
                            work(aktWorking.getData());
                            signal(aktWorking, true, true);
                            aktWorking = null;
                        }
                    } catch (Exception e) {
                        Logger.getLogger(MessageQueueEndpoint.class.getName()).log(Level.SEVERE, "MessageQueue", e);
                    }
                }
            }
        }.start();
    }

    private void signal(MessageRequest<T> message, boolean processing, boolean finished) {
        LoadBalancedRestClient lbrc = loadBalancer.restClient("/loadbalancer/guarantor/state", RestClient.RESTMETHOD.POST);
        lbrc.setJson(new MessageStatus(finished, processing, message.getMessageid()));
        for (String guarantorid : message.getGuarantorServiceIds()) {
            lbrc.toDistinctServiceFireAndForget(guarantorid, false);
        }
    }

    /**
     *
     * @param conversion
     * @param urlParameter
     */
    @Override
    public void call(Conversion conversion, Map<String, String> urlParameter) {

        if ("POST".equals(conversion.getRequest().getMethod())) {
            MessageRequest<T> mrequest = null;
            // T data;
            if (conversion.getRequest().getContentData() != null) {
                mrequest = (MessageRequest<T>) JsonHelper.fromString(conversion.getRequest().getContentData(), new TypeReference<MessageRequest<T>>() {
                });
                //data = mrequest.getData();
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
                if (item.getMessageid().equals(id)) {
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
        return aktWorking.getMessageid().equals(id);
    }

    /**
     *
     * @param data
     */
    public abstract void work(T data);

    /**
     *
     * @param conversion
     * @return
     */
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
