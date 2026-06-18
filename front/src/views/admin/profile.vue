<template>
  <div class="admin-profile-page">
    <el-card>
      <template #header>
        <span></span>
      </template>

      <div class="profile-container">
        <div class="avatar-section">
          <el-avatar :size="120" :icon="User" class="avatar" />
          <div class="admin-name">{{ profile.username }}</div>
          <div class="admin-role">管理员</div>
        </div>

        <div class="info-section">
          <el-form :model="profile" label-width="120px">
            <el-form-item label="用户名">
              <el-input v-model="profile.username" disabled />
            </el-form-item>
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="profile.realName" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profile.phone" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profile.email" />
            </el-form-item>
            <el-form-item label="状态">
              <el-tag :type="profile.status === 1 ? 'success' : 'danger'">
                {{ profile.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="action-bar">
        <el-button type="primary" @click="handleSave">保存修改</el-button>
        <el-button type="warning" @click="showPasswordDialog = true">修改密码</el-button>
      </div>
    </el-card>

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
import { User } from '@element-plus/icons-vue'
import { getAdminInfo, updateAdmin, updateAdminPassword } from '@/api/admin'

const showPasswordDialog = ref(false)
const passwordFormRef = ref(null)

const profile = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  status: 1
})

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
    const res = await getAdminInfo()
    Object.assign(profile, res)
  } catch (error) {
    console.error('获取个人信息失败', error)
  }
}

const handleSave = async () => {
  try {
    await updateAdmin({
      id: profile.id,
      realName: profile.realName,
      phone: profile.phone,
      email: profile.email
    })
    ElMessage.success('个人信息保存成功')
  } catch (error) {
    console.error('保存失败', error)
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    await updateAdminPassword({
      id: profile.id,
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
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped lang="scss">
.admin-profile-page {
  .profile-container {
    display: flex;
    gap: 40px;

    .avatar-section {
      text-align: center;

      .avatar {
        margin-bottom: 15px;
      }

      .admin-name {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 5px;
      }

      .admin-role {
        color: #666;
        font-size: 14px;
      }
    }

    .info-section {
      flex: 1;
    }
  }

  .action-bar {
    margin-top: 30px;
    display: flex;
    gap: 15px;
    justify-content: flex-end;
  }
}
</style>