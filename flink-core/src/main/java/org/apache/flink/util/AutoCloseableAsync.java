package org.apache.flink.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Closeable interface which allows to close a resource in a non
 * blocking fashion.
 */
public interface AutoCloseableAsync extends AutoCloseable {

    /**
     * Trigger the closing of the resource and return the corresponding
     * close future.
     *
     * @return Future which is completed once the resource has been closed
     */
    CompletableFuture<Void> closeAsync();

    default void close() throws Exception {
        try {
            closeAsync().get();
        } catch (ExecutionException e) {
            throw new FlinkException("Could not close resource.", ExceptionUtils.stripExecutionException(e)); //flink-core util FlinkException.java
        }
    }

}
