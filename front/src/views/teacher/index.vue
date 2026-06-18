<template>
  <div class="teacher-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-input v-model="searchForm.teacherName" placeholder="教师姓名" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.deptId" placeholder="所属学院" clearable class="search-select">
              <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
            <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="teacherNo" label="工号" align="center" />
        <el-table-column prop="teacherName" label="姓名" align="center" />
        <el-table-column prop="gender" label="性别" align="center" width="80" />
        <el-table-column prop="title" label="职称" align="center" />
        <el-table-column label="所属学院" align="center">
          <template #default="{ row }">
            {{ getDeptName(row.deptId) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" align="center" />
        <el-table-column prop="email" label="邮箱" align="center" />
        <el-table-column prop="status" label="状态" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle()" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="teacherNo">
              <el-input v-model="form.teacherNo" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="teacherName">
              <el-input v-model="form.teacherName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="教授" value="教授" />
                <el-option label="副教授" value="副教授" />
                <el-option label="讲师" value="讲师" />
                <el-option label="助教" value="助教" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学院" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择学院" style="width: 100%">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getDepartmentList } from '@/api/department'
import { getTeacherPage, addTeacher, updateTeacher, deleteTeacher } from '@/api/teacher'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const deptList = ref([])

const searchForm = reactive({ teacherName: '', deptId: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const form = reactive({
  id: null,
  teacherNo: '',
  teacherName: '',
  gender: '',
  title: '',
  phone: '',
  email: '',
  deptId: null,
  status: 1
})

const rules = {
  teacherNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  teacherName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属系', trigger: 'change' }]
}

const getDeptName = (deptId) => {
  const dept = deptList.value.find(item => item.id === deptId)
  return dept ? dept.deptName : ''
}

const dialogTitle = () => isEdit.value ? '编辑教师' : '新增教师'

const fetchDeptList = async () => {
  try {
    const res = await getDepartmentList()
    deptList.value = res || []
  } catch (error) {
    console.error('获取系列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTeacherPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      teacherName: searchForm.teacherName,
      deptId: searchForm.deptId
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const handleSizeChange = (size) => { pagination.pageSize = size; fetchData() }
const handleCurrentChange = (current) => { pagination.pageNum = current; fetchData() }
const handleAdd = () => { isEdit.value = false; dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除教师【${row.teacherName}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteTeacher(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    }).catch(() => {})
}

const resetForm = () => {
  Object.assign(form, {
    id: null, teacherNo: '', teacherName: '', gender: '', title: '',
    phone: '', email: '', deptId: null, status: 1
  })
  if (formRef.value) formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateTeacher(form)
      ElMessage.success('修改成功')
    } else {
      await addTeacher(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败', error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchDeptList()
  fetchData()
})
</script>

<style scoped lang="scss">
.teacher-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .header-actions { display: flex; gap: 10px; flex-wrap: wrap;
      .search-input { width: 180px; }
      .search-select { width: 150px; }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
}
</style>
