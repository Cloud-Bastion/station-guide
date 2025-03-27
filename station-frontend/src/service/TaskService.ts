import settings from "./settings/Settings";

const API_URL = "/v1/tasks"; //  Adjust this to your actual API endpoint

interface ScheduledTask {
    id: string;
    template: Task;
    frequency: string;
    daysOfWeek: Array<number>;
    daysOfMonth: Array<number>;
    startTime: string;//TODO: Change to date time object(only time no date)
    endTime: string;//TODO: Change to date time object(only time no date)
    endTimeDaysAdd: number;
    lastCreatedTask: string;//TODO: Change to date object with time
}

export interface Task {
    id: string;
    title: string;
    description: string;
    priority: number;
    permissionGroup: string;
    createdBy: string;
    startTime: string;//TODO: Change to date object with time
    endTime: string;//TODO: Change to date object with time
    completed: boolean;
    isTemplate: boolean;
}

export default {
    async getAllOpenTasks(): Promise<Task[]> {
        const response = await settings.apiClient.get<Task[]>(`${API_URL}/open_tasks`);
        return response.data;
    }
};
