import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";
import SilentRenew from "@/view/login/SilentRenew.vue";
import LoginCallbackView from "@/view/login/LoginCallbackView.vue";
import {useAuthStore} from "@/storage/AuthUserStore";
import AuthUserService from "@/service/AuthUserService";

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
            needPermission: null
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
            requiresAuth: true
        }
    },
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

    if (authStore.isLoading) {
        console.log("Auth guard waiting for loading state...");
        await new Promise(resolve => setTimeout(resolve, 50)); // Small delay, improve this
    }

    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const isAuthenticated = authStore.isAuthenticated; // Use Pinia getter
    const hasPermission = to.matched.some(record => record.meta.needPermission === null || record.meta.needPermission === undefined || authStore.hasPermission(record.meta.needPermission as string));

    if (requiresAuth && !isAuthenticated) {
        console.log(`Route ${to.path} requires auth, user not authenticated. Redirecting to login...`);
        await authService.login();
        // Prevent navigation until redirect happens
        next(false); // Or don't call next() if login() reliably redirects
    } else {
        if (hasPermission) {
            next();
        } else {
            next(false);
        }
    }
});

export default router
