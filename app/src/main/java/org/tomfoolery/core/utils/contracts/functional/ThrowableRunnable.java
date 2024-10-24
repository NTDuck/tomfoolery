package org.tomfoolery.core.utils.contracts.functional;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}
