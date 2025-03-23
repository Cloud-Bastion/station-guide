import settings from "./settings/Settings";
import ExpireProductService from "@/service/ExpireProductService";

const API_URL = "/v1/expire/products";
const ALL_PRODUCTS_API_URL = "/v1/expire/all-products";
const CATEGORY_API_URL = "/v1/expire/categories";

export interface ExpireProductCategory {
    id?: string;
    name: string;
    reduceProductTime?: number;
    showProducts: boolean;
}

export enum ExpireProductState {
    REDUCE = "Reduzieren",
    REDUCED = "Reduziert",
    SORT_OUT = "Aussortieren",
    SET_DATE = "Datum setzen"
}

export interface ExpireProduct {
    id: string;
    productId: number;
    name: string;
    reduceProductTime?: number | undefined;
    expireDate?: Date | undefined | null;
    lastUpdateDate?: Date | undefined;
    category?: ExpireProductCategory | undefined | null;
}

export default {
    async getExpiringItems(): Promise<ExpireProduct[]> {
        const response = await settings.apiClient.get<ExpireProduct[]>(API_URL)
        return response.data.map(product => {
            if (product.lastUpdateDate !== undefined && product.lastUpdateDate !== null)
                product.lastUpdateDate = new Date(product.lastUpdateDate + "T23:59:59")
            return product;
        });
    },

    async updateLastChange(product: ExpireProduct) {
        const response = await settings.apiClient.patch(API_URL + "/" + product.id, {
            id: product.id
        })
    },

    async updateExpireDate(product: ExpireProduct) {
        console.log("LOGGING FOR JAN")
        const response = await settings.apiClient.patch(API_URL + "/" + product.id, {
            id: product.id,
            expireDate: product?.expireDate,
            updateLastModifiedDate: false
        })
    },

    compareDates(date: Date, secondDate: Date): boolean {
        return (date.getDay() === secondDate.getDay()) &&
            (date.getMonth() === secondDate.getMonth()) &&
            (date.getFullYear() === secondDate.getFullYear());
    },

    getState(product: ExpireProduct): ExpireProductState {
        let currentDate = new Date(Date.now());

        if (product.expireDate === undefined || product.expireDate === null) {
            return ExpireProductState.SET_DATE;
        }

        if (product.lastUpdateDate !== null && product.lastUpdateDate !== undefined && ExpireProductService.compareDates(product.lastUpdateDate, currentDate)) {
            return ExpireProductState.REDUCED;
        }

        if (product.expireDate < currentDate) {
            return ExpireProductState.SORT_OUT;
        }

        return ExpireProductState.REDUCE
    },

    async getAllProducts(): Promise<ExpireProduct[]> {
        const response = await settings.apiClient.get<ExpireProduct[]>(ALL_PRODUCTS_API_URL);
        return response.data;
    },

    async getAllCategories(): Promise<ExpireProductCategory[]> {
        const response = await settings.apiClient.get<ExpireProductCategory[]>(CATEGORY_API_URL);
        return response.data;
    },

    async createCategory(categoryData: { name: string; reduceProductTime?: number }): Promise<ExpireProductCategory> {
        const response = await settings.apiClient.post<ExpireProductCategory>(CATEGORY_API_URL, categoryData);
        return response.data;
    },
}
