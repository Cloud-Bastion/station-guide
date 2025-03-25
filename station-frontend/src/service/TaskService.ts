import settings from "./settings/Settings";

const API_URL = "/v1/tasks"; //  Adjust this to your actual API endpoint

interface ScheduledTask {
    id: string;
    permissionGroup: string;
    startTime: string;
    endTime: string;
    schedule: string;
    title: string;
    description: string;
    subtasks: { id: string; title: string, completed: boolean }[]; // Updated subtask
    files: string[];
    priority: number;
    createdBy: string;
    completed: boolean;
    templateTaskId: string;
}

// Interface for creating a scheduled task
interface CreateScheduledTaskRequest {
    permissionGroup?: string;
    startTime?: string;
    endTime?: string;
    recurring: boolean;
    recurrenceRule?: string;
    title?: string;
    description?: string;
    files: string[];
    priority: number;
    createdBy?: string; // You might want to get this from the logged-in user context
}

export default {
    async getOpenPlannedTasks(): Promise<ScheduledTask[]> {
        const response = await settings.apiClient.get<ScheduledTask[]>(`${API_URL}/open-planned`);
        console.log(response.data)
        return response.data;
    },

    async createScheduledTask(taskData: CreateScheduledTaskRequest): Promise<ScheduledTask> {
        const response = await settings.apiClient.post<ScheduledTask>(`${API_URL}/scheduled`, taskData);
        return response.data;
    },
};
