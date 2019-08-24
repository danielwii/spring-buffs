package io.github.danielwii.buffs.spring.ids;

import lombok.extern.slf4j.Slf4j;

/**
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000   - 000000000000
 * N - 41:timestamp offset                           - 10:DC + Worker  - 12:serial
 * - 41bit timestamp: T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * - 10bit id: 1023 (5bit data center - 5bit worker)
 * - 12bit serial id: 4095
 */
@Slf4j
public class SnowFlakeIdWorker {

    /**
     * 2015-01-01 - 1420070400000L
     * 2016-01-01 - 1451606400000L
     * 2017-01-01 - 1483228800000L
     * 2018-01-01 - 1514764800000L
     * 2019-01-01 - 1546300800000L
     */
    private static final long START_EPOCH = 1514764800000L;

    private static final long DATACENTER_ID_BITS = 5;
    private static final long WORKER_ID_BITS     = 5;
    private static final long SEQUENCE_BITS      = 12;

    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_WORKER_ID     = ~(-1L << WORKER_ID_BITS);
//    private static final long MAX_SEQUENCE_ID   = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT      = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT  = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = DATACENTER_ID_SHIFT + DATACENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 2^5 - 1 = [0, 31]
    private long dataCenterId;
    // 2^5 - 1 = [0, 31]
    private long workerId;

    // 2^12 - 1 = [0, 4095]
    private long sequence  = 0L;
    private long lastStamp = -1L;

    private SnowFlakeIdWorker(long dataCenterId, long workerId) {
        if (dataCenterId > MAX_DATACENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId must be in [0, %s]", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId must be in [0, %s]", MAX_WORKER_ID));
        }

        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    private long tilNextMillis(long lastStamp) {
        long current = currentTimeMillis();
        while (current <= lastStamp) {
            current = currentTimeMillis();
        }
        return current;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static SnowFlakeIdWorker createWorker(long dataCenterId, long workerId) {
        return new SnowFlakeIdWorker(dataCenterId, workerId);
    }

    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastStamp) {
            log.error("clock is moving backwards. Rejecting requests until {}.", lastStamp);
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastStamp - timestamp));
        }

        sequence = (sequence + 1) & SEQUENCE_MASK;
        if (lastStamp == timestamp) {
            // 当前时间点序列溢出，则阻塞到下个时间点
            if (sequence == 0) {
                timestamp = tilNextMillis(lastStamp);
            }
        }

        lastStamp = timestamp;
        return ((timestamp - START_EPOCH) << TIMESTAMP_LEFT_SHIFT) |
                (dataCenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

//    public static void main(String[] args) {
//        SnowFlakeIdWorker idWorker = SnowFlakeIdWorker.createWorker(0, 0);
//        long startTime = System.nanoTime();
//        for (int i = 0; i < 50_000; i++) {
//            long id = idWorker.nextId();
//            System.out.println(String.format("0x%08X:%s", id, id));
//        }
//        System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
//    }

}
