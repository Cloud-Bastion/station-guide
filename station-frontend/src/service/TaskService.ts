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
// Interface for the open planned tasks.  Matches StationTaskDTO.
interface OpenPlannedTask {
    id: string;
    permissionGroup: string | null;
    endTime: string | null;
    isTemplate: boolean;
    scheduledTaskId: string | null;
    completed: boolean;
}
export default {
    async getOpenPlannedTasks(): Promise<OpenPlannedTask[]> { //  Return OpenPlannedTask[]
        const response = await settings.apiClient.get<OpenPlannedTask[]>(`${API_URL}/open-planned`);
        return response.data;
    },

    async createScheduledTask(taskData: CreateScheduledTaskRequest): Promise<ScheduledTask> {
        const response = await settings.apiClient.post<ScheduledTask>(`${API_URL}/scheduled`, taskData);
        return response.data;
    },
    //get all open tasks
    async getAllOpenTasks(): Promise<OpenPlannedTask[]> {
        const response = await settings.apiClient.get<OpenPlannedTask[]>(`${API_URL}/open`);
        console.log(response.data)
        return response.data;
    }
};
