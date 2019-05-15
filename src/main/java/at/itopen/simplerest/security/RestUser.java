/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.conversion.HttpStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class RestUser<T> extends BasicUser implements BasicAuthUser, JwtAuthUser {

    T user = null;

    @Override
    public void setAuth(Conversion conversion, String name, String password) {
        user = login(name, password);
        setAuthenticated(user != null);
    }

    abstract protected T login(String name, String password);

    abstract protected T get(String id);

    abstract protected long getLevel(T user);

    public T getUser() {
        return user;
    }

    public enum AccessType {
        READ, WRITE, DELETE
    };

    @Override
    public void setJwtAuth(Conversion conversion, String Id, String issuer, String Subject) {
        if (conversion.getRequest().getUri().getPath().get(0).equals(Subject)) {
            if (Subject.equals("user")) {
                user = get(Id);
                setAuthenticated(user != null);
            }

        } else {
            setAuthenticated(false);
        }

    }

    public static boolean DEBUG_AUTH = false;

    public boolean isLevel(Conversion conversion, AllowRule.AllowLevel level) {
        if (DEBUG_AUTH) {
            return true;
        }
        if (conversion.getRequest().getUser() == null) {
            return false;
        }
        if (((RestUser) conversion.getRequest().getUser()).getUser() == null) {
            return false;
        }
        if (getLevel(((RestUser<T>) conversion.getRequest().getUser()).getUser()) >= level.levelValue) {
            return true;
        }
        return false;
    }

    public static boolean isUser(Conversion conversion) {
        if (DEBUG_AUTH) {
            return true;
        }
        if (conversion.getRequest().getUser() == null) {
            return false;
        }
        if (((RestUser) conversion.getRequest().getUser()).getUser() == null) {
            return false;
        }
        return true;

    }

    public T getUser(Conversion conversion) {
        if (isUser(conversion)) {
            return (T) ((RestUser) conversion.getRequest().getUser()).getUser();
        }
        return null;
    }

    private static Map<Class, List<AllowRule>> allowRules = new HashMap<>();

    public static void addAllowRule(Class forClass, AllowRule rule) {
        if (!allowRules.containsKey(forClass)) {
            allowRules.put(forClass, new ArrayList<>());
        }
        allowRules.get(forClass).add(rule);
    }

    public boolean may(Conversion conversion, Object object, AccessType accessType) {
        if (object == null) {
            return false;
        }
        if (!allowRules.containsKey(object.getClass())) {
            return false;
        }
        for (AllowRule rule : allowRules.get(object.getClass())) {
            boolean test = (rule.check(conversion, object, this, accessType));
            if (test == false) {
                return false;
            }
        }
        return true;
    }

    public boolean mayWithFail(Conversion conversion, Object object, AccessType accessType) {
        if (may(conversion, object, accessType)) {
            return true;
        }
        conversion.getResponse().setStatus(HttpStatus.Forbidden);
        return false;
    }

}
