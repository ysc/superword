package org.apdplat.superword.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ysc on 17/06/2017.
 */
public class ThreadPool {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
}
