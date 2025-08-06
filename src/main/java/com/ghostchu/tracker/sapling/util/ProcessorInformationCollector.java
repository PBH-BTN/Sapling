package com.ghostchu.tracker.sapling.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Component
public class ProcessorInformationCollector {
    private static long[] prevTicks = new long[CentralProcessor.TickType.values().length];
    private static final SystemInfo si = new SystemInfo();
    @Getter
    private CpuUsage cpuUsage = new CpuUsage();
    @Getter
    private double[] sysLoads = new double[3];

    @Scheduled(fixedRate = 1000)
    public void updateCpuSamples() {
        CentralProcessor cpu = si.getHardware().getProcessor();
        long[] currentTicks = cpu.getSystemCpuLoadTicks();
        long[] ticks = cpu.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long total = user + nice + sys + idle + iowait + irq + softIrq + steal;
        CpuUsage usage = new CpuUsage();
        usage.us = (user + nice) * 100.0 / total;
        usage.sy = sys * 100.0 / total;
        usage.id = idle * 100.0 / total;
        usage.wa = iowait * 100.0 / total;
        usage.hi = irq * 100.0 / total;
        usage.si = softIrq * 100.0 / total;
        usage.st = steal * 100.0 / total;
        this.cpuUsage = usage;
        this.sysLoads = cpu.getSystemLoadAverage(3);
        ;
        prevTicks = currentTicks;
    }

    public static String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%d days, %2d:%02d", days, hours, minutes);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CpuUsage {
        double us;
        double sy;
        double id;
        double wa;
        double hi;
        double si;
        double st;
    }

}