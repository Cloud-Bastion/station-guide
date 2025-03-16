import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import TaskManagementView from "@/view/TaskManagementView.vue";
import ProductExpireManageView from "@/view/expire/ProductExpireManageView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/employee-management',
        name: 'employee-managment',
        component: EmployeeManageView
    },
    {
        path: '/product-expire-management',
        name: 'product-expire-management',
        component: ProductExpireManageView
    },
    {
        path: '/task-management',
        name: 'task-management',
        component: TaskManagementView
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router