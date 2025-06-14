@echo off
setlocal enabledelayedexpansion

REM ====== Configuration ======
set FILE_PATH=matrix_input.txt
set MATRIX_SIZE=1000
set THREAD_COUNTS=1 2 4 8 10 20 50 100 200 400 500

REM ====== Compile Java files ======
echo Compiling Java programs...
javac -d build\classes src\opsys_project\MatrixMultiplier_SingleThread.java
javac -d build\classes src\opsys_project\MatrixMultiplier_MultiThread.java

REM ====== Create CSV file for results ======
set OUTPUT_FILE=matrix_benchmark_results.csv
echo Threads,ExecutionTime(ms),Mode > %OUTPUT_FILE%

REM ====== Run Single-threaded version ======
echo Running single-threaded version...
(
    echo %FILE_PATH%
    echo %MATRIX_SIZE%
) | java -cp build\classes opsys_project.MatrixMultiplier_SingleThread > temp_output.txt

for /f "tokens=4 delims= " %%a in ('findstr "Time taken" temp_output.txt') do (
    echo 1,%%a,Single >> %OUTPUT_FILE%
)

REM ====== Run Multi-threaded versions ======
for %%T in (%THREAD_COUNTS%) do (
    echo Running multi-threaded version with %%T threads...
    (
        echo %FILE_PATH%
        echo %MATRIX_SIZE%
        echo %%T
    ) | java -cp build\classes opsys_project.MatrixMultiplier_MultiThread > temp_output.txt

    for /f "tokens=4 delims= " %%a in ('findstr "Time taken" temp_output.txt') do (
        echo %%T,%%a,Multi >> %OUTPUT_FILE%
    )
)

del temp_output.txt
echo Done. Results saved to %OUTPUT_FILE%.
pause