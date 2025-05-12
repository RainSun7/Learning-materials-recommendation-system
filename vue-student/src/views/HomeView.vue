<template>
  <div class="home">
    <Navbar />
    <div class="banner">
      <div class="banner-content">
        <h1 class="banner-title">发现优质学习资料</h1>
        <p class="banner-description">汇聚全网优质学习资源，助力你的学习成长</p>
      </div>
    </div>
    <div class="main-content">
      <div class="filters">
        <div class="section-title">全部资料</div>
      </div>
      <div class="materials-grid">
        <MaterialCard 
          v-for="material in materials" 
          :key="material.goodsId" 
          :material="{
            id: material.goodsId,
            title: material.goodsName,
            description: material.description,
            category: categories.find(c => c.categoryId === material.categoryId)?.categoryName || '未分类',
            image: material.pic?.startsWith('http') ? material.pic : baseURL + material.pic,
            visitCount: 0
          }"
          :showVisitCount="false"
        />
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Navbar from '@/components/Navbar.vue';
import MaterialCard from '@/components/MaterialCard.vue';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

interface Category {
  categoryId: number;
  categoryName: string;
}

interface Material {
  goodsId: number;
  goodsName: string;
  description: string;
  pic: string;
  categoryId: number;
  createTime: string;
  updateTime: string;
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data?: T;
  total?: number;
  rows?: T[];
}

const baseURL = 'http://localhost:8081';
const categories = ref<Category[]>([]);
const materials = ref<Material[]>([]);
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get('/system/category/withBookCount');
    const res = response.data as ApiResponse<Category[]>;
    if (res.code === 200) {
      categories.value = res.data || [];
    }
  } catch (error) {
    console.error('获取分类列表失败：', error);
  }
};

// 获取资料列表
const fetchMaterials = async () => {
  try {
    const response = await request.get('/system/goods/list', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    });
    
    const res = response.data as ApiResponse<Material>;
    if (res.code === 200 && Array.isArray(res.rows)) {
      materials.value = res.rows;
      total.value = res.total || 0;
    } else {
      ElMessage.error(res.msg || '获取资料列表失败');
    }
  } catch (error) {
    console.error('获取资料列表失败：', error);
  }
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  fetchMaterials();
};

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchMaterials();
};

onMounted(() => {
  fetchCategories();
  fetchMaterials();
});
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.banner {
  height: 500px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  overflow: hidden;
}

.banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('/images/pattern.png') repeat;
  opacity: 0.1;
  animation: movePattern 20s linear infinite;
}

@keyframes movePattern {
  0% { background-position: 0 0; }
  100% { background-position: 100% 100%; }
}

.banner-content {
  max-width: 800px;
  padding: 0 1rem;
  position: relative;
  z-index: 1;
}

.banner-title {
  font-size: 3rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.banner-description {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title {
  font-size: 1.8rem;
  color: #333;
  font-weight: 600;
}

.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-bottom: 3rem;
}

@media (max-width: 768px) {
  .banner {
    height: 400px;
  }
  
  .banner-title {
    font-size: 2rem;
  }
  
  .filters {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style> 