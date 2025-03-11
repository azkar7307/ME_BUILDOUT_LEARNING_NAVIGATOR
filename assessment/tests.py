#!/usr/bin/env python
"""
API Test Suite for Learning Management System
This test suite verifies all the core functionalities of the LMS API,
mirroring the Cypress test cases.
"""

from test_utils import APIClient, TestAssertions
from test_config import (
    BASE_URL, STUDENT, SUBJECT, EXAM, 
    STUDENT_VARIATIONS, SUBJECT_VARIATIONS,
    generate_unique_suffix, CLEANUP_AFTER_TESTS,
    ENABLE_DETAILED_LOGGING
)
from test_runner import TestRunner
import random
import uuid
import copy

# Initialize API client and test assertions
client = APIClient()
assertions = TestAssertions()

# Create deep copies of the test data to avoid modifying the originals
student_data = copy.deepcopy(STUDENT)
subject_data = copy.deepcopy(SUBJECT)
exam_data = copy.deepcopy(EXAM)

# Test state to track created entities for cleanup
TEST_DATA = {
    "created_entities": {
        "students": [],
        "subjects": [],
        "exams": []
    }
}

def log_info(message):
    """Log informational messages if detailed logging is enabled"""
    if ENABLE_DETAILED_LOGGING:
        print(f"[INFO] {message}")

def create_student():
    """
    Test Case: Create a new student
    Verifies that a student can be created with the expected default values.
    """
    student_name = student_data["name"]
    
    log_info(f"Creating student with name: {student_name}")
    response = client.post("students", json={"name": student_name})
    assertions.assert_status_code(response, 201)
    assertions.assert_json_field(response, "name", student_name)
    assertions.assert_json_field(response, "enrolledSubjects", [])
    assertions.assert_json_field(response, "enrolledExams", [])
    
    # Store the student ID for later tests
    student_id = response.json().get("id")
    student_data["id"] = student_id
    TEST_DATA["created_entities"]["students"].append(student_id)
    log_info(f"Created student with ID: {student_id}")

def create_subject():
    """
    Test Case: Create a new subject
    Verifies that a subject can be created successfully.
    """
    subject_name = subject_data["subjectName"]
    
    log_info(f"Creating subject with name: {subject_name}")
    response = client.post("subjects", json={"subjectName": subject_name})
    assertions.assert_status_code(response, 201)
    assertions.assert_json_field(response, "subjectName", subject_name)
    
    # Store the subject ID for later tests
    subject_id = response.json().get("id")
    subject_data["id"] = subject_id
    TEST_DATA["created_entities"]["subjects"].append(subject_id)
    log_info(f"Created subject with ID: {subject_id}")

def enroll_student_in_subject():
    """
    Test Case: Enroll a student in a subject
    Verifies that a student can be enrolled in a subject and 
    the subject appears in the student's enrolledSubjects array.
    """
    student_id = student_data["id"]
    subject_id = subject_data["id"]
    student_name = student_data["name"]
    subject_name = subject_data["subjectName"]
    
    if not all([student_id, subject_id]):
        raise AssertionError("Student ID or Subject ID not available")
    
    log_info(f"Enrolling student {student_id} in subject {subject_id}")
    response = client.post(f"students/{student_id}/subjects/{subject_id}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field(response, "name", student_name)
    assertions.assert_json_list_field_contains_text(response, "enrolledSubjects", "subjectName", subject_name)
    assertions.assert_json_field(response, "enrolledExams", [])

def prevent_duplicate_subject_enrollment():
    """
    Test Case: Prevent duplicate subject enrollment
    Verifies that a student cannot be enrolled in the same subject twice.
    """
    student_id = student_data["id"]
    subject_id = subject_data["id"]
    
    if not all([student_id, subject_id]):
        raise AssertionError("Student ID or Subject ID not available")
    
    log_info(f"Attempting to re-enroll student {student_id} in subject {subject_id}")
    response = client.post(f"students/{student_id}/subjects/{subject_id}", expected_code=409)
    assertions.assert_status_code(response, 409)
    assertions.assert_json_field_contains_text(response, "message", f"Student with id: {student_id} has already enrolled in subject")

def get_student_by_id():
    """
    Test Case: Get student by ID
    Verifies that student details can be retrieved, including enrolled subjects.
    """
    student_id = student_data["id"]
    student_name = student_data["name"]
    subject_name = subject_data["subjectName"]
    
    if not student_id:
        raise AssertionError("Student ID not available")
    
    log_info(f"Getting student with ID: {student_id}")
    response = client.get(f"students/{student_id}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field(response, "name", student_name)
    assertions.assert_json_field_contains(response, "enrolledSubjects", "subjectName", subject_name)
    assertions.assert_json_field(response, "enrolledExams", [])

def create_exam():
    """
    Test Case: Create an exam for a subject
    Verifies that an exam can be created for a specific subject.
    """
    subject_id = subject_data["id"]
    subject_name = subject_data["subjectName"]
    
    if not subject_id:
        raise AssertionError("Subject ID not available")
    
    log_info(f"Creating exam for subject with ID: {subject_id}")
    response = client.post(f"exams/subjects/{subject_id}")
    assertions.assert_status_code(response, 201)
    assertions.assert_json_field_contains_text(response, "examName", subject_name)
    
    # Store the exam ID and name for later tests
    exam_id = response.json().get("id")
    exam_data["id"] = exam_id
    exam_data["examName"] = response.json().get("examName")
    TEST_DATA["created_entities"]["exams"].append(exam_id)
    log_info(f"Created exam with ID: {exam_id} and name: {exam_data['examName']}")

def enroll_student_in_exam():
    """
    Test Case: Enroll a student in an exam
    Verifies that a student can be enrolled in an exam and
    the exam appears in the student's enrolledExams array.
    """
    student_id = student_data["id"]
    exam_id = exam_data["id"]
    student_name = student_data["name"]
    subject_name = subject_data["subjectName"]
    
    if not all([student_id, exam_id]):
        raise AssertionError("Student ID or Exam ID not available")
    
    log_info(f"Enrolling student {student_id} in exam {exam_id}")
    response = client.post(f"students/{student_id}/exams/{exam_id}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field(response, "name", student_name)
    assertions.assert_json_list_field_contains_text(response, "enrolledSubjects", "subjectName", subject_name)
    assertions.assert_json_list_field_contains_text(response, "enrolledExams", "examName", subject_name)

def prevent_duplicate_exam_enrollment():
    """
    Test Case: Prevent duplicate exam enrollment
    Verifies that a student cannot be enrolled in the same exam twice.
    """
    student_id = student_data["id"]
    exam_id = exam_data["id"]
    
    if not all([student_id, exam_id]):
        raise AssertionError("Student ID or Exam ID not available")
    
    log_info(f"Attempting to re-enroll student {student_id} in exam {exam_id}")
    response = client.post(f"students/{student_id}/exams/{exam_id}", expected_code=409)
    assertions.assert_status_code(response, 409)
    assertions.assert_json_field_contains_text(response, "message", f"Student with id: {student_id} has already enrolled for this particular exam with id: {exam_id}")

def check_easter_egg_feature():
    """
    Test Case: Easter egg feature
    Verifies that the hidden easter egg endpoint returns appropriate data.
    """
    random_number = random.randint(1, 20)
    
    log_info(f"Testing easter egg feature with number: {random_number}")
    response = client.get(f"easter-egg/hidden-feature/{random_number}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field_contains_text(response, "message", "hidden number fact")

def get_all_students():
    """
    Test Case: Get all students
    Verifies that all students can be retrieved and our test student is included.
    """
    student_name = student_data["name"]
    subject_name = subject_data["subjectName"]
    
    log_info("Getting all students")
    response = client.get("students")
    assertions.assert_status_code(response, 200)
    
    # Find our test student in the list
    student_found = False
    for i, student in enumerate(response.json()):
        if student.get("name") == student_name:
            student_found = True
            log_info(f"Found test student at index {i}")
            assertions.assert_json_field_contains_at_index(response, i, "enrolledSubjects", "subjectName", subject_name)
            if exam_data["id"]:
                assertions.assert_json_field_contains_text_at_index(response, i, "enrolledExams", "examName", subject_name)
            break
    
    assert student_found, f"Test student '{student_name}' not found in students list"

def get_all_exams():
    """
    Test Case: Get all exams
    Verifies that all exams can be retrieved and our test exam is included.
    """
    if not exam_data["examName"]:
        log_info("Skipping get_all_exams as no exam was created yet")
        return
        
    subject_name = subject_data["subjectName"]
    
    log_info("Getting all exams")
    response = client.get("exams")
    assertions.assert_status_code(response, 200)
    
    # Find our test exam in the list
    exam_found = False
    for i, exam in enumerate(response.json()):
        if subject_name in exam.get("examName", ""):
            exam_found = True
            log_info(f"Found test exam at index {i}")
            break
    
    assert exam_found, f"Test exam for subject '{subject_name}' not found in exams list"

def get_exam_by_id():
    """
    Test Case: Get exam by ID
    Verifies that exam details can be retrieved by ID.
    """
    exam_id = exam_data["id"]
    subject_name = subject_data["subjectName"]
    
    if not exam_id:
        log_info("Skipping get_exam_by_id as no exam was created yet")
        return
    
    log_info(f"Getting exam with ID: {exam_id}")
    response = client.get(f"exams/{exam_id}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field_contains_text(response, "examName", subject_name)

def get_all_subjects():
    """
    Test Case: Get all subjects
    Verifies that all subjects can be retrieved and our test subject is included.
    """
    subject_name = subject_data["subjectName"]
    
    log_info("Getting all subjects")
    response = client.get("subjects")
    assertions.assert_status_code(response, 200)
    
    # Find our test subject in the list
    subject_found = False
    for i, subject in enumerate(response.json()):
        if subject.get("subjectName") == subject_name:
            subject_found = True
            log_info(f"Found test subject at index {i}")
            break
    
    assert subject_found, f"Test subject '{subject_name}' not found in subjects list"

def get_subject_by_id():
    """
    Test Case: Get subject by ID
    Verifies that subject details can be retrieved by ID.
    """
    subject_id = subject_data["id"]
    subject_name = subject_data["subjectName"]
    
    if not subject_id:
        raise AssertionError("Subject ID not available")
    
    log_info(f"Getting subject with ID: {subject_id}")
    response = client.get(f"subjects/{subject_id}")
    assertions.assert_status_code(response, 200)
    assertions.assert_json_field(response, "subjectName", subject_name)

def cleanup_test_data():
    """
    Cleanup: Remove all test data
    Not a test case, but ensures that created entities are removed after testing.
    """
    if not CLEANUP_AFTER_TESTS:
        log_info("Cleanup skipped due to configuration")
        return
        
    try:
        # Delete entities in reverse order of dependencies
        for exam_id in TEST_DATA["created_entities"]["exams"]:
            if exam_id:
                log_info(f"Deleting exam with ID: {exam_id}")
                client.delete(f"exams/{exam_id}")
        
        for student_id in TEST_DATA["created_entities"]["students"]:
            if student_id:
                log_info(f"Deleting student with ID: {student_id}")
                client.delete(f"students/{student_id}")
        
        for subject_id in TEST_DATA["created_entities"]["subjects"]:
            if subject_id:
                log_info(f"Deleting subject with ID: {subject_id}")
                client.delete(f"subjects/{subject_id}")
                
        log_info("Test data cleanup completed successfully")
    except Exception as e:
        print(f"Error during cleanup: {str(e)}")

# Test cases with ordered execution - matching Cypress test cases
tests = {
    "Create Student": create_student,
    "Create Subject": create_subject,
    "Enroll Student in Subject": enroll_student_in_subject,
    "Prevent Duplicate Subject Enrollment": prevent_duplicate_subject_enrollment,
    "Get Student By ID": get_student_by_id,
    "Create Exam": create_exam,
    "Enroll Student in Exam": enroll_student_in_exam,
    "Prevent Duplicate Exam Enrollment": prevent_duplicate_exam_enrollment,
    "Check Easter Egg Feature": check_easter_egg_feature,
    "Get All Students": get_all_students,
    "Get All Exams": get_all_exams,
    "Get Exam By ID": get_exam_by_id,
    "Get All Subjects": get_all_subjects,
    "Get Subject By ID": get_subject_by_id,
    "Cleanup Test Data": cleanup_test_data  # Not in Cypress but needed for cleanup
}

if __name__ == "__main__":
    print(f"\nRunning tests with unique run identifier: {uuid.uuid4().hex[:8]}")
    TestRunner().run(tests)