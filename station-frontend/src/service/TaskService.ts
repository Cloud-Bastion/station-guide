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
    subtasks: { id: string; title: string }[];
    files: string[];
    priority: number;
    createdBy: string;
    completed: boolean;
    templateTaskId: string;
}

export default {
    async getScheduledTasks(): Promise<ScheduledTask[]> {
        const response = await settings.apiClient.get<ScheduledTask[]>(`${API_URL}/scheduled`); // Added /scheduled
        return response.data;
    },
    // Add other methods (create, update, etc.) as needed
};
