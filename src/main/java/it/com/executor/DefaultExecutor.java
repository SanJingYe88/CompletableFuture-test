package it.com.executor;

import java.util.concurrent.*;

public class DefaultExecutor {

    private final static Integer CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private final static Integer MAX_SIZE = CORE_SIZE * 2;

    private static final ExecutorService EXECUTOR_SERVICE;

    static {
        EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("lh"));
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }
}
