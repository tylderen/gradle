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

package org.gradle.language.swift

import groovy.io.FileType
import org.gradle.nativeplatform.fixtures.AbstractInstalledToolChainIntegrationSpec
import org.gradle.nativeplatform.fixtures.app.IncrementalSwiftModifyExpectedOutputApp
import org.gradle.nativeplatform.fixtures.app.IncrementalSwiftModifyExpectedOutputAppWithLib
import org.gradle.nativeplatform.fixtures.app.IncrementalSwiftStaleCompileOutputApp
import org.gradle.nativeplatform.fixtures.app.IncrementalSwiftStaleCompileOutputLib
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition

@Requires(TestPrecondition.SWIFT_SUPPORT)
class SwiftIncrementalCompileIntegrationTest extends AbstractInstalledToolChainIntegrationSpec {
    def "rebuilds application when a single source file changes"() {
        settingsFile << "rootProject.name = 'app'"
        def app = new IncrementalSwiftModifyExpectedOutputApp()

        given:
        app.writeToProject(testDirectory)

        and:
        buildFile << """
            apply plugin: 'swift-executable'
         """

        when:
        succeeds "assemble"

        then:
        result.assertTasksExecuted(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")
        result.assertTasksNotSkipped(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")
        executable("build/exe/main/debug/App").exec().out == app.expectedOutput

        when:
        app.applyChangesToProject(testDirectory)
        succeeds "assemble"

        then:
        result.assertTasksExecuted(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")
        result.assertTasksNotSkipped(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")
        executable("build/exe/main/debug/App").exec().out == app.expectedAlternateOutput
    }

    def "rebuilds application when a single source file in library changes"() {
        settingsFile << "include 'app', 'greeter'"
        def app = new IncrementalSwiftModifyExpectedOutputAppWithLib()

        given:
        buildFile << """
            project(':app') {
                apply plugin: 'swift-executable'
                dependencies {
                    implementation project(':greeter')
                }
            }
            project(':greeter') {
                apply plugin: 'swift-library'
            }
"""
        app.library.writeToProject(file("greeter"))
        app.executable.writeToProject(file("app"))

        when:
        succeeds ":app:assemble"

        then:
        result.assertTasksExecuted(":greeter:compileDebugSwift", ":greeter:linkDebug", ":app:compileDebugSwift", ":app:linkDebug", ":app:installMain", ":app:assemble")
        result.assertTasksNotSkipped(":greeter:compileDebugSwift", ":greeter:linkDebug", ":app:compileDebugSwift", ":app:linkDebug", ":app:installMain", ":app:assemble")
        installation("app/build/install/App").exec().out == app.expectedOutput

        when:
        app.library.applyChangesToProject(file('greeter'))
        succeeds ":app:assemble"

        then:
        result.assertTasksExecuted(":greeter:compileDebugSwift", ":greeter:linkDebug", ":app:compileDebugSwift", ":app:linkDebug", ":app:installMain", ":app:assemble")
        result.assertTasksNotSkipped(":greeter:compileDebugSwift", ":greeter:linkDebug", ":app:linkDebug", ":app:installMain", ":app:assemble")
        installation("app/build/install/App").exec().out == app.alternateLibraryOutput
    }

    def "stale object files are removed for executable"() {
        settingsFile << "rootProject.name = 'app'"
        def app = new IncrementalSwiftStaleCompileOutputApp()

        given:
        app.writeToProject(testDirectory)

        and:
        buildFile << """
            apply plugin: 'swift-executable'
         """

        and:
        succeeds "assemble"
        app.applyChangesToProject(testDirectory)

        expect:
        succeeds "assemble"
        result.assertTasksExecuted(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")
        result.assertTasksNotSkipped(":compileDebugSwift", ":linkDebug", ":installMain", ":assemble")

        files("build/obj/main")*.name as Set == app.alternateApp.expectedIntermediateFilenames
        executable("build/exe/main/debug/App").assertExists()
        installation("build/install/App").exec().out == app.expectedAlternateOutput
    }

    def "stale object files are removed library"() {
        def lib = new IncrementalSwiftStaleCompileOutputLib()
        settingsFile << "rootProject.name = 'hello'"

        given:
        lib.writeToProject(testDirectory)

        and:
        buildFile << """
            apply plugin: 'swift-library'
         """

        and:
        succeeds "assemble"
        lib.applyChangesToProject(testDirectory)

        expect:
        succeeds "assemble"
        result.assertTasksExecuted(":compileDebugSwift", ":linkDebug", ":assemble")
        result.assertTasksNotSkipped(":compileDebugSwift", ":linkDebug", ":assemble")


        files("build/obj/main")*.name as Set == lib.alternateApp.expectedIntermediateFilenames
        sharedLibrary("build/lib/main/debug/Hello").assertExists()
    }

    private Set<File> files(Object path) {
        File directory = file(path)
        directory.assertIsDir()

        def result = [] as Set
        directory.eachFileRecurse(FileType.FILES) {
            result += it
        }

        return result
    }
}
