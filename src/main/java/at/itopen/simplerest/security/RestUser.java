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

    public static enum AUTH_TYPE {
        BASIC, JWT
    }

    private AUTH_TYPE auth_type = null;

    @Override
    public void setAuth(Conversion conversion, String name, String password) {
        user = login(name, password);
        if (user != null) {
            setAuthenticated(true);
            auth_type = AUTH_TYPE.BASIC;
        }
    }

    abstract protected T login(String name, String password);

    abstract protected long getLevel(T user);

    public T getUser() {
        return user;
    }

    public AUTH_TYPE getAuth_type() {
        return auth_type;
    }

    public enum AccessType {
        READ, WRITE, DELETE, CREATE
    };

    @Override
    public void setJwtAuth(Conversion conversion, String Id, String issuer, String Subject) {
        user = jwt_check(conversion, Id, issuer, Subject);
        if (user != null) {
            setAuthenticated(true);
            auth_type = AUTH_TYPE.JWT;
        } else {
            setAuthenticated(false);
        }
    }

    abstract protected T jwt_check(Conversion conversion, String Id, String issuer, String Subject);

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
