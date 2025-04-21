import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
// Import User type for type safety (optional but recommended)
import type { User } from 'oidc-client-ts';

function decodeJwt(token: string) {
    const payload = token.split('.')[1];
    const decodedPayload = atob(payload); // Decode base64 to string
    return JSON.parse(decodedPayload); // Parse JSON string to object
}

export const useAuthStore = defineStore('authStore', () => {
    const user = ref(null);
    const isLoading = ref(true);

    const isAuthenticated = computed(() => {
        // Check if user exists and is not expired
        return !!user.value && !user.value.expired;
    });

    const hasPermission = computed(() => {
        return (permission: string) => {
            if (!isAuthenticated.value || !user.value?.access_token) {
                return false;
            }

            // Decode the access token to check for permissions (scope)
            const decodedToken = decodeJwt(user.value.access_token);

            // Check if the scope is an array or a string and then look for the permission
            const scope = decodedToken.scope;
            let hasPermission = false;

            if (Array.isArray(scope)) {
                // If scope is an array, check if the permission is in the array
                hasPermission = scope.includes(permission);
            } else if (typeof scope === 'string') {
                // If scope is a string, split and check for the permission
                hasPermission = scope.split(' ').includes(permission);
            }

            return hasPermission;
        };
    });

    const accessToken = computed(() => {
        return user.value?.access_token;
    });

    const idToken = computed(() => {
        return user.value?.id_token;
    });

    const userProfile = computed(() => {
        return user.value?.profile; // Contains claims from ID token/userinfo
    });

    const getUserProfileInfo = computed(() =>  {
        return {
            id: "",
            badgeId: "",
            firstname: "Melvin",
            lastname: "Schneider",
            profilePictureUrl: "https://cdn.discordapp.com/avatars/433630460212543498/bad60d241049ff400e9a3ed16a27e5f2?size=1024",
            roleName: "Store Manager"
        }
    });

    // Actions
    function setUser(newUser /* : User | null */) {
        user.value = newUser;
    }

    function setLoading(status /* : boolean */) {
        isLoading.value = status;
    }

    async function initializeAuth() {
        const authService = (await import('@/service/AuthUserService')).default;
        setLoading(true);
        try {
            const loadedUser = await authService.getUser();
            setUser(loadedUser);
            console.log("Auth initialized, user:", loadedUser);
        } catch (error) {
            console.error("Error initializing auth:", error);
            setUser(null);
        } finally {
            setLoading(false);
        }
    }

    return {
        user,
        isLoading,
        isAuthenticated,
        getUserProfileInfo,
        hasPermission,
        accessToken,
        idToken,
        userProfile,
        setUser,
        setLoading,
        initializeAuth,
    };
});