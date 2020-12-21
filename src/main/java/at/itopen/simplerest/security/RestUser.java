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
 * @param <T> Type
 */
public abstract class RestUser<T> extends BasicUser implements BasicAuthUser, JwtAuthUser {

    T user = null;

    /**
     *
     */
    public enum AUTHTYPE {

        /**
         *
         */
        BASIC,
        /**
         *
         */
        JWT
    }

    private AUTHTYPE auth_type = null;

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
            auth_type = AUTHTYPE.BASIC;
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
    public AUTHTYPE getAuthType() {
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
     * @param id
     * @param issuer
     * @param subject
     */
    @Override
    public void setJwtAuth(Conversion conversion, String id, String issuer, String subject) {
        T tuser = jwtCheck(conversion, id, issuer, subject);
        if (tuser != null) {
            user = tuser;
            setAuthenticated(true);
            auth_type = AUTHTYPE.JWT;
        }
    }

    /**
     *
     * @param conversion
     * @param id
     * @param issuer
     * @param subject
     * @return
     */
    abstract protected T jwtCheck(Conversion conversion, String id, String issuer, String subject);

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
        return (getLevel(((RestUser<T>) conversion.getRequest().getUser()).getUser()) >= level.levelValue);
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
        return !(((RestUser) conversion.getRequest().getUser()).getUser() == null);

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
            boolean test = rule.check(conversion, object, this, accessType);
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
