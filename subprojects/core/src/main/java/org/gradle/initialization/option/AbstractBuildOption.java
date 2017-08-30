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

package org.gradle.initialization.option;

/**
 * Provides a basic infrastructure for build option implementations.
 *
 * @since 4.2
 */
public abstract class AbstractBuildOption<T> implements BuildOption<T> {

    private final Class<T> settingsType;
    protected final String gradleProperty;
    protected final CommandLineOptionConfiguration commandLineOptionConfiguration;

    protected AbstractBuildOption(Class<T> settingsType, String gradleProperty) {
        this(settingsType, gradleProperty, null);
    }

    public AbstractBuildOption(Class<T> settingsType, String gradleProperty, CommandLineOptionConfiguration commandLineOptionConfiguration) {
        this.settingsType = settingsType;
        this.gradleProperty = gradleProperty;
        this.commandLineOptionConfiguration = commandLineOptionConfiguration;
    }

    @Override
    public String getGradleProperty() {
        return gradleProperty;
    }

    public boolean isTrue(String value) {
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    boolean hasCommandLineOption() {
        return commandLineOptionConfiguration != null;
    }
}
