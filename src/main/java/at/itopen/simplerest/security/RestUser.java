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
 * @param <T>
 */
public abstract class RestUser<T> extends BasicUser implements BasicAuthUser, JwtAuthUser {

    T user = null;

    /**
     *
     */
    public static enum AUTH_TYPE {

        /**
         *
         */
        BASIC,
        /**
         *
         */
        JWT
    }

    private AUTH_TYPE auth_type = null;

    /**
     *
     * @param conversion
     * @param name
     * @param password
     */
    @Override
    public void setAuth(Conversion conversion, String name, String password) {
        T tuser = login(name, password);
        if (tuser != null) {
            user = tuser;
            setAuthenticated(true);
            auth_type = AUTH_TYPE.BASIC;
        }
    }

    /**
     *
     * @param name
     * @param password
     * @return
     */
    abstract protected T login(String name, String password);

    /**
     *
     * @param user
     * @return
     */
    abstract protected long getLevel(T user);

    /**
     *
     * @return
     */
    public T getUser() {
        return user;
    }

    /**
     *
     * @param user
     */
    public void setUser(T user) {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public AUTH_TYPE getAuth_type() {
        return auth_type;
    }

    /**
     *
     */
    public enum AccessType {

        /**
         *
         */
        READ,
        /**
         *
         */
        WRITE,
        /**
         *
         */
        DELETE,
        /**
         *
         */
        CREATE
    };

    /**
     *
     * @param conversion
     * @param Id
     * @param issuer
     * @param Subject
     */
    @Override
    public void setJwtAuth(Conversion conversion, String Id, String issuer, String Subject) {
        T tuser = jwt_check(conversion, Id, issuer, Subject);
        if (tuser != null) {
            user = tuser;
            setAuthenticated(true);
            auth_type = AUTH_TYPE.JWT;
        }
    }

    /**
     *
     * @param conversion
     * @param Id
     * @param issuer
     * @param Subject
     * @return
     */
    abstract protected T jwt_check(Conversion conversion, String Id, String issuer, String Subject);

    /**
     *
     */
    public static boolean DEBUG_AUTH = false;

    /**
     *
     * @param conversion
     * @param level
     * @return
     */
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

    /**
     *
     * @param conversion
     * @return
     */
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

    /**
     *
     * @param conversion
     * @return
     */
    public T getUser(Conversion conversion) {
        if (isUser(conversion)) {
            return (T) ((RestUser) conversion.getRequest().getUser()).getUser();
        }
        return null;
    }

    private static Map<Class, List<AllowRule>> allowRules = new HashMap<>();

    /**
     *
     * @param forClass
     * @param rule
     */
    public static void addAllowRule(Class forClass, AllowRule rule) {
        if (!allowRules.containsKey(forClass)) {
            allowRules.put(forClass, new ArrayList<>());
        }
        allowRules.get(forClass).add(rule);
    }

    /**
     *
     * @param conversion
     * @param object
     * @param accessType
     * @return
     */
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

    /**
     *
     * @param conversion
     * @param object
     * @param accessType
     * @return
     */
    public boolean mayWithFail(Conversion conversion, Object object, AccessType accessType) {
        if (may(conversion, object, accessType)) {
            return true;
        }
        conversion.getResponse().setStatus(HttpStatus.Forbidden);
        return false;
    }

}
