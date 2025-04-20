import settings from "./settings/Settings";

const API_URL = "/v1/employees"; // Base URL for employees in resource server

export interface Employee {
    id: string;
    badgeNumber?: number;
    firstName: string;
    lastName: string;
    email: string;
    address: EmployeeAddress;
    birthDate: Date;
    taxId: number;
    socialSecurityId: string;
    minijob?: boolean;
    hourlyWage?: number;
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
    password?: string;
    authorities: Set<AccountAuthority>;
}

const EmployeeService = {
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

    async createEmployee(employeeData: Employee, accountData: Account): Promise<Employee> { // Assuming backend returns the created Employee
        try {
            const payload = {
                firstName: employeeData.firstName,
                lastName: employeeData.lastName,
                email: employeeData.email,
                password: accountData.password,
                address: employeeData.address,
                birthDate: employeeData.birthDate,
                taxId: employeeData.taxId,
                socialSecurityId: employeeData.socialSecurityId,
                createdBy: null,
                createdAt: null,
            };

            const authPayLoad = {
                email: employeeData.email,
                firstName: employeeData.firstName,
                lastName: employeeData.lastName,
                password: accountData.password,
                initialAuthorities: accountData.authorities
            };

            // Send the nested payload
            const response = await settings.apiClient.post(API_URL + "/create", payload);
            console.log(response.data)
            const authResponse = await settings.authApiClient.post("/api/v1/auth/create-account", authPayLoad)
            console.log(authResponse.data)
            return authResponse.data;
        } catch (error) {
            console.error("Error creating employee:", error);
            throw error;
        }
    },
};

export default EmployeeService;