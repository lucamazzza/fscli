package ch.supsi.fscli.backend.data;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {
    private static final AtomicLong nextId = new AtomicLong(0);

    private IdGenerator() {
        // Prevent instantiation
    }

    public static Long getNextId() {
        return nextId.incrementAndGet();
    }
}
