<template>
  <div class="student-course-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-input v-model="searchForm.studentNo" placeholder="学号" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-input v-model="searchForm.courseName" placeholder="课程名称" clearable class="search-input" @keyup.enter="handleSearch" />
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
   <el-table-column label="学号" align="center">
     <template #default="{ row }">{{ getStudentInfo(row.studentId)?.studentNo || '' }}</template>
   </el-table-column>
   <el-table-column label="学生姓名" align="center">
     <template #default="{ row }">{{ getStudentInfo(row.studentId)?.studentName || '' }}</template>
   </el-table-column>
   <el-table-column label="课程名称" align="center">
     <template #default="{ row }">{{ getCourseInfo(row.courseId)?.courseName || '' }}</template>
   </el-table-column>
   <el-table-column label="课程编码" align="center">
     <template #default="{ row }">{{ getCourseInfo(row.courseId)?.courseCode || '' }}</template>
   </el-table-column>
   <el-table-column label="学分" align="center" width="80">
     <template #default="{ row }">{{ getCourseInfo(row.courseId)?.credit || '' }}</template>
   </el-table-column>
   <el-table-column label="授课教师" align="center">
     <template #default="{ row }">{{ getCourseInfo(row.courseId)?.teacherName || '' }}</template>
   </el-table-column>
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已选' : '已退选' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选课时间" align="center" width="180">
          <template #default="{ row }">
            {{ formatDate(row.selectTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button 
      type="danger" 
      link 
      @click="handleDelete(row)"
      :disabled="getCourseInfo(row.courseId)?.courseType === '必修'"
      :title="getCourseInfo(row.courseId)?.courseType === '必修' ? '必修课不能退选' : ''"
    >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle()" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number v-model="form.studentId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-input-number v-model="form.courseId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">已选</el-radio>
            <el-radio :value="0">已退选</el-radio>
          </el-radio-group>
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
import { Search } from '@element-plus/icons-vue'
import { getStudentCoursePage, updateStudentCourse, deleteStudentCourse, addStudentCourse } from '@/api/studentCourse'
import { getStudentList } from '@/api/student'
import { getCourseList } from '@/api/course'
import { getTeacherList } from '@/api/teacher'
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({ studentNo: '', courseName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const form = reactive({
  id: null,
  studentId: null,
  courseId: null,
  status: 1
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  courseId: [{ required: true, message: '请输入课程ID', trigger: 'blur' }]
}

const dialogTitle = () => isEdit.value ? '编辑选课' : '新增选课'

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentCoursePage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      studentNo: searchForm.studentNo,
      courseName: searchForm.courseName
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}
const studentList = ref([])
const courseList = ref([])
const teacherList = ref([])
const fetchStudentList = async () => {
  try {
    const res = await getStudentList()
    studentList.value = res || []
  } catch (error) {
    console.error('获取学生列表失败', error)
  }
}
const fetchTeacherList = async () => {
 try {
   const res = await getTeacherList()
   teacherList.value = res || []
 } catch (error) {
   console.error('获取教师列表失败', error)
 }
}

const getCourseInfo = (courseId) => {
  const course = courseList.value.find(item => item.id === courseId)
  if (course && course.teacherId && !course.teacherName) {
   // 如果只有 teacherId 没有 teacherName，尝试映射
   const teacher = teacherList.value.find(t => t.id === course.teacherId)
   return { ...course, teacherName: teacher ? teacher.teacherName : '' }
 }
  return course
}
const fetchCourseList = async () => {
  try {
    const res = await getCourseList()
    courseList.value = res || []
  } catch (error) {
    console.error('获取课程列表失败', error)
  }
}

const getStudentInfo = (studentId) => {
  return studentList.value.find(item => item.id === studentId)
}

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const handleSizeChange = (size) => { pagination.pageSize = size; fetchData() }
const handleCurrentChange = (current) => { pagination.pageNum = current; fetchData() }
const handleAdd = () => { isEdit.value = false; dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.studentId = row.studentId
  form.courseId = row.courseId
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = (row) => {
  // 检查是否为必修课
  const course = getCourseInfo(row.courseId)
  if (course && course.courseType === '必修') {
    ElMessage.warning('必修课不能退选')
    return
  }
  ElMessageBox.confirm(`确定要删除这条选课记录吗？`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteStudentCourse(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败', error)
      }
    }).catch(() => {})
}

const resetForm = () => {
  form.id = null
  form.studentId = null
  form.courseId = null
  form.status = 1
  if (formRef.value) formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateStudentCourse(form)
      ElMessage.success('修改成功')
    } else {
      await addStudentCourse(form)
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
  fetchData()
  fetchStudentList()
  fetchCourseList()
  fetchTeacherList()
})
</script>

<style scoped lang="scss">
.student-course-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .header-actions { display: flex; gap: 10px;
      .search-input { width: 180px; }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
}
</style>
