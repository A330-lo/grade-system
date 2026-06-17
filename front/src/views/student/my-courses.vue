<template>
  <div class="student-my-courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-select v-model="searchForm.semester" placeholder="选择学期" clearable class="search-select">
              <el-option label="2024-2025学年第一学期" value="2024-2025-1" />
              <el-option label="2024-2025学年第二学期" value="2024-2025-2" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="filteredData" v-loading="loading" border stripe>
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
            <el-tag :type="(row.course?.courseType || '') === '必修' ? 'primary' : 'success'">
              {{ row.course?.courseType || '选修' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授课教师" align="center">
          <template #default="{ row }">{{ row.course?.teacherName || '' }}</template>
        </el-table-column>
        <el-table-column label="上课时间" align="center">
          <template #default="{ row }">{{ row.course?.schedule || '' }}</template>
        </el-table-column>
        <el-table-column label="上课地点" align="center">
          <template #default="{ row }">{{ row.course?.classroom || '' }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已选' : '已退选' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="summary-bar">
        <div class="summary-item">
          <span class="label">总课程数</span>
          <span class="value">{{ filteredData.length }} 门</span>
        </div>
        <div class="summary-item">
          <span class="label">总学分</span>
          <span class="value">{{ totalCredit }} 学分</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const searchForm = reactive({ semester: '' })
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/student-course/my-courses')
    tableData.value = res || []
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

// 前端根据学期过滤
const filteredData = computed(() => {
  if (!searchForm.semester) return tableData.value
  return tableData.value.filter(item => item.course?.semester === searchForm.semester)
})

const totalCredit = computed(() =>
  filteredData.value.reduce((sum, item) => sum + (item.course?.credit || 0), 0)
)

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.student-my-courses-page {
  .card-header {
    display: flex; align-items: center; justify-content: space-between;
    .search-select { width: 200px; }
  }
  .summary-bar {
    margin-top: 20px;
    display: flex;
    gap: 30px;
    .summary-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 15px 30px;
      background: #f5f7fa;
      border-radius: 8px;
      .label { font-size: 13px; color: #666; }
      .value { font-size: 20px; font-weight: bold; color: #1890ff; }
    }
  }
}
</style>