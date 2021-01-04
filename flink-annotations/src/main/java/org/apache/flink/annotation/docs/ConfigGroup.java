package org.apache.flink.annotation.docs;

import org.apache.flink.annotation.Internal;

import java.lang.annotation.Target;

/**
 * A class that specifies a group of config options. The name of the group will be used as the basis for the
 * filename of the generated html file, as defined in {@link ConfigOptionsDocGenerator}.
 *
 * @see ConfigGroups
 */
@Target({})
@Internal
public @interface ConfigGroup {
    String name();
    String keyPrefix();
}
