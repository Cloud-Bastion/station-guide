import axios from "axios";
import { useRouter } from 'vue-router'; // Import if redirection is needed here

// Use the base URL of the auth server
// Ensure it points to the root of the auth server (e.g., http://localhost:8081)
const AUTH_SERVER_URL: string = window.env.AUTH_BASE_URL || 'http://localhost:8081'; // Fallback added
const TOKEN_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/token`;
const CLIENT_ID = 'station-frontend-client'; // Client ID registered in auth server
const SCOPE = 'openid profile station.read station.write'; // Scopes needed

// Define expected token response structure
interface TokenResponse {
    access_token: string;
    refresh_token?: string; // Optional
    token_type: string;
    expires_in: number;
    scope: string;
    id_token?: string; // If openid scope is used
}

// Define structure for potential error response from token endpoint
interface TokenErrorResponse {
    error: string;
    error_description?: string;
}

const AUTH_TOKEN_KEY = 'auth_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

export default {
    /**
     * Logs in a user using the OAuth2 Resource Owner Password Credentials (ROPC) grant.
     * @param email The user's email (used as username).
     * @param password The user's password.
     * @returns A promise that resolves with the token response on success.
     * @throws An error with a user-friendly message on failure.
     */
    async loginWithPassword(email: string, password: string): Promise<TokenResponse> {
        const params = new URLSearchParams();
        params.append('grant_type', 'password'); // Use 'password' grant type for ROPC
        params.append('username', email);       // Spring Security uses 'username' by default
        params.append('password', password);
        params.append('client_id', CLIENT_ID);
        params.append('scope', SCOPE);

        try {
            console.log(`Attempting ROPC login to ${TOKEN_ENDPOINT} for user ${email}`);
            const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                    // No 'Authorization' header needed here for public clients (ClientAuthenticationMethod.NONE)
                }
            });

            // Store tokens upon successful login
            if (response.data.access_token) {
                localStorage.setItem(AUTH_TOKEN_KEY, response.data.access_token);
                console.log("Access token stored.");
            }
            if (response.data.refresh_token) {
                localStorage.setItem(REFRESH_TOKEN_KEY, response.data.refresh_token);
                 console.log("Refresh token stored.");
            }

            return response.data;

        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                const errorData = error.response.data as TokenErrorResponse;
                console.error("OAuth Token Error (Password Grant):", error.response.status, errorData.error, errorData.error_description);
                // Provide a more user-friendly message based on the error
                if (errorData.error === 'invalid_grant' || error.response.status === 400 || error.response.status === 401) {
                    throw new Error('Ungültige Anmeldedaten. Bitte überprüfen Sie E-Mail und Passwort.');
                } else if (errorData.error === 'invalid_client') {
                     throw new Error('Client-Konfigurationsfehler.');
                } else {
                    throw new Error(errorData.error_description || errorData.error || 'Login fehlgeschlagen.');
                }
            } else {
                console.error("Error during password login:", error);
                throw new Error('Ein unerwarteter Fehler ist beim Login aufgetreten.');
            }
        }
    },

    /**
     * Logs the user out by removing tokens from local storage.
     */
    logout() {
        localStorage.removeItem(AUTH_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        console.log("User logged out, tokens removed.");
        // Optional: Redirect to login page after logout
        // const router = useRouter(); // Cannot use hooks outside setup, handle redirection in component
        // router.push({ name: 'login' });
    },

    /**
     * Retrieves the stored access token.
     * @returns The access token string or null if not found.
     */
    getToken(): string | null {
        return localStorage.getItem(AUTH_TOKEN_KEY);
    },

    /**
     * Checks if the user is currently authenticated (i.e., has an access token).
     * Note: This does not check token validity/expiration.
     * @returns True if an access token exists, false otherwise.
     */
    isAuthenticated(): boolean {
        return !!localStorage.getItem(AUTH_TOKEN_KEY);
    },

    // --- OIDC Methods Removed ---
    // async login(): Promise<void> { ... }
    // async handleCallback(): Promise<User | null> { ... }
    // async getUser(): Promise<User | null> { ... }
    // getUserManager(): UserManager { ... }

    // Optional: Add function for refresh token grant if needed
    // async refreshToken(): Promise<TokenResponse | null> {
    //     const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    //     if (!refreshToken) {
    //         console.log("No refresh token available.");
    //         return null;
    //     }

    //     const params = new URLSearchParams();
    //     params.append('grant_type', 'refresh_token');
    //     params.append('refresh_token', refreshToken);
    //     params.append('client_id', CLIENT_ID);

    //     try {
    //         console.log(`Attempting token refresh using ${TOKEN_ENDPOINT}`);
    //         const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
    //             headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    //         });

    //         if (response.data.access_token) {
    //             localStorage.setItem(AUTH_TOKEN_KEY, response.data.access_token);
    //             console.log("Access token refreshed and stored.");
    //         }
    //         // Potentially update refresh token if a new one is issued
    //         if (response.data.refresh_token) {
    //             localStorage.setItem(REFRESH_TOKEN_KEY, response.data.refresh_token);
    //             console.log("Refresh token updated and stored.");
    //         } else {
    //             // If no new refresh token, the old one might still be valid or rotation is not used
    //             console.log("No new refresh token received.");
    //         }
    //         return response.data;

    //     } catch (error) {
    //         console.error("Error refreshing token:", error);
    //         // If refresh fails (e.g., expired refresh token), log the user out
    //         this.logout();
    //         // Optionally redirect to login
    //         // window.location.href = '/login';
    //         return null; // Indicate refresh failure
    //     }
    // }
}
