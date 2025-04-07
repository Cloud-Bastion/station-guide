import axios from "axios";
import {User, UserManager, WebStorageStateStore} from 'oidc-client-ts'
import {computed, ref} from "vue";
import {useRoute, useRouter} from "vue-router";

const OAUTH_BASE_URL = "http://localhost:8081/"
const LOGIN_URL = "http://localhost:8081/api/v1/auth/login"
const FRONTEND_BASE_URL = "http://localhost:5173"
const LOCAL_CLIENT_ID = "station-frontend"

interface LoginResponse {
    token: string,
    error: string
}

export const useUserSession = computed(() => {
    // token will be synced with local storage
    // @see https://vueuse.org/core/usestorage/

    const user = ref<User | undefined>()
    const loading = ref(true)
    const toSingup = ref(false)
    const otpTokenBuffer = ref<string>('')
    const userEmailBuffer = ref<string>('')
    const router = useRouter()

    const isLoggedIn = computed(() => {
        return user.value !== undefined
    })

    const userManager = new UserManager({
        userStore: new WebStorageStateStore({store: window.localStorage}),
        authority: OAUTH_BASE_URL,
        client_id: "station-frontend",
        redirect_uri: location.protocol + "//" + location.host,
        post_logout_redirect_uri: location.protocol + "//" + location.host + "/login",
        automaticSilentRenew: true,
        includeIdTokenInSilentSignout: true,
        silent_redirect_uri: location.protocol + "//" + location.host + "/silent-renew",
        response_type: "code",
        scope: "openid",
        prompt: "",
        iframeScriptOrigin: location.protocol + "//" + location.host,
        iframeNotifyParentOrigin: location.protocol + "//" + location.host,
        // metadataSeed: {
        //     frontchannel_logout_session_supported: true,
        //     frontchannel_logout_supported: true,
        //     // backchannel_logout_session_supported: true,
        //     // backchannel_logout_supported: true
        // }
    })

    async function verify() {
        console.log("verify")
        return await userManager
            .getUser().then(async u => {
                if (u !== null && !u.expired) {
                    console.log("user not expired, not null")
                    user.value = u
                    return user.value
                } else {
                    await userManager.signinSilent()
                        .then(async () => {
                            console.log('signinSilent worked')
                            user.value = await userManager.getUser()
                            return user.value
                        }).catch(async (error) => {
                            console.log('signinSilent didnt work')
                            await userManager.signinCallback()
                            user.value = undefined
                            await userManager.removeUser()
                            if (router.currentRoute.value.name !== "login") {
                                await router.push({ name: "login" })
                            }
                            throw error
                        })
                }
            }).catch(() => {
                console.log("user get error")
                user.value = undefined
            })
    }

    function setLoading(newLoading: boolean) {
        loading.value = newLoading
    }

    async function logoutUser(silent: boolean = true) {
        if(silent) {
            await userManager.signoutSilent()
        } else {
            await userManager.signoutRedirect()
        }
        user.value = undefined
    }

    async function logInEmailPw(emailInput: string, passwordInput: string) {
        return axios.post(LOGIN_URL, {
            username: emailInput,
            password: passwordInput,
        }, {withCredentials: true})
    }

    return {
        user,
        isLoggedIn,
        loading,
        logoutUser,
        setLoading,
        logInEmailPw,
        userManager,
        verify,
    } as const
})

export default {}