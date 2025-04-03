import settings from "./settings/Settings";

const API_URL = "/v1/tasks"; // Base URL for tasks

export interface ScheduledTask {
    id: string;
    template: Task;
    frequency: string;
    daysOfWeek: Array<number>;
    daysOfMonth: Array<number>;
    startTime: string;
    endTime: string;
    endTimeDaysAdd: number;
    lastCreatedTask: string;
}

export interface Task {
    id: string;
    title: string;
    description: string;
    priority: string;
    permissionGroup: string;
    createdBy: string;
    startTime: string;
    endTime: string;
    completed: boolean;
}

export default {
    async getAllOpenTasks(): Promise<Task[]> {
        const response = await settings.apiClient.get<Task[]>(`${API_URL}/open_tasks`);
        return response.data;
    },

    async closeOrOpenTask(id: string, state: boolean): Promise<Task> {
        const response = await settings.apiClient.post<Task>(`${API_URL}/close`, {
            id: id,
            state: state
        })
        return response.data
    },

    async createTask(taskData: Task): Promise<Task> {
        const response = await settings.apiClient.post<Task>(API_URL, taskData);
        return response.data;
    },

    async createScheduledTask(scheduledTaskData: ScheduledTask): Promise<ScheduledTask> {
        const response = await settings.apiClient.post<ScheduledTask>(`${API_URL}/scheduled`, scheduledTaskData);
        return response.data;
    }
};
