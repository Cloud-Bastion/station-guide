import settings from "./settings/Settings";

export interface UserAuthority {
    id?: string;
    name: string;
}

export interface UserRole {
    id?: string;
    displayName: string;
    name: string;
    authorities: UserAuthority[];
}

export interface UserRoleCreateRequest {
    name: string;
    displayName: string;
    initialAuthorities: string[]; // Array of authority names
}

const API_URL = "/api/v1/auth/role"; // Base URL on the auth server

const RoleAuthorityService = {

    async getAllRoles(): Promise<UserRole[]> {
        try {
            // Use authApiClient as the endpoint is on the auth server
            const response = await settings.authApiClient.get<UserRole[]>(API_URL);
            return response.data;
        } catch (error) {
            console.error("Error fetching roles:", error);
            throw error;
        }
    },

    async getRoleByName(roleName: string): Promise<UserRole> {
        try {
            const response = await settings.authApiClient.get<UserRole>(`${API_URL}/${encodeURIComponent(roleName)}`);
            return response.data;
        } catch (error) {
            console.error(`Error fetching role ${roleName}:`, error);
            throw error;
        }
    },

    async createRole(roleData: UserRoleCreateRequest): Promise<UserRole> {
        try {
            const response = await settings.authApiClient.post<UserRole>(API_URL, roleData);
            return response.data;
        } catch (error) {
            console.error("Error creating role:", error);
            throw error;
        }
    },

    async addAuthorityToRole(roleName: string, authorityName: string): Promise<UserRole> {
        try {
            const response = await settings.authApiClient.patch<UserRole>(`${API_URL}/add/${encodeURIComponent(roleName)}/${encodeURIComponent(authorityName)}`);
            return response.data;
        } catch (error) {
            console.error(`Error adding authority ${authorityName} to role ${roleName}:`, error);
            throw error;
        }
    },

    async removeAuthorityFromRole(roleName: string, authorityName: string): Promise<UserRole> {
        try {
            const response = await settings.authApiClient.patch<UserRole>(`${API_URL}/remove/${encodeURIComponent(roleName)}/${encodeURIComponent(authorityName)}`);
            return response.data;
        } catch (error) {
            console.error(`Error removing authority ${authorityName} from role ${roleName}:`, error);
            throw error;
        }
    },

    async setAuthoritiesForRole(roleName: string, authorities: string[]): Promise<UserRole> {
        try {
            const response = await settings.authApiClient.patch<UserRole>(`${API_URL}/set/${encodeURIComponent(roleName)}`, authorities);
            return response.data;
        } catch (error) {
            console.error(`Error setting authorities for role ${roleName}:`, error);
            throw error;
        }
    },

    extractUniqueAuthorities(roles: UserRole[]): UserAuthority[] {
        const authorityMap = new Map<string, UserAuthority>();
        roles.forEach(role => {
            role.authorities.forEach(auth => {
                if (!authorityMap.has(auth.name)) {
                    authorityMap.set(auth.name, auth);
                }
            });
        });
        return Array.from(authorityMap.values()).sort((a, b) => a.name.localeCompare(b.name));
    }
};

export default RoleAuthorityService;
