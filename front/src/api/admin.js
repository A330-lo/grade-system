import request from '@/utils/request'

export function getAdminPage(params) {
  return request({
    url: '/admin/page',
    method: 'get',
    params
  })
}

export function getAdmin(id) {
  return request({
    url: `/admin/${id}`,
    method: 'get'
  })
}

export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

export function addAdmin(data) {
  return request({
    url: '/admin',
    method: 'post',
    data
  })
}

export function updateAdmin(data) {
  return request({
    url: '/admin',
    method: 'put',
    data
  })
}

export function updateAdminPassword(data) {
  return request({
    url: '/admin/password',
    method: 'post',
    data
  })
}

export function deleteAdmin(id) {
  return request({
    url: `/admin/${id}`,
    method: 'delete'
  })
}
