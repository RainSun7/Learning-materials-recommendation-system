import { defineStore } from 'pinia';
import { ref } from 'vue';

interface UserInfo {
  token: string;
  username: string;
  role?: string;
  [key: string]: any;
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null);

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info;
    // 保存到localStorage
    localStorage.setItem('userInfo', JSON.stringify(info));
  };

  // 获取用户信息
  const getUserInfo = () => {
    if (!userInfo.value) {
      const stored = localStorage.getItem('userInfo');
      if (stored) {
        userInfo.value = JSON.parse(stored);
      }
    }
    return userInfo.value;
  };

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null;
    localStorage.removeItem('userInfo');
    localStorage.removeItem('token');
  };

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!getUserInfo();
  };

  // 检查是否是管理员
  const isAdmin = () => {
    return userInfo.value?.role === 'admin';
  };

  return {
    userInfo,
    setUserInfo,
    getUserInfo,
    clearUserInfo,
    isLoggedIn,
    isAdmin
  };
}); 