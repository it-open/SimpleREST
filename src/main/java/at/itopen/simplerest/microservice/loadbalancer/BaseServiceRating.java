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
public class BaseServiceRating extends AbstratctServiceRating {

    /**
     *
     * @param service
     * @return
     */
    @Override
    public double rate(Service service) {
        double cpupower = service.getInfo().getCpu_count() * service.getInfo().getCpu_freq();
        cpupower = cpupower / (4L * (3L * 1000L * 1000L * 1000L)); // Normal 4 Cores 3Ghz
        cpupower = cpupower * (1 - service.getInfo().getCpu_load());

        double ram = 1.0;
        if (((service.getInfo().getMem_max() - service.getInfo().getMem_used()) / 1024 / 1024) < 1000) {
            ram = 0.7; // less than 1Gb free
        }
        if (((service.getInfo().getMem_max() - service.getInfo().getMem_used()) / 1024 / 1024) < 500) {
            ram = 0.2; // less than 500Mb Free
        }
        double disk = 1.0;
        if (service.getInfo().getDisk_queue_length() > 500) {
            disk = 0.5;
        }

        long tp = (service.getInfo().getNet_read_bytes_per_second() + service.getInfo().getNet_write_bytes_per_second()) * 8;
        long max = service.getInfo().getNet_speed();
        double net = 1 - ((1 / max) * tp);

        return cpupower * ram * disk * net;

    }

    /**
     *
     * @return
     */
    @Override
    public double wearleveling() {
        return 0.1;
    }

}
