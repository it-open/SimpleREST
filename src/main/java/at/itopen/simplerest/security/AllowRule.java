/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Conversion;
import at.itopen.simplerest.security.RestUser.AccessType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author roland
 * @param <T>
 */
public abstract class AllowRule<T> {

    /**
     *
     */
    public static class AllowLevel {

        int levelValue;

        /**
         *
         * @param levelValue
         */
        public AllowLevel(int levelValue) {
            this.levelValue = levelValue;
        }

        /**
         *
         * @param level
         * @return
         */
        public boolean can(AllowLevel level) {
            return (levelValue >= level.levelValue);
        }

        /**
         *
         * @return
         */
        public int getLevelValue() {
            return levelValue;
        }

    }

    private final static Map<String, AllowLevel> levels = new HashMap<>();

    static {
        addAllowLevel("none", new AllowLevel(0));
    }

    /**
     *
     * @param name
     * @param level
     */
    public static void addAllowLevel(String name, AllowLevel level) {
        levels.put(name.toUpperCase(), level);
    }

    /**
     *
     * @param name
     * @return
     */
    public static AllowLevel getAllowLevel(String name) {
        return levels.get(name.toUpperCase());
    }

    /**
     *
     */
    public AllowRule() {
    }

    /**
     *
     * @param conversion
     * @param data
     * @param user
     * @param accessType
     * @return
     */
    public abstract boolean check(Conversion conversion, T data, RestUser user, AccessType accessType);

}
