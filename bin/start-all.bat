@echo off
setlocal EnableExtensions

rem OA demo one-click startup script for Windows.
rem Start order: infrastructure -> auth/system -> OA services -> gateway -> UI.

set "ROOT=%~dp0.."
for %%I in ("%ROOT%") do set "ROOT=%%~fI"
set "NACOS_HOME=%NACOS_HOME%"
if not defined NACOS_HOME set "NACOS_HOME=D:\env\nacos-server-3.0.2"
set "JAVA_OPTS=-Dfile.encoding=utf-8 -Xms256m -Xmx512m"

echo.
echo ========================================
echo   RuoYi OA Demo - Start All
echo ========================================
echo Root : %ROOT%
echo Nacos: %NACOS_HOME%
echo.

call :check_command java "Java"
if errorlevel 1 goto :fail

if not defined JAVA_HOME (
    for /f "delims=" %%J in ('where java 2^>nul') do if not defined JAVA_HOME for %%K in ("%%J") do set "JAVA_HOME=%%~dpK.."
)
for %%J in ("%JAVA_HOME%") do set "JAVA_HOME=%%~fJ"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] JAVA_HOME is not set and could not be inferred from java on PATH.
    echo         Set JAVA_HOME to a JDK directory before running this script.
    goto :fail
)
echo [OK] JAVA_HOME=%JAVA_HOME%

call :check_port 3306 "MySQL"
if errorlevel 1 goto :fail
call :check_port 6379 "Redis"
if errorlevel 1 goto :fail

call :check_or_start_nacos
if errorlevel 1 goto :fail

call :check_jar "ruoyi-auth\target\ruoyi-auth.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-system\target\ruoyi-modules-system.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-approval\target\ruoyi-modules-approval.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-todo\target\ruoyi-modules-todo.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-calendar\target\ruoyi-modules-calendar.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-contacts\target\ruoyi-modules-contacts.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-modules\ruoyi-portal\target\ruoyi-modules-portal.jar"
if errorlevel 1 goto :fail
call :check_jar "ruoyi-gateway\target\ruoyi-gateway.jar"
if errorlevel 1 goto :fail

call :start_jar 9200 "ruoyi-auth" "ruoyi-auth\target" "ruoyi-auth.jar"
call :start_jar 9201 "ruoyi-system" "ruoyi-modules\ruoyi-system\target" "ruoyi-modules-system.jar"
call :start_jar 9210 "ruoyi-approval" "ruoyi-modules\ruoyi-approval\target" "ruoyi-modules-approval.jar"
call :start_jar 9211 "ruoyi-todo" "ruoyi-modules\ruoyi-todo\target" "ruoyi-modules-todo.jar"
call :start_jar 9212 "ruoyi-calendar" "ruoyi-modules\ruoyi-calendar\target" "ruoyi-modules-calendar.jar"
call :start_jar 9213 "ruoyi-contacts" "ruoyi-modules\ruoyi-contacts\target" "ruoyi-modules-contacts.jar"
call :start_jar 9214 "ruoyi-portal" "ruoyi-modules\ruoyi-portal\target" "ruoyi-modules-portal.jar"

call :check_port 8000 "Gateway"
if errorlevel 1 (
    echo [INFO] Port 8000 is free. Starting OA gateway.
    start "OA gateway" /D "%ROOT%\ruoyi-gateway\target" cmd /k "java %JAVA_OPTS% -jar ruoyi-gateway.jar"
) else (
    echo [WARN] Port 8000 is already occupied. OA gateway will not be started.
    echo [WARN] Check whether the ERP gateway owns port 8000 before opening the UI.
)

if exist "%ROOT%\ruoyi-ui\node_modules" (
    start "OA frontend" /D "%ROOT%\ruoyi-ui" cmd /k "npm run dev"
) else (
    echo [WARN] ruoyi-ui\node_modules does not exist. Run npm install first.
)

echo.
echo [OK] Core OA services have been started in separate windows.
echo [INFO] URLs: UI http://localhost, Gateway http://localhost:8000
echo [INFO] Optional gen/job/file/monitor services are not started by this script.
echo [INFO] gen 9202 conflicts with the ERP service in the shared environment.
echo.
exit /b 0

:check_command
where %~1 >nul 2>nul
if errorlevel 1 (
    echo [ERROR] %~2 was not found in PATH.
    exit /b 1
)
exit /b 0

:check_port
powershell -NoProfile -ExecutionPolicy Bypass -Command "$c=Get-NetTCPConnection -LocalPort %~1 -State Listen -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>nul
if errorlevel 1 (
    echo [ERROR] %~2 is not listening on port %~1.
    exit /b 1
)
echo [OK] %~2 is listening on %~1.
exit /b 0

:check_or_start_nacos
powershell -NoProfile -ExecutionPolicy Bypass -Command "$c=Get-NetTCPConnection -LocalPort 8848 -State Listen -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>nul
if not errorlevel 1 (
    echo [OK] Nacos is already listening on 8848.
    exit /b 0
)
if not exist "%NACOS_HOME%\bin\startup.cmd" (
    echo [ERROR] Nacos startup script not found: %NACOS_HOME%\bin\startup.cmd
    exit /b 1
)
echo [INFO] Starting Nacos in standalone mode...
start "Nacos" /D "%NACOS_HOME%\bin" cmd /k "startup.cmd -m standalone"
for /L %%N in (1,1,30) do (
    timeout /t 1 /nobreak >nul
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$c=Get-NetTCPConnection -LocalPort 8848 -State Listen -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>nul
    if not errorlevel 1 (
        echo [OK] Nacos is listening on 8848.
        exit /b 0
    )
)
echo [ERROR] Nacos did not become ready within 30 seconds.
exit /b 1

:check_jar
if not exist "%ROOT%\%~1" (
    echo [ERROR] Jar not found: %ROOT%\%~1
    echo         Build it first with Maven package -DskipTests.
    exit /b 1
)
exit /b 0

:start_jar
powershell -NoProfile -ExecutionPolicy Bypass -Command "$c=Get-NetTCPConnection -LocalPort %~1 -State Listen -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>nul
if not errorlevel 1 (
    echo [SKIP] %~2 is already listening on %~1.
    exit /b 0
)
echo [INFO] Starting %~2 on %~1...
start "%~2" /D "%ROOT%\%~3" cmd /k "java %JAVA_OPTS% -jar %~4"
exit /b 0

:fail
echo.
echo [FAILED] Startup aborted. Fix the error above and run start-all.bat again.
pause
exit /b 1
