@echo off
setlocal EnableExtensions

rem OA demo one-click startup script for Windows.
rem Start order: infrastructure -> shared auth/system -> OA services -> shared gateway -> UI.
rem This script never stops ERP services on ports 9217-9223.

set "ROOT=%~dp0.."
for %%I in ("%ROOT%") do set "ROOT=%%~fI"
set "NACOS_HOME=%NACOS_HOME%"
if not defined NACOS_HOME set "NACOS_HOME=D:\env\nacos-server-3.0.2"
set "JAVA_OPTS=-Dfile.encoding=utf-8 -Xms256m -Xmx512m"
set "CHECK_ONLY=0"

echo.
echo ========================================
echo   RuoYi OA Demo - Start All
echo ========================================
echo Root : %ROOT%
echo Nacos: %NACOS_HOME%
echo.

if /I "%~1"=="--check" (
    set "CHECK_ONLY=1"
    goto :check_only
)

call :cleanup_oa
if errorlevel 1 goto :fail

call :check_command java "Java"
if errorlevel 1 goto :fail
call :check_command mysql "MySQL client"
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
call :check_oa_tables
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
if errorlevel 1 goto :fail
call :start_jar 9201 "ruoyi-system" "ruoyi-modules\ruoyi-system\target" "ruoyi-modules-system.jar"
if errorlevel 1 goto :fail
call :start_jar 9211 "ruoyi-todo" "ruoyi-modules\ruoyi-todo\target" "ruoyi-modules-todo.jar"
if errorlevel 1 goto :fail
call :start_jar 9212 "ruoyi-calendar" "ruoyi-modules\ruoyi-calendar\target" "ruoyi-modules-calendar.jar"
if errorlevel 1 goto :fail
call :start_jar 9213 "ruoyi-contacts" "ruoyi-modules\ruoyi-contacts\target" "ruoyi-modules-contacts.jar"
if errorlevel 1 goto :fail
call :start_jar 9215 "ruoyi-approval" "ruoyi-modules\ruoyi-approval\target" "ruoyi-modules-approval.jar"
if errorlevel 1 goto :fail
call :start_jar 9214 "ruoyi-portal" "ruoyi-modules\ruoyi-portal\target" "ruoyi-modules-portal.jar"
if errorlevel 1 goto :fail

call :is_port_open 8000
if errorlevel 1 (
    echo [INFO] Port 8000 is free. Starting OA gateway.
    start "OA gateway" /D "%ROOT%\ruoyi-gateway\target" cmd /k "java %JAVA_OPTS% -jar ruoyi-gateway.jar"
    call :wait_for_port 8000 "ruoyi-gateway"
    if errorlevel 1 goto :fail
) else (
    echo [WARN] Port 8000 is already occupied. OA gateway will not be started.
    echo [WARN] The existing shared gateway must contain both OA and ERP routes.
)

if exist "%ROOT%\ruoyi-ui\node_modules" (
    start "OA frontend" /D "%ROOT%\ruoyi-ui" cmd /k "set port=80&& npm run dev"
    call :wait_for_port 80 "OA frontend"
    if errorlevel 1 goto :fail
) else (
    echo [ERROR] ruoyi-ui\node_modules does not exist. Run npm install first.
    goto :fail
)

call :verify_required_ports
if errorlevel 1 goto :fail
call :verify_nacos_services
if errorlevel 1 goto :fail
call :verify_application
if errorlevel 1 goto :fail

echo.
echo [OK] OA services have been started in separate windows.
echo [INFO] URLs: UI http://localhost, Gateway http://localhost:8000
echo [INFO] Nacos: http://localhost:18088
echo [INFO] Login: admin / admin123
echo [INFO] Optional gen/job/file/monitor services are not started by this script.
echo [INFO] Shared Nacos/auth/system/gateway and ERP ports were intentionally preserved.
echo.
exit /b 0

:check_only
echo [INFO] Running startup checks without changing any process...
call :check_command java "Java"
if errorlevel 1 goto :fail
call :check_command mysql "MySQL client"
if errorlevel 1 goto :fail
call :check_port 3306 "MySQL"
if errorlevel 1 goto :fail
call :check_port 6379 "Redis"
if errorlevel 1 goto :fail
call :check_oa_tables
if errorlevel 1 goto :fail
if not exist "%NACOS_HOME%\bin\startup.cmd" (
    echo [ERROR] Nacos startup script not found: %NACOS_HOME%\bin\startup.cmd
    goto :fail
)
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
if not exist "%ROOT%\ruoyi-ui\node_modules" (
    echo [ERROR] ruoyi-ui\node_modules does not exist. Run npm install first.
    goto :fail
)
echo [OK] Startup checks passed. No process was stopped or started.
exit /b 0

:cleanup_oa
echo [INFO] Cleaning existing OA application processes...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$root=[regex]::Escape('%ROOT%');" ^
  "$targets=Get-CimInstance Win32_Process | Where-Object {" ^
  "  $_.CommandLine -and (" ^
  "    $_.CommandLine -match 'ruoyi-modules-(approval|todo|calendar|contacts|portal)\.jar' -or" ^
  "    ($_.CommandLine -match 'vue-cli-service' -and $_.CommandLine -match $root)" ^
  "  )" ^
  "};" ^
  "$targets | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"
timeout /t 3 /nobreak >nul

for %%P in (9211 9212 9213 9214 9215 80) do (
    call :is_port_open %%P
    if not errorlevel 1 (
        echo [ERROR] Port %%P is still occupied after OA cleanup.
        echo         Close the remaining process before starting OA.
        exit /b 1
    )
)
echo [OK] Existing OA application processes have been cleaned.
echo [INFO] Shared Nacos/auth/system/gateway and ERP ports were intentionally preserved.
exit /b 0

:check_command
where %~1 >nul 2>nul
if errorlevel 1 (
    echo [ERROR] %~2 was not found in PATH.
    exit /b 1
)
exit /b 0

:check_port
call :is_port_open %~1
if errorlevel 1 (
    echo [ERROR] %~2 is not listening on port %~1.
    exit /b 1
)
echo [OK] %~2 is listening on %~1.
exit /b 0

:check_or_start_nacos
call :is_port_open 8848
if not errorlevel 1 (
    echo [OK] Nacos is already listening on 8848.
    exit /b 0
)
if not exist "%NACOS_HOME%\bin\startup.cmd" (
    echo [ERROR] Nacos startup script not found: %NACOS_HOME%\bin\startup.cmd
    exit /b 1
)
echo [INFO] Starting Nacos in standalone mode...
start "OA Nacos" /D "%NACOS_HOME%\bin" cmd /k "startup.cmd -m standalone"
call :wait_for_port 8848 "Nacos"
exit /b %errorlevel%

:check_jar
if not exist "%ROOT%\%~1" (
    echo [ERROR] Jar not found: %ROOT%\%~1
    echo         Build it first with Maven package -DskipTests.
    exit /b 1
)
exit /b 0

:start_jar
call :is_port_open %~1
if not errorlevel 1 (
    echo [SKIP] %~2 is already listening on %~1.
    exit /b 0
)
echo [INFO] Starting %~2 on %~1...
start "%~2" /D "%ROOT%\%~3" cmd /k "java %JAVA_OPTS% -jar %~4"
call :wait_for_port %~1 "%~2"
exit /b %errorlevel%

:wait_for_port
for /L %%N in (1,1,90) do (
    timeout /t 1 /nobreak >nul
    call :is_port_open %~1
    if not errorlevel 1 (
        echo [OK] %~2 is listening on %~1.
        exit /b 0
    )
)
echo [ERROR] %~2 did not become ready on port %~1 within 90 seconds.
exit /b 1

:verify_required_ports
echo [INFO] Verifying required OA ports...
for %%P in (8848 9200 9201 9211 9212 9213 9214 9215 8000 80) do (
    call :is_port_open %%P
    if errorlevel 1 (
        echo [ERROR] Required port %%P is not listening.
        exit /b 1
    )
)
echo [OK] All required OA ports are listening.
exit /b 0

:check_oa_tables
set "OA_TABLE_COUNT="
for /f "tokens=*" %%C in ('mysql -h 127.0.0.1 -P 3306 -u root -N -B ry-cloud -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='ry-cloud' AND table_name IN ('oa_approval_apply','oa_approval_flow','oa_todo_item','oa_schedule_event','oa_contact_person');" 2^>nul') do set "OA_TABLE_COUNT=%%C"
if not "%OA_TABLE_COUNT%"=="5" (
    echo [ERROR] OA database schema is not ready. Expected 5 oa_* tables, found %OA_TABLE_COUNT%.
    echo         Stop OA services and run:
    echo         mysql -h 127.0.0.1 -P 3306 -u root ry-cloud ^< sql\oa_table_prefix_migration.sql
    exit /b 1
)
echo [OK] OA database schema contains all 5 oa_* tables.
exit /b 0

:verify_nacos_services
echo [INFO] Verifying Nacos service registrations...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$required=@('ruoyi-auth','ruoyi-system','ruoyi-todo','ruoyi-calendar','ruoyi-contacts','ruoyi-approval','ruoyi-portal','ruoyi-gateway');" ^
  "$bad=@();" ^
  "foreach($name in $required){" ^
  "  try{$r=Invoke-RestMethod -Uri ('http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName='+$name+'&namespaceId=public') -TimeoutSec 10;" ^
  "      if(-not @($r.hosts | Where-Object {$_.healthy -and $_.enabled}).Count){$bad+=$name}}" ^
  "  catch{$bad+=$name}" ^
  "};" ^
  "if($bad.Count){Write-Output ('[ERROR] Missing or unhealthy Nacos services: '+($bad -join ', ')); exit 1};" ^
  "Write-Output '[OK] All required services have healthy Nacos instances.'"
exit /b %errorlevel%

:verify_application
echo [INFO] Verifying login and core OA endpoints...
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "try{" ^
  "  $body=@{username='admin';password='admin123'} | ConvertTo-Json;" ^
  "  $login=Invoke-RestMethod -Uri 'http://127.0.0.1:8000/auth/login' -Method Post -ContentType 'application/json' -Body $body -TimeoutSec 15;" ^
  "  if($login.code -ne 200 -or -not $login.data.access_token){throw 'login failed'};" ^
  "  $headers=@{Authorization='Bearer '+$login.data.access_token};" ^
  "  foreach($path in @('/oa/approval/list','/oa/todo/list','/oa/calendar/events','/oa/contacts/list','/oa/dashboard/panel')){" ^
  "    $r=Invoke-RestMethod -Uri ('http://127.0.0.1:8000'+$path) -Headers $headers -TimeoutSec 15;" ^
  "    if($r.code -ne 200){throw ($path+' returned code '+$r.code)}" ^
  "  };" ^
  "  Write-Output '[OK] Login and core OA endpoints passed.'" ^
  "}catch{Write-Output ('[ERROR] OA endpoint verification failed: '+$_.Exception.Message); exit 1}"
exit /b %errorlevel%

:is_port_open
powershell -NoProfile -ExecutionPolicy Bypass -Command "$c=Get-NetTCPConnection -LocalPort %~1 -State Listen -ErrorAction SilentlyContinue; if($c){exit 0}else{exit 1}" >nul 2>nul
exit /b %errorlevel%

:fail
echo.
echo [FAILED] Startup aborted. Fix the error above and run start-all.bat again.
if "%CHECK_ONLY%"=="1" exit /b 1
pause
exit /b 1
