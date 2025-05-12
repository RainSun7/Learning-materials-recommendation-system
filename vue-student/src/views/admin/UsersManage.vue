<template>
  <div class="users-manage">
    <div class="page-header">
      <h2>用户管理</h2>
      <button class="add-btn" @click="handleAdd">新增用户</button>
    </div>

    <div class="search-section">
      <div class="search-form">
        <input 
          type="text" 
          class="search-input" 
          v-model="queryParams.userName"
          placeholder="搜索用户名称..."
        >
        <input 
          type="text" 
          class="search-input" 
          v-model="queryParams.phonenumber"
          placeholder="搜索手机号码..."
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
            <th>用户编号</th>
            <th>用户名称</th>
            <th>用户昵称</th>
            <th>手机号码</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in userList" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td>{{ user.userName }}</td>
            <td>{{ user.nickName }}</td>
            <td>{{ user.phonenumber }}</td>
            <td>
              <el-switch
                v-model="user.status"
                active-value="0"
                inactive-value="1"
                @change="handleStatusChange(user)"
              />
            </td>
            <td>{{ user.createTime }}</td>
            <td>
              <div class="actions">
                <button class="edit-btn" @click="handleEdit(user)">编辑</button>
                <el-dropdown @command="(command: 'add' | 'edit' | 'delete' | 'export' | 'resetPwd' | 'setAdmin') => handleCommand(command, user)">
                  <button class="more-btn">
                    更多<i class="el-icon-arrow-down"></i>
                  </button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="resetPwd">重置密码</el-dropdown-item>
                      <el-dropdown-item command="setAdmin">
                        {{ user.remark === 'admin' ? '取消管理员' : '设为管理员' }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
                <button class="delete-btn" @click="handleDelete(user)">删除</button>
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

    <!-- 添加/修改用户对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="600px"
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名称" prop="userName" v-if="form.userId === 0">
          <el-input v-model="form.userName" placeholder="请输入用户名称" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入用户昵称" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="用户密码" prop="password" v-if="form.userId === 0">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
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
import type { FormInstance, FormRules } from 'element-plus'

interface User {
  userId: number;
  userName: string;
  nickName: string;
  email: string;
  phonenumber: string;
  status: string;
  createTime: string;
  dept?: {
    deptId: number;
    deptName: string;
  };
  roles: any[];
  roleIds: number[] | null;
  postIds: number[] | null;
  admin: boolean;
  remark: string;
}

interface ApiResponse<T> {
  code: number;
  msg: string;
  data?: T;
  roleIds?: number[];
  postIds?: number[];
  roles?: any[];
  posts?: any[];
}

interface PageResponse<T> {
  rows: T[];
  total: number;
}

interface UserListResponse {
  code: number;
  msg: string;
  rows: User[];
  total: number;
}

// 定义命令类型
type CommandType = 'add' | 'edit' | 'delete' | 'export' | 'resetPwd' | 'setAdmin';

// 定义用户表单接口
interface UserForm {
  userId: number;
  userName: string;
  nickName: string;
  email: string;
  phonenumber: string;
  status: string;
  roleIds: number[];
  postIds: number[];
  password?: string;
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: '',
  beginTime: '',
  endTime: ''
});

const userList = ref<User[]>([]);
const total = ref(0);
const open = ref(false);
const title = ref('');
const formRef = ref();
const loading = ref(false);
const dialogVisible = ref(false);
const selectedRows = ref<User[]>([]);
const dialogTitle = ref<string>('');
const form = ref<UserForm>({
  userId: 0,
  userName: '',
  nickName: '',
  email: '',
  phonenumber: '',
  status: '0',
  roleIds: [],
  postIds: []
});

const currentUser = ref<UserForm | null>(null);

// 表单校验规则
const rules = {
  userName: [
    { required: true, message: '用户名称不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '用户昵称不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '用户密码不能为空', trigger: 'blur' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

// 获取用户列表
const getList = async () => {
  loading.value = true;
  try {
    const response = await request.get<UserListResponse>('/system/user/list', {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        userName: queryParams.userName,
        phonenumber: queryParams.phonenumber,
        status: queryParams.status,
        beginTime: queryParams.beginTime,
        endTime: queryParams.endTime
      }
    });
    const res = response.data;
    if (res.code === 200) {
      userList.value = res.rows || [];
      total.value = res.total || 0;
    } else {
      ElMessage.error(res.msg || '获取用户列表失败');
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

// 表单重置
const reset = () => {
  form.value.userId = 0;
  form.value.userName = '';
  form.value.nickName = '';
  form.value.email = '';
  form.value.phonenumber = '';
  form.value.status = '0';
  form.value.roleIds = [];
  form.value.postIds = [];
};

// 取消按钮
const cancel = () => {
  open.value = false;
  reset();
};

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置按钮操作
const resetQuery = () => {
  queryParams.userName = '';
  queryParams.phonenumber = '';
  queryParams.status = '';
  handleQuery();
};

// 新增按钮操作
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = '添加用户';
};

// 修改按钮操作
const handleEdit = async (row: User) => {
  reset();
  try {
    const response = await request.get<ApiResponse<User>>(`/system/user/${row.userId}`);
    const res = response.data;
    if (res.code === 200 && res.data) {
      const userData = res.data;
      Object.assign(form.value, {
        userId: userData.userId,
        userName: userData.userName,
        nickName: userData.nickName,
        email: userData.email,
        phonenumber: userData.phonenumber,
        status: userData.status,
        roleIds: res.roleIds || [],
        postIds: res.postIds || []
      });
      open.value = true;
      title.value = '修改用户';
    } else {
      ElMessage.error(res.msg || '获取用户信息失败');
    }
  } catch (error) {
    console.error('获取用户信息失败：', error);
    ElMessage.error('获取用户信息失败');
  }
};

// 提交按钮
const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    if (form.value.userId !== 0) {
      await request.put('/system/user', form.value);
      ElMessage.success('修改成功');
    } else {
      await request.post('/system/user', form.value);
      ElMessage.success('新增成功');
    }
    open.value = false;
    getList();
  } catch (error) {
    console.error('提交失败：', error);
  }
};

// 用户状态修改
const handleStatusChange = async (row: User) => {
  try {
    await request.put('/system/user', row);
    ElMessage.success(`修改成功`);
  } catch (error) {
    console.error('修改状态失败：', error);
    // 回滚状态
    row.status = row.status === '0' ? '1' : '0';
  }
};

// 设置/取消管理员
const handleSetAdmin = async (user: User) => {
  try {
    const isAdmin = user.remark === 'admin';
    const action = isAdmin ? '取消管理员' : '设为管理员';
    await ElMessageBox.confirm(`确认${action}吗？`, '提示', {
      type: 'warning'
    });
    
    // 构建完整的用户信息
    const userData = {
      userId: user.userId,
      userName: user.userName,
      nickName: user.nickName,
      email: user.email,
      phonenumber: user.phonenumber,
      status: user.status,
      remark: isAdmin ? 'common' : 'admin',
      roleIds: user.roleIds || [],
      postIds: user.postIds || []
    };
    
    await request.put('/system/user', userData);
    
    ElMessage.success(`${action}成功`);
    getList();
  } catch {
    // 用户取消操作
  }
};

// 更多操作下拉菜单处理函数
const handleCommand = (command: CommandType, user?: User) => {
  switch (command) {
    case 'add':
      currentUser.value = {
        userId: 0,
        userName: '',
        nickName: '',
        email: '',
        phonenumber: '',
        status: '0',
        roleIds: [],
        postIds: [],
        password: ''
      };
      dialogVisible.value = true;
      break;
    case 'edit':
      if (user) {
        currentUser.value = {
          ...user,
          roleIds: user.roleIds || [],
          postIds: user.postIds || []
        };
        dialogVisible.value = true;
      }
      break;
    case 'delete':
      if (user) {
        handleDelete(user);
      }
      break;
    case 'export':
      handleExport();
      break;
    case 'resetPwd':
      if (user) {
        handleResetPwd(user);
      }
      break;
    case 'setAdmin':
      if (user) {
        handleSetAdmin(user);
      }
      break;
  }
};

// 删除按钮操作
const handleDelete = async (user: User) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', {
      type: 'warning'
    });
    await request.delete(`/system/user/${user.userId}`);
    ElMessage.success('删除成功');
    getList();
  } catch {
    // 用户取消删除
  }
};

// 重置密码
const handleResetPwd = async (user: User) => {
  try {
    await ElMessageBox.confirm('确认重置该用户密码吗？', '提示', {
      type: 'warning'
    });
    await request.put('/system/user/resetPwd', {
      userId: user.userId,
      password: '123123123' // 默认重置密码
    });
    ElMessage.success('密码重置成功');
  } catch {
    // 用户取消重置
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

const handleReset = () => {
  queryParams.userName = '';
  queryParams.phonenumber = '';
  queryParams.status = '';
  queryParams.pageNum = 1;
  getList();
};

const handleExport = () => {
  // TODO: 实现导出功能
  ElMessage.info('导出功能开发中');
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.users-manage {
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
.role-select {
  padding: 0.5rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 0.9rem;
}

.search-input {
  width: 200px;
}

.role-select {
  width: 150px;
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

.actions {
  display: flex;
  gap: 0.5rem;
}

.edit-btn,
.more-btn,
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

.more-btn {
  background: #909399;
  color: white;
}

.more-btn:hover {
  background: #a6a9ad;
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
  margin-top: 1rem;
}

.dialog-footer {
  text-align: right;
  margin-top: 1rem;
}
</style> 