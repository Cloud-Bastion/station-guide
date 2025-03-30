import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import LoginView from "@/view/login/LoginView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";
import AuthUserService from '@/service/AuthUserService'; // Import auth service for check
// --- REMOVED OIDC Callback View Import ---
// import OAuthCallbackView from '@/view/login/OAuthCallbackView.vue';

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: NotFoundView
    },
    {
        path: '/',
        name: 'home',
        component: LoginView, // Default to login view
        // Redirect handled by global beforeEach guard
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: { isPublic: true } // Mark login page as public
    },
    // --- REMOVED OIDC Callback Route ---
    // {
    //     path: '/oidc-callback',
    //     name: 'oidc-callback',
    //     component: OAuthCallbackView,
    //     meta: { isPublic: true }
    // },
    // --- END REMOVED Route ---
    {
        path: '/employee/management',
        name: 'employee-management',
        component: EmployeeManageView,
        meta: {
            requiresAuth: true // Mark as requiring authentication
        }
    },
    {
        path: '/expire/management',
        name: 'expire-management', // Consistent naming convention
        component: ProductExpireManageView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/tasks',
        name: 'tasks',
        component: TaskManagementView,
        meta: {
            requiresAuth: true
        }
    }
]

const router = createRouter({
    history: createWebHistory(), // Use createWebHistory for cleaner URLs
    routes
})

// Global Navigation Guard
router.beforeEach((to, from, next) => {
    const isAuthenticated = AuthUserService.isAuthenticated(); // Check if token exists
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const isPublic = to.matched.some(record => record.meta.isPublic); // Check if route is explicitly public

    console.log(`Navigating to: ${to.path}, Requires Auth: ${requiresAuth}, Is Public: ${isPublic}, Is Authenticated: ${isAuthenticated}`);

    if (requiresAuth && !isAuthenticated) {
        // If trying to access a protected route without being authenticated,
        // redirect to the login page.
        console.log(`Redirecting to login. Target: ${to.fullPath}`);
        next({ name: 'login' });
    } else if (to.name === 'login' && isAuthenticated) {
        // If trying to access the login page while already authenticated,
        // redirect to the default authenticated route (e.g., dashboard).
        console.log(`User already logged in, redirecting from login to dashboard.`);
        next({ name: 'employee-management' }); // Or your default authenticated route
    } else {
        // Allow navigation if:
        // - Route requires auth and user is authenticated
        // - Route does not require auth (is public or no meta field)
        console.log(`Proceeding to route: ${to.fullPath}`);
        next();
    }
});

export default router
