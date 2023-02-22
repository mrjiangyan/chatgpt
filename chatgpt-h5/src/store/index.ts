import { createStore } from 'vuex'
// import modules from "./modules";
import user, { UserState } from './modules/user'
export default createStore<{ user: UserState }>({
  modules: {
    user
  }
})
