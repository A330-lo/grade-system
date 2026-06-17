<template>
  <div class="student-score-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable class="search-input" />             
            <el-select v-model="searchForm.semester" placeholder="选择学期" clearable class="search-select">
              <el-option label="2024-2025学年第一学期" value="2024-2025-1" />
              <el-option label="2024-2025学年第二学期" value="2024-2025-2" />
            </el-select>
            <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>查询</el-button>
          </div>
        </div>
      </template>

      <div class="summary-cards">
        <div class="summary-card">
          <div class="card-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">平均成绩</div>
            <div class="card-value">{{ averageScore }} 分</div>
          </div>
        </div>
        <div class="summary-card">
          <div class="card-icon success">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">已修学分</div>
            <div class="card-value">{{ completedCredit }} 学分</div>
          </div>
        </div>
        <div class="summary-card">
          <div class="card-icon warning">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">平均绩点</div>
            <div class="card-value">{{ averageGPA }}</div>
          </div>
        </div>
        <div class="summary-card">
          <div class="card-icon danger">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">不及格科目</div>
            <div class="card-value">{{ failedCount }} 门</div>
          </div>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
     <el-table-column label="课程名称" align="center">
     <template #default="{ row }">{{ row.course?.courseName || '' }}</template>
     </el-table-column>
     <el-table-column label="课程编码" align="center">
      <template #default="{ row }">{{ row.course?.courseCode || '' }}</template>
     </el-table-column>
    <el-table-column label="学分" align="center" width="80">
     <template #default="{ row }">{{ row.course?.credit || '' }}</template>
      </el-table-column>
      <el-table-column label="课程类型" align="center" width="100">
      <template #default="{ row }">
      <el-tag :type="(row.course?.courseType || '') === '必修' ? 'primary' : 'success'">{{ row.course?.courseType || '选修' }}</el-tag>
      </template>
      </el-table-column>
        <el-table-column label="授课教师" align="center">
          <template #default="{ row }">{{ row.course?.teacherName || '' }}</template>
        </el-table-column>
        <el-table-column label="学期" align="center">
          <template #default="{ row }">{{ row.course?.semester || '' }}</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总成绩" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.totalScore >= 60 ? 'success' : 'danger'">{{ row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gpa" label="绩点" align="center" width="80" />
        <el-table-column prop="isPass" label="是否及格" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPass ? 'success' : 'danger'">{{ row.isPass ? '及格' : '不及格' }}</el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Reading, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)

const searchForm = reactive({ semester: '', courseName: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      semester: searchForm.semester,
      courseName: searchForm.courseName
    }
    if (searchForm.semester) {
      params.semester = searchForm.semester
    }
    if (searchForm.courseName) {
      params.courseName = searchForm.courseName
    }
    const res = await request.get('/score/my-scores', { params })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    console.error('获取数据失败', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const averageScore = computed(() => {
  const scores = tableData.value.filter(item => item.totalScore)
  if (scores.length === 0) return '-'
  return (scores.reduce((sum, item) => sum + item.totalScore, 0) / scores.length).toFixed(1)
})

const completedCredit = computed(() => tableData.value.reduce((sum, item) => sum + (item.course?.credit || 0), 0))

const averageGPA = computed(() => {
  const gpas = tableData.value.filter(item => item.gpa)
  if (gpas.length === 0) return '-'
  return (gpas.reduce((sum, item) => sum + item.gpa, 0) / gpas.length).toFixed(2)
})

const failedCount = computed(() => tableData.value.filter(item => !item.isPass).length)

const handleSearch = () => { pagination.pageNum = 1; fetchData() }
const handleSizeChange = (size) => { pagination.pageSize = size; fetchData() }
const handleCurrentChange = (current) => { pagination.pageNum = current; fetchData() }

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.student-score-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .header-actions { display: flex; gap: 10px;
      .search-select { width: 200px; }
    }
  }
  .summary-cards {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    .summary-card {
      flex: 1;
      min-width: 200px;
      display: flex;
      align-items: center;
      padding: 20px;
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.08);
      .card-icon {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: #e8f4fd;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        font-size: 24px;
        color: #1890ff;
        &.success { background: #f6ffed; color: #52c41a; }
        &.warning { background: #fff7e6; color: #faad14; }
        &.danger { background: #fff1f0; color: #f5222d; }
      }
      .card-content {
        .card-title {
          font-size: 13px;
          color: #666;
          margin-bottom: 5px;
        }
        .card-value {
          font-size: 24px;
          font-weight: bold;
          color: #333;
        }
      }
    }
  }
  .pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
  .search-input { width: 200px; } 
}
</style>