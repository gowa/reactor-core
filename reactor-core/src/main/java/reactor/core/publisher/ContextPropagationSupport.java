/*
 * Copyright (c) 2023 VMware Inc. or its affiliates, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.core.publisher;

import reactor.util.Logger;
import reactor.util.Loggers;

final class ContextPropagationSupport {
    static final Logger LOGGER = Loggers.getLogger(ContextPropagationSupport.class);

    // Note: If reflection is used for this field, then the name of the field should end with 'Available'.
    // The preprocessing for native-image support is in Spring Framework, and is a short term solution.
    // The field should end with 'Available'. See org.springframework.aot.nativex.feature.PreComputeFieldFeature.
    // Ultimately the long term solution should be provided by Reactor Core.
    static final boolean isContextPropagationOnClasspath;
    static boolean propagateContextToThreadLocals = false;

    static {
        boolean contextPropagation = false;
        try {
            Class.forName("io.micrometer.context.ContextRegistry");
            contextPropagation = true;
        } catch (ClassNotFoundException notFound) {
        } catch (LinkageError linkageErr) {
        } catch (Throwable err) {
            LOGGER.error("Unexpected exception while detecting ContextPropagation feature." +
                    " The feature is considered disabled due to this:", err);
        }
        isContextPropagationOnClasspath = contextPropagation;
    }

    /**
     * Is Micrometer {@code context-propagation} API on the classpath?
     *
     * @return true if context-propagation is available at runtime, false otherwise
     */
    static boolean isContextPropagationAvailable() {
        return isContextPropagationOnClasspath;
    }

    static boolean shouldPropagateContextToThreadLocals() {
        return isContextPropagationOnClasspath && propagateContextToThreadLocals;
    }

    static boolean shouldRestoreThreadLocalsInSomeOperators() {
        return isContextPropagationOnClasspath && !propagateContextToThreadLocals;
    }
}
