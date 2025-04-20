import { UserManager, WebStorageStateStore, Log } from 'oidc-client-ts';
import { useAuthStore } from '@/storage/AuthUserStore';
import Settings from "@/service/settings/Settings"; // We'll create/update this Pinia store

const userManagerSettings = {
    authority: Settings.AUTH_SERVER_URL,
    client_id: Settings.AUTH_CLIENT_ID,
    redirect_uri: Settings.AUTH_REDIRECT_URI,
    silent_redirect_uri: Settings.AUTH_SILENT_REDIRECT_URI,
    post_logout_redirect_uri: Settings.AUTH_POST_LOGOUT_REDIRECT_URI,
    response_type: 'code',
    scope: Settings.AUTH_SCOPES,
    automaticSilentRenew: true,
    userStore: new WebStorageStateStore({ store: window.localStorage }),

    loadUserInfo: true,
    monitorSession: true,
    filterProtocolClaims: true,
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

    login() {
        useAuthStore().setLoading(true);
        return this.userManager.signinRedirect({ state: { /* optional data */ } });
    }

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

    logout() {
        useAuthStore().setLoading(true);
        return this.userManager.signoutRedirect({ state: { /* optional data */ } });
    }

    async handleLogoutCallback() {
        await this.userManager.signoutRedirectCallback();
    }

    async getUser() {
        const user = await this.userManager.getUser();
        return user;
    }

    async getAccessToken() {
        const user = await this.getUser();
        if (user && !user.expired) {
            return user.access_token;
        }
        return null;
    }

    getUserManager(): UserManager {
        return this.userManager;
    }
}

export default new AuthService();