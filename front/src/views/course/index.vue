<template>
  <div class="course-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-input v-model="searchForm.courseName" placeholder="课程名称" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-select v-model="searchForm.semester" placeholder="学期" clearable class="search-select">
              <el-option v-for="sem in semesters" :key="sem" :label="sem" :value="sem" />
            </el-select>
            <el-select v-model="searchForm.courseType" placeholder="课程类型" clearable class="search-select">
              <el-option label="必修" value="必修" />
              <el-option label="选修" value="选修" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
            <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="courseName" label="课程名称" align="center" />
        <el-table-column prop="courseCode" label="课程编码" align="center" />
        <el-table-column prop="credit" label="学分" align="center" width="80" />
        <el-table-column prop="classHours" label="学时" align="center" width="80" />
        <el-table-column prop="semester" label="学期" align="center" />
        <el-table-column prop="courseType" label="类型" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.courseType === '必修' ? 'danger' : 'success'" size="small">
              {{ row.courseType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="授课教师" align="center" />
        <el-table-column prop="maxStudent" label="最大人数" align="center" width="100" />
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程编码" prop="courseCode">
              <el-input v-model="form.courseCode" placeholder="请输入课程编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学分" prop="credit">
              <el-input v-model.number="form.credit" type="number" placeholder="请输入学分" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学时" prop="classHours">
              <el-input v-model.number="form.classHours" type="number" placeholder="请输入学时" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大人数" prop="maxStudent">
              <el-input v-model.number="form.maxStudent" type="number" placeholder="请输入最大人数" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-input v-model="form.semester" placeholder="如：2024-2025-1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" style="width: 100%">
                <el-option label="必修" value="必修" />
                <el-option label="选修" value="选修" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacherId">
              <el-select v-model="form.teacherId" placeholder="请选择教师" style="width: 100%" filterable>
                <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.teacherName" :value="teacher.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课教室" prop="classroom">
              <el-input v-model="form.classroom" placeholder="请输入教室" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
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
import { getTeacherList } from '@/api/teacher'
import { getCoursePage, addCourse, updateCourse, deleteCourse, getSemesters } from '@/api/course'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const teacherList = ref([])
const semesters = ref([])

const searchForm = reactive({ courseName: '', semester: '', courseType: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const form = reactive({
  id: null, courseName: '', courseCode: '', credit: 0, classHours: 0,
  semester: '', courseType: '必修', maxStudent: 50, description: '',
  teacherId: null, classroom: ''
})

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程编码', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  classHours: [{ required: true, message: '请输入学时', trigger: 'blur' }],
  semester: [{ required: true, message: '请输入学期', trigger: 'blur' }]
}

const dialogTitle = () => isEdit.value ? '编辑课程' : '新增课程'

const fetchTeacherList = async () => {
  try {
    const res = await getTeacherList()
    teacherList.value = res || []
  } catch (error) {
    console.error('获取教师列表失败', error)
  }
}

const fetchSemesters = async () => {
  try {
    const res = await getSemesters()
    semesters.value = res || []
  } catch (error) {
    console.error('获取学期列表失败', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCoursePage({
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
  ElMessageBox.confirm(`确定要删除课程【${row.courseName}】吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteCourse(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    }).catch(() => {})
}

const resetForm = () => {
  Object.assign(form, {
    id: null, courseName: '', courseCode: '', credit: 3, classHours: 48,
    semester: '', courseType: '必修', maxStudent: 50, description: '',
    teacherId: null, classroom: ''
  })
  if (formRef.value) formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateCourse(form)
      ElMessage.success('修改成功')
    } else {
      await addCourse(form)
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
  fetchTeacherList()
  fetchSemesters()
  fetchData()
})
</script>

<style scoped lang="scss">
.course-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .header-actions { display: flex; gap: 8px; flex-wrap: wrap;
      .search-input { width: 160px; }
      .search-select { width: 130px; }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
}
</style>
