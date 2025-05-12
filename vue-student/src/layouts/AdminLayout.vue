<template>
  <div class="admin-layout">
    <div class="sidebar">
      <div class="logo">资料推荐平台</div>
      <div class="menu">
        <router-link to="/admin" class="menu-item" exact>
          <i class="fas fa-home"></i>
          首页
        </router-link>
        <router-link to="/admin/category" class="menu-item">
          <i class="fas fa-folder"></i>
          分类管理
        </router-link>
        <router-link to="/admin/materials" class="menu-item">
          <i class="fas fa-book"></i>
          学习资料管理
        </router-link>
        <router-link to="/admin/users" class="menu-item">
          <i class="fas fa-users"></i>
          用户管理
        </router-link>
        <router-link to="/admin/roles" class="menu-item">
          <i class="fas fa-user-shield"></i>
          角色管理
        </router-link>
      </div>
    </div>
    <div class="main">
      <div class="header">
        <div class="breadcrumb">
          <!-- 面包屑导航 -->
        </div>
        <div class="user-info">
          <template v-if="isLoggedIn">
            欢迎，{{ username }}
            <a href="#" class="logout" @click.prevent="handleLogout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login" class="login-btn">登录</router-link>
          </template>
        </div>
      </div>
      <div class="content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isLoggedIn = ref(false); // 这里应该从状态管理或登录状态获取
const username = ref('张三'); // 这里应该从状态管理或登录状态获取

const handleLogout = () => {
  // 处理退出登录逻辑
  isLoggedIn.value = false;
  router.push('/login');
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background-color: #304156;
  color: white;
  padding: 1rem 0;
}

.logo {
  font-size: 1.2rem;
  font-weight: bold;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu {
  padding: 1rem 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 0.8rem 1.5rem;
  color: #bfcbd9;
  text-decoration: none;
  transition: all 0.3s;
}

.menu-item:hover,
.menu-item.router-link-active {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.menu-item i {
  margin-right: 0.8rem;
  width: 20px;
  text-align: center;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.header {
  height: 64px;
  background: white;
  padding: 0 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #666;
}

.login-btn,
.logout {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  transition: all 0.3s;
}

.login-btn {
  background: #1890ff;
  color: white;
}

.login-btn:hover {
  background: #40a9ff;
}

.logout {
  color: #ff4d4f;
}

.logout:hover {
  background: rgba(255, 77, 79, 0.1);
}

.content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}
</style> 