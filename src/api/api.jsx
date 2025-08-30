import axios from "axios"
import { Vite_Backend_Url } from "../.env"

const api= axios.create({
        baseURL:`${Vite_Backend_Url}/api`
    
});

export default api;

