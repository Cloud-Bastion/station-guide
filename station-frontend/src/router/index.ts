import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import LoginView from "@/view/login/LoginView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";
import SilentRenew from "@/view/login/SilentRenew.vue";
import OAuthUserService, {useUserSession} from "@/service/OAuthUserService";
import LogoutView from "@/view/login/LogoutView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: NotFoundView
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        meta: {
            requiresAuth: false
        }
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
    },
    {
        path: '/logout',
        name: 'logout',
        component: LogoutView,
        meta: {
            requiresAuth: true
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
// Global Navigation Guard
router.beforeEach((to, from, next) => {
    const userManager = useUserSession.value
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

    if (requiresAuth) {
        userManager.verify().then(() => {
            const isAuthenticated = userManager.isLoggedIn.value
            console.log(isAuthenticated)

            console.log(`Navigating to: ${to.path}, Requires Auth: ${requiresAuth}, Is Authenticated: ${isAuthenticated}`);

            if (!isAuthenticated) {
                console.log(`Redirecting to login. Target: ${to.fullPath}`);
                next({name: 'login'});
            } else {
                next()
            }

        })
    } else {
        next()
    }


});

export default router
