import request from '@/utils/request'

export function getStudentPage(params) {
  return request({
    url: '/student/page',
    method: 'get',
    params
  })
}

export function getStudentList() {
  return request({
    url: '/student/list',
    method: 'get'
  })
}

export function getStudent(id) {
  return request({
    url: `/student/${id}`,
    method: 'get'
  })
}

export function getStudentInfo() {
  return request({
    url: '/student/info',
    method: 'get'
  })
}

export function getStudentHome() {
  return request({
    url: '/student/home',
    method: 'get'
  })
}

export function addStudent(data) {
  return request({
    url: '/student',
    method: 'post',
    data
  })
}

export function updateStudent(data) {
  return request({
    url: '/student',
    method: 'put',
    data
  })
}

export function updateStudentProfile(data) {
  return request({
    url: '/student/profile',
    method: 'put',
    data
  })
}

export function updateStudentPassword(data) {
  return request({
    url: '/student/password',
    method: 'post',
    data
  })
}

export function deleteStudent(id) {
  return request({
    url: `/student/${id}`,
    method: 'delete'
  })
}

export function deleteStudentBatch(ids) {
  return request({
    url: '/student/batch',
    method: 'delete',
    data: ids
  })
}

export function importStudent(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/student/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function exportStudent(params) {
  return request({
    url: '/student/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
