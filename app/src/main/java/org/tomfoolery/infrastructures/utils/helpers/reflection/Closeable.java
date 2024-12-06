package org.tomfoolery.infrastructures.utils.helpers.reflection;

import lombok.val;

public interface Closeable extends AutoCloseable {
    @Override
    default void close() throws Exception {
        Class<?> class_ = this.getClass();

        do {
            for (val field : class_.getDeclaredFields()) {
                field.setAccessible(true);
                val resource = field.get(this);

                if (resource instanceof AutoCloseable closeableResource)
                    closeableResource.close();
            }

            class_ = class_.getSuperclass();

        } while (class_ != null);
    }
}
