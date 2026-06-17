import request from '@/utils/request'

export function getCoursePage(params) {
  return request({
    url: '/course/page',
    method: 'get',
    params
  })
}

export function getCourseList() {
  return request({
    url: '/course/list',
    method: 'get'
  })
}

export function getCourseListByTeacher(teacherId) {
  return request({
    url: '/course/list-by-teacher',
    method: 'get',
    params: teacherId ? { teacherId } : {}
  })
}

export function getCourseListBySemester(semester) {
  return request({
    url: '/course/list-by-semester',
    method: 'get',
    params: { semester }
  })
}

export function getCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

export function getOptionalCourses(params) {
  return request({
    url: '/course/optional',
    method: 'get',
    params
  })
}

export function getSemesters() {
  return request({
    url: '/course/semesters',
    method: 'get'
  })
}

export function addCourse(data) {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

export function updateCourse(data) {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}
