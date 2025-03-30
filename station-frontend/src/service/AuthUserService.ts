import axios from "axios";
import { generateRandomString, generateCodeChallenge } from '@/utils/pkce'; // Import PKCE utils

// Use the base URL of the auth server
const AUTH_SERVER_URL: string = window.env.AUTH_BASE_URL || 'http://localhost:8081'; // Fallback added
// --- CORRECTED ENDPOINTS ---
const AUTHORIZE_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/authorize`; // Removed /api/v1/auth
const TOKEN_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/token`;       // Removed /api/v1/auth
// --- END CORRECTIONS ---
const CLIENT_ID = 'station-frontend-client'; // Client ID registered in auth server
const REDIRECT_URI = 'http://localhost:5173/oidc-callback'; // Must match registered redirect URI and callback route path
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
const PKCE_VERIFIER_KEY = 'pkce_code_verifier'; // Key for storing PKCE verifier

export default {
    /**
     * Initiates the OAuth2 Authorization Code Flow with PKCE.
     * Generates PKCE codes, stores the verifier, and redirects the user
     * to the authorization server's authorization endpoint.
     */
    async initiateLogin(): Promise<void> {
        // 1. Generate Code Verifier and Challenge
        const codeVerifier = generateRandomString(128); // Generate a secure random string
        const codeChallenge = await generateCodeChallenge(codeVerifier); // Hash and encode the verifier

        // 2. Store the Code Verifier in sessionStorage (it's needed after redirect)
        sessionStorage.setItem(PKCE_VERIFIER_KEY, codeVerifier);
        console.log("Stored PKCE Code Verifier.");

        // 3. Construct the Authorization URL
        const params = new URLSearchParams();
        params.append('response_type', 'code');
        params.append('client_id', CLIENT_ID);
        params.append('redirect_uri', REDIRECT_URI);
        params.append('scope', SCOPE);
        params.append('code_challenge', codeChallenge);
        params.append('code_challenge_method', 'S256');
        // Optional: Add state parameter for CSRF protection
        // const state = generateRandomString(32);
        // sessionStorage.setItem('oauth_state', state);
        // params.append('state', state);

        const authorizationUrl = `${AUTHORIZE_ENDPOINT}?${params.toString()}`;
        console.log(`Redirecting user to: ${authorizationUrl}`);

        // 4. Redirect the user's browser
        window.location.href = authorizationUrl;
    },

    /**
     * Exchanges the authorization code for an access token using the PKCE flow.
     * This is called from the OAuth callback component.
     * @param code The authorization code received from the authorization server.
     * @param codeVerifier The PKCE code verifier stored before the redirect.
     * @returns A promise that resolves with the token response on success.
     * @throws An error with a user-friendly message on failure.
     */
    async exchangeCodeForToken(code: string, codeVerifier: string): Promise<TokenResponse> {
        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('code', code);
        params.append('redirect_uri', REDIRECT_URI); // Must match the URI used in the initial request
        params.append('client_id', CLIENT_ID);
        params.append('code_verifier', codeVerifier); // Send the original verifier

        try {
            console.log(`Exchanging authorization code at ${TOKEN_ENDPOINT}`);
            const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                    // No 'Authorization' header needed here for public clients (ClientAuthenticationMethod.NONE)
                }
            });

            // Store tokens upon successful exchange
            if (response.data.access_token) {
                localStorage.setItem(AUTH_TOKEN_KEY, response.data.access_token);
                console.log("Access token stored.");
            }
            if (response.data.refresh_token) {
                localStorage.setItem(REFRESH_TOKEN_KEY, response.data.refresh_token);
                console.log("Refresh token stored.");
            }

            // Clean up PKCE verifier from session storage
            sessionStorage.removeItem(PKCE_VERIFIER_KEY);
            console.log("Removed PKCE Code Verifier.");

            return response.data;

        } catch (error) {
             // Clean up PKCE verifier even on error
            sessionStorage.removeItem(PKCE_VERIFIER_KEY);
            console.log("Removed PKCE Code Verifier after error.");

            if (axios.isAxiosError(error) && error.response) {
                const errorData = error.response.data as TokenErrorResponse;
                console.error("OAuth Token Error (Authorization Code Grant):", error.response.status, errorData.error, errorData.error_description);
                // Provide a more user-friendly message based on the error
                if (errorData.error === 'invalid_grant') {
                    throw new Error('Ungültiger oder abgelaufener Authorisierungscode.');
                } else if (errorData.error === 'invalid_client') {
                     throw new Error('Client-Konfigurationsfehler.');
                } else if (errorData.error === 'invalid_request' && errorData.error_description?.includes('PKCE')) {
                     throw new Error('PKCE Code Verifier Fehler.');
                } else {
                    throw new Error(errorData.error_description || errorData.error || 'Tokenaustausch fehlgeschlagen.');
                }
            } else {
                console.error("Error exchanging code for token:", error);
                throw new Error('Ein unerwarteter Fehler ist beim Tokenaustausch aufgetreten.');
            }
        }
    },

    /**
     * Logs the user out by removing tokens from local storage.
     */
    logout() {
        localStorage.removeItem(AUTH_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        // Also clear PKCE verifier in case logout happens mid-flow
        sessionStorage.removeItem(PKCE_VERIFIER_KEY);
        console.log("User logged out, tokens and PKCE verifier removed.");
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

    // --- ROPC Method Removed ---
    // async loginWithPassword(email: string, password: string): Promise<TokenResponse> { ... }

    // Optional: Add function for refresh token grant if needed
    // async refreshToken(): Promise<TokenResponse | null> { ... } // (Implementation remains the same as before)
}
