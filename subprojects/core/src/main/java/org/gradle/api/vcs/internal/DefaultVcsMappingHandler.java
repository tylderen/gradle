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

package org.gradle.api.vcs.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.gradle.api.vcs.VcsMapping;
import org.gradle.api.vcs.VcsRepository;
import org.gradle.api.vcs.VcsRepositoryContainer;

// TODO: Add MethodMixIn so we can do:
// vcsMappings { foo maven("com.example", "foo") }
// instead of calling add() directly.
public class DefaultVcsMappingHandler implements VcsMappingHandlerInternal {
    private final VcsRepositoryContainer vcsRepositories;
    private final Multimap<VcsRepository, VcsMapping> vcsMappings;

    public DefaultVcsMappingHandler(VcsRepositoryContainer vcsRepositories) {
        this.vcsRepositories = vcsRepositories;
        this.vcsMappings = LinkedHashMultimap.create();
    }

    @Override
    public VcsMapping maven(String group, String name) {
        Preconditions.checkNotNull(group, "Group must be specified");
        Preconditions.checkNotNull(name, "Module name must be specified");
        return new DefaultMavenVcsMapping(group, name);
    }

    @Override
    public VcsMapping add(String repositoryName, VcsMapping mapping) {
        VcsRepository vcsRepository = vcsRepositories.getByName(repositoryName);
        vcsMappings.put(vcsRepository, mapping);
        return mapping;
    }

    @Override
    public Multimap<VcsRepository, VcsMapping> getVcsMappings() {
        return vcsMappings;
    }
}