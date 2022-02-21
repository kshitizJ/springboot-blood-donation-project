import axios from "axios"

const LOGIN_API = "http://localhost:8080/admin/login"

export function login(user) {
    return axios.post(LOGIN_API, user)
}