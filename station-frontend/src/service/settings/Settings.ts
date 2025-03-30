import axios from "axios";

const API_BASE_URL: string = window.env.API_BASE_URL; // Resource Server URL
const AUTH_BASE_URL: string = window.env.AUTH_BASE_URL; // Auth Server URL (if needed separately)

// Axios instance for Resource Server API calls
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

// Axios instance specifically for Auth Server calls (if needed, e.g., for user info endpoint)
const authApiClient = axios.create({
    baseURL: AUTH_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});


// --- Axios Request Interceptor for Resource Server API ---
// Add the Authorization header to requests if a token exists
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// --- Optional: Axios Response Interceptor for Resource Server API ---
// Handle token expiration (e.g., by using refresh token or redirecting to login)
apiClient.interceptors.response.use(
    (response) => {
        // If request successful, just return the response
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // Check if it's a 401 Unauthorized error and not a retry attempt
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true; // Mark as retry attempt

            // Option 1: Try to refresh the token (if refresh token logic is implemented)
            /*
            const refreshToken = localStorage.getItem('refresh_token');
            if (refreshToken) {
                try {
                    // Call your refresh token function (needs to be implemented in AuthUserService)
                    const newTokenResponse = await AuthUserService.refreshToken(refreshToken);
                    localStorage.setItem('auth_token', newTokenResponse.access_token);
                    if (newTokenResponse.refresh_token) {
                        localStorage.setItem('refresh_token', newTokenResponse.refresh_token);
                    }
                    // Update the header in the original request and retry
                    originalRequest.headers.Authorization = `Bearer ${newTokenResponse.access_token}`;
                    return apiClient(originalRequest);
                } catch (refreshError) {
                    console.error("Unable to refresh token:", refreshError);
                    // If refresh fails, clear tokens and redirect to login
                    localStorage.removeItem('auth_token');
                    localStorage.removeItem('refresh_token');
                    // Redirect to login (use router instance if available, otherwise window.location)
                    window.location.href = '/login'; // Or use router.push('/login')
                    return Promise.reject(refreshError);
                }
            } else {
                // No refresh token available, redirect to login
                console.log("No refresh token, redirecting to login.");
                localStorage.removeItem('auth_token');
                 window.location.href = '/login'; // Or use router.push('/login')
            }
            */

            // Option 2: Simple redirect to login on 401
            console.log("Unauthorized (401). Token might be expired. Redirecting to login.");
            localStorage.removeItem('auth_token');
            localStorage.removeItem('refresh_token'); // Also remove refresh token if used
            // Use window.location for simplicity here, or inject router if needed elsewhere
            window.location.href = '/login';
        }

        // For other errors, just reject the promise
        return Promise.reject(error);
    }
);


export default {
    API_BASE_URL,
    AUTH_BASE_URL,
    apiClient, // Use this for Resource Server calls
    authApiClient // Use this for Auth Server calls (if any direct calls are needed)
};
