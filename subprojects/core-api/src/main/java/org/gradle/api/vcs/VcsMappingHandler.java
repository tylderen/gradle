/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.vcs;

import org.gradle.api.Incubating;

/**
 * A {@code VcsMappingHandler} is used to declare VCS mappings to repositories.
 *
 * @since 4.2
 */
@Incubating
public interface VcsMappingHandler {
    /**
     * Adds a VCS mapping for the given repository name.
     * @param repositoryName name of the repository that provides the mapping
     * @param mapping dependency mapping
     *
     * @return the dependency mapping.
     */
    VcsMapping add(String repositoryName, VcsMapping mapping);

    // TODO: Other useful methods like DependencyHandler has for adding mappings
}