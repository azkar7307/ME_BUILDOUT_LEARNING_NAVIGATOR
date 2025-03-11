"""Test configuration and constants"""

import uuid
from datetime import datetime

# API Configuration
BASE_URL = "http://localhost:8081"
TIMEOUT = 5

# Generate unique test identifiers
RUN_ID = uuid.uuid4().hex[:8]
TIMESTAMP = datetime.now().strftime("%Y%m%d_%H%M%S")

# Test Data with unique identifiers
STUDENT = {
    "name": "Akash",
    "id": None  # Will be populated during test execution
}

SUBJECT = {
    "subjectName": "ENGLISH",
    "id": None  # Will be populated during test execution
}

EXAM = {
    "examName": None,  # Will be derived from subject name
    "id": None  # Will be populated during test execution
}

# Test scenario variations - can be used to create additional test entities
STUDENT_VARIATIONS = [
    {"name": f"Student_A_{RUN_ID}_{TIMESTAMP}"},
    {"name": f"Student_B_{RUN_ID}_{TIMESTAMP}"},
    {"name": f"Student_C_{RUN_ID}_{TIMESTAMP}"}
]

SUBJECT_VARIATIONS = [
    {"subjectName": f"Math_{RUN_ID}"},
    {"subjectName": f"Science_{RUN_ID}"},
    {"subjectName": f"History_{RUN_ID}"}
]

# Test Control Settings
CLEANUP_AFTER_TESTS = True
ENABLE_DETAILED_LOGGING = True
RETRY_COUNT = 1  # Number of retries for failed tests

# Generate random suffixes for entity creation
def generate_unique_suffix():
    """Generate a unique suffix for test entities"""
    return f"{uuid.uuid4().hex[:6]}_{int(datetime.now().timestamp())}"