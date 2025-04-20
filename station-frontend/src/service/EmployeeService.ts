import settings from "./settings/Settings";

const API_URL = "/v1/employees"; // Base URL for employees in resource server

// Interface representing the data structure returned by GET /v1/employees
// Based on EmployeeDTO summary
export interface Employee {
    id: string;
    badgeNumber?: number; // Made optional as it might not always be present initially
    firstName: string;
    lastName: string;
    email: string;
    address: EmployeeAddress; // Keep full address structure as defined
    minijob?: boolean; // Added from EmployeeManageView example
    hourlyWage?: number; // Added from EmployeeManageView example
    // Add other fields from EmployeeDTO if needed for display/use
    // birthDate: string;
    // taxId: number;
    // socialSecurityId: string;
    // createdAt: string;
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


// Interface for creating a new employee/account
// Based on Account interface and backend needs for user creation
export interface CreateEmployeeAccount {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    // authorities?: Set<AccountAuthority>; // Optional: Backend might assign defaults
    // badgeNumber?: number; // Optional: Backend likely generates this
}

// Interface representing user authorities (from auth-server)
export interface AccountAuthority {
    id: string;
    name: string;
}

// Interface representing the user account (from auth-server UserDto)
export interface Account {
    id: string;
    badgeNumber: number;
    email: string;
    firstName: string;
    lastName: string;
    password?: string; // Password usually not sent back from API
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
            // TODO: Add data transformation if needed (e.g., date parsing)
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
     * @param creationData Data required to create the employee account (firstName, lastName, email, password).
     */
    async createEmployee(creationData: CreateEmployeeAccount): Promise<Employee> { // Assuming backend returns the created Employee
        try {
            // Prepare the nested payload structure
            const payload = {
                employee: { // Data primarily for the EmployeeEntity
                    firstName: creationData.firstName,
                    lastName: creationData.lastName,
                    email: creationData.email,
                    // Address, minijob, hourlyWage etc. are not collected in the current dialog,
                    // so they are omitted here. The backend might set defaults or require
                    // these fields to be updated later.
                },
                account: { // Data primarily for the User/Account entity
                    firstName: creationData.firstName,
                    lastName: creationData.lastName,
                    email: creationData.email,
                    password: creationData.password,
                    // Authorities might be set to default by the backend
                }
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
// (Employee, EmployeeAddress, AccountAuthority, Account, CreateEmployeeAccount are already exported)
