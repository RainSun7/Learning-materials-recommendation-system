<template>
  <div class="category-manage">
    <div class="search-bar">
      <div class="search-input">
        <span>分类名称</span>
        <el-input v-model="searchQuery" placeholder="请输入分类名称" />
      </div>
      <div class="search-buttons">
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </div>
    </div>

    <div class="toolbar">
      <div class="left-buttons">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
        <el-button type="info" @click="selectedRow && handleEdit(selectedRow)" :disabled="!selectedRow">
          <el-icon><Edit /></el-icon>修改
        </el-button>
        <el-button type="danger" @click="selectedRow && handleDelete(selectedRow)" :disabled="!selectedRow">
          <el-icon><Delete /></el-icon>删除
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>导出
        </el-button>
      </div>
    </div>

    <el-table
      :data="filteredTableData"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      v-loading="loading"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="categoryId" label="分类ID" width="180" />
      <!-- <el-table-column prop="orderNum" label="显示顺序" width="180" /> -->
      <el-table-column prop="categoryName" label="分类名称" />
      <el-table-column prop="categoryDescription" label="分类描述" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button
            type="text"
            size="small"
            @click="handleEdit(scope.row)"
          >修改</el-button>
          <el-button
            type="text"
            size="small"
            style="color: #ff4d4f"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 40]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <!-- <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="1" />
        </el-form-item> -->
        <el-form-item label="分类描述" prop="categoryDescription">
          <el-input
            v-model="form.categoryDescription"
            type="textarea"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="分类图标" prop="categoryIcon">
          <el-input v-model="form.categoryIcon" placeholder="请输入图标URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import request from '@/utils/request';

interface CategoryData {
  categoryId: number;
  categoryName: string;
  categoryDescription: string;
  categoryIcon: string;
  orderNum: number;
  bookCount?: number;
  createTime?: string;
  updateTime?: string;
}

const loading = ref(false);
const searchQuery = ref('');
const tableData = ref<CategoryData[]>([]);
const selectedRow = ref<CategoryData | null>(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();

const form = reactive({
  categoryId: null as number | null,
  categoryName: '',
  categoryDescription: '',
  categoryIcon: '',
  orderNum: 1
});

const rules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
};

// 过滤和分页后的数据
const filteredTableData = computed(() => {
  // 先过滤
  const filtered = searchQuery.value 
    ? tableData.value.filter(item => item.categoryName.includes(searchQuery.value))
    : tableData.value;
  
  // 计算总数
  total.value = filtered.length;
  
  // 然后分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  
  return filtered.slice(startIndex, endIndex);
});

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true;
  try {
    const response = await request.get('/system/category/withBookCount');
    // 正确处理响应格式: {msg: '操作成功', code: 200, data: Array(1)}
    if (response && response.data) {
      if (Array.isArray(response.data)) {
        // 直接是数组的情况
        tableData.value = response.data;
        total.value = response.data.length;
      } else if (response.data.data && Array.isArray(response.data.data)) {
        // 嵌套在data字段中的数组
        tableData.value = response.data.data;
        total.value = response.data.data.length;
      } else {
        console.error('无法解析的数据格式:', response.data);
        tableData.value = [];
        total.value = 0;
      }
    } else {
      tableData.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取分类列表失败：', error);
    tableData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  // 不需要重新请求，使用过滤
};

// 处理重置
const handleReset = () => {
  searchQuery.value = '';
  currentPage.value = 1;
  fetchCategories();
};

// 处理新增
const handleAdd = () => {
  dialogTitle.value = '新增分类';
  form.categoryId = null;
  form.categoryName = '';
  form.categoryDescription = '';
  form.categoryIcon = '';
  form.orderNum = 1;
  dialogVisible.value = true;
};

// 处理编辑
const handleEdit = (row: CategoryData) => {
  dialogTitle.value = '修改分类';
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 处理删除
const handleDelete = async (row: CategoryData) => {
  try {
    await ElMessageBox.confirm('确认要删除该分类吗？', '提示', {
      type: 'warning'
    });
    await request.delete(`/system/category/${row.categoryId}`);
    ElMessage.success('删除成功');
    fetchCategories();
  } catch (error) {
    console.error('删除失败：', error);
  }
};

// 处理导出
const handleExport = () => {
  // TODO: 实现导出功能
};

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    if (form.categoryId) {
      // 修改
      await request.put('/system/category', form);
      ElMessage.success('修改成功');
    } else {
      // 新增
      await request.post('/system/category', form);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchCategories();
  } catch (error) {
    console.error('提交失败：', error);
  }
};

// 处理选择变化
const handleSelectionChange = (selection: CategoryData[]) => {
  selectedRow.value = selection[0] || null;
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  // 不需要重新请求，使用计算属性分页
};

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  // 不需要重新请求，使用计算属性分页
};

onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.category-manage {
  padding: 20px;
}

.search-bar {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.left-buttons {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 