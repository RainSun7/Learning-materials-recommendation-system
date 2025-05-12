<template>
  <div class="roles-manage">
    <div class="page-header">
      <h2>角色管理</h2>
      <button class="add-btn" @click="handleAdd">新增角色</button>
    </div>

    <div class="search-section">
      <div class="search-form">
        <input 
          type="text" 
          class="search-input" 
          v-model="queryParams.roleName"
          placeholder="搜索角色名称..."
        >
        <input 
          type="text" 
          class="search-input" 
          v-model="queryParams.roleKey"
          placeholder="搜索权限字符..."
        >
        <select v-model="queryParams.status" class="role-select">
          <option value="">全部状态</option>
          <option value="0">正常</option>
          <option value="1">停用</option>
        </select>
        <button class="search-btn" @click="handleQuery">搜索</button>
        <button class="reset-btn" @click="resetQuery">重置</button>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>角色编号</th>
            <th>角色名称</th>
            <th>权限字符</th>
            <th>显示顺序</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="role in roles" :key="role.roleId">
            <td>{{ role.roleId }}</td>
            <td>{{ role.roleName }}</td>
            <td>{{ role.roleKey }}</td>
            <td>{{ role.roleSort }}</td>
            <td>
              <el-switch
                v-model="role.status"
                active-value="0"
                inactive-value="1"
                @change="handleStatusChange(role)"
              />
            </td>
            <td>{{ role.createTime }}</td>
            <td>
              <div class="actions">
                <button class="edit-btn" @click="handleEdit(role)">编辑</button>
                <button class="permission-btn" @click="handlePermission(role)">权限设置</button>
                <button class="delete-btn" @click="handleDelete(role)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/修改角色对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="600px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

interface Role {
  roleId: number;
  roleName: string;
  roleKey: string;
  roleSort: number;
  status: string;
  createTime: string;
  updateTime: string;
  remark?: string;
  menuIds?: number[];
  deptIds?: number[];
  menuCheckStrictly?: boolean;
  deptCheckStrictly?: boolean;
}

interface RoleForm {
  roleId?: number;
  roleName: string;
  roleKey: string;
  roleSort: number;
  status: string;
  remark?: string;
  menuIds: number[];
  deptIds: number[];
  menuCheckStrictly: boolean;
  deptCheckStrictly: boolean;
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data?: T;
  rows?: T[];
  total?: number;
}

interface RoleListResponse {
  code: number;
  msg: string;
  rows: Role[];
  total: number;
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: ''
});

const roles = ref<Role[]>([]);
const total = ref(0);
const loading = ref(false);
const open = ref(false);
const title = ref('');
const form = ref<RoleForm>({
  roleName: '',
  roleKey: '',
  roleSort: 1,
  status: '0',
  menuIds: [],
  deptIds: [],
  menuCheckStrictly: true,
  deptCheckStrictly: true
});

// 获取角色列表
const getList = async () => {
  loading.value = true;
  try {
    const response = await request.get<RoleListResponse>('/system/role/list', {
      params: queryParams
    });
    const res = response.data;
    if (res.code === 200) {
      roles.value = res.rows || [];
      total.value = res.total || 0;
    } else {
      ElMessage.error(res.msg || '获取角色列表失败');
    }
  } catch (error) {
    console.error('获取角色列表失败:', error);
    ElMessage.error('获取角色列表失败');
  } finally {
    loading.value = false;
  }
};

// 表单重置
const reset = () => {
  form.value = {
    roleName: '',
    roleKey: '',
    roleSort: 1,
    status: '0',
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true
  };
};

// 取消按钮
const cancel = () => {
  open.value = false;
  reset();
};

// 新增按钮操作
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = '添加角色';
};

// 修改按钮操作
const handleEdit = async (row: Role) => {
  try {
    const response = await request.get<ApiResponse<Role>>(`/system/role/${row.roleId}`);
    const res = response.data;
    if (res.code === 200 && res.data) {
      const roleData = res.data;
      Object.assign(form.value, {
        roleId: roleData.roleId,
        roleName: roleData.roleName,
        roleKey: roleData.roleKey,
        roleSort: roleData.roleSort,
        status: roleData.status,
        remark: roleData.remark,
        menuIds: roleData.menuIds || [],
        deptIds: roleData.deptIds || [],
        menuCheckStrictly: roleData.menuCheckStrictly ?? true,
        deptCheckStrictly: roleData.deptCheckStrictly ?? true
      });
      open.value = true;
      title.value = '修改角色';
    } else {
      ElMessage.error(res.msg || '获取角色信息失败');
    }
  } catch (error) {
    console.error('获取角色信息失败：', error);
    ElMessage.error('获取角色信息失败');
  }
};

// 提交按钮
const submitForm = async () => {
  try {
    if (form.value.roleId) {
      await request.put('/system/role', form.value);
      ElMessage.success('修改成功');
    } else {
      await request.post('/system/role', form.value);
      ElMessage.success('新增成功');
    }
    open.value = false;
    getList();
  } catch (error) {
    console.error('提交失败：', error);
  }
};

// 删除按钮操作
const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm('确认删除该角色吗？', '提示', {
      type: 'warning'
    });
    await request.delete(`/system/role/${row.roleId}`);
    ElMessage.success('删除成功');
    getList();
  } catch {
    // 用户取消删除
  }
};

// 分页大小改变
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val;
  getList();
};

// 页码改变
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val;
  getList();
};

// 用户状态修改
const handleStatusChange = async (row: Role) => {
  try {
    await request.put('/system/role', row);
    ElMessage.success(`修改成功`);
  } catch (error) {
    console.error('修改状态失败：', error);
    // 回滚状态
    row.status = row.status === '0' ? '1' : '0';
  }
};

// 权限设置
const handlePermission = (row: Role) => {
  // TODO: 实现权限设置功能
  ElMessage.info('权限设置功能开发中');
};

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置按钮操作
const resetQuery = () => {
  queryParams.roleName = '';
  queryParams.roleKey = '';
  queryParams.status = '';
  handleQuery();
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.roles-manage {
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
  gap: 0.5rem;
}

.search-input {
  padding: 0.5rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.role-select {
  padding: 0.5rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.search-btn,
.reset-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background: #1890ff;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover,
.reset-btn:hover {
  background: #40a9ff;
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

.role-name {
  font-weight: 500;
  color: #1890ff;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.add-btn,
.edit-btn,
.permission-btn,
.delete-btn {
  padding: 0.3rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-btn {
  background: #52c41a;
  color: white;
}

.add-btn:hover {
  background: #73d13d;
}

.edit-btn {
  background: #1890ff;
  color: white;
}

.edit-btn:hover {
  background: #40a9ff;
}

.permission-btn {
  background: #722ed1;
  color: white;
}

.permission-btn:hover {
  background: #9254de;
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
</style> 