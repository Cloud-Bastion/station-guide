import axios from "axios";
// PKCE utils are no longer needed for ROPC flow
// import { generateRandomString, generateCodeChallenge } from '@/utils/pkce';

// Use the base URL of the auth server
const AUTH_SERVER_URL: string = window.env.AUTH_BASE_URL || 'http://localhost:8081'; // Fallback added
// Only TOKEN_ENDPOINT is needed for ROPC
const TOKEN_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/token`;
const CLIENT_ID = 'station-frontend-client'; // Client ID registered in auth server
// REDIRECT_URI and SCOPE might still be useful if refresh token grant needs scope, but not strictly for password grant itself.
const SCOPE = 'openid profile station.read station.write'; // Scopes requested

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
// PKCE_VERIFIER_KEY is no longer needed
// const PKCE_VERIFIER_KEY = 'pkce_code_verifier';

async function generatePKCE() {
    const code_verifier = generateRandomString(64);
    const code_challenge = await sha256(code_verifier);

    localStorage.setItem("pkce_code_verifier", code_verifier); // Store it for later use

    return code_challenge;
}

function generateRandomString(length: number) {
    const charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    let result = "";
    for (let i = 0; i < length; i++) {
        result += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return result;
}

async function sha256(plain: string) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plain);
    const hashBuffer = await crypto.subtle.digest("SHA-256", data);
    return btoa(String.fromCharCode(...new Uint8Array(hashBuffer)))
        .replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, ""); // Base64URL encode
}

export default {

    async loginWithUsernamePassword(username: string, password: string) {
          
    },

    /**
     * Logs the user in using the Resource Owner Password Credentials (ROPC) flow.
     * @param username The user's email address.
     * @param password The user's password.
     * @returns A promise that resolves with the token response on success.
     * @throws An error with a user-friendly message on failure.
     */
    async loginWithPassword(username: string, password: string): Promise<TokenResponse> {
        const params = new URLSearchParams();
        params.append('grant_type', 'password');
        params.append('username', username);
        params.append('password', password);
        params.append('client_id', CLIENT_ID);
        // Optionally add scope if your auth server requires/supports it for password grant
        params.append('scope', SCOPE);

        try {
            console.log(`Attempting ROPC login for ${username} at ${TOKEN_ENDPOINT}`);
            const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                    // No 'Authorization' header needed here for public clients (ClientAuthenticationMethod.NONE)
                    // If your client was confidential, you'd add Basic Auth here.
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
                    // 400 or 401 with invalid_grant usually means bad username/password
                    throw new Error('Ungültige E-Mail oder Passwort.');
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
        // Clear PKCE verifier just in case (though not used in ROPC)
        sessionStorage.removeItem('pkce_code_verifier');
        console.log("User logged out, tokens removed.");
        // Optional: Redirect to a post-logout page or login page
        // window.location.href = '/login'; // Or use router.push('/login') in component
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

    // --- Authorization Code Flow Methods Removed ---
    // async initiateLogin(): Promise<void> { ... }
    // async exchangeCodeForToken(code: string, codeVerifier: string): Promise<TokenResponse> { ... }

    // Optional: Add function for refresh token grant if needed
    // async refreshToken(): Promise<TokenResponse | null> { ... }
}
