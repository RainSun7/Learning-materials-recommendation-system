import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SearchView from '@/views/SearchView.vue'
import DetailView from '@/views/DetailView.vue'
import ProfileView from '@/views/ProfileView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import CategoryManage from '@/views/admin/CategoryManage.vue'
import AboutView from '@/views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/search',
      name: 'search',
      component: SearchView
    },
    {
      path: '/detail/:id',
      name: 'detail',
      component: DetailView
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView
    },
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        {
          path: '',
          name: 'admin-home',
          component: () => import('@/views/admin/AdminHome.vue')
        },
        {
          path: 'category',
          name: 'category-manage',
          component: CategoryManage
        },
        {
          path: 'materials',
          name: 'materials-manage',
          component: () => import('@/views/admin/MaterialsManage.vue')
        },
        {
          path: 'users',
          name: 'users-manage',
          component: () => import('@/views/admin/UsersManage.vue')
        },
        {
          path: 'roles',
          name: 'roles-manage',
          component: () => import('@/views/admin/RolesManage.vue')
        }
      ]
    }
  ]
})

export default router
