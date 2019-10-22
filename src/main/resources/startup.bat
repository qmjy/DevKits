@echo off
if "%JAVA_HOME%" == "" (
    echo Can't find JAVA_HOME
) else (
    start javaw -jar devkits-1.0.0.jar
)