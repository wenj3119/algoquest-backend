#!/bin/sh
# Judge container entrypoint — baked into the image, NOT user-controlled.
# /workspace: read-only ConfigMap mount (Solution.java + Main.java)
# /tmp:       writable emptyDir tmpfs (compile output + result file)
#
# Result anti-spoofing: the framework's Main.java writes CASE_RESULT lines to
# /tmp/judge-result/result.txt AFTER all solution calls complete. This script
# cats the file AFTER the JVM exits, between sentinels. User code running inside
# the JVM cannot inject output after this script's echo statements.
# Any sentinel lines printed by user code via System.out appear BEFORE the real
# sentinels; K8sJobJudgeExecutor uses extractLastBetween() to take the last pair.

set -e

# Compile (output goes to stderr, redirected to a temp file for clean reporting)
if ! javac -proc:none -d /tmp /workspace/Solution.java /workspace/Main.java 2>/tmp/compile-stderr.txt; then
    echo "===JUDGE_COMPILE_ERROR==="
    cat /tmp/compile-stderr.txt
    echo "===JUDGE_COMPILE_ERROR_END==="
    exit 1
fi

# Run solution — user code executes here.
# Do NOT use set -e here: non-zero exit from JVM (e.g. System.exit(2)) is expected.
java -cp /tmp Main || true

# Output the result file written by Main.java — this happens AFTER the JVM exits.
echo "===JUDGE_RESULTS_START==="
cat /tmp/judge-result/result.txt 2>/dev/null || true
echo "===JUDGE_RESULTS_END==="
