<template>
  <div class="student-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-input v-model="searchForm.studentNo" placeholder="学号" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-input v-model="searchForm.studentName" placeholder="姓名" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.clazzId" placeholder="班级" clearable class="search-select">
              <el-option v-for="clazz in clazzList" :key="clazz.id" :label="clazz.clazzName" :value="clazz.id" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
            <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="studentNo" label="学号" align="center" />
        <el-table-column prop="studentName" label="姓名" align="center" />
        <el-table-column prop="gender" label="性别" align="center" width="80" />
        <el-table-column prop="phone" label="手机号" align="center" />
        <el-table-column prop="email" label="邮箱" align="center" />
        <el-table-column prop="clazzName" label="班级" align="center" />
        <el-table-column prop="status" label="状态" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle()" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
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
            <el-form-item label="班级" prop="clazzId">
              <el-select v-model="form.clazzId" placeholder="请选择班级" style="width: 100%">
                <el-option v-for="clazz in clazzList" :key="clazz.id" :label="clazz.clazzName" :value="clazz.id" />
              </el-select>
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入学日期" prop="enrollmentDate">
              <el-date-picker v-model="form.enrollmentDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
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
import { Search, Plus} from '@element-plus/icons-vue'
import { getClazzList } from '@/api/clazz'
import {
  getStudentPage,
  addStudent,
  updateStudent,
  deleteStudent,
  deleteStudentBatch
} from '@/api/student'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const uploadRef = ref(null)
const clazzList = ref([])
const currentFile = ref(null)

const searchForm = reactive({
  studentNo: '',
  studentName: '',
  clazzId: null
})

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])
const selectedIds = ref([])

const form = reactive({
  id: null,
  studentNo: '',
  studentName: '',
  gender: '',
  phone: '',
  email: '',
  clazzId: null,
  enrollmentDate: '',
  status: 1
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

const dialogTitle = () => isEdit.value ? '编辑学生' : '新增学生'

const fetchClazzList = async () => {
  try {
    const res = await getClazzList()
    clazzList.value = res || []
  } catch (error) {
    console.error('获取班级列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
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
  ElMessageBox.confirm(`确定要删除学生【${row.studentName}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteStudent(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    }).catch(() => {})
}

const handleImport = () => {
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  currentFile.value = file.raw
}

const handleImportSubmit = async () => {
  if (!currentFile.value) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  importLoading.value = true
  try {
    await importStudent(currentFile.value)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('导入失败', error)
  } finally {
    importLoading.value = false
  }
}

const handleExport = async () => {
  try {
    const blob = await exportStudent(searchForm)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '学生信息.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('导出失败', error)
  }
}

const resetForm = () => {
  form.id = null
  form.studentNo = ''
  form.studentName = ''
  form.gender = ''
  form.phone = ''
  form.email = ''
  form.clazzId = null
  form.enrollmentDate = ''
  form.status = 1
  if (formRef.value) formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateStudent(form)
      ElMessage.success('修改成功')
    } else {
      await addStudent(form)
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
  fetchClazzList()
  fetchData()
})
</script>

<style scoped lang="scss">
.student-page {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .header-actions {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
      .search-input { width: 150px; }
      .search-select { width: 130px; }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
  .mt-20 { margin-top: 20px; }
}
</style>
