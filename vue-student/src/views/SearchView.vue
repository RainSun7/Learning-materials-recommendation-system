<template>
  <div class="search">
    <Navbar />
    <div class="main-content">
      <div class="search-section">
        <div class="search-bar">
          <input 
            type="text" 
            class="search-input" 
            placeholder="输入关键词搜索..."
            v-model="searchQuery"
            @keyup.enter="performSearch"
          >
          <button class="search-button" @click="performSearch">搜索</button>
        </div>

        <div class="filters">
          <div class="filter-group">
            <span class="filter-label">分类：</span>
            <select class="filter-select" v-model="selectedCategory">
              <option value="">全部</option>
              <option value="tech">技术文档</option>
              <option value="design">设计资源</option>
              <option value="business">商业资料</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">排序：</span>
            <select class="filter-select" v-model="sortBy">
              <option value="latest">最新</option>
              <option value="popular">最热</option>
              <option value="rating">评分</option>
            </select>
          </div>
        </div>
      </div>

      <div class="results-count">找到 {{ filteredMaterials.length }} 个结果</div>

      <div class="materials-grid">
        <MaterialCard 
          v-for="material in filteredMaterials" 
          :key="material.id" 
          :material="material"
        />
      </div>

      <div class="pagination">
        <button class="page-button" @click="prevPage" :disabled="currentPage === 1">上一页</button>
        <button 
          v-for="page in totalPages" 
          :key="page"
          class="page-button"
          :class="{ active: page === currentPage }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        <button class="page-button" @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import Navbar from '@/components/Navbar.vue';
import MaterialCard from '@/components/MaterialCard.vue';

const route = useRoute();
const searchQuery = ref(route.query.q as string || '');
const selectedCategory = ref('');
const sortBy = ref('latest');
const currentPage = ref(1);
const itemsPerPage = 9;

// 模拟数据
const materials = ref([
  {
    id: 1,
    title: 'Python编程入门指南',
    description: '这是一份详细的Python编程入门教程...',
    category: 'tech',
    image: '/images/placeholder.jpg',
    visitCount: 100
  },
  // 更多资料数据...
]);

const filteredMaterials = computed(() => {
  let result = materials.value.filter(material => {
    const matchesSearch = material.title.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                         material.description.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesCategory = !selectedCategory.value || material.category === selectedCategory.value;
    return matchesSearch && matchesCategory;
  });

  if (sortBy.value === 'latest') {
    result.sort((a, b) => b.id - a.id);
  } else if (sortBy.value === 'popular') {
    result.sort((a, b) => b.visitCount - a.visitCount);
  }

  return result;
});

const totalPages = computed(() => Math.ceil(filteredMaterials.value.length / itemsPerPage));

const performSearch = () => {
  currentPage.value = 1;
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const goToPage = (page: number) => {
  currentPage.value = page;
};

// 监听路由参数变化
watch(() => route.query.q, (newQuery) => {
  if (newQuery) {
    searchQuery.value = newQuery as string;
    performSearch();
  }
});
</script>

<style scoped>
.search {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.search-section {
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.search-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-input {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-button {
  padding: 0.8rem 1.5rem;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.search-button:hover {
  background: #40a9ff;
}

.filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-label {
  color: #666;
}

.filter-select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
}

.results-count {
  color: #666;
  margin: 1rem 0;
}

.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.page-button:hover {
  background: #f0f0f0;
}

.page-button.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 