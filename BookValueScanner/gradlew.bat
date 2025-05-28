@rem Placeholder gradlew.bat script.
@rem In a real project, this would be the full Gradle wrapper script.

@echo off
SET DIRNAME=%~dp0
IF "%DIRNAME%" == "" SET DIRNAME=.
SET APP_BASE_NAME=%~n0
SET APP_HOME=%DIRNAME%

SET DEFAULT_JVM_OPTS=

%JAVA_HOME%\bin\java %DEFAULT_JVM_OPTS% %GRADLE_OPTS% %JAVA_OPTS% -Dorg.gradle.appname=%APP_BASE_NAME% -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
