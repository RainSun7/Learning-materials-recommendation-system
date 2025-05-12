<template>
  <nav class="navbar">
    <div class="navbar-container">
      <router-link to="/" class="logo">
        <h1>资料管理系统</h1>
      </router-link>
      <div class="nav-links">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/about" class="nav-link">资料浏览</router-link>
      </div>
      <div class="user-actions">
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="login-btn">登录</router-link>
        </template>
        <template v-else>
          <el-dropdown>
            <span class="user-profile">
              {{ userInfo?.username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/profile">个人中心</router-link>
                </el-dropdown-item>
                <el-dropdown-item v-if="isAdmin">
                  <router-link to="/admin">后台管理</router-link>
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ArrowDown } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn());
const userInfo = computed(() => userStore.getUserInfo());
const isAdmin = computed(() => userInfo.value?.role === 'admin');

const handleLogout = () => {
  userStore.clearUserInfo();
  ElMessage.success('已退出登录');
  router.push('/login');
};
</script>

<style scoped>
.navbar {
  background-color: #fff;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  text-decoration: none;
}

.logo h1 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: #666;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #1890ff;
}

.user-actions {
  display: flex;
  align-items: center;
}

.login-btn {
  text-decoration: none;
  color: #1890ff;
  font-weight: 500;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  color: #333;
}

:deep(.el-dropdown-menu__item) {
  text-align: center;
  padding: 8px 20px;
}

:deep(.el-dropdown-menu__item a) {
  text-decoration: none;
  color: inherit;
  display: block;
  width: 100%;
}

:deep(.el-dropdown-menu) {
  min-width: 120px;
}
</style> 