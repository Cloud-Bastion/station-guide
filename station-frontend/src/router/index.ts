import {createRouter, createWebHashHistory, createWebHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/task/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";
import LoginView from "@/view/login/LoginView.vue";
import NotFoundView from "@/view/error/NotFoundView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: NotFoundView
    },
    {
        path: '/',
        name: 'home',
        component: LoginView
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
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
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const isAuthenticated = true

    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
    } else {
        next();
    }
});

export default router
