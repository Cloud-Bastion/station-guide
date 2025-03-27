import axios from "axios";

const API_BASE_URL: string = "http://localhost:8081/api/v1/auth";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

interface UserAuthentication {
    token: string,
    error: string
}

export default {
    async getUserAuth(username: string, password: string): Promise<UserAuthentication> {
        const response = await apiClient.post<UserAuthentication>("login", {
            username: username,
            password: password,
        });
        return response.data;
    },

    async getGoogleAuth(username: string, password: string): Promise<UserAuthentication> {
        const response = await apiClient.post<UserAuthentication>("login", {
            username: username,
            password: password,
        });
        return response.data;
    },
}
