import request from '@/utils/request'

export function getDepartmentPage(params) {
  return request({
    url: '/department/page',
    method: 'get',
    params
  })
}

export function getDepartmentList() {
  return request({
    url: '/department/list',
    method: 'get'
  })
}

export function getDepartment(id) {
  return request({
    url: `/department/${id}`,
    method: 'get'
  })
}

export function addDepartment(data) {
  return request({
    url: '/department',
    method: 'post',
    data
  })
}

export function updateDepartment(data) {
  return request({
    url: '/department',
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/department/${id}`,
    method: 'delete'
  })
}
