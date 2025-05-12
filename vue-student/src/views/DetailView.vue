<template>
  <div class="detail">
    <Navbar />
    <div class="main-content">
      <!-- 测试用户ID设置区域 -->
      <!-- <div class="test-user-panel">
        <span>当前用户ID: {{ currentUserId }}</span>
        <div class="user-id-input">
          <el-input v-model="inputUserId" placeholder="输入测试用户ID" style="width: 200px;"></el-input>
          <el-button type="primary" @click="setUserId">设置</el-button>
        </div>
      </div> -->

      <div class="material-detail" v-loading="loading">
        <div class="material-header">
          <h1 class="material-title">{{ material.goodsName }}</h1>
<!--          <div class="material-meta">-->
<!--            <span class="material-category">-->
<!--              {{ categories.find(c => c.categoryId === material.categoryId)?.categoryName || '未分类' }}-->
<!--            </span>-->
<!--          </div>-->
        </div>
        
        <img 
          v-if="material.pic" 
          :src="formatImageUrl(material.pic)" 
          class="material-cover" 
          alt="资料封面"
        >

        <p class="material-description">{{ material.description }}</p>

        <div class="action-buttons">
          <button 
            class="action-button like-button" 
            @click="handleLike"
            :disabled="isProcessing"
          >
            {{ isProcessing ? '处理中...' : '点赞' }}
          </button>
          <button 
            class="action-button dislike-button" 
            @click="handleDislike"
            :disabled="isProcessing"
          >
            {{ isProcessing ? '处理中...' : '点踩' }}
          </button>
          <a 
            v-if="material.otherPic" 
            :href="material.otherPic" 
            target="_blank" 
            class="action-button download-button"
            download
            @click.prevent="handleDownload"
          >
            下载资料
          </a>
        </div>
      </div>

      <div class="recommended-section" v-if="relatedMaterials.length > 0">
        <h2 class="section-title">相关推荐</h2>
        <div class="materials-grid">
          <MaterialCard 
            v-for="relatedMaterial in relatedMaterials" 
            :key="relatedMaterial.goodsId" 
            :material="{
              id: relatedMaterial.goodsId,
              title: relatedMaterial.goodsName,
              description: relatedMaterial.description || '暂无描述',
              category: categories.find(c => c.categoryId === relatedMaterial.categoryId)?.categoryName || '未分类',
              image: formatImageUrl(relatedMaterial.pic)
            }"
          />
        </div>
      </div>

      <!-- 个性化推荐 -->
      <div class="recommended-section">
        <div class="section-header">
          <h2 class="section-title">个性化推荐</h2>
          <el-button type="primary" @click="fetchPersonalRecommendations" :loading="loadingRecommendations">
            刷新推荐
          </el-button>
        </div>
        <div v-if="loadingRecommendations" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="personalRecommendations.length > 0" class="materials-grid">
          <MaterialCard 
            v-for="recommendation in personalRecommendations" 
            :key="recommendation.goodsId" 
            :material="{
              id: recommendation.goodsId,
              title: recommendation.goodsName,
              description: recommendation.description || '暂无描述',
              category: categories.find(c => c.categoryId === recommendation.categoryId)?.categoryName || '未分类',
              image: formatImageUrl(recommendation.pic)
            }"
          />
        </div>
        <div v-else class="empty-recommendations">
          <el-empty description="暂无个性化推荐" />
          <p class="recommendation-tip">点赞一些资料，获取更多推荐</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import Navbar from '@/components/Navbar.vue';
import MaterialCard from '@/components/MaterialCard.vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';

const route = useRoute();
const materialId = parseInt(route.params.id as string);
const baseURL = 'http://localhost:8081';

// 从Pinia获取用户信息
const userStore = useUserStore();
const userInfo = computed(() => userStore.getUserInfo());
const userId = computed(() => userInfo.value?.userId || '1'); // 默认使用1，如果没有登录

// 以下代码仅用于测试，生产环境应移除
// 当前用户ID，从localStorage获取，如果没有则使用默认值
const currentUserId = ref(localStorage.getItem('userId') || '10001');
const inputUserId = ref(currentUserId.value);

// 设置用户ID
const setUserId = () => {
  if (inputUserId.value && inputUserId.value.trim()) {
    currentUserId.value = inputUserId.value.trim();
    localStorage.setItem('userId', currentUserId.value);
    ElMessage.success(`用户ID已设置为: ${currentUserId.value}`);
  } else {
    ElMessage.warning('请输入有效的用户ID');
  }
};

interface Material {
  goodsId: number;
  goodsName: string;
  description: string;
  pic: string;
  otherPic: string | null;
  categoryId: number;
  createTime: string;
  updateTime: string;
  price: number;
  stock: number;
}

interface Category {
  categoryId: number;
  categoryName: string;
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data: T;
  total?: number;
  rows?: T;
}

const material = ref<Material>({
  goodsId: 0,
  goodsName: '',
  description: '',
  pic: '',
  otherPic: null,
  categoryId: 0,
  createTime: '',
  updateTime: '',
  price: 0,
  stock: 0
});

const categories = ref<Category[]>([]);
const relatedMaterials = ref<Material[]>([]);
const personalRecommendations = ref<Material[]>([]);
const isProcessing = ref(false);
const loadingRecommendations = ref(false);
const loading = ref(false); // 资料详情加载状态

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get<any, ApiResponse<Category[]>>('/system/category/withBookCount');
    if (response.code === 200) {
      categories.value = response.data;
    }
  } catch (error) {
    console.error('获取分类列表失败：', error);
  }
};

// 获取资料详情
const fetchMaterialDetail = async () => {
  loading.value = true;
  try {
    const response = await request.get<any, any>(`/system/goods/${materialId}`);
    console.log('资料详情原始响应:', response);
    
    // 处理多种可能的响应格式
    if (response) {
      let materialData = null;
      
      // 情况1: 标准ApiResponse格式 {code, msg, data}
      if (response.code === 200 && response.data) {
        materialData = response.data;
      } 
      // 情况2: axios包装的响应 {data: {code, msg, data}}
      else if (response.data && response.data.code === 200) {
        materialData = response.data.data;
      }
      
      if (materialData) {
        material.value = materialData;
        console.log('获取到资料详情:', material.value);
        // 获取相关推荐
        fetchRelatedMaterials();
      } else {
        console.error('无法解析资料详情数据:', response);
        ElMessage.error('获取资料详情失败: 数据格式异常');
      }
    } else {
      ElMessage.error('获取资料详情失败: 响应为空');
    }
  } catch (error: any) {
    console.error('获取资料详情失败：', error);
    // 提供更详细的错误信息
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误响应数据:', error.response.data);
    }
    ElMessage.error(`获取资料详情失败: ${error.message || '未知错误'}`);
  } finally {
    loading.value = false;
  }
};

// 获取相关推荐
const fetchRelatedMaterials = async () => {
  try {
    const response = await request.get<any, ApiResponse<Material[]>>('/system/goods/list', {
      params: {
        pageNum: 1,
        pageSize: 3,
        categoryId: material.value.categoryId,
        excludeId: material.value.goodsId
      }
    });
    if (response.code === 200) {
      relatedMaterials.value = response.rows || [];
    }
  } catch (error) {
    console.error('获取相关推荐失败：', error);
  }
};

// 获取个性化推荐
const fetchPersonalRecommendations = async () => {
  loadingRecommendations.value = true;
  personalRecommendations.value = []; // 清空现有推荐
  
  try {
    console.log('正在获取个性化推荐，用户ID:', userId.value);
    const response = await request.get<any, any>(`/system/goods/recommend`, {
      params: {
        userId: userId.value, // 使用Pinia中的userId
        page: 0,
        size: 6
      }
    });
    
    console.log('个性化推荐响应:', response);
    
    // 处理各种可能的响应格式
    if (response) {
      // 情况1: 标准TableDataInfo格式 {total, rows, code, msg}
      if (response.rows && Array.isArray(response.rows)) {
        personalRecommendations.value = response.rows;
      } 
      // 情况2: 嵌套在response.data里的数组 {code, msg, data: [...]}
      else if (response.data && Array.isArray(response.data)) {
        personalRecommendations.value = response.data;
      }
      // 情况3: axios包装的响应 {data: {code, msg, rows/data}}
      else if (response.data) {
        if (response.data.rows && Array.isArray(response.data.rows)) {
          personalRecommendations.value = response.data.rows;
        } else if (response.data.data && Array.isArray(response.data.data)) {
          personalRecommendations.value = response.data.data;
        } else {
          console.error('无法识别的数据格式', response);
          ElMessage.warning('数据格式异常，请联系管理员');
        }
      } else {
        console.error('获取个性化推荐失败：响应格式不正确', response);
        ElMessage.warning('获取个性化推荐失败，请稍后再试');
      }
      
      console.log('处理后的个性化推荐数据:', personalRecommendations.value);
      
      if (personalRecommendations.value.length === 0) {
        ElMessage.info('暂无个性化推荐，请先点赞一些资料');
      }
    }
  } catch (error) {
    console.error('获取个性化推荐失败：', error);
    ElMessage.error('获取个性化推荐失败，请稍后再试');
  } finally {
    loadingRecommendations.value = false;
  }
};

// 处理用户行为后刷新推荐
const handleUserAction = async (actionType: 'LIKE' | 'DISLIKE') => {
  if (isProcessing.value) return;
  
  isProcessing.value = true;
  try {
    const postData = {
      materialId: material.value.goodsId,
      actionType: actionType
    };
    
    console.log('发送用户行为请求:', {
      userId: userId.value, // 使用Pinia中的userId
      ...postData
    });
    
    // 尝试使用fetch API直接发送请求
    try {
      const response = await fetch(`${baseURL}/system/goods/action?userId=${encodeURIComponent(userId.value)}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(postData)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP错误! 状态: ${response.status}`);
      }
      
      const data = await response.text();
      console.log('用户行为响应:', data || '操作成功');
      ElMessage.success(actionType === 'LIKE' ? '点赞成功' : '点踩成功');
      
      // 更新推荐列表
      if (actionType === 'DISLIKE') {
        fetchRelatedMaterials();
      }
      
      // 点赞后刷新个性化推荐
      fetchPersonalRecommendations();
    } catch (fetchError: any) {
      console.error(`Fetch错误: ${fetchError.message}`);
      
      // 如果fetch失败，尝试使用axios作为备用
      console.log('尝试使用axios作为备用方法...');
      const response = await request.post(
        `/system/goods/action?userId=${encodeURIComponent(userId.value)}`, 
        postData
      );
      
      console.log('用户行为响应 (axios):', response);
      ElMessage.success(actionType === 'LIKE' ? '点赞成功' : '点踩成功');
      
      // 更新推荐列表
      if (actionType === 'DISLIKE') {
        fetchRelatedMaterials();
      }
      
      // 点赞后刷新个性化推荐
      fetchPersonalRecommendations();
    }
  } catch (error: any) {
    console.error(`${actionType === 'LIKE' ? '点赞' : '点踩'}失败：`, error);
    
    // 更详细的错误信息
    if (error.response) {
      console.error('错误响应状态:', error.response.status);
      console.error('错误响应数据:', error.response.data);
    }
    
    // 如果是401错误，提示可能是权限问题
    if (error.response && error.response.status === 401) {
      ElMessage.error('权限不足，请先登录或联系管理员');
    } else {
      ElMessage.error(`${actionType === 'LIKE' ? '点赞' : '点踩'}失败: ${error.message || '未知错误'}`);
    }
  } finally {
    isProcessing.value = false;
  }
};

// 处理点赞
const handleLike = () => {
  handleUserAction('LIKE');
};

// 处理点踩
const handleDislike = () => {
  handleUserAction('DISLIKE');
};

// 处理下载
const handleDownload = () => {
  if (material.value.otherPic) {
    // 创建一个隐藏的 a 标签
    const link = document.createElement('a');
    link.href = material.value.otherPic;
    // 从 URL 中提取文件名
    const fileName = material.value.otherPic.split('/').pop() || `${material.value.goodsName}`;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
};

// 格式化图片URL
const formatImageUrl = (url: string) => {
  if (!url) return '';
  return url.startsWith('http') ? url : baseURL + url;
};

onMounted(() => {
  fetchCategories();
  fetchMaterialDetail();
  fetchPersonalRecommendations();
});
</script>

<style scoped>
.detail {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

/* 添加测试面板样式 */
.test-user-panel {
  background: #fff;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-id-input {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.material-detail {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.material-header {
  margin-bottom: 2rem;
}

.material-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
}

.material-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.material-category {
  display: inline-block;
  padding: 0.3rem 0.8rem;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
}

.material-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.material-cover {
  width: 300px;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin: 2rem 0;
  display: block;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  margin: 2rem 0;
}

.action-button {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 1rem;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.like-button {
  background: #52c41a;
  color: white;
}

.dislike-button {
  background: #ff4d4f;
  color: white;
}

.download-button {
  background: #1890ff;
  color: white;
}

.action-button:hover {
  opacity: 0.8;
}

.recommended-section {
  margin-top: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #333;
}

.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.loading-container {
  min-height: 200px;
  padding: 20px;
}

.empty-recommendations {
  padding: 30px;
  text-align: center;
}

.recommendation-tip {
  margin-top: 15px;
  color: #909399;
  font-size: 14px;
}
</style> 