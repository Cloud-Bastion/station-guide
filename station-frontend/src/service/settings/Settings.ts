import axios from "axios";

const API_BASE_URL: string = "https://api.cakmak-station.de/api/";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

export default {
    API_BASE_URL, apiClient
};