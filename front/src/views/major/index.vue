<template>
  <div class="major-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-select v-model="searchForm.deptId" placeholder="选择学院" clearable class="search-select">
              <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
            </el-select>
            <el-input
              v-model="searchForm.majorName"
              placeholder="请输入专业名称"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="majorName" label="专业名称" align="center" />
        <el-table-column prop="majorCode" label="专业编码" align="center" />
        <el-table-column label="所属学院" align="center">
      <template #default="{ row }">
    {{ getDeptName(row.deptId) }}
      </template>
    </el-table-column>
        <el-table-column prop="createTime" label="创建时间" align="center" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle()"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="form.majorName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业编码" prop="majorCode">
          <el-input v-model="form.majorCode" placeholder="请输入专业编码" />
        </el-form-item>
        <el-form-item label="所属学院" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择学院" style="width: 100%">
            <el-option v-for="dept in deptList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
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
import {
  getMajorPage,
  addMajor,
  updateMajor,
  deleteMajor
} from '@/api/major'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const deptList = ref([])
const getDeptName = (deptId) => {
  const dept = deptList.value.find(item => item.id === deptId)
  return dept ? dept.deptName : ''
}
const searchForm = reactive({
  majorName: '',
  deptId: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const form = reactive({
  id: null,
  majorName: '',
  majorCode: '',
  deptId: null
})

const rules = {
  majorName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  majorCode: [{ required: true, message: '请输入专业编码', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属学院', trigger: 'change' }]
}

const dialogTitle = () => isEdit.value ? '编辑专业' : '新增专业'

const fetchDeptList = async () => {
  try {
    const res = await getDepartmentList()
    deptList.value = res || []
  } catch (error) {
    console.error('获取学院列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMajorPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      majorName: searchForm.majorName,
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

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchData()
}

const handleCurrentChange = (current) => {
  pagination.pageNum = current
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除专业【${row.majorName}】吗？`,
    '提示',
    { type: 'warning' }
  ).then(async () => {
    try {
      await deleteMajor(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const resetForm = () => {
  form.id = null
  form.majorName = ''
  form.majorCode = ''
  form.deptId = null
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateMajor(form)
      ElMessage.success('修改成功')
    } else {
      await addMajor(form)
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

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchDeptList()
  fetchData()
})
</script>

<style scoped lang="scss">
.major-page {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .header-actions {
      display: flex;
      gap: 10px;
      .search-input { width: 200px; }
      .search-select { width: 150px; }
    }
  }
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
