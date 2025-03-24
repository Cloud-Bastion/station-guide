import axios from "axios";
import {ExpireProduct} from "@/service/ExpireProductService";

const API_BASE_URL: string = "https://auth.cakmak-station.de/api/v1/auth";

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
}
