import request from '@/utils/request'

export function getMajorPage(params) {
  return request({
    url: '/major/page',
    method: 'get',
    params
  })
}

export function getMajorList() {
  return request({
    url: '/major/list',
    method: 'get'
  })
}

export function getMajorListByDept(deptId) {
  return request({
    url: '/major/list-by-dept',
    method: 'get',
    params: { deptId }
  })
}

export function getMajor(id) {
  return request({
    url: `/major/${id}`,
    method: 'get'
  })
}

export function addMajor(data) {
  return request({
    url: '/major',
    method: 'post',
    data
  })
}

export function updateMajor(data) {
  return request({
    url: '/major',
    method: 'put',
    data
  })
}

export function deleteMajor(id) {
  return request({
    url: `/major/${id}`,
    method: 'delete'
  })
}
