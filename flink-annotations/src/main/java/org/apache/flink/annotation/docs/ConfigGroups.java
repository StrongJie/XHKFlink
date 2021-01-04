package org.apache.flink.annotation.docs;


import org.apache.flink.annotation.Internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on classes containing config options that enables the separation of options into different
 * tables based on key prefixes. A config option is assigned to a {@link ConfigGroup} if the option key matches
 * the group prefix. If a key matches multiple prefixes the longest matching prefix takes priority. An option is never
 * assigned to multiple groups. Options that don't match any group are implicitly added to a default group.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Internal
public @interface ConfigGroups {
    ConfigGroup[] groups() default {};
}
