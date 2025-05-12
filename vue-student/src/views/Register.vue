<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>
      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-item">
          <label>用户名</label>
          <input 
            type="text" 
            v-model="username"
            placeholder="请输入用户名（4-16个字符）"
            required
          >
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <input 
            type="email" 
            v-model="email"
            placeholder="请输入邮箱地址"
            required
          >
        </div>
        <div class="form-item">
          <label>密码</label>
          <input 
            type="password" 
            v-model="password"
            placeholder="请输入密码（至少8个字符）"
            required
          >
        </div>
        <div class="form-item">
          <label>确认密码</label>
          <input 
            type="password" 
            v-model="confirmPassword"
            placeholder="请再次输入密码"
            required
          >
        </div>
        <div class="form-item">
          <label>角色</label>
          <select v-model="role" required>
            <option value="">请选择角色</option>
            <option value="teacher">教师</option>
            <option value="student">学生</option>
          </select>
        </div>
        <div class="form-agreement">
          <label class="agreement">
            <input type="checkbox" v-model="agreeTerms" required>
            我已阅读并同意
            <a href="#" class="terms-link">服务条款</a>
            和
            <a href="#" class="terms-link">隐私政策</a>
          </label>
        </div>
        <button type="submit" class="register-btn" :disabled="!agreeTerms">注册</button>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const role = ref('');
const agreeTerms = ref(false);

const handleRegister = async () => {
  try {
    if (password.value !== confirmPassword.value) {
      alert('两次输入的密码不一致');
      return;
    }

    // TODO: 实现注册逻辑
    console.log('注册信息：', {
      username: username.value,
      email: email.value,
      password: password.value,
      role: role.value
    });
    
    // 模拟注册成功，跳转到登录页
    router.push('/login');
  } catch (error) {
    console.error('注册失败：', error);
  }
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  padding: 2rem 0;
}

.register-box {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.register-box h2 {
  text-align: center;
  color: #333;
  margin-bottom: 2rem;
  font-size: 1.8rem;
}

.register-form {
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

.form-item input,
.form-item select {
  padding: 0.8rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-item input:focus,
.form-item select:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-agreement {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
}

.agreement {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  cursor: pointer;
}

.terms-link {
  color: #1890ff;
  text-decoration: none;
}

.terms-link:hover {
  color: #40a9ff;
}

.register-btn {
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

.register-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.register-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
  margin-left: 0.5rem;
}

.login-link a:hover {
  color: #40a9ff;
}
</style> 