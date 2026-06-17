<template>
  <div class="admin-score-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩管理</span>
          <div class="header-actions">
            <el-select v-model="searchForm.studentId" placeholder="选择学生" clearable filterable
              class="search-select" :options="studentOptions" />
            <el-select v-model="searchForm.courseId" placeholder="选择课程" clearable filterable
              class="search-select" :options="courseOptions" />
            <el-select v-model="searchForm.isPass" placeholder="是否及格" clearable class="search-select">
              <el-option label="全部" value="" />
              <el-option label="及格" :value="1" />
              <el-option label="不及格" :value="0" />
            </el-select>
            <el-select v-model="searchForm.status" placeholder="发布状态" clearable class="search-select">
              <el-option label="全部" value="" />
              <el-option label="待审核" :value="0" />
              <el-option label="已发布" :value="1" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
            <el-button type="success" @click="handleBatchPublish" :disabled="selectedIds.length === 0">批量发布</el-button>
            <el-button type="warning" @click="handleBatchReject" :disabled="selectedIds.length === 0">批量退回</el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">批量删除</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="学生" align="center" min-width="140">
          <template #default="{ row }">
            <div>{{ row.student?.studentNo || '' }}</div>
            <div>{{ row.student?.studentName || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="班级" align="center" width="130">
          <template #default="{ row }">{{ row.student?.clazzName || '' }}</template>
        </el-table-column>
        <el-table-column label="课程" align="center" min-width="140">
          <template #default="{ row }">
            <div>{{ row.course?.courseName || '' }}</div>
            <div style="font-size: 12px; color: #999;">{{ row.course?.courseCode || '' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="学分" align="center" width="60">
          <template #default="{ row }">{{ row.course?.credit || '' }}</template>
        </el-table-column>
        <el-table-column prop="usualScore" label="平时" align="center" width="70" />
        <el-table-column prop="finalScore" label="期末" align="center" width="70" />
        <el-table-column prop="totalScore" label="总分" align="center" width="70">
          <template #default="{ row }">
            <el-tag :type="(row.totalScore || 0) >= 60 ? 'success' : 'danger'">{{ row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gpa" label="绩点" align="center" width="60" />
        <el-table-column prop="isPass" label="及格" align="center" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isPass === 1 ? 'success' : 'danger'">{{ row.isPass === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已发布' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleReject(row)">退回</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="formData.studentId" placeholder="请选择学生" filterable style="width: 100%">
            <el-option v-for="stu in studentOptions" :key="stu.value" :label="stu.label" :value="stu.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="formData.courseId" placeholder="请选择课程" filterable style="width: 100%">
            <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="平时成绩" prop="usualScore">
          <el-input-number v-model="formData.usualScore" :min="0" :max="100" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="期末成绩" prop="finalScore">
          <el-input-number v-model="formData.finalScore" :min="0" :max="100" :step="0.1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除这条成绩记录吗？此操作不可撤销。</p>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleConfirmDelete" :loading="deleteLoading">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const deleteLoading = ref(false)

const studentOptions = ref([])
const courseOptions = ref([])
const selectedIds = ref([])

const searchForm = reactive({
  studentId: null,
  courseId: null,
  isPass: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const currentId = ref(null)

const dialogTitle = computed(() => isEdit.value ? '编辑成绩' : '新增成绩')

const formData = reactive({
  studentId: null,
  courseId: null,
  usualScore: 0,
  finalScore: 0
})

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  usualScore: [{ required: true, message: '请输入平时成绩', trigger: 'blur' }],
  finalScore: [{ required: true, message: '请输入期末成绩', trigger: 'blur' }]
}

const fetchOptions = async () => {
  try {
    const [students, courses] = await Promise.all([
      request.get('/student/list'),
      request.get('/course/list')
    ])
    studentOptions.value = (students || []).map(s => ({
      label: `${s.studentNo} ${s.studentName}`,
      value: s.id
    }))
    courseOptions.value = (courses || []).map(c => ({
      label: c.courseName,
      value: c.id
    }))
  } catch (error) {
    console.error('获取选项失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      studentId: searchForm.studentId,
      courseId: searchForm.courseId,
      isPass: searchForm.isPass || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    }
    const res = await request.get('/score/page', { params })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取数据失败', error)
    ElMessage.error('获取数据失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.studentId = null
  searchForm.courseId = null
  searchForm.isPass = ''
  searchForm.status = ''
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
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  formData.studentId = row.studentId
  formData.courseId = row.courseId
  formData.usualScore = Number(row.usualScore || 0)
  formData.finalScore = Number(row.finalScore || 0)
  dialogVisible.value = true
}

const resetForm = () => {
  formData.studentId = null
  formData.courseId = null
  formData.usualScore = 0
  formData.finalScore = 0
}

const handleDialogClose = () => {
  formRef.value?.clearValidate()
  resetForm()
}

const handleSubmit = async () => {
  await formRef.value?.validate(async (valid) => {
    if (!valid) return
    if (formData.usualScore < 0 || formData.usualScore > 100) {
      ElMessage.warning('平时成绩必须在0-100之间')
      return
    }
    if (formData.finalScore < 0 || formData.finalScore > 100) {
      ElMessage.warning('期末成绩必须在0-100之间')
      return
    }
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await request.put('/score', { id: currentId.value, ...formData })
        ElMessage.success('修改成功')
      } else {
        await request.post('/score', formData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      console.error('保存失败', error)
      ElMessage.error('保存失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.id)
}

const handlePublish = async (row) => {
  try {
    await request.put(`/score/publish/${row.id}`)
    ElMessage.success('已发布')
    fetchData()
  } catch (error) { ElMessage.error('操作失败') }
}

const handleReject = async (row) => {
  await ElMessageBox.confirm('确定要退回吗？退回后学生将无法查看。', '确认')
  try {
    await request.put(`/score/reject/${row.id}`)
    ElMessage.success('已退回')
    fetchData()
  } catch (error) { ElMessage.error('操作失败') }
}

const handleBatchPublish = async () => {
  try {
    await request.put('/score/publish-batch', selectedIds.value)
    ElMessage.success('批量发布成功')
    fetchData()
  } catch (error) { ElMessage.error('操作失败') }
}

const handleBatchReject = async () => {
  await ElMessageBox.confirm('确定要批量退回吗？退回后学生将无法查看。', '确认')
  try {
    await request.put('/score/reject-batch', selectedIds.value)
    ElMessage.success('批量退回成功')
    fetchData()
  } catch (error) { ElMessage.error('操作失败') }
}

const handleBatchDelete = async () => {
  await ElMessageBox.confirm('确定要批量删除选中的成绩吗？', '确认')
  try {
    await request.delete('/score/batch', { data: selectedIds.value })
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) { ElMessage.error('操作失败') }
}

const handleDelete = (row) => {
  currentId.value = row.id
  deleteDialogVisible.value = true
}

const handleConfirmDelete = async () => {
  deleteLoading.value = true
  try {
    await request.delete(`/score/${currentId.value}`)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('删除失败', error)
    ElMessage.error('删除失败')
  } finally {
    deleteLoading.value = false
  }
}

onMounted(() => {
  fetchOptions()
  fetchData()
})
</script>

<style scoped lang="scss">
.admin-score-page {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .header-actions {
      display: flex;
      gap: 10px;
      align-items: center;
      .search-select {
        width: 160px;
      }
    }
  }
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>