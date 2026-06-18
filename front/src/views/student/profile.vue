<template>
  <div class="student-profile-page">
    <el-card>
      <template #header>
        <span></span>
      </template>

      <div class="profile-container">
        <div class="avatar-section">
          <el-avatar :size="120" :icon="User" class="avatar" />
          <div class="student-name">{{ profile.studentName }}</div>
          <div class="student-no">{{ profile.studentNo }}</div>
        </div>

        <div class="info-section">
          <el-form :model="profile" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学号">
                  <el-input v-model="profile.studentNo" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="姓名">
                  <el-input v-model="profile.studentName" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-radio-group v-model="profile.gender" disabled>
                    <el-radio value="男">男</el-radio>
                    <el-radio value="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="班级">
                  <el-input v-model="profile.clazzName" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号">
                  <el-input v-model="profile.phone" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱">
                  <el-input v-model="profile.email" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="入学日期">
                  <el-input v-model="profile.enrollmentDate" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-tag :type="profile.status === 1 ? 'success' : 'danger'">
                    {{ profile.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>

      <div class="action-bar">
        <el-button type="primary" @click="handleEdit">修改信息</el-button>
        <el-button type="warning" @click="showPasswordDialog = true">修改密码</el-button>
      </div>
    </el-card>

    <!-- 修改信息弹窗 -->
    <el-dialog v-model="showEditDialog" title="修改个人信息" width="600px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="editForm.studentName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="editForm.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { User, Phone, Message, Location, Calendar, Edit, Close } from '@element-plus/icons-vue'

const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const editFormRef = ref(null)
const passwordFormRef = ref(null)
const saveLoading = ref(false)
const passwordLoading = ref(false)

const profile = reactive({
  id: null,
  studentNo: '',
  studentName: '',
  gender: '',
  clazzId: null,
  clazzName: '',
  phone: '',
  email: '',
  enrollmentDate: '',
  status: 1
})

const editForm = reactive({
  id: null,
  studentName: '',
  phone: '',
  email: ''
})

const editRules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const fetchProfile = async () => {
  try {
    const res = await request.get('/student/info')
    Object.assign(profile, res)
    // 处理日期格式
    if (res.enrollmentDate) {
      profile.enrollmentDate = res.enrollmentDate.split('T')[0]
    }
  } catch (error) {
    console.error('获取个人信息失败', error)
    ElMessage.error('获取个人信息失败')
  }
}

const handleEdit = () => {
  Object.assign(editForm, {
    id: profile.id,
    studentName: profile.studentName,
    phone: profile.phone,
    email: profile.email
  })
  showEditDialog.value = true
}

const handleSave = async () => {
  if (!editFormRef.value) return
  try {
    await editFormRef.value.validate()
    saveLoading.value = true
    await request.put('/student/profile', {
      id: editForm.id,
      studentName: editForm.studentName,
      phone: editForm.phone,
      email: editForm.email
    })
    ElMessage.success('个人信息保存成功')
    showEditDialog.value = false
    await fetchProfile()
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    saveLoading.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    await request.put('/student/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('密码修改失败', error)
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped lang="scss">
.student-profile-page {
  .profile-container {
    display: flex; gap: 40px;
    .avatar-section {
      text-align: center;
      .avatar {
        margin-bottom: 15px;
      }
      .student-name {
        font-size: 20px; font-weight: bold; margin-bottom: 5px;
      }
      .student-no {
        color: #666; font-size: 14px;
      }
    }
    .info-section {
      flex: 1;
    }
  }
  .action-bar {
    margin-top: 30px;
    display: flex; gap: 15px;
    justify-content: flex-end;
  }
}
</style>