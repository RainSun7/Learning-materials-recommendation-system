<template>
  <div class="material-card">
    <router-link :to="'/detail/' + material.id">
      <img :src="material.image" :alt="material.title" class="material-image">
      <div class="material-info">
        <span class="material-category">{{ material.category }}</span>
        <h3 class="material-title">{{ material.title }}</h3>
        <p class="material-description">{{ material.description }}</p>
        <div v-if="showVisitCount" class="visit-count">访问次数: {{ material.visitCount }}</div>
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
interface Material {
  id: number;
  title: string;
  description: string;
  category: string;
  image: string;
  visitCount?: number;
}

defineProps<{
  material: Material;
  showVisitCount?: boolean;
}>();
</script>

<style scoped>
.material-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.material-card:hover {
  transform: translateY(-5px);
}

.material-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.material-info {
  padding: 1rem;
}

.material-title {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.material-category {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.8rem;
  margin-bottom: 0.5rem;
}

.material-description {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.visit-count {
  color: #999;
  font-size: 0.9rem;
}
</style> 