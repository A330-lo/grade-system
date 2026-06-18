C:\Users\19178\Desktop\grade\front\src\views\teacher\score.vue
<template>
  <div class="teacher-score-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span></span>
          <div class="header-actions">
            <el-select v-model="searchForm.courseId" placeholder="选择课程" clearable filterable
              class="search-select" @change="handleSearch">
              <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" :index="(idx) => idx + 1" label="序号" width="60" align="center" />
        <el-table-column label="学号" align="center" width="120">
          <template #default="{ row }">{{ row.student?.studentNo || '' }}</template>
        </el-table-column>
        <el-table-column label="姓名" align="center" width="100">
          <template #default="{ row }">{{ row.student?.studentName || '' }}</template>
        </el-table-column>
        <el-table-column label="班级" align="center" width="130">
          <template #default="{ row }">{{ row.student?.clazzName || '' }}</template>
        </el-table-column>
        <el-table-column label="平时成绩" align="center" width="150">
          <template #default="{ row }">
            <div class="score-cell">
              <el-input-number
                v-model="row.usualScore"
                :step="0.1"
                :disabled="row.status === 1"
                :class="{ 'is-error': getScoreError(row.usualScore) }"
                size="small" controls-position="right"
                style="width: 100px"
                @change="calcTotal(row)"
              />
              <span v-if="getScoreError(row.usualScore)" class="error-tip">{{ getScoreError(row.usualScore) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="期末成绩" align="center" width="150">
          <template #default="{ row }">
            <div class="score-cell">
              <el-input-number
                v-model="row.finalScore"
                :step="0.1"
                :disabled="row.status === 1"
                :class="{ 'is-error': getScoreError(row.finalScore) }"
                size="small" controls-position="right"
                style="width: 100px"
                @change="calcTotal(row)"
              />
              <span v-if="getScoreError(row.finalScore)" class="error-tip">{{ getScoreError(row.finalScore) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="总分" align="center" width="80">
          <template #default="{ row }">
            <template v-if="row.totalScore != null">
              <el-tag :type="row.totalScore >= 60 ? 'success' : 'danger'">
                {{ row.totalScore }}
              </el-tag>
            </template>
            <template v-else>-</template>
          </template>
        </el-table-column>
        <el-table-column label="绩点" align="center" width="70">
          <template #default="{ row }">{{ row.gpa || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已发布' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :disabled="row.status === 1" @click="handleSave(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="footer-actions">
        <el-button type="primary" @click="handleSaveAll" :loading="saveLoading">保存全部</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const saveLoading = ref(false)

const courseOptions = ref([])
const tableData = ref([])

const searchForm = reactive({
  courseId: null
})

const fetchCourses = async () => {
  try {
    const res = await request.get('/course/list-by-teacher')
    courseOptions.value = (res || []).map(c => ({
      label: `${c.courseName} (${c.courseCode})`,
      value: c.id
    }))
  } catch (error) {
    console.error('获取课程失败', error)
  }
}

const isScoreValid = (score) => {
  return score !== null && score !== undefined && score !== '' && score >= 0 && score <= 100
}

const calcTotal = (row) => {
  if (isScoreValid(row.usualScore) && isScoreValid(row.finalScore)) {
    const usual = Number(row.usualScore)
    const final = Number(row.finalScore)
    row.totalScore = Math.round((usual * 0.3 + final * 0.7) * 10) / 10
    row.gpa = calcGpa(row.totalScore)
  } else {
    row.totalScore = null
    row.gpa = null
  }
}

const calcGpa = (score) => {
  if (score >= 90) return 4.0
  if (score >= 85) return 3.7
  if (score >= 82) return 3.3
  if (score >= 78) return 3.0
  if (score >= 75) return 2.7
  if (score >= 72) return 2.3
  if (score >= 68) return 2.0
  if (score >= 64) return 1.5
  if (score >= 60) return 1.0
  return 0
}

const fetchData = async () => {
  if (!searchForm.courseId) {
    tableData.value = []
    return
  }
  loading.value = true
  try {
    const res = await request.get('/score/course-scores', {
      params: { courseId: searchForm.courseId }
    })
    tableData.value = (res || []).map(item => ({
      ...item,
      usualScore: Number(item.usualScore) || 0,
      finalScore: Number(item.finalScore) || 0,
      totalScore: item.totalScore || 0,
      gpa: item.gpa || null
    }))
  } catch (error) {
    console.error('获取数据失败', error)
    ElMessage.error('获取数据失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchData()
}

const getScoreError = (score) => {
  if (score === null || score === undefined || score === '') return '不能为空'
  if (score < 0 || score > 100) return '必须在 0-100 之间'
  return ''
}

const validateScore = (score, label) => {
  const err = getScoreError(score)
  if (err) {
    ElMessage.warning(`「${label}」${err}`)
    return false
  }
  return true
}

const handleSave = async (row) => {
  if (!validateScore(row.usualScore, '平时成绩')) return
  if (!validateScore(row.finalScore, '期末成绩')) return
  try {
    await request.put('/score', {
      id: row.id,
      usualScore: row.usualScore,
      finalScore: row.finalScore,
      totalScore: row.totalScore,
      gpa: row.gpa
    })
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleSaveAll = async () => {
  if (!searchForm.courseId) {
    ElMessage.warning('请先选择课程')
    return
  }
  const pendingRows = tableData.value.filter(row => row.status !== 1)
  if (pendingRows.length === 0) {
    ElMessage.warning('没有待审核的成绩需要保存')
    return
  }
  for (const row of pendingRows) {
    if (!validateScore(row.usualScore, '平时成绩')) return
    if (!validateScore(row.finalScore, '期末成绩')) return
  }
  saveLoading.value = true
  try {
    for (const row of pendingRows) {
      await request.put('/score', {
        id: row.id,
        usualScore: row.usualScore,
        finalScore: row.finalScore,
        totalScore: row.totalScore,
        gpa: row.gpa
      })
    }
    ElMessage.success(`已保存 ${pendingRows.length} 条成绩`)
    fetchData()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.teacher-score-page {
  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .header-actions {
      display: flex;
      gap: 10px;
      align-items: center;
      .search-select {
        width: 260px;
      }
    }
  }
  .footer-actions {
    margin-top: 20px;
    display: flex;
    gap: 10px;
    justify-content: flex-end;
  }
  .score-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    .error-tip {
      color: #f56c6c;
      font-size: 12px;
      margin-top: 2px;
      line-height: 1;
    }
  }
  :deep(.el-input-number.is-error .el-input__wrapper) {
    box-shadow: 0 0 0 1px #f56c6c inset;
  }
}
</style>