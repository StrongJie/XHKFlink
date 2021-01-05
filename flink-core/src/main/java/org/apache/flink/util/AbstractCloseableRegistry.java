package org.apache.flink.util;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.flink.annotation.Internal;
import org.apache.flink.shaded.guava18.com.google.common.annotations.VisibleForTesting;
import org.apache.flink.shaded.guava18.com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * This is the abstract base class for registries that allow to register instances of {@link Closeable}, which are all
 * closed if this registry is closed.
 *
 * <p>Registering to an already closed registry will throw an exception and close the provided {@link Closeable}
 *
 * <p>All methods in this class are thread-safe.
 *
 * @param <C> Type of the closeable this registers
 * @param <T> Type for potential meta data associated with the registering closeables
 */
@Internal
public abstract class AbstractCloseableRegistry<C extends Closeable, T> implements Closeable{

    /** Lock that guards state of this registry. **/
    private final Object lock;

    /** Map from tracked Closeables to some associated meta data. */
    @GuardedBy("lock")
    protected final Map<Closeable, T> closeableToRef;  //该文件内的核心数据结构

    /** Indicates if this registry is closed. */
    @GuardedBy("lock")
    private boolean closed;

    /**
     * This is the abstract base class for registries that allow to register instances of {@link Closeable}, which are all
     * closed if this registry is closed.
     *
     * <p>Registering to an already closed registry will throw an exception and close the provided {@link Closeable}
     *
     * <p>All methods in this class are thread-safe.
     *
     * @param <C> Type of the closeable this registers
     * @param <T> Type for potential meta data associated with the registering closeables
     */
    public AbstractCloseableRegistry(@Nonnull Map<Closeable, T> closeableToRef) {
        this.lock = new Object();
        this.closeableToRef = Preconditions.checkNotNull(closeableToRef);
        this.closed = false;
    }

    /**
     * Registers a {@link Closeable} with the registry. In case the registry is already closed, this method throws an
     * {@link IllegalStateException} and closes the passed {@link Closeable}.
     *
     * @param closeable Closeable tor register
     * @throws IOException exception when the registry was closed before
     */
    public final void registerCloseable(C closeable) throws IOException {
        if (null == closeable) {
            return;
        }

        synchronized (getSynchronizationLock()) {
            if (!closed) {
                doRegister(closeable, closeableToRef);
                return;
            }
        }

        IOUtils.closeQuietly(closeable);
        throw new IOException("Cannot register Closeable, registry is already closed. Closing argument.");
    }

    /**
     * Removes a {@link Closeable} from the registry.
     *
     * @param closeable instance to remove from the registry.
     * @return true if the closeable was previously registered and became unregistered through this call.
     */
    public final boolean unregisterCloseable(C closeable) {

        if (null == closeable) {
            return false;
        }

        synchronized (getSynchronizationLock()) {
            return doUnRegister(closeable, closeableToRef);
        }
    }

    @Override
    public void close() throws IOException {
        Collection<Closeable> toCloseCopy;

        synchronized (getSynchronizationLock()) {
            if (closed) {
                return;
            }

            closed = true;

            toCloseCopy = getReferencesToClose();

            closeableToRef.clear();
        }

        IOUtils.closeQuietly((Closeable) toCloseCopy);
    }

    public boolean isClosed() {
        synchronized (getSynchronizationLock()) {
            return closed;
        }
    }

    protected Collection<Closeable> getReferencesToClose() {
        return new ArrayList<>(closeableToRef.keySet());
    }

    /**
     * Does the actual registration of the closeable with the registry map. This should not do any long running or
     * potentially blocking operations as is is executed under the registry's lock.
     */
    protected abstract void doRegister(@Nonnull C closeable, @Nonnull Map<Closeable, T> closeableMap);

    /**
     * Does the actual un-registration of the closeable from the registry map. This should not do any long running or
     * potentially blocking operations as is is executed under the registry's lock.
     */
    protected abstract boolean doUnRegister(@Nonnull C closeable, @Nonnull Map<Closeable, T> closeableMap);

    protected final Object getSynchronizationLock() {
        return lock;
    }

    /**
     * Adds a mapping to the registry map, respecting locking.
     */
    protected final void addCloseableInternal(Closeable closeable, T metaData) {
        synchronized (getSynchronizationLock()) {
            closeableToRef.put(closeable, metaData);
        }
    }

    /**
     * Removes a mapping from the registry map, respecting locking.
     */
    protected final void removeCloseableInternal(Closeable closeable) {
        synchronized (getSynchronizationLock()) {
            closeableToRef.remove(closeable);
        }
    }

    @VisibleForTesting
    public final int getNumberOfRegisteredCloseables() {
        synchronized (getSynchronizationLock()) {
            return closeableToRef.size();
        }
    }

    @VisibleForTesting
    public final boolean isCloseableRegistered(Closeable c) {
        synchronized (getSynchronizationLock()) {
            return closeableToRef.containsKey(c);
        }
    }



}
