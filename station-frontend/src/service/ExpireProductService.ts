import settings from "./settings/Settings";

const API_URL = "/v1/expire/products";

export interface ExpireProductCategory {
    name: string;
    reduceProductTime?: number;
}

export interface ExpireProduct {
    productId: number;
    name: string;
    reduceProductTime?: number | undefined;
    expireDate?: Date | undefined;
    lastUpdateDate?: Date | undefined;
    category?: ExpireProductCategory | undefined | null;
}

export default {
    async getExpiringItems(): Promise<ExpireProduct[]> {
        const response = await settings.apiClient.get<ExpireProduct[]>(API_URL)
        return response.data;
    }
}