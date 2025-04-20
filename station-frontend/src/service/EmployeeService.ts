import settings from "./settings/Settings";
import { AccountAuthority } from "./EmployeeService"; // Keep AccountAuthority if used elsewhere

const API_URL = "/v1/employees"; // Base URL for employees in resource server

// Interface representing the data structure returned by GET /v1/employees
// Based on EmployeeDTO summary
export interface Employee {
    id: string;
    badgeNumber?: number; // Made optional as it might not always be present initially
    firstName: string;
    lastName: string;
    email: string;
    // address: EmployeeAddress; // Keep if needed, simplify if not displayed in list
    // birthDate: string; // Use string for simplicity if only displaying
    // taxId: number;
    // socialSecurityId: string;
    // createdAt: string; // Use string
    // createdBy?: Employee; // Can be complex, make optional or simplify
    minijob?: boolean; // Added from EmployeeManageView example
    hourlyWage?: number; // Added from EmployeeManageView example
}

// Interface for creating a new employee/account
// Based on Account interface and backend needs
export interface CreateEmployeeAccount {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    // authorities?: Set<AccountAuthority>; // Optional: Backend might assign defaults
    // badgeNumber?: number; // Optional: Backend likely generates this
}


// --- Service Functions ---

export default {
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
     * Assumes the backend endpoint POST /v1/employees handles this.
     * @param accountData Data required to create the employee account.
     */
    async createEmployee(accountData: CreateEmployeeAccount): Promise<Employee> { // Assuming backend returns the created Employee
        try {
            // The backend needs to handle creating the User in the auth-server
            // and the EmployeeEntity in the resource-server based on this request.
            const response = await settings.apiClient.post<Employee>(API_URL, accountData);
            return response.data;
        } catch (error) {
            console.error("Error creating employee:", error);
            throw error; // Re-throw to be handled by the caller
        }
    },

    // --- Keep other interfaces if they are used elsewhere ---

    // Example: Kept from original file, might not be directly used for list/create
    address: interface EmployeeAddress {
        zipcode: number,
        city: string,
        street: string,
        streetNumber: string,
        apartment?: string // Made optional
    }

    // Example: Kept from original file, might not be directly used for list/create
    authority: interface AccountAuthority {
        id: string,
        name: string,
    }

    // Example: Kept from original file, represents auth server user, not directly used for list/create here
    account: interface Account {
        id: string,
        badgeNumber: number,
        email: string,
        firstName: string,
        lastName: string,
        password?: string, // Password usually not sent back
        authorities: Set<AccountAuthority>,
    }
};

// Export interfaces separately if needed in multiple places
export type { EmployeeAddress, AccountAuthority, Account };
