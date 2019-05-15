/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import at.itopen.simplerest.conversion.Conversion;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author roland
 */
public abstract class AllowRule<T> {

    static {
        addLEVEL("none", new AllowLevel(0));
    }

    public static class AllowLevel {

        int levelValue;

        public AllowLevel(int levelValue) {
            this.levelValue = levelValue;
        }

        public boolean can(AllowLevel level) {
            return (levelValue >= level.levelValue);
        }

    }

    private final static Map<String, AllowLevel> levels = new HashMap<>();

    public static void addLEVEL(String name, AllowLevel level) {
        levels.put(name.toUpperCase(), level);
    }

    public AllowLevel getLevel(String name) {
        return levels.get(name.toUpperCase());
    }

    public AllowRule() {
    }

    public abstract AllowLevel check(Conversion conversion, T data);

}
