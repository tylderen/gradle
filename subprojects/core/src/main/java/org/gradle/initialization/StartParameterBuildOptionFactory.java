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

package org.gradle.initialization;

import org.gradle.StartParameter;
import org.gradle.api.Transformer;
import org.gradle.api.internal.file.BasicFileResolver;
import org.gradle.initialization.option.BooleanBuildOption;
import org.gradle.initialization.option.BuildOption;
import org.gradle.initialization.option.BuildOptionFactory;
import org.gradle.initialization.option.CommandLineOptionConfiguration;
import org.gradle.initialization.option.NoArgumentBuildOption;
import org.gradle.initialization.option.StringBuildOption;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartParameterBuildOptionFactory implements BuildOptionFactory<StartParameter> {

    @Override
    public List<BuildOption<StartParameter>> create() {
        List<BuildOption<StartParameter>> options = new ArrayList<BuildOption<StartParameter>>();
        options.add(new ProjectCacheDirOption());
        options.add(new RerunTasksOption());
        options.add(new RecompileScriptsOption());
        options.add(new ProfileOption());
        options.add(new ContinueOption());
        options.add(new OfflineOption());
        options.add(new RefreshDependenciesOption());
        options.add(new DryRunOption());
        options.add(new ContinuousOption());
        options.add(new NoProjectDependenciesRebuildOption());
        options.add(new SettingsFileOption());
        options.add(new ConfigureOnDemandOption());
        options.add(new BuildCacheOption());
        options.add(new BuildScanOption());
        return options;
    }

    public static class ProjectCacheDirOption extends StringBuildOption<StartParameter> {
        public ProjectCacheDirOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("project-cache-dir", "Specify the project-specific cache directory. Defaults to .gradle in the root project directory.").hasArgument());
        }

        @Override
        public void applyTo(String value, StartParameter settings) {
            Transformer<File, String> resolver = new BasicFileResolver(settings.getCurrentDir());
            settings.setProjectCacheDir(resolver.transform(value));
        }
    }

    public static class RerunTasksOption extends NoArgumentBuildOption<StartParameter> {
        public RerunTasksOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("rerun-tasks", "Ignore previously cached task results."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setRerunTasks(true);
        }
    }

    public static class RecompileScriptsOption extends NoArgumentBuildOption<StartParameter> {
        public RecompileScriptsOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("recompile-scripts", "Force build script recompiling."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setRecompileScripts(true);
        }
    }

    public static class ProfileOption extends NoArgumentBuildOption<StartParameter> {
        public ProfileOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("profile", "Profile build execution time and generates a report in the <build_dir>/reports/profile directory."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setProfile(true);
        }
    }

    public static class ContinueOption extends NoArgumentBuildOption<StartParameter> {
        public ContinueOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("continue", "Continue task execution after a task failure."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setContinueOnFailure(true);
        }
    }

    public static class OfflineOption extends NoArgumentBuildOption<StartParameter> {
        public OfflineOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("offline", "Execute the build without accessing network resources."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setOffline(true);
        }
    }

    public static class RefreshDependenciesOption extends NoArgumentBuildOption<StartParameter> {
        public RefreshDependenciesOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("refresh-dependencies", "Refresh the state of dependencies."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setRefreshDependencies(true);
        }
    }

    public static class DryRunOption extends NoArgumentBuildOption<StartParameter> {
        public DryRunOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("dry-run", "m", "Run the builds with all task actions disabled."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setDryRun(true);
        }
    }

    public static class ContinuousOption extends NoArgumentBuildOption<StartParameter> {
        public ContinuousOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("continuous", "t", "Enables continuous build. Gradle does not exit and will re-execute tasks when task file inputs change.").incubating());
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setContinuous(true);
        }
    }

    public static class NoProjectDependenciesRebuildOption extends NoArgumentBuildOption<StartParameter> {
        public NoProjectDependenciesRebuildOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("no-rebuild", "a", "Do not rebuild project dependencies."));
        }

        @Override
        public void applyTo(StartParameter settings) {
            settings.setBuildProjectDependencies(false);
        }
    }

    public static class SettingsFileOption extends StringBuildOption<StartParameter> {
        public SettingsFileOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("settings-file", "c", "Specify the settings file.").hasArgument());
        }

        @Override
        public void applyTo(String value, StartParameter settings) {
            Transformer<File, String> resolver = new BasicFileResolver(settings.getCurrentDir());
            settings.setSettingsFile(resolver.transform(value));
        }
    }

    public static class ConfigureOnDemandOption extends BooleanBuildOption<StartParameter> {
        public static final String GRADLE_PROPERTY = "org.gradle.configureondemand";

        public ConfigureOnDemandOption() {
            super(StartParameter.class, GRADLE_PROPERTY, CommandLineOptionConfiguration.create("configure-on-demand", "Configure necessary projects only. Gradle will attempt to reduce configuration time for large multi-project builds.").incubating());
        }

        @Override
        public void applyTo(boolean value, StartParameter settings) {
            settings.setConfigureOnDemand(value);
        }
    }

    public static class BuildCacheOption extends BooleanBuildOption<StartParameter> {
        public static final String GRADLE_PROPERTY = "org.gradle.caching";

        public BuildCacheOption() {
            super(StartParameter.class, GRADLE_PROPERTY, CommandLineOptionConfiguration.create("build-cache", "Enables the Gradle build cache. Gradle will try to reuse outputs from previous builds.").incubating());
        }

        @Override
        public void applyTo(boolean value, StartParameter settings) {
            settings.setBuildCacheEnabled(value);
        }
    }

    public static class BuildScanOption extends BooleanBuildOption<StartParameter> {
        public BuildScanOption() {
            super(StartParameter.class, null, CommandLineOptionConfiguration.create("scan", "Creates a build scan. Gradle will emit a warning if the build scan plugin has not been applied. (https://gradle.com/build-scans)").incubating());
        }

        @Override
        public void applyTo(boolean value, StartParameter settings) {
            settings.setBuildScan(value);
        }
    }
}