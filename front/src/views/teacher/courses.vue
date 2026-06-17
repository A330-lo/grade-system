<template>
  <div class="teacher-courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>授课查询</span>
          <div class="header-actions">
            <el-select v-model="searchForm.semester" placeholder="选择学期" clearable class="search-select">
              <el-option label="2024-2025学年第一学期" value="2024-2025-1" />
              <el-option label="2024-2025学年第二学期" value="2024-2025-2" />
              <el-option label="2025-2026学年第一学期" value="2025-2026-1" />
              <el-option label="2025-2026学年第二学期" value="2025-2026-2" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程名称" align="center" />
        <el-table-column prop="courseCode" label="课程编码" align="center" />
        <el-table-column prop="credit" label="学分" align="center" width="80" />
        <el-table-column prop="classHours" label="学时" align="center" width="80" />
        <el-table-column prop="courseType" label="课程类型" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseType === '必修' ? 'primary' : 'success'">{{ row.courseType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" align="center" />
        <el-table-column label="上课时间" align="center">
          <template #default="{ row }">
            {{ formatSchedule(row.weekDay, row.startSection, row.endSection) }}
          </template>
        </el-table-column>
        <el-table-column prop="classroom" label="上课地点" align="center" />
        <el-table-column prop="selectedCount" label="选课人数" align="center" width="100" />
        <el-table-column prop="maxStudent" label="最大人数" align="center" width="100" />
        <el-table-column label="操作" align="center" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
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

    <!-- 课程详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="700px">
      <div v-if="currentCourse" class="course-detail">
        <el-descriptions title="课程基本信息" :column="2" border>
          <el-descriptions-item label="课程名称">{{ currentCourse.courseName }}</el-descriptions-item>
          <el-descriptions-item label="课程编码">{{ currentCourse.courseCode }}</el-descriptions-item>
          <el-descriptions-item label="学分">{{ currentCourse.credit }}</el-descriptions-item>
          <el-descriptions-item label="学时">{{ currentCourse.classHours }}</el-descriptions-item>
          <el-descriptions-item label="课程类型">{{ currentCourse.courseType }}</el-descriptions-item>
          <el-descriptions-item label="学期">{{ currentCourse.semester }}</el-descriptions-item>
          <el-descriptions-item label="上课时间">{{ formatSchedule(currentCourse.weekDay, currentCourse.startSection, currentCourse.endSection) }}</el-descriptions-item>
          <el-descriptions-item label="上课地点">{{ currentCourse.classroom }}</el-descriptions-item>
          <el-descriptions-item label="选课人数">{{ currentCourse.selectedCount }}/{{ currentCourse.maxStudent }}</el-descriptions-item>
          <el-descriptions-item label="课程描述">{{ currentCourse.description || '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 class="mt-20">选课学生列表</h4>
        <el-table :data="currentCourse.students || []" border>
          <el-table-column prop="student.studentNo" label="学号" />
          <el-table-column prop="student.studentName" label="姓名" />
          <el-table-column prop="student.gender" label="性别" />
          <el-table-column prop="student.clazzName" label="班级" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentCourse = ref(null)

const searchForm = reactive({ semester: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

// 格式化上课时间
const formatSchedule = (weekDay, startSection, endSection) => {
  if (!weekDay || !startSection || !endSection) return ''
  const weeks = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return `${weeks[weekDay]}${startSection}-${endSection}节`
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      semester: searchForm.semester || undefined
    }
    const res = await request.get('/course/list-by-teacher', { params })
    tableData.value = res || []
    pagination.total = res?.length || 0
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const handleSizeChange = (size) => { pagination.pageSize = size; fetchData() }
const handleCurrentChange = (current) => { pagination.pageNum = current; fetchData() }

const viewDetail = async (row) => {
  currentCourse.value = row
  // 获取选课学生列表
  try {
    const res = await request.get('/student-course/course-students', { 
      params: { courseId: row.id } 
    })
    currentCourse.value.students = res || []
  } catch (error) {
    console.error('获取学生列表失败', error)
    currentCourse.value.students = []
  }
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.teacher-courses-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .header-actions { display: flex; gap: 10px;
      .search-select { width: 200px; }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
  .mt-20 { margin-top: 20px; }
}
</style>