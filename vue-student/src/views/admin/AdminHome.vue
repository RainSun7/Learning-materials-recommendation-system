<template>
  <div class="admin-home">
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon users">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">1</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon materials">
          <i class="fas fa-book"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">3</div>
          <div class="stat-label">资料总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon categories">
          <i class="fas fa-folder"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">4</div>
          <div class="stat-label">分类总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon visits">
          <i class="fas fa-eye"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">10</div>
          <div class="stat-label">今日访问量</div>
        </div>
      </div>
    </div>

    <div class="data-section">
      <div class="section-header">
        <h3>最近活动</h3>
      </div>
      <div class="activity-list">
        <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
          <div class="activity-icon">
            <i :class="activity.icon"></i>
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const recentActivities = ref([
  {
    icon: 'fas fa-user-plus',
    title: '用户登录',
  },
  {
    icon: 'fas fa-upload',
    title: '新增学习资料',
  },
  {
    icon: 'fas fa-edit',
    title: '更新分类',
  }
]);
</script>

<style scoped>
.admin-home {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.users { background-color: #1890ff; }
.materials { background-color: #52c41a; }
.categories { background-color: #722ed1; }
.visits { background-color: #faad14; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.data-section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  color: #333;
  font-size: 1.2rem;
  margin: 0;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.activity-item:hover {
  background-color: #f5f5f5;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #e6f7ff;
  color: #1890ff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-content {
  flex: 1;
}

.activity-title {
  color: #333;
  margin-bottom: 0.25rem;
}

.activity-time {
  color: #999;
  font-size: 0.9rem;
}
</style> 