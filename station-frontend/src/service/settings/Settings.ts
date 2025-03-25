import axios from "axios";

const API_BASE_URL: string = import.meta.env.API_BASE_URL || "http://localhost:8080/api/";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

export default {
    API_BASE_URL, apiClient
};