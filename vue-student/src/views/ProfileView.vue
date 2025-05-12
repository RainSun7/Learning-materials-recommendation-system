<template>
  <div class="profile">
    <Navbar />
    <div class="main-content">
      <div class="profile-header">
<!--        <img :src="user.avatar" alt="用户头像" class="avatar">-->
        <div class="user-info">
          <h1>{{ user.name }}</h1>
          <div class="user-details">
            <div class="detail-item">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ user.email || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">手机：</span>
              <span class="detail-value">{{ user.phonenumber || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">性别：</span>
              <span class="detail-value">{{ user.sex === '1' ? '男' : user.sex === '2' ? '女' : '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">部门：</span>
              <span class="detail-value">{{ user.deptName || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">角色：</span>
              <span class="detail-value">{{ user.roleName || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">注册时间：</span>
              <span class="detail-value">{{ user.createTime ? new Date(user.createTime).toLocaleString() : '未知' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">搜索历史</h2>
        <div v-if="loading.searchHistory" class="loading">加载中...</div>
        <ul v-else class="search-history">
          <li v-for="(search, index) in searchHistory" :key="index" class="search-item">
            <span class="search-term">{{ search.term }}</span>
            <span class="search-time">{{ search.time }}</span>
          </li>
        </ul>
      </div>

      <div class="section">
        <h2 class="section-title">访问记录</h2>
        <div v-if="loading.visitedMaterials" class="loading">加载中...</div>
        <div v-else class="visited-materials">
          <MaterialCard
              v-for="material in visitedMaterials"
              :key="material.id"
              :material="material"
              :show-visit-count="true"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Navbar from '@/components/Navbar.vue';
import MaterialCard from '@/components/MaterialCard.vue';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';

// 用户信息类型
interface UserInfo {
  id: number;
  name: string;
  avatar: string;
  email?: string;
  phonenumber?: string;
  createTime?: string;
  sex?: string;
  deptName?: string;
  roleName?: string;
}

// 访问记录类型
interface VisitedMaterial {
  id: number;
  title: string;
  description: string;
  category: string;
  image: string;
  visitCount: number;
}

// 搜索历史类型
interface SearchHistoryItem {
  term: string;
  time: string;
}

const userStore = useUserStore();
const userId = ref<number>(1);

// 加载状态
const loading = ref({
  searchHistory: false,
  visitedMaterials: false
});

// 用户数据
const user = ref<UserInfo>({
  id: 1,
  name: '用户名',
  avatar: '/images/avatar.jpg',
  email: '',
  phonenumber: '',
  createTime: '',
  sex: '',
  deptName: '',
  roleName: ''
});

// 搜索历史数据
const searchHistory = ref<SearchHistoryItem[]>([]);

// 访问记录数据
const visitedMaterials = ref<VisitedMaterial[]>([]);

// 分类映射字典
const categoryMap: { [key: string]: string } = {
  '7': '技术文档',
  '8': '设计资源',
  '9': '学术论文'
};

// 获取分类名称
const getCategoryName = (code: string): string => {
  return categoryMap[code] || '未分类';
};

// 获取搜索历史
const fetchSearchHistory = async () => {
  loading.value.searchHistory = true;
  try {
    const userInfo = userStore.getUserInfo();
    if (userInfo?.token) {
      const response = await request.get(`/system/goods/getSearchHistory/${userId.value}`);
      if (response.data.code === 200) {
        searchHistory.value = response.data.data.map((item: any) => ({
          term: item.searchContent,
          time: new Date(item.searchTime).toLocaleString()
        }));
      }
    }
  } catch (error) {
    console.error('获取搜索历史失败:', error);
    searchHistory.value = [];
  } finally {
    loading.value.searchHistory = false;
  }
};

// 获取访问记录
const fetchVisitedMaterials = async () => {
  loading.value.visitedMaterials = true;
  try {
    const userInfo = userStore.getUserInfo();
    if (userInfo?.token) {
      const { data } = await request.get(`/system/goods/getCountInfo/${userId.value}`);
      if (data.code === 200) {
        visitedMaterials.value = data.data.map((item: any) => ({
          id: Number(item.goodId),
          title: item.goodName,
          description: item.goodDesc || '暂无描述',
          category: getCategoryName(item.classification),
          image: item.cover || '/images/placeholder.jpg',
          visitCount: Number(item.count) || 0
        }));
      }
    }
  } catch (error) {
    console.error('获取访问记录失败:', error);
    visitedMaterials.value = [];
  } finally {
    loading.value.visitedMaterials = false;
  }
};

// 获取用户详细信息
const fetchUserInfo = async () => {
  try {
    const userInfo = userStore.getUserInfo();
    if (userInfo?.token) {
      const response = await request.get('/getInfo');
      if (response.data.code === 200 && response.data.user) {
        const userData = response.data.user;
        user.value = {
          ...user.value,
          id: userData.userId,
          name: userData.nickName || userData.userName,
          avatar: userData.avatar || '/images/avatar.jpg',
          email: userData.email,
          phonenumber: userData.phonenumber,
          createTime: userData.createTime,
          sex: userData.sex,
          deptName: userData.dept?.deptName,
          roleName: userData.roles?.[0]?.roleName
        };
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

// 页面加载时初始化
onMounted(() => {
  const userInfo = userStore.getUserInfo();
  if (userInfo?.id) {
    userId.value = userInfo.id;
    // 更新用户显示信息
    user.value = {
      ...user.value,
      id: userInfo.id,
      name: userInfo.nickname || userInfo.username,
      avatar: userInfo.avatar || '/images/avatar.jpg'
    };
  }

  fetchUserInfo();
  fetchSearchHistory();
  fetchVisitedMaterials();
});
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.profile-header {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 2rem;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info h1 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.user-details {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.detail-item {
  display: flex;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.detail-label {
  color: #666;
  width: 80px;
}

.detail-value {
  color: #333;
  flex: 1;
}

.section {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #f0f0f0;
}

.search-history {
  list-style: none;
  padding: 0;
  margin: 0;
}

.search-item {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-item:last-child {
  border-bottom: none;
}

.search-term {
  font-weight: bold;
  color: #333;
}

.search-time {
  color: #999;
  font-size: 0.9rem;
}

.visited-materials {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #999;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .visited-materials {
    grid-template-columns: 1fr;
  }
}
</style>