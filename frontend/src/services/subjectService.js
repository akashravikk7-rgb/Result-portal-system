import api from './api';

const subjectService = {
  getAll: () => api.get('/subjects').then(res => res.data),
  getById: (id) => api.get(`/subjects/${id}`).then(res => res.data),
  getBySemester: (semester) => api.get(`/subjects/semester/${semester}`).then(res => res.data),
  create: (data) => api.post('/subjects', data).then(res => res.data),
  update: (id, data) => api.put(`/subjects/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/subjects/${id}`).then(res => res.data),
};

export default subjectService;
