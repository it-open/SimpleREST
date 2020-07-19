/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

/**
 *
 * @author roland
 */
public class SystemInfoData {
    
    private String operatingSystem;
    private String cpu_info;
    private int cpu_packages;
    private int cpu_cores;
    private int cpu_count;
    private long cpu_freq;
    
    private double cpu_load;
    
    private double cpu_load_avg_1;
    private double cpu_load_avg_5;
    
    private double cpu_load_avg_15;
    private long mem_max;
    private long mem_used;
    private double mem_swap_used_percent;
    
    private long disk_max;
    private long disk_used;
    private long disk_queue_length;
    
    
    private String net_ip;
    private long net_speed;
    private long net_read_bytes_per_second;
    private long net_write_bytes_per_second;

    /**
     * @return the operatingSystem
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * @param operatingSystem the operatingSystem to set
     */
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * @return the cpu_info
     */
    public String getCpu_info() {
        return cpu_info;
    }

    /**
     * @param cpu_info the cpu_info to set
     */
    public void setCpu_info(String cpu_info) {
        this.cpu_info = cpu_info;
    }

    /**
     * @return the cpu_packages
     */
    public int getCpu_packages() {
        return cpu_packages;
    }

    /**
     * @param cpu_packages the cpu_packages to set
     */
    public void setCpu_packages(int cpu_packages) {
        this.cpu_packages = cpu_packages;
    }

    /**
     * @return the cpu_cores
     */
    public int getCpu_cores() {
        return cpu_cores;
    }

    /**
     * @param cpu_cores the cpu_cores to set
     */
    public void setCpu_cores(int cpu_cores) {
        this.cpu_cores = cpu_cores;
    }

    /**
     * @return the cpu_count
     */
    public int getCpu_count() {
        return cpu_count;
    }

    /**
     * @param cpu_count the cpu_count to set
     */
    public void setCpu_count(int cpu_count) {
        this.cpu_count = cpu_count;
    }

    /**
     * @return the cpu_freq
     */
    public long getCpu_freq() {
        return cpu_freq;
    }

    /**
     * @param cpu_freq the cpu_freq to set
     */
    public void setCpu_freq(long cpu_freq) {
        this.cpu_freq = cpu_freq;
    }

    /**
     * @return the cpu_load
     */
    public double getCpu_load() {
        return cpu_load;
    }

    /**
     * @param cpu_load the cpu_load to set
     */
    public void setCpu_load(double cpu_load) {
        this.cpu_load = cpu_load;
    }

    /**
     * @return the cpu_load_avg_1
     */
    public double getCpu_load_avg_1() {
        return cpu_load_avg_1;
    }

    /**
     * @param cpu_load_avg_1 the cpu_load_avg_1 to set
     */
    public void setCpu_load_avg_1(double cpu_load_avg_1) {
        this.cpu_load_avg_1 = cpu_load_avg_1;
    }

    /**
     * @return the cpu_load_avg_5
     */
    public double getCpu_load_avg_5() {
        return cpu_load_avg_5;
    }

    /**
     * @param cpu_load_avg_5 the cpu_load_avg_5 to set
     */
    public void setCpu_load_avg_5(double cpu_load_avg_5) {
        this.cpu_load_avg_5 = cpu_load_avg_5;
    }

    /**
     * @return the cpu_load_avg_10
     */
    public double getCpu_load_avg_15() {
        return cpu_load_avg_15;
    }

    /**
     * @param cpu_load_avg_15
     */
    public void setCpu_load_avg_15(double cpu_load_avg_15) {
        this.cpu_load_avg_15 = cpu_load_avg_15;
    }

    /**
     * @return the mem_max
     */
    public long getMem_max() {
        return mem_max;
    }

    /**
     * @param mem_max the mem_max to set
     */
    public void setMem_max(long mem_max) {
        this.mem_max = mem_max;
    }

    /**
     * @return the mem_used
     */
    public long getMem_used() {
        return mem_used;
    }

    /**
     * @param mem_used the mem_used to set
     */
    public void setMem_used(long mem_used) {
        this.mem_used = mem_used;
    }

    /**
     * @return the disk_max
     */
    public long getDisk_max() {
        return disk_max;
    }

    /**
     * @param disk_max the disk_max to set
     */
    public void setDisk_max(long disk_max) {
        this.disk_max = disk_max;
    }

    /**
     * @return the disk_used
     */
    public long getDisk_used() {
        return disk_used;
    }

    /**
     * @param disk_used the disk_used to set
     */
    public void setDisk_used(long disk_used) {
        this.disk_used = disk_used;
    }

   
    /**
     * @return the net_ip
     */
    public String getNet_ip() {
        return net_ip;
    }

    /**
     * @param net_ip the net_ip to set
     */
    public void setNet_ip(String net_ip) {
        this.net_ip = net_ip;
    }

    /**
     * @return the net_speed
     */
    public long getNet_speed() {
        return net_speed;
    }

    /**
     * @param net_speed the net_speed to set
     */
    public void setNet_speed(long net_speed) {
        this.net_speed = net_speed;
    }

    /**
     * @return the net_read_bytes_per_second
     */
    public long getNet_read_bytes_per_second() {
        return net_read_bytes_per_second;
    }

    /**
     * @param net_read_bytes_per_second the net_read_bytes_per_second to set
     */
    public void setNet_read_bytes_per_second(long net_read_bytes_per_second) {
        this.net_read_bytes_per_second = net_read_bytes_per_second;
    }

    /**
     * @return the net_write_bytes_per_second
     */
    public long getNet_write_bytes_per_second() {
        return net_write_bytes_per_second;
    }

    /**
     * @param net_write_bytes_per_second the net_write_bytes_per_second to set
     */
    public void setNet_write_bytes_per_second(long net_write_bytes_per_second) {
        this.net_write_bytes_per_second = net_write_bytes_per_second;
    }

    /**
     * @return the mem_swap_used_percent
     */
    public double getMem_swap_used_percent() {
        return mem_swap_used_percent;
    }

    /**
     * @param mem_swap_used_percent the mem_swap_used_percent to set
     */
    public void setMem_swap_used_percent(double mem_swap_used_percent) {
        this.mem_swap_used_percent = mem_swap_used_percent;
    }

    /**
     * @return the disk_queue_length
     */
    public long getDisk_queue_length() {
        return disk_queue_length;
    }

    /**
     * @param disk_queue_length the disk_queue_length to set
     */
    public void setDisk_queue_length(long disk_queue_length) {
        this.disk_queue_length = disk_queue_length;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        
        sb.append("OS:").append(operatingSystem).append("\n");
        sb.append("CPU:").append(cpu_info).append("\n");
        sb.append("CPU Packages:").append(cpu_packages).append("\n");
        sb.append("CPU Cores:").append(cpu_cores).append("\n");
        sb.append("CPU Count:").append(cpu_count).append("\n");
        sb.append("CPU Freq:").append(cpu_freq).append("\n");
        sb.append("CPU Load:").append(cpu_load).append("\n");
        sb.append("CPU Load avg 1:").append(cpu_load_avg_1).append("\n");
        sb.append("CPU Load avg 5:").append(cpu_load_avg_5).append("\n");
        sb.append("CPU Load avg 15:").append(cpu_load_avg_15).append("\n");

        sb.append("MEM Max:").append(mem_max).append("\n");
        sb.append("MEM Used:").append(mem_used).append("\n");
        sb.append("MEM Swap Percent:").append(mem_swap_used_percent).append("\n");

        sb.append("DISK Max:").append(disk_max).append("\n");
        sb.append("DISK Used:").append(disk_used).append("\n");
        sb.append("DISK Queue:").append(disk_queue_length).append("\n");


        sb.append("NET Ip:").append(net_ip).append("\n");
        sb.append("NET Speed:").append(net_speed).append("\n");
        sb.append("NET Read-Bytes/Sec:").append(net_read_bytes_per_second).append("\n");
        sb.append("NET Write-Bytes/Sec:").append(net_write_bytes_per_second).append("\n");
        return sb.toString();
    }
    
    

    
    
    
}
