import api from './api';

const resultService = {
  getAll: () => api.get('/results').then(res => res.data),
  getById: (id) => api.get(`/results/${id}`).then(res => res.data),
  getByStudent: (studentId) => api.get(`/results/student/${studentId}`).then(res => res.data),
  getByStudentAndSemester: (studentId, semester) =>
    api.get(`/results/student/${studentId}/semester/${semester}`).then(res => res.data),
  create: (data) => api.post('/results', data).then(res => res.data),
  update: (id, data) => api.put(`/results/${id}`, data).then(res => res.data),
  delete: (id) => api.delete(`/results/${id}`).then(res => res.data),
  downloadPdf: (studentId) =>
    api.get(`/results/download-pdf/${studentId}`, { responseType: 'blob' }).then(res => res.data),
  uploadCsv: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/admin/upload-csv', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then(res => res.data);
  },
  getDashboardStats: () => api.get('/admin/dashboard-stats').then(res => res.data),
};

export default resultService;
