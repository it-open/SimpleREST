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
        double cpupower = service.getInfo().getCpuCount() * service.getInfo().getCpuFreq();
        cpupower = cpupower / (4L * (3L * 1000L * 1000L * 1000L)); // Normal 4 Cores 3Ghz
        cpupower = cpupower * (1 - service.getInfo().getCpuLoad());

        double ram = 1.0;
        if (((service.getInfo().getMemMax() - service.getInfo().getMemUsed()) / 1024 / 1024) < 1000) {
            ram = 0.7; // less than 1Gb free
        }
        if (((service.getInfo().getMemMax() - service.getInfo().getMemUsed()) / 1024 / 1024) < 500) {
            ram = 0.2; // less than 500Mb Free
        }
        double disk = 1.0;
        if (service.getInfo().getDiskQueueLength() > 500) {
            disk = 0.5;
        }

        long tp = (service.getInfo().getNetReadBytesPerSecond() + service.getInfo().getNetWriteBytesPerSecond()) * 8;
        long max = service.getInfo().getNetSpeed();
        double net = 1 - ((1 / max) * tp);

        return cpupower * ram * disk * net;

    }

}
