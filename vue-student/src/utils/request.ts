import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';

// 定义响应数据类型
interface ApiResponse<T = any> {
  code: number;
  msg: string;
  data?: T;
  token?: string;
  img?: string;
  uuid?: string;
}

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8081', // 移除dev-api部分
  timeout: 5000
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    // 添加 isToken 请求头
    config.headers['isToken'] = token ? 'true' : 'false';
    return config;
  },
  error => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    
    // 成功状态返回
    if (res.code === 200) {
      // 针对不同的API返回不同的数据结构
      // 对于验证码和登录接口，直接返回数据对象
      if (response.config.url?.includes('/captchaImage') || response.config.url?.includes('/login')) {
        return res;
      } 
      // 对于其他接口，保持返回原始响应结构
      return response;
    } else {
      ElMessage.error(res.msg || '请求失败');
      return Promise.reject(new Error(res.msg || '请求失败'));
    }
  },
  (error) => {
    // 处理401未授权错误，清除用户信息并跳转到登录页
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore();
      const router = useRouter();
      
      userStore.clearUserInfo();
      router.push('/login');
      ElMessage.error('登录状态已过期，请重新登录');
      return Promise.reject(error);
    }
    
    console.log('err' + error);
    let { message } = error;
    if (message === 'Network Error') {
      message = '后端接口连接异常';
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时';
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.substr(message.length - 3) + '异常';
    }
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);

export default service; 