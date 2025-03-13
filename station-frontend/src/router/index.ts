import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import EmployeeManageView from "@/view/EmployeeManageView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/employeemanagement',
        name: 'employeemanagment',
        component: EmployeeManageView
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router