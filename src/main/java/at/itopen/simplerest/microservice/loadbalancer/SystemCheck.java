/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import java.io.File;
import static java.lang.Thread.interrupted;
import java.util.logging.Level;
import java.util.logging.Logger;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

/**
 *
 * @author roland
 */
public class SystemCheck {

    Thread checkerThread;
    SystemInfoData sid = new SystemInfoData();
    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    long bsent;
    long brecv;
    long timestamp = 0;

    private SystemCheck() {
        updateSystemInfo();
        checkerThread = new Thread("System Check") {
            @Override
            public void run() {
                while (!interrupted()) {
                    try {
                        updateSystemInfo();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Logger.getLogger(SystemCheck.class.getName()).log(Level.SEVERE, "Check", e);
                    }
                }
            }

        };
        checkerThread.start();
    }

    /**
     *
     * @return
     */
    public static SystemCheck getInstance() {
        return SystemCheckHolder.INSTANCE;
    }

    private static class SystemCheckHolder {

        private static final SystemCheck INSTANCE = new SystemCheck();
    }

    private void updateSystemInfo() {
        SystemInfoData sid = new SystemInfoData();
        sid.setOperatingSystem(si.getOperatingSystem().toString());
        sid.setCpuInfo(hal.getProcessor().toString());
        sid.setCpuPackages(hal.getProcessor().getPhysicalPackageCount());
        sid.setCpuCores(hal.getProcessor().getPhysicalProcessorCount());
        sid.setCpuCount(hal.getProcessor().getLogicalProcessorCount());
        sid.setCpuFreq(hal.getProcessor().getMaxFreq());
        sid.setCpuLoadAvg1(hal.getProcessor().getSystemLoadAverage(3)[0]);
        sid.setCpuLoadAvg5(hal.getProcessor().getSystemLoadAverage(3)[1]);
        sid.setCpuLoadAvg15(hal.getProcessor().getSystemLoadAverage(3)[2]);
        sid.setCpuLoad(hal.getProcessor().getSystemCpuLoadBetweenTicks(hal.getProcessor().getSystemCpuLoadTicks()));

        sid.setMemMax(hal.getMemory().getTotal());
        sid.setMemUsed(hal.getMemory().getTotal() - hal.getMemory().getAvailable());
        sid.setMemSwapUsedPercent((100.0 / hal.getMemory().getVirtualMemory().getSwapTotal()) * hal.getMemory().getVirtualMemory().getSwapUsed());

        File root = new File(".");
        sid.setDiskMax(root.getTotalSpace());
        sid.setDiskUsed(root.getTotalSpace() - root.getFreeSpace());
        long diskQueueSize = 0;

        for (HWDiskStore disk : hal.getDiskStores()) {
            diskQueueSize += disk.getCurrentQueueLength();
        }
        sid.setDiskQueueLength(diskQueueSize);

        for (NetworkIF nw : hal.getNetworkIFs()) {
            for (String ip : nw.getIPv4addr()) {
                if (ip.startsWith("127")) {
                    continue;
                }
                if (nw.getBytesRecv() == 0) {
                    continue;
                }
                sid.setNetIp(ip);
                sid.setNetSpeed(nw.getSpeed());

                if (timestamp > 0) {
                    sid.setNetReadBytesPerSecond((nw.getBytesRecv() - brecv) / (System.currentTimeMillis() - timestamp));
                    sid.setNetWriteBytesPerSecond((nw.getBytesSent() - bsent) / (System.currentTimeMillis() - timestamp));
                }
                timestamp = System.currentTimeMillis();
                brecv = nw.getBytesRecv();
                bsent = nw.getBytesSent();

            }
        }
        this.sid = sid;

    }

    /**
     *
     * @return
     */
    public SystemInfoData getaktSystemInfoData() {
        return sid;
    }

}
