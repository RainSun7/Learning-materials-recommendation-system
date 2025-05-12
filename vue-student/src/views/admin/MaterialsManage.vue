<template>
  <div class="materials-manage">
    <div class="page-header">
      <h2>学习资料管理</h2>
      <button class="add-btn" @click="handleAdd">新增资料</button>
    </div>

    <div class="search-section">
      <div class="search-form">
        <input 
          type="text" 
          class="search-input" 
          v-model="searchQuery"
          placeholder="搜索资料名称..."
        >
        <select v-model="selectedCategory" class="category-select">
          <option value="">全部分类</option>
          <option 
            v-for="category in categories" 
            :key="category.categoryId" 
            :value="category.categoryId"
          >
            {{ category.categoryName }}
          </option>
        </select>
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="handleReset">重置</button>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>资料编号</th>
            <th>资料名称</th>
            <th>资料封面</th>
            <th>分类</th>
            <th>资料描述</th>
            <th>下载链接</th>
            <th>创建时间</th>
            <th>更新时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in materials" :key="item.goodsId">
            <td>{{ item.goodsId }}</td>
            <td>{{ item.goodsName }}</td>
            <td>
              <img 
                v-if="item.pic"
                :src="item.pic" 
                class="material-cover"
                @click="previewImage(item.pic)"
                alt="资料封面"
              >
              <span v-else>暂无封面</span>
            </td>
            <td>
              <span class="category-tag">
                {{ findCategoryName(item.categoryId) }}
              </span>
            </td>
            <td class="description-cell">{{ item.description }}</td>
            <td class="download-cell">
              <template v-if="item.otherPic">
                <a 
                  v-for="(link, index) in item.otherPic.split(',')" 
                  :key="index"
                  :href="link" 
                  target="_blank" 
                  class="download-link"
                >
                  下载资料{{ item.otherPic.split(',').length > 1 ? `(${index + 1})` : '' }}
                </a>
              </template>
              <span v-else>暂无下载链接</span>
            </td>
            <td>{{ item.createTime }}</td>
            <td>{{ item.updateTime }}</td>
            <td>
              <div class="actions">
                <button class="edit-btn" @click="handleEdit(item)">编辑</button>
                <button class="delete-btn" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1" 
        @click="handlePrevPage"
      >上一页</button>
      <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button 
        class="page-btn" 
        :disabled="currentPage === totalPages" 
        @click="handleNextPage"
      >下一页</button>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      class="material-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="资料名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入资料名称" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资料描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资料描述"
          />
        </el-form-item>
        <el-form-item label="资料封面" prop="pic">
          <el-upload
            class="avatar-uploader"
            :action="`${baseURL}/common/upload`"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handlePicSuccess"
            :before-upload="beforeUpload"
            accept="image/jpeg,image/png"
          >
            <img v-if="form.pic" :src="form.pic" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持 jpg/png 格式，大小不超过 2MB</div>
        </el-form-item>
        <el-form-item label="资料文件" prop="otherPic">
          <el-upload
            class="file-uploader"
            :action="`${baseURL}/common/upload`"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleFileSuccess"
            :before-upload="beforeUpload"
          >
            <el-button type="primary" :icon="Plus">
              {{ form.otherPic ? '重新上传' : '上传资料' }}
            </el-button>
            <template #tip>
              <div class="upload-tip">文件大小不超过 20MB</div>
            </template>
          </el-upload>
          <div v-if="form.otherPic" class="file-info">
            <el-icon><Document /></el-icon>
            <span class="file-name">已上传资料文件</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="[previewUrl]"
      @close="showViewer = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Document } from '@element-plus/icons-vue';
import axios from 'axios';

interface MaterialData {
  goodsId: number;
  goodsName: string;
  description: string;
  pic: string;
  otherPic: string | null;
  categoryId: number;
  price: number;
  stock: number;
  craftsmanName: string | null;
  createTime: string;
  updateTime: string;
  remark: string | null;
}

interface Category {
  categoryId: number;
  categoryName: string;
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data?: T;
  rows?: T[];
  total?: number;
  url?: string;
}

// 创建独立的axios实例
const materialAxios = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000
});

// 请求拦截器
materialAxios.interceptors.request.use(
  config => {
    // 获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器 - 确保返回的数据类型正确
materialAxios.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    let message = '请求失败';
    if (error.response && error.response.status === 401) {
      message = '登录状态已过期，请重新登录';
      // 可以在这里处理登出逻辑
      localStorage.removeItem('token');
    }
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

// 类型化的API请求方法
const api = {
  get: async <T>(url: string, config?: any): Promise<ApiResponse<T>> => {
    try {
      return await materialAxios.get(url, config);
    } catch (error) {
      console.error('GET请求错误:', error);
      throw error;
    }
  },
  post: async <T>(url: string, data?: any, config?: any): Promise<ApiResponse<T>> => {
    try {
      return await materialAxios.post(url, data, config);
    } catch (error) {
      console.error('POST请求错误:', error);
      throw error;
    }
  },
  put: async <T>(url: string, data?: any, config?: any): Promise<ApiResponse<T>> => {
    try {
      return await materialAxios.put(url, data, config);
    } catch (error) {
      console.error('PUT请求错误:', error);
      throw error;
    }
  },
  delete: async <T>(url: string, config?: any): Promise<ApiResponse<T>> => {
    try {
      return await materialAxios.delete(url, config);
    } catch (error) {
      console.error('DELETE请求错误:', error);
      throw error;
    }
  }
};

const baseURL = 'http://localhost:8081';
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
};

const searchQuery = ref('');
const selectedCategory = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(1);
const materials = ref<MaterialData[]>([]);
const categories = ref<Category[]>([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();
const showViewer = ref(false);
const previewUrl = ref('');

const form = reactive({
  goodsId: null as number | null,
  goodsName: '',
  description: '',
  pic: '',
  otherPic: '',
  categoryId: undefined as number | undefined,
  price: '1', // 默认值
  stock: '1' // 默认值
});

const rules = {
  goodsName: [
    { required: true, message: '请输入资料名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入资料描述', trigger: 'blur' }
  ],
  pic: [
    { required: true, message: '请上传资料封面', trigger: 'change' }
  ]
};

// 获取分类列表 - 独立处理
const fetchCategories = async () => {
  try {
    const response = await api.get<Category[]>('/system/category/withBookCount');
    if (response.code === 200) {
      categories.value = Array.isArray(response.data) ? response.data : [];
    } else {
      categories.value = [];
      ElMessage.error(response.msg || '获取分类列表失败');
    }
  } catch (error) {
    categories.value = [];
    console.error('获取分类列表失败：', error);
  }
};

// 获取资料列表 - 独立处理
const fetchMaterials = async () => {
  try {
    const response = await api.get<MaterialData>('/system/goods/list', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        goodsName: searchQuery.value || undefined,
        categoryId: selectedCategory.value || undefined
      }
    });
    
    if (response.code === 200) {
      materials.value = Array.isArray(response.rows) ? response.rows : [];
      total.value = response.total || 0;
      totalPages.value = Math.ceil(total.value / pageSize.value) || 1;
    } else {
      materials.value = [];
      total.value = 0;
      totalPages.value = 1;
      ElMessage.error(response.msg || '获取资料列表失败');
    }
  } catch (error) {
    materials.value = [];
    total.value = 0;
    totalPages.value = 1;
    console.error('获取资料列表失败：', error);
    ElMessage.error('获取资料列表失败');
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchMaterials();
};

// 处理重置
const handleReset = () => {
  searchQuery.value = '';
  selectedCategory.value = '';
  currentPage.value = 1;
  fetchMaterials();
};

// 处理新增
const handleAdd = () => {
  dialogTitle.value = '新增资料';
  form.goodsId = null;
  form.goodsName = '';
  form.description = '';
  form.pic = '';
  form.otherPic = '';
  form.categoryId = undefined;
  dialogVisible.value = true;
};

// 处理编辑
const handleEdit = (material: MaterialData) => {
  dialogTitle.value = '修改资料';
  // 处理图片路径
  const editData = {
    ...material,
    pic: material.pic,
    otherPic: material.otherPic
  };
  Object.assign(form, editData);
  dialogVisible.value = true;
};

// 处理删除
const handleDelete = async (material: MaterialData) => {
  try {
    await ElMessageBox.confirm('确认要删除该资料吗？', '提示', {
      type: 'warning'
    });
    const response = await api.delete<any>(`/system/goods/${material.goodsId}`);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      fetchMaterials();
    } else {
      ElMessage.error(response.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除失败：', error);
  }
};

// 上传前校验
const beforeUpload = (file: File) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (isImage && !isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!');
    return false;
  }

  if (!isImage) {
    const isLt20M = file.size / 1024 / 1024 < 20;
    if (!isLt20M) {
      ElMessage.error('上传文件大小不能超过 20MB!');
      return false;
    }
  }
  return true;
};

// 处理封面上传成功
const handlePicSuccess = (response: ApiResponse<any>) => {
  if (response.code === 200 && response.url) {
    form.pic = response.url;
  } else {
    ElMessage.error(response.msg || '上传失败');
  }
};

// 处理资料文件上传成功
const handleFileSuccess = (response: ApiResponse<any>) => {
  if (response.code === 200 && response.url) {
    form.otherPic = response.url;
  } else {
    ElMessage.error(response.msg || '上传失败');
  }
};

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    const submitData = {
      ...form,
      pic: form.pic,
      otherPic: form.otherPic || null
    };

    let response;
    if (form.goodsId) {
      // 修改
      response = await api.put<any>('/system/goods', submitData);
    } else {
      // 新增
      response = await api.post<any>('/system/goods', submitData);
    }

    if (response.code === 200) {
      ElMessage.success(form.goodsId ? '修改成功' : '新增成功');
      dialogVisible.value = false;
      fetchMaterials();
    } else {
      ElMessage.error(response.msg || '提交失败');
    }
  } catch (error) {
    console.error('提交失败：', error);
    ElMessage.error('提交失败');
  }
};

// 处理上一页
const handlePrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    fetchMaterials();
  }
};

// 处理下一页
const handleNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    fetchMaterials();
  }
};

// 预览图片
const previewImage = (url: string) => {
  previewUrl.value = url;
  showViewer.value = true;
};

// 安全查找分类名称
const findCategoryName = (categoryId: number) => {
  if (!Array.isArray(categories.value)) {
    return '未分类';
  }
  const category = categories.value.find(c => c.categoryId === categoryId);
  return category ? category.categoryName : '未分类';
};

onMounted(() => {
  fetchCategories();
  fetchMaterials();
});
</script>

<style scoped>
.materials-manage {
  background: white;
  border-radius: 4px;
  padding: 1.5rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.page-header h2 {
  font-size: 1.5rem;
  color: #333;
}

.search-section {
  margin-bottom: 1.5rem;
}

.search-form {
  display: flex;
  gap: 1rem;
}

.search-input,
.category-select {
  padding: 0.5rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-input {
  flex: 1;
}

.category-select {
  width: 200px;
}

.search-btn,
.reset-btn,
.add-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn {
  background: #1890ff;
  color: white;
}

.search-btn:hover {
  background: #40a9ff;
}

.reset-btn {
  background: #f4f4f5;
  color: #606266;
}

.reset-btn:hover {
  background: #e9e9eb;
}

.add-btn {
  background: #52c41a;
  color: white;
}

.add-btn:hover {
  background: #73d13d;
}

.table-container {
  margin-bottom: 1rem;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-weight: 500;
}

.description-cell {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.material-cover {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.material-cover:hover {
  transform: scale(1.1);
}

.category-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.edit-btn,
.delete-btn {
  padding: 0.3rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-btn {
  background: #1890ff;
  color: white;
}

.edit-btn:hover {
  background: #40a9ff;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

.delete-btn:hover {
  background: #ff7875;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  color: #1890ff;
  border-color: #1890ff;
}

.page-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.page-info {
  color: #666;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
}

.material-dialog :deep(.el-dialog__body) {
  padding-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.download-cell {
  max-width: 200px;
}

.download-link {
  color: #1890ff;
  text-decoration: none;
  transition: color 0.3s;
}

.download-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.file-uploader {
  display: flex;
  flex-direction: column;
}

.file-info {
  display: flex;
  align-items: center;
  margin-top: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.file-info .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.file-name {
  color: #606266;
  font-size: 14px;
}
</style> 