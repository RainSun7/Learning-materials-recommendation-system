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
        <MaterialCard v-for="material in materials" :key="material.goodsId" :material="{
          id: material.goodsId,
          title: material.goodsName,
          description: material.description,
          category: categories.find(c => c.categoryId === material.categoryId)?.categoryName || '未分类',
          image: material.pic?.startsWith('http') ? material.pic : baseURL + material.pic,
          visitCount: 0
        }" :showVisitCount="false" />
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>

  <!-- 客服按钮 -->
  <button class="customer-service-button" @click="toggleChatWindow">客服</button>
  <!-- 对话窗口 -->
  <div v-if="showChatWindow" class="modal-content">
    <div class="chat-header">
      AI客服为您服务
    </div>
    <div id="chat-window">
      <div v-for="(message, index) in chatMessages" :key="index" class="message-container"
        :class="{ 'user-message': message.sender === 'user', 'ai-message': message.sender === 'ai' }">
        <div class="message-bubble">
          {{ message.text }}
        </div>
        <div v-if="message.sender === 'user'" class="user-avatar">
          <!-- 用户头像 -->
          <img src="https://img.88icon.com/download/jpg/20201226/9d64c2d320c079cbd4f38f10e066dc71_512_512.jpg!bg"
            alt="User Avatar" />
        </div>
      </div>
      <p v-if="isThinking" class="thinking-message">AI正在思考...</p>
    </div>
    <input type="text" v-model="userMessage" @keyup.enter="sendMessage" placeholder="输入你的消息..." />
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


// ... AI数据 ...

interface ChatMessage {
  sender: string;
  text: string;
}

let showChatWindow = ref(false);
let userMessage = ref('');
const chatMessages = ref<ChatMessage[]>([]);
let isThinking = ref(false);


// AI函数开始

const toggleChatWindow = () => {
  console.log("数据", showChatWindow)
  showChatWindow.value = !showChatWindow.value;
};
const res = () => {
  console.log("res")
};

const sendRequestToServer =async (message: string) => {
  let AIMessageModel = { "message": message }
  console.log(message)
      await request.post('/ai/message',AIMessageModel,{timeout:100000}).then(res=>{
        console.log(res,"第指")
           if(res.status==200){
       chatMessages.value.push({ sender: 'ai', text: res.data.msg });
       isThinking.value = false;
     }
     });
  // request.post('/ai/message', AIMessageModel, { timeout: 1000000 }).then(res => {

  // })
};

const sendMessage = () => {
  console.log(userMessage)
  if (userMessage.value.trim() === '') return;
  const addMessage = userMessage.value.trim().toLowerCase();
  chatMessages.value.push({ sender: 'user', text: addMessage });
  console.log(addMessage)
  userMessage.value = '';

  isThinking.value = true;


  // 预定义的问答
  let aiResponse = '';
  switch (addMessage) {
    case '你好':
      aiResponse = '你好，请向我提问吧！';
      chatMessages.value.push({ sender: 'ai', text: aiResponse });
      isThinking.value = false;
      return;
    case '你是谁':
      aiResponse = '我是智能客服。';
      chatMessages.value.push({ sender: 'ai', text: aiResponse });
      isThinking.value = false;
      return;
    default:
      // 发送请求到服务器
      sendRequestToServer(addMessage);
      break;
  }
  // AI函数结
}

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
.customer-service-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #ff9800;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.modal-content {
  z-index: 9999;
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 350px;
  height: 400px;
  position: fixed;
  bottom: 70px;
  right: 20px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
  border-bottom: 1px solid #ccc;
  padding-bottom: 10px;
}

#chat-window {
  flex: 1;
  margin-bottom: 10px;
  border-top: 1px solid #ccc;
  padding-top: 10px;
  overflow-y: auto;
}

.message-container {
  display: flex;
  margin: 5px 0;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.user-avatar {
  margin-right: 10px;
}

.user-avatar img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.message-bubble {
  max-width: 70%;
  padding: 10px;
  border-radius: 10px;
  background-color: #f1f0f0;
  position: relative;
}

.user-message .message-bubble {
  background-color: #e1ffc7;
}

.message-bubble strong {
  font-weight: bold;
  color: #333;
}

.thinking-message {
  text-align: center;
  margin-top: 10px;
}

.modal-content input[type="text"] {
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
}


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
  0% {
    background-position: 0 0;
  }

  100% {
    background-position: 100% 100%;
  }
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
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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