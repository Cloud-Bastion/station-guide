import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/employee/EmployeeManageView.vue";
import MHDManageView from "@/view/MHDManageView.vue";
import TaskManagementView from "@/view/TaskManagementView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/employee-management',
        name: 'employee-managment',
        component: EmployeeManageView
    },
    {
        path: '/mhd-management',
        name: 'mhd-management',
        component: MHDManageView
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