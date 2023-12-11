@echo off
if "%JAVA_HOME%" == "" (
    echo Can't find JAVA_HOME, Contact to 'admin@devkits.cn' for service!
	pause
) else (
    start javaw -Dfile.encoding=gbk -jar devkits-1.0.3.jar
)