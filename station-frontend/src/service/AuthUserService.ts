import axios from "axios";

// Use the base URL of the auth server, not the specific API path
const AUTH_SERVER_URL: string = window.env.AUTH_BASE_URL.replace('/api/v1/auth/', '');
const TOKEN_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/token`;
// CLIENT_ID and REDIRECT_URI are defined within the functions now or passed as arguments

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

export default {
    /**
     * @deprecated Use OAuth2 flow instead.
     */
    /*
    async getUserAuth(username: string, password: string): Promise<UserAuthentication> { ... }
    */

    /**
     * @deprecated Replaced by ROPC flow in loginWithPassword.
     */
    /*
    async exchangeCodeForToken(code: string, codeVerifier: string): Promise<TokenResponse> { ... }
    */

    /**
     * Logs in a user using the OAuth2 Resource Owner Password Credentials (ROPC) grant.
     * @param username The user's username (email).
     * @param password The user's password.
     * @param clientId The OAuth2 client ID.
     * @param scope The requested scopes.
     * @returns A promise that resolves with the token response.
     */
    async loginWithPassword(username: string, password: string, clientId: string, scope: string): Promise<TokenResponse> {
        const params = new URLSearchParams();
        params.append('grant_type', 'password');
        params.append('username', username);
        params.append('password', password);
        params.append('client_id', clientId);
        params.append('scope', scope);

        try {
            const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                    // No 'Authorization' header needed here for public clients (ClientAuthenticationMethod.NONE)
                    // If using client_secret_basic, it would be:
                    // 'Authorization': 'Basic ' + btoa(clientId + ':' + clientSecret)
                }
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                const errorData = error.response.data as TokenErrorResponse;
                console.error("OAuth Token Error (Password Grant):", errorData.error, errorData.error_description);
                // Provide a more user-friendly message based on the error
                if (errorData.error === 'invalid_grant') {
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

    // Optional: Add function for refresh token grant if needed
    // async refreshToken(refreshToken: string): Promise<TokenResponse> { ... }

    // Optional: Add function for logout (might involve redirecting to auth-server logout endpoint or just clearing local tokens)
    logout() {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('refresh_token');
        // Optionally redirect to login page or auth server logout endpoint
        // window.location.href = '/login';
        // Or: window.location.href = `${AUTH_SERVER_URL}/logout?id_token_hint=...&post_logout_redirect_uri=...`;
        console.log("User logged out locally.");
    }

    /**
     * @deprecated Google login should ideally go through the main auth-server flow.
     */
    /*
    async getGoogleAuth(username: string, password: string): Promise<UserAuthentication> { ... }
    */
}
