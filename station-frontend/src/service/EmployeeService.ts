
export interface EmployeeAddress {
    zipcode: number,
    city: string,
    street: string,
    streetNumber: string,
    apartment: string
}

export interface Employee {
    id: string,
    badgeNumber: number,
    firstName: string,
    lastName: string,
    email: string,
    address: EmployeeAddress,
    birthDate: Date,
    taxId: number,
    socialSecurityId: string,
    createdAt: Date,
    createdBy: Employee
}

export interface AccountAuthority {
    id: string,
    name: string,
}

export interface Account {
    id: string,
    badgeNumber: number,
    email: string,
    firstName: string,
    lastName: string,
    password: string,
    authorities: Set<AccountAuthority>,
}