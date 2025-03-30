import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import LoginView from "@/view/login/LoginView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";
// Removed OAuthCallbackView import

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: NotFoundView
    },
    {
        path: '/',
        name: 'home',
        component: LoginView,
        // Redirect to dashboard if already logged in
        beforeEnter: (to, from, next) => {
            if (localStorage.getItem('auth_token')) {
                next({ name: 'employee-management' }); // Or your default authenticated route
            } else {
                next();
            }
        }
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
         // Redirect to dashboard if already logged in
        beforeEnter: (to, from, next) => {
            if (localStorage.getItem('auth_token')) {
                next({ name: 'employee-management' }); // Or your default authenticated route
            } else {
                next();
            }
        }
    },
    // Removed OAuth callback route
    // {
    //     path: '/oauth/callback',
    //     name: 'oauth-callback',
    //     component: OAuthCallbackView,
    // },
    // Remove or adapt Google specific callback if not needed anymore
    // {
    //     path: '/google-callback',
    //     name: 'google-callback',
    //     component: GoogleCallbackView // Replace with OAuthCallbackView or remove
    // },
    {
        path: '/employee/management',
        name: 'employee-management',
        component: EmployeeManageView,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/expire/management',
        name: 'expire/management',
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

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('auth_token');
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

    // Allow access to login page even if requiresAuth is true initially
    if (to.name === 'login') { // Removed 'oauth-callback'
        // If trying to access login page while already logged in, redirect to dashboard
        if (token) {
             next({ name: 'employee-management' });
        } else {
            next();
        }
        return;
    }

    if (requiresAuth && !token) {
        console.log("Redirecting to login, requiresAuth:", requiresAuth, "token:", token);
        next({ name: 'login' }); // Redirect to login if auth is required and no token
    } else {
        next(); // Proceed as normal
    }
});

export default router
