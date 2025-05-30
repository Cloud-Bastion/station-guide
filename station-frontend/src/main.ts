import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import {library} from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import {fas} from '@fortawesome/free-solid-svg-icons'
import {fab} from '@fortawesome/free-brands-svg-icons'
import {far} from '@fortawesome/free-regular-svg-icons'
import i18next from 'i18next'
import I18NextHttpBackend from "i18next-http-backend";
import {createPinia} from "pinia";
const iconPack = {
    fas,
    fab,
    far
};

const pinia = createPinia()

library.add(fas, fab, far)
i18next
    .use(I18NextHttpBackend)
    .init({
        lng: navigator.language,
        fallbackLng: 'de',
        backend: {
            loadPath: '/i18n/{{lng}}.json'
        }
    }).then(() => {
    createApp(App)
        .use(router)
        .use(pinia)
        .component("FontAwesomeIcon", FontAwesomeIcon)
        .mount('#app')
})