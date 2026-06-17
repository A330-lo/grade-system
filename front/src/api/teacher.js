import request from '@/utils/request'

export function getTeacherPage(params) {
  return request({
    url: '/teacher/page',
    method: 'get',
    params
  })
}

export function getTeacherList() {
  return request({
    url: '/teacher/list',
    method: 'get'
  })
}

export function getTeacherListByDept(deptId) {
  return request({
    url: '/teacher/list-by-dept',
    method: 'get',
    params: { deptId }
  })
}

export function getTeacher(id) {
  return request({
    url: `/teacher/${id}`,
    method: 'get'
  })
}

export function getTeacherInfo() {
  return request({
    url: '/teacher/info',
    method: 'get'
  })
}

export function getTeacherHome() {
  return request({
    url: '/teacher/home',
    method: 'get'
  })
}

export function addTeacher(data) {
  return request({
    url: '/teacher',
    method: 'post',
    data
  })
}

export function updateTeacher(data) {
  return request({
    url: '/teacher',
    method: 'put',
    data
  })
}

export function updateTeacherProfile(data) {
  return request({
    url: '/teacher/profile',
    method: 'put',
    data
  })
}

export function updateTeacherPassword(data) {
  return request({
    url: '/teacher/password',
    method: 'post',
    data
  })
}

export function deleteTeacher(id) {
  return request({
    url: `/teacher/${id}`,
    method: 'delete'
  })
}
