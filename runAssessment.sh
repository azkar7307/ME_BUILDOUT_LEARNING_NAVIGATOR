#!/bin/bash

# Configuration
FE_PORT=8081
SPRING_LOG="gradle_output.log"
MAX_WAIT_SECONDS=120

# Function to check if port is available
is_port_available() {
    ! ss -tna | grep "LISTEN" | grep -q ":$1 "
}

# Function to wait for Spring Boot to be ready
wait_for_spring_boot() {
    local attempt=0
    echo "Waiting for Spring Boot to start..."
    while ! ss -tna | grep "LISTEN" | grep -q ":$FE_PORT "; do
        if [ $attempt -ge $MAX_WAIT_SECONDS ]; then
            echo "Timeout waiting for Spring Boot to start"
            return 1
        fi
        if ! ps -p $SPRING_PID > /dev/null; then
            echo "Spring Boot process died unexpectedly"
            echo "Last few lines of log:"
            tail -n 20 "$SPRING_LOG"
            return 1
        fi
        if (( attempt % 5 == 0 )); then
            echo -n "Still waiting... ($attempt/$MAX_WAIT_SECONDS seconds)"
        fi
        echo -n "."
        sleep 1
        ((attempt++))
    done
    echo -e "\nSpring Boot is ready!"
    return 0
}

# Cleanup function
cleanup() {
    echo "Cleaning up..."

    # Kill Spring Boot if running
    if [[ -n "$SPRING_PID" ]] && ps -p $SPRING_PID > /dev/null; then
        kill $SPRING_PID 2>/dev/null || true
    fi

    # Kill any process on the frontend port
    if command -v lsof &>/dev/null; then
        lsof -ti tcp:$FE_PORT | xargs kill 2>/dev/null || true
    else
        fuser -k ${FE_PORT}/tcp 2>/dev/null || true
    fi

    # Stop Gradle daemon
    ./gradlew --stop 2>/dev/null || true

    # Remove log file
    rm -f "$SPRING_LOG"
}

# Set up cleanup on script exit
trap cleanup EXIT

# Initial cleanup
cleanup
sleep 2  # Give the system time to release the port

# Ensure port is available
if ! is_port_available $FE_PORT; then
    echo "Port $FE_PORT is already in use"
    exit 1
fi

# Start Spring Boot
echo "Starting Spring Boot..."
./gradlew bootRun > "$SPRING_LOG" 2>&1 &
SPRING_PID=$!
export SPRING_PID

# Wait for Spring Boot to be ready
if ! wait_for_spring_boot; then
    echo "Failed to start Spring Boot"
    exit 1
fi

# Run tests
cd assessment || exit 1

# Run the tests
python3 tests.py
TEST_EXIT_CODE=$?

# Exit with test result
exit $TEST_EXIT_CODE
