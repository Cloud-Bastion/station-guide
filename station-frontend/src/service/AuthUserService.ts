import axios from "axios";

// Use the base URL of the auth server, not the specific API path
const AUTH_SERVER_URL: string = window.env.AUTH_BASE_URL.replace('/api/v1/auth/', '');
const TOKEN_ENDPOINT = `${AUTH_SERVER_URL}/oauth2/token`;
const CLIENT_ID = "station-frontend-client"; // Must match RegisteredClient in auth-server
const REDIRECT_URI = "http://localhost:5173/oauth/callback"; // Must match RegisteredClient and router

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
    async getUserAuth(username: string, password: string): Promise<UserAuthentication> {
        // This uses the direct password login, which we are replacing with OAuth2
        const response = await axios.post(`${window.env.AUTH_BASE_URL}/login`, {
            username: username,
            password: password,
        });
        // Assuming the old endpoint returned { token: string } or { error: string }
        // Adjust based on actual old response structure if needed for reference
        return response.data;
    },
    */

    /**
     * Exchanges an authorization code for an access token using the Authorization Code Grant with PKCE.
     * @param code The authorization code received from the authorization server.
     * @param codeVerifier The PKCE code verifier corresponding to the code challenge sent earlier.
     * @returns A promise that resolves with the token response.
     */
    async exchangeCodeForToken(code: string, codeVerifier: string): Promise<TokenResponse> {
        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('code', code);
        params.append('redirect_uri', REDIRECT_URI);
        params.append('client_id', CLIENT_ID);
        params.append('code_verifier', codeVerifier);

        try {
            const response = await axios.post<TokenResponse>(TOKEN_ENDPOINT, params, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                // Log or handle the specific OAuth2 error from the server
                const errorData = error.response.data as TokenErrorResponse;
                console.error("OAuth Token Error:", errorData.error, errorData.error_description);
                throw new Error(errorData.error_description || errorData.error || 'Failed to exchange code for token');
            } else {
                // Handle other network or unexpected errors
                console.error("Error exchanging code for token:", error);
                throw new Error('An unexpected error occurred during token exchange.');
            }
        }
    },

    // Optional: Add function for refresh token grant if needed
    // async refreshToken(refreshToken: string): Promise<TokenResponse> { ... }

    // Optional: Add function for logout (might involve redirecting to auth-server logout endpoint)
    // async logout() { ... }

    /**
     * @deprecated Google login should ideally go through the main auth-server flow.
     */
    /*
    async getGoogleAuth(username: string, password: string): Promise<UserAuthentication> {
        const response = await apiClient.post<UserAuthentication>("/login", {
            username: username,
            password: password,
        });
        return response.data;
    },
    */
}
