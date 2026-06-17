import request from '@/utils/request'

export function getScorePage(params) {
  return request({
    url: '/score/page',
    method: 'get',
    params
  })
}

export function getScore(id) {
  return request({
    url: `/score/${id}`,
    method: 'get'
  })
}

export function getMyScores() {
  return request({
    url: '/score/my-scores',
    method: 'get'
  })
}

export function getCourseScores(courseId) {
  return request({
    url: '/score/course-scores',
    method: 'get',
    params: { courseId }
  })
}

export function addScore(data) {
  return request({
    url: '/score',
    method: 'post',
    data
  })
}

export function updateScore(data) {
  return request({
    url: '/score',
    method: 'put',
    data
  })
}

export function batchSaveScore(data) {
  return request({
    url: '/score/batch',
    method: 'post',
    data
  })
}

export function deleteScore(id) {
  return request({
    url: `/score/${id}`,
    method: 'delete'
  })
}
