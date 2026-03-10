import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import MainHall from '../views/MainHall.vue'
import GameTable from '../views/GameTable.vue'
import Leaderboard from '../views/Leaderboard.vue'
import Exchange from '../views/Exchange.vue'
import Settings from '../views/Settings.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginPage
  },
  {
    path: '/hall',
    name: 'MainHall',
    component: MainHall
  },
  {
    path: '/table/:id',
    name: 'GameTable',
    component: GameTable
  },
  {
    path: '/leaderboard',
    name: 'Leaderboard',
    component: Leaderboard
  },
  {
    path: '/exchange',
    name: 'Exchange',
    component: Exchange
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
