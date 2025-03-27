import settings from "./settings/Settings";
import { TaskDTO } from "@/view/task/TaskDTO"; // Assuming TaskDTO is defined here or imported correctly

const API_URL = "/v1/tasks"; // Base URL for tasks
const SCHEDULED_API_URL = "/v1/tasks/scheduled"; // URL for scheduled tasks

// Interface for creating a regular Task
export interface TaskCreateRequest {
    title: string;
    description?: string | null;
    priority?: number;
    permissionGroup?: string | null;
    createdBy?: string | null; // Usually set by backend based on logged-in user
    startTime?: string | null; // ISO DateTime string
    endTime?: string | null;   // ISO DateTime string
    // completed and isTemplate are typically handled by the backend
}

// Interface for the template part of a scheduled task creation request
export interface ScheduledTaskTemplateRequest {
    title: string;
    description?: string | null;
    priority?: number;
    permissionGroup?: string | null;
    createdBy?: string | null; // Usually set by backend
}

// Interface for creating a Scheduled Task
export interface ScheduledTaskCreateRequest {
    template: ScheduledTaskTemplateRequest;
    frequency: string; // e.g., "DAILY", "WEEKLY"
    daysOfWeek?: number[] | null;
    daysOfMonth?: number[] | null;
    startTime?: string | null; // LocalTime string "HH:mm:ss"
    endTime?: string | null;   // LocalTime string "HH:mm:ss"
    endTimeDaysAdd?: number | null;
    // lastCreatedTask is handled by the backend
}


// Existing interfaces (assuming TaskDTO is similar to Task)
export interface ScheduledTask {
    id: string;
    template: TaskDTO; // Use TaskDTO here
    frequency: string;
    daysOfWeek: Array<number>;
    daysOfMonth: Array<number>;
    startTime: string; // LocalTime string "HH:mm:ss"
    endTime: string;   // LocalTime string "HH:mm:ss"
    endTimeDaysAdd: number;
    lastCreatedTask: string; // LocalDateTime string
}

export interface Task extends TaskDTO { // Extend or use TaskDTO directly
    // TaskDTO already has: id, title, description, priority, permissionGroup, createdBy, startTime, endTime, completed, isTemplate
}

export default {
    async getAllOpenTasks(): Promise<Task[]> {
        const response = await settings.apiClient.get<Task[]>(`${API_URL}/open_tasks`);
        // Ensure date strings are correctly handled if needed, though often components handle ISO strings
        return response.data;
    },

    // --- NEW: Create a regular task ---
    async createTask(taskData: TaskCreateRequest): Promise<Task> {
        const response = await settings.apiClient.post<Task>(API_URL, taskData);
        return response.data;
    },

    // --- NEW: Create a scheduled task ---
    async createScheduledTask(scheduledTaskData: ScheduledTaskCreateRequest): Promise<ScheduledTask> {
        const response = await settings.apiClient.post<ScheduledTask>(SCHEDULED_API_URL, scheduledTaskData);
        return response.data;
    }

    // --- TODO: Add methods for updating, deleting tasks if needed ---
};
