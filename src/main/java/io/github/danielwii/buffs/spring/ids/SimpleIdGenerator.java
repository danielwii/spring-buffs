package io.github.danielwii.buffs.spring.ids;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * m bits - n bits             -
 * m:pool - n:timestamp offset -
 */
@Slf4j
public class SimpleIdGenerator {

    private static final long START_EPOCH = 1514764800000L;

    private long poolId;
    private long lastStamp = -1L;

    private long sequence = 0L;
    private long sequenceBits;
    private long sequenceMask;

    public SimpleIdGenerator(long poolId, long sequenceBits) {
        this.poolId = poolId;
        this.sequenceBits = sequenceBits;
        this.sequenceMask = ~(-1L << sequenceBits);
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

    private synchronized long nextId() {
        long timestamp = currentTimeMillis();

        sequence = (sequence + 1) & sequenceMask;
        if (lastStamp == timestamp) {
            // 当前时间点序列溢出，则阻塞到下个时间点
            if (sequence == 0) {
                timestamp = tilNextMillis(timestamp);
            }
        }

        lastStamp = timestamp;
        return
                ((lastStamp - START_EPOCH) << sequenceBits) |
//                (poolId << sequenceBits) |
                        sequence;
    }

//    public static void main(String[] args) {
//        SimpleIdGenerator idGenerator = new SimpleIdGenerator(10, 7);
//        long              startTime   = System.nanoTime();
//        Set               ids         = new LinkedHashSet();
//        for (int i = 0; i < 50_000; i++) {
//            long id = idGenerator.nextId();
//            ids.add(id);
//            System.out.println(String.format(
//                    "%s - %s - 0x%08X:%s", idGenerator.lastStamp, idGenerator.sequence, id, id));
//            System.out.println(Long.toBinaryString(idGenerator.lastStamp));
//        }
//        System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
//        System.out.println(ids.size());
//    }

}
