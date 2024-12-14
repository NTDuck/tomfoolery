package org.tomfoolery.infrastructures.utils.helpers.reflection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class Cloner {
    /**
     * @param sourceInstance the {@code Source} instance to clone from
     * @param destinationClass marks the {@code Destination} type to clone to
     * @return a new instance of {@code Destination} with attributes cloned from {@code sourceInstance}
     *
     * @exception IllegalArgumentException thrown when {@code Source} and {@code Destination} have different set of attributes or have teh same set of attributes but in a different order
     * @exception NoSuchMethodException thrown when {@code Destination} does not have a {@code lombok.AllArgsConstructor}
     */
    public static <Source, Destination> Destination cloneFrom(@NonNull Source sourceInstance, @NonNull Class<Destination> destinationClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        val sourceFields = sourceInstance.getClass().getDeclaredFields();
        val constructorArgs = new Object[sourceFields.length];

        for (int index = 0; index < sourceFields.length; index++) {
            val field = sourceFields[index];
            field.setAccessible(true);
            constructorArgs[index] = field.get(sourceInstance);
        }

        val matchingConstructor = findMatchingConstructorOfDestination(destinationClass, sourceFields);
        return matchingConstructor.newInstance(constructorArgs);
    }

    private static <D> Constructor<D> findMatchingConstructorOfDestination(Class<D> destClass, Field[] sourceFields) throws NoSuchMethodException {
        val parameterTypes = new Class<?>[sourceFields.length];

        for (int index = 0; index < sourceFields.length; index++)
            parameterTypes[index] = sourceFields[index].getType();

        return destClass.getDeclaredConstructor(parameterTypes);
    }
}
