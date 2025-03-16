import settings from "./settings/Settings";
import axios from "axios";

const API_URL = settings.API_BASE_URL + "v1/expire/products";

export async function fetchAllProducts() {
    const response = await axios.get(API_URL);
    return response.data;
}