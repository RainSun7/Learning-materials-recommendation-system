<template>
  <div class="login-container">
    <div class="login-box">
      <h2>资料管理系统</h2>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-item">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="username"
            placeholder="请输入用户名"
            required
          >
        </div>
        <div class="form-item">
          <label>密码</label>
          <input 
            type="password" 
            v-model="password"
            placeholder="请输入密码"
            required
          >
        </div>
        <div class="form-item captcha-item">
          <label>验证码</label>
          <div class="captcha-input">
            <input 
              type="text" 
              v-model="code"
              placeholder="请输入验证码"
              required
            >
            <img 
              :src="captchaImage" 
              alt="验证码" 
              class="captcha-image"
              @click="getCaptcha"
            >
          </div>
        </div>
        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe">
            记住我
          </label>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>
        <button type="submit" class="login-btn">登录</button>
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

const username = ref('');
const password = ref('');
const code = ref('');
const uuid = ref('');
const rememberMe = ref(false);
const captchaImage = ref('');

interface CaptchaResponse {
  code: number;
  msg: string;
  img: string;
  uuid: string;
}

interface LoginResponse {
  code: number;
  msg: string;
  token: string;
}

// 获取验证码
const getCaptcha = async () => {
  try {
    const response = await request.get<any, CaptchaResponse>('/captchaImage');
    if (response.code === 200) {
      // 图片已经是完整的base64编码，直接使用
      captchaImage.value = 'data:image/jpeg;base64,' + response.img;
      uuid.value = response.uuid;
    } else {
      ElMessage.error(response.msg || '获取验证码失败');
    }
  } catch (error) {
    console.error('获取验证码失败：', error);
    ElMessage.error('获取验证码失败，请检查网络连接');
  }
};

// 登录处理
const handleLogin = async () => {
  try {
    console.log('尝试登录，发送数据:', {
      username: username.value,
      password: password.value,
      code: code.value,
      uuid: uuid.value
    });
    
    // 直接请求登录接口，与curl示例一致
    const response = await fetch('http://localhost:8081/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
        code: code.value,
        uuid: uuid.value
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP错误! 状态: ${response.status}`);
    }
    
    const data = await response.json();
    console.log('登录响应:', data);
    
    if (data.code === 200) {
      // 保存token到localStorage
      const token = data.token;
      localStorage.setItem('token', token);
      
      // 保存用户信息到状态管理
      userStore.setUserInfo({
        token,
        username: username.value,
        // 保存其他返回的用户信息
        ...(({ token: _, ...rest }) => rest)(data)
      });

      ElMessage.success('登录成功');
      // 跳转到首页
      router.push('/');
    } else {
      ElMessage.error(data.msg || '登录失败');
      // 登录失败，刷新验证码
      getCaptcha();
    }
  } catch (error: any) {
    console.error('登录失败:', error);
    ElMessage.error(`登录失败: ${error.message || '未知错误'}`);
    // 登录失败，刷新验证码
    getCaptcha();
  }
};

// 页面加载时获取验证码
onMounted(() => {
  getCaptcha();
  // 如果本地存储有用户名，自动填充
  const savedUsername = localStorage.getItem('username');
  if (savedUsername) {
    username.value = savedUsername;
    rememberMe.value = true;
  }
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-box h2 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-item label {
  color: #666;
  font-size: 0.9rem;
}

.form-item input {
  padding: 0.8rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-item input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.captcha-item {
  margin-bottom: 1rem;
}

.captcha-input {
  display: flex;
  gap: 1rem;
}

.captcha-input input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #d9d9d9;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  cursor: pointer;
}

.forgot-password {
  color: #1890ff;
  text-decoration: none;
}

.forgot-password:hover {
  color: #40a9ff;
}

.login-btn {
  width: 100%;
  padding: 0.8rem;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #40a9ff;
}

.register-link {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
  margin-left: 0.5rem;
}

.register-link a:hover {
  color: #40a9ff;
}
</style> 