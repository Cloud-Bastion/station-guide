import settings from "./settings/Settings";

const API_URL = "/v1/employees"; // Base URL for employees in resource server

export interface Employee {
    id: string;
    badgeNumber?: number;
    firstName: string;
    lastName: string;
    address: EmployeeAddress;
    email: string;
    minijob?: boolean;
    hourlyWage?: number;
}

export interface EmployeeAddress {
    zipcode: number;
    city: string;
    street: string;
    streetNumber: string;
    apartment?: string;
}

export interface AccountAuthority {
    id: string;
    name: string;
}

export interface Account {
    id: string;
    badgeNumber: number;
    email: string;
    firstName: string;
    lastName: string;
    password?: string;
    authorities: Set<AccountAuthority>;
}

// --- Service Functions ---

const EmployeeService = {
    async getAllEmployees(): Promise<Employee[]> {
        try {
            const response = await settings.apiClient.get<Employee[]>(API_URL);
            return response.data;
        } catch (error) {
            console.error("Error fetching employees:", error);
            throw error;
        }
    },

    async createEmployee(accountData: CreateEmployeeAccount): Promise<Employee> {
        try {
            const response = await settings.apiClient.post<Employee>(API_URL, accountData);
            return response.data;
        } catch (error) {
            console.error("Error creating employee:", error);
            throw error;
        }
    },
};

export default EmployeeService;
