import api from './api';

const studentService = {
  getAll: () => api.get('/students').then(res => res.data),
  getById: (id) => api.get(`/students/${id}`).then(res => res.data),
  getByRegistration: (regNumber) => api.get(`/students/registration/${regNumber}`).then(res => res.data),
  getByDepartment: (department) => api.get(`/students/department/${department}`).then(res => res.data),
  create: (data) => api.post('/students', data).then(res => res.data),
  update: (id, data) => api.put(`/students/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/students/${id}`).then(res => res.data),
};

export default studentService;
