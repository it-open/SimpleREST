/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.path;

/**
 *
 * @author roland
 */
public class RootPath {
    
    private static RestPath ROOT=null;
    private static RestEndpoint NOT_FOUND=null;
    private static RestEndpoint INDEX=null;
    private static RestEndpoint EXCEPTION=null;

    public static void setROOT(RestPath ROOT) {
        RootPath.ROOT = ROOT;
    }

    public static RestPath getROOT() {
        return ROOT;
    }

    public static RestEndpoint getNOT_FOUND() {
        return NOT_FOUND;
    }

    public static void setNOT_FOUND(RestEndpoint NOT_FOUND) {
        RootPath.NOT_FOUND = NOT_FOUND;
    }

    public static RestEndpoint getINDEX() {
        return INDEX;
    }

    public static void setINDEX(RestEndpoint INDEX) {
        RootPath.INDEX = INDEX;
    }

    public static RestEndpoint getEXCEPTION() {
        return EXCEPTION;
    }

    public static void setEXCEPTION(RestEndpoint EXCEPTION) {
        RootPath.EXCEPTION = EXCEPTION;
    }
    
    
    
    
    
    
    
    
    
}
