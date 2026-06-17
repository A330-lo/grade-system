<template> 
   <div class="teacher-profile-page"> 
     <el-card> 
       <template #header> 
         <span>个人信息</span> 
       </template> 
 
       <div class="profile-container"> 
         <div class="avatar-section"> 
           <el-avatar :size="120" icon="User" class="avatar" /> 
           <h3 class="name">{{ profile.teacherName }}</h3> 
           <p class="role">{{ profile.title }} · {{ profile.deptName }}</p> 
         </div> 
 
         <el-descriptions :column="2" border class="info-section"> 
           <el-descriptions-item label="工号">{{ profile.teacherNo }}</el-descriptions-item> 
           <el-descriptions-item label="姓名">{{ profile.teacherName }}</el-descriptions-item> 
           <el-descriptions-item label="性别">{{ profile.gender }}</el-descriptions-item> 
           <el-descriptions-item label="职称">{{ profile.title }}</el-descriptions-item> 
           <el-descriptions-item label="所属学院">{{ profile.deptName }}</el-descriptions-item> 
           <el-descriptions-item label="状态"> 
             <el-tag :type="profile.status === 1 ? 'success' : 'danger'"> 
               {{ profile.status === 1 ? '正常' : '禁用' }} 
             </el-tag> 
           </el-descriptions-item> 
           <el-descriptions-item label="手机号">{{ profile.phone }}</el-descriptions-item> 
           <el-descriptions-item label="邮箱">{{ profile.email }}</el-descriptions-item> 
         </el-descriptions> 
 
         <div class="button-section"> 
           <el-button type="primary" @click="showEditDialog = true">编辑信息</el-button> 
           <el-button type="warning" @click="showPasswordDialog = true">修改密码</el-button> 
         </div> 
       </div> 
     </el-card> 
 
     <!-- 编辑信息弹窗 --> 
     <el-dialog v-model="showEditDialog" title="编辑个人信息" width="600px"> 
       <el-form :model="editForm" :rules="editRules" label-width="100px" ref="editFormRef"> 
         <el-row :gutter="20"> 
           <el-col :span="12"> 
             <el-form-item label="姓名" prop="teacherName"> 
               <el-input v-model="editForm.teacherName" /> 
             </el-form-item> 
           </el-col> 
           <el-col :span="12"> 
             <el-form-item label="职称" prop="title"> 
               <el-input v-model="editForm.title" /> 
             </el-form-item> 
           </el-col> 
         </el-row> 
         <el-row :gutter="20"> 
           <el-col :span="12"> 
             <el-form-item label="手机号" prop="phone"> 
               <el-input v-model="editForm.phone" /> 
             </el-form-item> 
           </el-col> 
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
       <el-form :model="passwordForm" :rules="passwordRules" label-width="80px"> 
         <el-form-item label="旧密码" prop="oldPassword"> 
           <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码" /> 
         </el-form-item> 
         <el-form-item label="新密码" prop="newPassword"> 
           <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" /> 
         </el-form-item> 
         <el-form-item label="确认密码" prop="confirmPassword"> 
           <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入密码" /> 
         </el-form-item> 
       </el-form> 
       <template #footer> 
         <el-button @click="showPasswordDialog = false">取消</el-button> 
         <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">确定</el-button> 
       </template> 
     </el-dialog> 
   </div> 
 </template> 
 
<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const saveLoading = ref(false)
const passwordLoading = ref(false)
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const editFormRef = ref(null)

const profile = reactive({
  id: null,
  teacherNo: '',
  teacherName: '',
  gender: '',
  title: '',
  deptId: null,
  deptName: '',
  phone: '',
  email: '',
  status: 1
})

const editForm = reactive({
  id: null,
  teacherName: '',
  title: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const editRules = {
  teacherName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
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
    const res = await request.get('/teacher/info')
    Object.assign(profile, res)
  } catch (error) {
    console.error('获取个人信息失败', error)
    ElMessage.error('获取个人信息失败')
  }
}

const handleSave = async () => {
  if (!editFormRef.value) return
  try {
    await editFormRef.value.validate()
    saveLoading.value = true
    
    await request.put('/teacher/profile', {
      id: editForm.id,
      teacherName: editForm.teacherName,
      title: editForm.title,
      phone: editForm.phone,
      email: editForm.email
    })
    
    ElMessage.success('个人信息保存成功')
    showEditDialog.value = false
    
    await fetchProfile()
  } catch (error) {
    console.error('保存失败', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    saveLoading.value = false
  }
}

const handleChangePassword = async () => {
  passwordLoading.value = true
  try {
    await request.put('/teacher/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改失败', error)
    ElMessage.error('修改失败')
  } finally {
    passwordLoading.value = false
  }
}

watch(showEditDialog, (val) => {
  if (val) {
    Object.assign(editForm, {
      id: profile.id,
      teacherName: profile.teacherName,
      title: profile.title,
      phone: profile.phone,
      email: profile.email
    })
  }
})

onMounted(() => {
  fetchProfile()
})
</script>
 
 <style scoped lang="scss"> 
 .teacher-profile-page { 
   .profile-container { 
     display: flex; 
     flex-direction: column; 
     align-items: center; 
     padding: 20px; 
   } 
   .avatar-section { 
     text-align: center; 
     margin-bottom: 30px; 
     .avatar { 
       margin-bottom: 15px; 
     } 
     .name { 
       font-size: 24px; 
       font-weight: bold; 
       margin: 0 0 8px 0; 
     } 
     .role { 
       color: #666; 
       margin: 0; 
     } 
   } 
   .info-section { 
     width: 100%; 
     max-width: 800px; 
     margin-bottom: 30px; 
   } 
   .button-section { 
     margin-top: 20px; 
     display: flex; 
     gap: 10px; 
   } 
 } 
 </style>  