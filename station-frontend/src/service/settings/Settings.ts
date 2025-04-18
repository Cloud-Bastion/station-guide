import axios from 'axios';

const API_BASE_URL: string = window.env.API_BASE_URL;
const AUTH_SERVER_URL: string = window.env.AUTH_SERVER_URL;
const AUTH_CLIENT_ID: string = window.env.AUTH_CLIENT_ID;
const AUTH_REDIRECT_URI: string = window.env.AUTH_REDIRECT_URI;
const AUTH_POST_LOGOUT_REDIRECT_URI: string = window.env.AUTH_POST_LOGOUT_REDIRECT_URI;
const AUTH_SCOPES: string = window.env.AUTH_SCOPES;
const AUTH_SILENT_REDIRECT_URI: string = window.env.AUTH_SILENT_REDIRECT_URI;

const authApiClient = axios.create({
    baseURL: AUTH_SERVER_URL,
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    }
});

// Request Interceptor: Add Authorization header
authApiClient.interceptors.request.use(
    async (config) => { // Make interceptor async
        const authService = (await import('@/service/AuthUserService')).default;
        const token = await authService.getAccessToken(); // Get token from AuthService
        if (token && !config.headers.Authorization) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response Interceptor (Optional - Simplified)
// oidc-client-ts aims to refresh *before* expiry, reducing the need for 401 handling.
// You might keep a basic handler for unexpected 401s (e.g., token revoked server-side).
authApiClient.interceptors.response.use(
    (response) => response, // Pass through successful responses
    async (error) => {
        if (error.response?.status === 401) {
            console.warn("Received 401 Unauthorized. Token might be invalid or revoked. Logging out.");
            const authService = (await import('@/service/AuthUserService')).default;
            // If silent renew failed or token is truly invalid, logging out might be the best option.
            authService.logout(); // Trigger logout
            // Avoid retrying the request as the user is being logged out.
            return Promise.reject(new Error("Unauthorized request, user logged out."));
        }
        // For other errors, reject as usual
        return Promise.reject(error);
    }
);

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    }
});

// Request Interceptor: Add Authorization header
apiClient.interceptors.request.use(
    async (config) => { // Make interceptor async
        const authService = (await import('@/service/AuthUserService')).default;
        const token = await authService.getAccessToken(); // Get token from AuthService
        if (token && !config.headers.Authorization) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response Interceptor (Optional - Simplified)
// oidc-client-ts aims to refresh *before* expiry, reducing the need for 401 handling.
// You might keep a basic handler for unexpected 401s (e.g., token revoked server-side).
apiClient.interceptors.response.use(
    (response) => response, // Pass through successful responses
    async (error) => {
        if (error.response?.status === 401) {
            console.warn("Received 401 Unauthorized. Token might be invalid or revoked. Logging out.");
            // If silent renew failed or token is truly invalid, logging out might be the best option.
            const authService = (await import('@/service/AuthUserService')).default;
            await authService.logout(); // Trigger logout
            // Avoid retrying the request as the user is being logged out.
            return Promise.reject(new Error("Unauthorized request, user logged out."));
        }
        // For other errors, reject as usual
        return Promise.reject(error);
    }
);


export default {
    apiClient,
    authApiClient,
    API_BASE_URL,
    AUTH_SERVER_URL,
    AUTH_CLIENT_ID,
    AUTH_REDIRECT_URI,
    AUTH_POST_LOGOUT_REDIRECT_URI,
    AUTH_SCOPES,
    AUTH_SILENT_REDIRECT_URI,
    AUTH_SILENT_REDIRECT_URI,
}