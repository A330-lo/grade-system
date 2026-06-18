<template>
  <div class="clazz-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-select v-model="searchForm.majorId" placeholder="选择专业" clearable class="search-select">
              <el-option v-for="major in majorList" :key="major.id" :label="major.majorName" :value="major.id" />
            </el-select>
            <el-input
              v-model="searchForm.clazzName"
              placeholder="请输入班级名称"
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
        <el-table-column prop="clazzName" label="班级名称" align="center" />
        <el-table-column prop="clazzCode" label="班级编码" align="center" />
        <el-table-column prop="grade" label="年级" align="center" />
        <el-table-column label="所属专业" align="center">
          <template #default="{ row }">
            {{ getMajorName(row.majorId) }}
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle()" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级名称" prop="clazzName">
          <el-input v-model="form.clazzName" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="班级编码" prop="clazzCode">
          <el-input v-model="form.clazzCode" placeholder="请输入班级编码" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <el-select v-model="form.majorId" placeholder="请选择专业" style="width: 100%">
            <el-option v-for="major in majorList" :key="major.id" :label="major.majorName" :value="major.id" />
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
import { getMajorList } from '@/api/major'
import { getClazzPage, addClazz, updateClazz, deleteClazz } from '@/api/clazz'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const majorList = ref([])
const getMajorName = (majorId) => {
  const major = majorList.value.find(item => item.id === majorId)
  return major ? major.majorName : ''
}

const searchForm = reactive({ clazzName: '', majorId: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const form = reactive({
  id: null,
  clazzName: '',
  clazzCode: '',
  grade: '',
  majorId: null
})

const rules = {
  clazzName: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  clazzCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  grade: [{ required: true, message: '请输入年级', trigger: 'blur' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }]
}

const dialogTitle = () => isEdit.value ? '编辑班级' : '新增班级'

const fetchMajorList = async () => {
  try {
    const res = await getMajorList()
    majorList.value = res || []
  } catch (error) {
    console.error('获取专业列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getClazzPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      clazzName: searchForm.clazzName,
      majorId: searchForm.majorId
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
  ElMessageBox.confirm(`确定要删除班级【${row.clazzName}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteClazz(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    }).catch(() => {})
}

const resetForm = () => {
  form.id = null
  form.clazzName = ''
  form.clazzCode = ''
  form.grade = ''
  form.majorId = null
  if (formRef.value) formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateClazz(form)
      ElMessage.success('修改成功')
    } else {
      await addClazz(form)
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
  fetchMajorList()
  fetchData()
})
</script>

<style scoped lang="scss">
.clazz-page {
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
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
}
</style>
