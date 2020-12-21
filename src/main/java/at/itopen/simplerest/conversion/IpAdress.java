/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.conversion;

import java.net.InetSocketAddress;

/**
 *
 * @author roland
 */
public class IpAdress {

    /**
     *
     * @param adress
     * @param port
     */
    public IpAdress(byte[] adress, int port) {
        this.adress = adress;
        this.port = port;
    }

    /**
     *
     * @param inetSocketAddress
     */
    public IpAdress(InetSocketAddress inetSocketAddress) {
        adress = inetSocketAddress.getAddress().getAddress();
        port = inetSocketAddress.getPort();
    }

    /**
     *
     * @param addr
     * @param port
     */
    public IpAdress(String addr, int port) {
        this.adress = new byte[4];
        String[] ipAddressInArray = addr.split("\\.");
        for (int i = 3; i >= 0; i--) {
            this.adress[i] = (byte) Long.parseLong(ipAddressInArray[3 - i]);
        }
        this.port = port;
    }

    byte[] adress;
    int port;

    /**
     *
     * @return
     */
    public byte[] getAdress() {
        return adress;
    }

    /**
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @return
     */
    public String getIpAdressAsString() {
        return toString();
    }

    /**
     *
     * @return
     */
    public String getLocationAsString() {
        return getIpAdressAsString() + ":" + getPort();
    }

    /**
     *
     * @return
     */
    public String toString() {
        long i = longIP();
        return ((i >> 24) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "."
                + (i & 0xFF);
    }

    /**
     *
     * @return
     */
    public Long longIP() {

        long num = 0;
        for (int i = 0; i < this.adress.length; i++) {
            int power = 3 - i;
            num += this.adress[i] % 256 * Math.pow(256, power);
        }
        return num;
    }

}
