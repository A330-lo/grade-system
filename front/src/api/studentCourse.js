import request from '@/utils/request'

export function getStudentCoursePage(params) {
  return request({
    url: '/student-course/page',
    method: 'get',
    params
  })
}

export function getStudentCourse(id) {
  return request({
    url: `/student-course/${id}`,
    method: 'get'
  })
}

export function getMyCourses() {
  return request({
    url: '/student-course/my-courses',
    method: 'get'
  })
}

export function getCourseStudents(courseId) {
  return request({
    url: '/student-course/course-students',
    method: 'get',
    params: { courseId }
  })
}

export function selectCourse(courseId) {
  return request({
    url: '/student-course/select',
    method: 'post',
    params: { courseId }
  })
}

export function dropCourse(courseId) {
  return request({
    url: '/student-course/drop',
    method: 'post',
    params: { courseId }
  })
}

export function isSelected(courseId) {
  return request({
    url: '/student-course/is-selected',
    method: 'get',
    params: { courseId }
  })
}

export function addStudentCourse(data) {
  return request({
    url: '/student-course',
    method: 'post',
    data
  })
}

export function updateStudentCourse(data) {
  return request({
    url: '/student-course',
    method: 'put',
    data
  })
}

export function deleteStudentCourse(id) {
  return request({
    url: `/student-course/${id}`,
    method: 'delete'
  })
}
