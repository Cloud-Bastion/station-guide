import { UserManager, WebStorageStateStore, Log } from 'oidc-client-ts';
import { useAuthStore } from '@/storage/AuthUserStore';
import Settings from "@/service/settings/Settings"; // We'll create/update this Pinia store

// Optional: Configure logging level
// Log.setLevel(Log.DEBUG);
// Log.setLogger(console);

const userManagerSettings = {
    authority: Settings.AUTH_SERVER_URL,
    client_id: Settings.AUTH_CLIENT_ID,
    redirect_uri: Settings.AUTH_REDIRECT_URI,
    silent_redirect_uri: Settings.AUTH_SILENT_REDIRECT_URI,
    post_logout_redirect_uri: Settings.AUTH_POST_LOGOUT_REDIRECT_URI,

    response_type: 'code', // Use Authorization Code flow (PKCE is automatic)
    scope: Settings.AUTH_SCOPES, // Ensure 'openid' is present

    // --- Silent Renew ---
    automaticSilentRenew: true, // Enable silent renewal
    // checkSessionIntervalInSeconds: 10, // How often to check session status (optional)
    // silentRequestTimeoutInSeconds: 10, // Timeout for silent renew iframe (optional)

    // --- Storage ---
    // Defaults to sessionStorage, use localStorage to persist across tabs/windows
    userStore: new WebStorageStateStore({ store: window.localStorage }),

    // --- Optional Settings ---
    loadUserInfo: true, // Fetch user info from userinfo endpoint after login/refresh
    monitorSession: true, // Recommended: Enable monitoring session status via check session iframe
    filterProtocolClaims: true, // Remove OIDC protocol claims (like nbf, iss, aud, etc.) from profile
    // metadata: { // Provide endpoints explicitly if discovery is problematic
    //   issuer: import.meta.env.VITE_AUTH_SERVER_URL,
    //   authorization_endpoint: `${import.meta.env.VITE_AUTH_SERVER_URL}/oauth2/authorize`,
    //   token_endpoint: `${import.meta.env.VITE_AUTH_SERVER_URL}/oauth2/token`,
    //   userinfo_endpoint: `${import.meta.env.VITE_AUTH_SERVER_URL}/userinfo`, // If using OIDC userinfo
    //   end_session_endpoint: `${import.meta.env.VITE_AUTH_SERVER_URL}/connect/logout`, // If using OIDC logout
    //   jwks_uri: `${import.meta.env.VITE_AUTH_SERVER_URL}/oauth2/jwks`,
    // }
};

class AuthService {
    constructor() {
        this.userManager = new UserManager(userManagerSettings);
        //this.setupEventHandlers();
    }

    setupEventHandlers() {
        const authStore = useAuthStore(); // Get Pinia store instance

        // Called when a user object is loaded (initial load, login callback, silent renew)
        this.userManager.events.addUserLoaded((user) => {
            console.log('AuthService: User loaded', user);
            authStore.setUser(user); // Update Pinia store
            authStore.setLoading(false);
        });

        // Called when the access token is nearing expiry and silent renew is triggered
        this.userManager.events.addAccessTokenExpiring(() => {
            console.log('AuthService: Access token expiring, silent renew starting...');
            // No action needed usually, library handles the renew
        });

        // Called when silent renew succeeds
        this.userManager.events.addUserSignedIn(() => {
            console.log("AuthService: User signed in silently.");
            // Refresh user state if necessary, though addUserLoaded should also fire
            this.getUser().then(user => authStore.setUser(user));
        });

        // Called when the access token has expired (silent renew might have failed)
        this.userManager.events.addAccessTokenExpired(() => {
            console.warn('AuthService: Access token expired. User may need to log in again.');
            // Optionally attempt manual signinSilent or trigger logout
            this.userManager.signinSilent().catch(err => {
                console.error("Manual silent signin failed after expiry:", err);
                this.logout(); // Force logout if silent renew ultimately fails
            });
        });

        // Called when silent renew fails
        this.userManager.events.addSilentRenewError((error) => {
            console.error('AuthService: Silent renew error', error);
            authStore.setUser(null); // Clear user state on failure
            authStore.setLoading(false);
            // Potentially trigger a full login redirect or show error message
            // this.login();
        });

        // Called when user is logged out or session is terminated elsewhere
        this.userManager.events.addUserSignedOut(() => {
            console.log('AuthService: User signed out');
            authStore.setUser(null);
            authStore.setLoading(false);
            // Could redirect to login or logged-out page
        });

        // Called when user session has changed (e.g. logged in/out in another tab)
        this.userManager.events.addUserSessionChanged(() => {
            console.log("AuthService: User session changed.");
            // Reload user data to reflect the change
            this.getUser().then(user => authStore.setUser(user));
        });
    }

    // --- Public Methods ---

    // Trigger login redirect
    login() {
        useAuthStore().setLoading(true);
        return this.userManager.signinRedirect({ state: { /* optional data */ } });
    }

    // Handle the callback from login redirect
    async handleLoginCallback() {
        console.log("in callback method")
        useAuthStore().setLoading(true);
        try {
            const user = await this.userManager.signinRedirectCallback();
            console.log('AuthService: Login callback successful', user);
            useAuthStore().setLoading(false);
            // setUser is handled by the addUserLoaded event handler
            return user;
        } catch (error) {
            console.error("AuthService: Error handling login callback:", error);
            useAuthStore().setUser(null);
            useAuthStore().setLoading(false);
            throw error; // Re-throw for component
        }
    }

    // Trigger logout redirect
    logout() {
        useAuthStore().setLoading(true);
        return this.userManager.signoutRedirect({ state: { /* optional data */ } });
    }

    // Handle the callback from logout redirect (if needed, often just redirects)
    async handleLogoutCallback() {
        // Usually no user object is returned, just completes the redirect
        await this.userManager.signoutRedirectCallback();
        // setUser(null) handled by addUserSignedOut event
    }

    // Get the current user object (may be null)
    async getUser() {
        const user = await this.userManager.getUser();
        // Ensure Pinia store is updated on initial check if needed
        // useAuthStore().setUser(user); // Handled by addUserLoaded generally
        return user;
    }

    // Get the current valid access token (returns null if not available/expired)
    async getAccessToken() {
        const user = await this.getUser();
        if (user && !user.expired) {
            return user.access_token;
        }
        return null; // Or potentially trigger silent renew manually if needed: this.userManager.signinSilent()
    }

    // Expose the UserManager instance if needed for advanced scenarios
    getUserManager(): UserManager {
        return this.userManager;
    }
}

// Export a singleton instance
export default new AuthService();