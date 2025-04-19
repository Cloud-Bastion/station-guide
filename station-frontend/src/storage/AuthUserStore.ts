import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
// Import User type for type safety (optional but recommended)
import type { User } from 'oidc-client-ts';

export const useAuthStore = defineStore('authStore', () => {
    // State
    const user = ref(null); // Will hold the User object from oidc-client-ts
    const isLoading = ref(true); // Start as true until initial check completes

    // Getters
    const isAuthenticated = computed(() => {
        // Check if user exists and is not expired
        return !!user.value && !user.value.expired;
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

    // Actions
    function setUser(newUser /* : User | null */) {
        user.value = newUser;
    }

    function setLoading(status /* : boolean */) {
        isLoading.value = status;
    }

    // Action to initialize on app load (called from App.vue or router)
    async function initializeAuth() {
        const authService = (await import('@/service/AuthUserService')).default; // Dynamic import if circular dependency risk
        setLoading(true);
        try {
            const loadedUser = await authService.getUser();
            setUser(loadedUser); // Set user based on current state in storage
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
        accessToken,
        idToken,
        userProfile,
        setUser,
        setLoading,
        initializeAuth,
    };
});