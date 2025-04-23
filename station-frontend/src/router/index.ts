import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";
import SilentRenew from "@/view/login/SilentRenew.vue";
import LoginCallbackView from "@/view/login/LoginCallbackView.vue";
import {useAuthStore} from "@/storage/AuthUserStore";
import AuthUserService from "@/service/AuthUserService";
// Import the new view
import RoleAuthorityManagementView from "@/view/admin/RoleAuthorityManagementView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: NotFoundView
    },
    {
        path: '',
        redirect: {
            name: 'tasks'
        }
    },
    {
        path: '/employee/management',
        name: 'employee-management',
        component: EmployeeManageView,
        meta: {
            requiresAuth: true, // Mark as requiring authentication
            needPermission: null // Or specific permission like 'employee:read'
        }
    },
    {
        path: '/expire/management',
        name: 'expire-management', // Consistent naming convention
        component: ProductExpireManageView,
        meta: {
            requiresAuth: true,
            needPermission: "expire_product:read"
        }
    },
    {
        path: '/tasks',
        name: 'tasks',
        component: TaskManagementView,
        meta: {
            requiresAuth: true,
            needPermission: null // Or specific permission like 'task:read'
        }
    },
    // --- New Route for Role/Authority Management ---
    {
        path: '/admin/roles',
        name: 'role-authority-management',
        component: RoleAuthorityManagementView,
        meta: {
            requiresAuth: true,
            needPermission: "role:read" // Requires 'role:read' permission
        }
    },
    // --- End New Route ---
    {
        path: '/callback',
        name: 'callback',
        component: LoginCallbackView,
        meta: {
            requiresAuth: false
        }
    },
    {
        path: '/silent-renew',
        name: 'silent-renew',
        component: SilentRenew,
        meta: {
            requiresAuth: false
        }
    },
]

const router = createRouter({
    history: createWebHistory(), // Use createWebHistory for cleaner URLs
    routes
})

const authService = AuthUserService
// Global Navigation Guard
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();

    // Wait for auth initialization if it's still loading
    while (authStore.isLoading) {
        console.log("Auth guard waiting for loading state...");
        await new Promise(resolve => setTimeout(resolve, 50)); // Small delay
    }

    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const isAuthenticated = authStore.isAuthenticated;
    const requiredPermission = to.meta.needPermission as string | undefined;

    if (requiresAuth && !isAuthenticated) {
        console.log(`Route ${to.path} requires auth, user not authenticated. Redirecting to login...`);
        await authService.login();
        // Prevent navigation until redirect happens
        next(false);
    } else if (requiresAuth && requiredPermission && !authStore.hasPermission(requiredPermission)) {
        // If auth is required and a specific permission is needed but missing
        console.warn(`User authenticated but lacks permission '${requiredPermission}' for route ${to.path}. Redirecting or showing 'Forbidden'.`);
        // Option 1: Redirect to a 'Forbidden' page
        // next({ name: 'forbidden' }); // Assuming you have a 'forbidden' route
        // Option 2: Redirect to a safe page like home/tasks
        next({ name: 'tasks' });
        // Option 3: Prevent navigation
        // next(false);
    } else {
        // Allow navigation if:
        // - Route doesn't require auth OR
        // - User is authenticated and no specific permission is needed OR
        // - User is authenticated and has the required permission
        next();
    }
});

export default router
