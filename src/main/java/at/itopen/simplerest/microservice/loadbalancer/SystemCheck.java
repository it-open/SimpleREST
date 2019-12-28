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
    long bsent, brecv, timestamp = 0;

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
        sid.setCpu_info(hal.getProcessor().toString());
        sid.setCpu_packages(hal.getProcessor().getPhysicalPackageCount());
        sid.setCpu_cores(hal.getProcessor().getPhysicalProcessorCount());
        sid.setCpu_count(hal.getProcessor().getLogicalProcessorCount());
        sid.setCpu_freq(hal.getProcessor().getVendorFreq());
        sid.setCpu_load_avg_1(hal.getProcessor().getSystemLoadAverage(3)[0]);
        sid.setCpu_load_avg_5(hal.getProcessor().getSystemLoadAverage(3)[1]);
        sid.setCpu_load_avg_15(hal.getProcessor().getSystemLoadAverage(3)[2]);
        sid.setCpu_load(hal.getProcessor().getSystemCpuLoadBetweenTicks(hal.getProcessor().getSystemCpuLoadTicks()));

        sid.setMem_max(hal.getMemory().getTotal());
        sid.setMem_used(hal.getMemory().getTotal() - hal.getMemory().getAvailable());
        sid.setMem_swap_used_percent((100.0 / hal.getMemory().getVirtualMemory().getSwapTotal()) * hal.getMemory().getVirtualMemory().getSwapUsed());

        File root = new File(".");
        sid.setDisk_max(root.getTotalSpace());
        sid.setDisk_used(root.getTotalSpace() - root.getFreeSpace());
        long disk_queue_size = 0;

        for (HWDiskStore disk : hal.getDiskStores()) {
            disk_queue_size += disk.getCurrentQueueLength();
        }
        sid.setDisk_queue_length(disk_queue_size);

        for (NetworkIF nw : hal.getNetworkIFs()) {
            for (String ip : nw.getIPv4addr()) {
                if (ip.startsWith("127")) {
                    continue;
                }
                if (nw.getBytesRecv() == 0) {
                    continue;
                }
                sid.setNet_ip(ip);
                sid.setNet_speed(nw.getSpeed());

                if (timestamp > 0) {
                    sid.setNet_read_bytes_per_second((nw.getBytesRecv() - brecv) / (System.currentTimeMillis() - timestamp));
                    sid.setNet_write_bytes_per_second((nw.getBytesSent() - bsent) / (System.currentTimeMillis() - timestamp));
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
