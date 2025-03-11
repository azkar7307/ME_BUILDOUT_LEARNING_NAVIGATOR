"""Utility functions and classes for API testing"""

import requests
import json
from typing import Dict, Any, Optional, List, Union
from test_config import BASE_URL, TIMEOUT

def format_test_name(name: str) -> str:
    """Format test name for display by:
    1. Removing 'test_' prefix
    2. Replacing underscores with spaces
    3. Converting to title case
    4. Ensuring 'ID' stays uppercase
    """
    name = name.replace("test_", "").replace("_", " ").title()
    return name.replace("Id", "ID")

class APIClient:
    """Generic API client for making HTTP requests"""
    
    def __init__(self, base_url: str = BASE_URL, timeout: int = TIMEOUT):
        self.base_url = base_url.rstrip('/')
        self.timeout = timeout
        self.session = requests.Session()
        self.request_history = []

    def _make_request(self, method: str, endpoint: str, expected_code: int = None, **kwargs) -> requests.Response:
        """Make HTTP request with error handling"""
        url = f"{self.base_url}/{endpoint.lstrip('/')}"
        kwargs['timeout'] = self.timeout
        
        # Add request to history for debugging
        request_info = {
            'method': method,
            'url': url,
            'params': kwargs.get('params'),
            'json': kwargs.get('json'),
        }
        self.request_history.append(request_info)
        
        try:
            response = self.session.request(method, url, **kwargs)
            
            # Log unexpected status codes with detailed information
            if expected_code is not None and response.status_code != expected_code:
                print(f"Expected status code {expected_code}, got {response.status_code}")
                
                # Try to parse and print JSON response
                try:
                    if response.headers.get('content-type', '').startswith('application/json'):
                        print(f"Response body: {json.dumps(response.json(), indent=2)}")
                    else:
                        print(f"Response text: {response.text[:500]}...")  # Truncate long responses
                except Exception as e:
                    print(f"Could not parse response: {e}")
                    
            return response
        except requests.RequestException as e:
            print(f"Request failed for URL {url}: {str(e)}")
            raise AssertionError(f"Request failed: {str(e)}")

    def get(self, endpoint: str, params: Optional[Dict] = None, expected_code: int = None) -> requests.Response:
        """Make GET request"""
        return self._make_request('GET', endpoint, expected_code, params=params)

    def post(self, endpoint: str, json: Dict[str, Any] = None, expected_code: int = None) -> requests.Response:
        """Make POST request"""
        return self._make_request('POST', endpoint, expected_code, json=json)

    def put(self, endpoint: str, json: Dict[str, Any] = None, expected_code: int = None) -> requests.Response:
        """Make PUT request"""
        return self._make_request('PUT', endpoint, expected_code, json=json)

    def delete(self, endpoint: str, expected_code: int = None) -> requests.Response:
        """Make DELETE request"""
        return self._make_request('DELETE', endpoint, expected_code)
        
    def get_last_request(self) -> Dict:
        """Return information about the last request made"""
        return self.request_history[-1] if self.request_history else {}

class TestAssertions:
    """Common test assertions for API responses"""
    
    @staticmethod
    def assert_status_code(response: requests.Response, expected_code: int):
        """Assert response status code"""
        assert response.status_code == expected_code, \
            f"Expected status code {expected_code}, got {response.status_code}"

    @staticmethod
    def assert_json_field(response: requests.Response, field: str, expected_value: Any):
        """Assert JSON response field value"""
        data = response.json()
        assert field in data, f"Field '{field}' not found in response"
        assert data[field] == expected_value, \
            f"Expected {field}='{expected_value}', got '{data[field]}'"

    @staticmethod
    def assert_json_field_contains_text(response: requests.Response, field: str, expected_text: str):
        """Assert JSON response field contains substring"""
        data = response.json()
        assert field in data, f"Field '{field}' not found in response"
        assert expected_text.lower() in str(data[field]).lower(), \
            f"Expected text '{expected_text}' not found in field '{field}': '{data[field]}'"

    @staticmethod
    def assert_json_list_field_contains_text(response: requests.Response, list_field: str, field: str, expected_text: str):
        """Assert JSON response list field contains object with field containing text"""
        data = response.json()
        assert list_field in data, f"List field '{list_field}' not found in response"
        assert isinstance(data[list_field], list), f"Field '{list_field}' is not a list"
        found = False
        for item in data[list_field]:
            if expected_text.lower() in str(item.get(field, "")).lower():
                found = True
                break
        assert found, f"No item with {field} containing '{expected_text}' found in '{list_field}'"
    
    @staticmethod
    def assert_json_fields_present(response: requests.Response, fields: list):
        """Assert JSON response contains required fields"""
        data = response.json()
        for field in fields:
            assert field in data, f"Required field '{field}' not found in response"

    @staticmethod
    def assert_sorted_by_field(response: requests.Response, field: str, reverse: bool = False):
        """Assert JSON response array is sorted by field"""
        data = response.json()
        values = [item[field] for item in data]
        assert values == sorted(values, reverse=reverse), \
            f"Response not sorted by '{field}'"
    
    @staticmethod
    def assert_json_field_contains(response: requests.Response, list_field: str, field: str, expected_value: Any):
        """Assert JSON response list field contains object with given field value"""
        data = response.json()
        assert list_field in data, f"List field '{list_field}' not found in response"
        assert isinstance(data[list_field], list), f"Field '{list_field}' is not a list"
        found = False
        for item in data[list_field]:
            if item.get(field) == expected_value:
                found = True
                break
        assert found, f"No item with {field}='{expected_value}' found in '{list_field}'"
    
    @staticmethod
    def assert_list_length(response: requests.Response, expected_length: int):
        """Assert JSON response array has expected length"""
        data = response.json()
        assert isinstance(data, list), "Response is not a list"
        assert len(data) == expected_length, f"Expected list length {expected_length}, got {len(data)}"
    
    @staticmethod
    def assert_minimum_list_length(response: requests.Response, min_length: int):
        """Assert JSON response array has at least the minimum length"""
        data = response.json()
        assert isinstance(data, list), "Response is not a list"
        assert len(data) >= min_length, f"Expected list length at least {min_length}, got {len(data)}"
    
    @staticmethod
    def assert_json_field_at_index(response: requests.Response, index: int, field: str, expected_value: Any):
        """Assert JSON response array item at index has field with expected value"""
        data = response.json()
        assert isinstance(data, list), "Response is not a list"
        assert index < len(data), f"Index {index} out of bounds for list of length {len(data)}"
        assert field in data[index], f"Field '{field}' not found in item at index {index}"
        assert data[index][field] == expected_value, \
            f"Expected {field}='{expected_value}' at index {index}, got '{data[index][field]}'"
    
    @staticmethod
    def assert_json_field_contains_at_index(response: requests.Response, index: int, list_field: str, field: str, expected_value: Any):
        """Assert JSON response array item at index has list field containing object with given field value"""
        data = response.json()
        assert isinstance(data, list), "Response is not a list"
        assert index < len(data), f"Index {index} out of bounds for list of length {len(data)}"
        assert list_field in data[index], f"List field '{list_field}' not found in item at index {index}"
        assert isinstance(data[index][list_field], list), f"Field '{list_field}' is not a list at index {index}"
        found = False
        for item in data[index][list_field]:
            if item.get(field) == expected_value:
                found = True
                break
        assert found, f"No item with {field}='{expected_value}' found in '{list_field}' at index {index}"
        
    @staticmethod
    def assert_json_field_contains_text_at_index(response: requests.Response, index: int, list_field: str, field: str, expected_text: str):
        """Assert JSON response array item at index has list field containing object with field containing text"""
        data = response.json()
        assert isinstance(data, list), "Response is not a list"
        assert index < len(data), f"Index {index} out of bounds for list of length {len(data)}"
        assert list_field in data[index], f"List field '{list_field}' not found in item at index {index}"
        assert isinstance(data[index][list_field], list), f"Field '{list_field}' is not a list at index {index}"
        found = False
        for item in data[index][list_field]:
            if expected_text.lower() in str(item.get(field, "")).lower():
                found = True
                break
        assert found, f"No item with {field} containing '{expected_text}' found in '{list_field}' at index {index}"