@echo off
set REBEL_HOME=C:\jrebel
set JAVA_OPTS=-javaagent:%REBEL_HOME%\jrebel.jar -Xms256m -Xmx512m -XX:MaxPermSize=256m %JAVA_OPTS%
call "%~dp0\standalone.bat" %*