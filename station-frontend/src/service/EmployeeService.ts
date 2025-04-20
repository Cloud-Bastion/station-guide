import settings from "./settings/Settings";

const API_URL = "/v1/employees"; // Base URL for employees in resource server

// Interface representing the data structure returned by GET /v1/employees
// Based on EmployeeDTO summary - Added birthDate, taxId, socialSecurityId
export interface Employee {
    id: string;
    badgeNumber?: number; // Made optional as it might not always be present initially
    firstName: string;
    lastName: string;
    email: string;
    address: EmployeeAddress; // Keep full address structure as defined
    birthDate: string; // Added - Expecting ISO date string (YYYY-MM-DD) from frontend
    taxId: number; // Added
    socialSecurityId: string; // Added
    minijob?: boolean; // Added from EmployeeManageView example
    hourlyWage?: number; // Added from EmployeeManageView example
    // createdAt and createdBy are typically handled by the backend
    // createdAt?: string;
    // createdBy?: Employee;
}

// Interface for the address part of an Employee
export interface EmployeeAddress {
    zipcode: number;
    city: string;
    street: string;
    streetNumber: string;
    apartment?: string; // Made optional
}

// Interface representing user authorities (from auth-server)
export interface AccountAuthority {
    id: string;
    name: string;
}

// Interface representing the user account (from auth-server UserDto)
// Note: Password is included here for creation context, but usually not returned by GET requests.
export interface Account {
    id: string;
    badgeNumber: number;
    email: string;
    firstName: string;
    lastName: string;
    password?: string; // Password included for creation, usually not sent back from API
    authorities: Set<AccountAuthority>;
    // oAuthId?: string; // Add if needed
}


// --- Service Functions ---

const EmployeeService = {
    /**
     * Fetches all employees.
     * Assumes the backend returns an array of objects matching the Employee interface.
     */
    async getAllEmployees(): Promise<Employee[]> {
        try {
            const response = await settings.apiClient.get<Employee[]>(API_URL);
            // TODO: Add data transformation if needed (e.g., date parsing for display)
            return response.data;
        } catch (error) {
            console.error("Error fetching employees:", error);
            throw error; // Re-throw to be handled by the caller
        }
    },

    /**
     * Creates a new employee and their associated account.
     * Sends a nested structure containing employee and account details.
     * Assumes the backend endpoint POST /v1/employees expects { employee: {...}, account: {...} }.
     * @param employeeData Data conforming to the Employee interface (excluding generated fields like id, badgeNumber).
     * @param accountData Data conforming to the Account interface (excluding generated fields like id, badgeNumber, authorities).
     */
    async createEmployee(employeeData: Employee, accountData: Account): Promise<Employee> { // Assuming backend returns the created Employee
        try {
            // Prepare the nested payload structure using the provided arguments
            // Backend should handle setting id, badgeNumber, createdAt, createdBy etc.
            const payload = {
                employee: employeeData, // Send the complete Employee object as received
                account: accountData   // Send the complete Account object as received
            };

            // Send the nested payload
            const response = await settings.apiClient.post<Employee>(API_URL, payload);
            return response.data;
        } catch (error) {
            console.error("Error creating employee:", error);
            throw error; // Re-throw to be handled by the caller
        }
    },
};

export default EmployeeService;

// Export interfaces separately if needed in multiple places
export type { EmployeeAddress, AccountAuthority, Account }; // Employee is already exported implicitly
