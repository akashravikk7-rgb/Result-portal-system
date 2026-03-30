import { useState, useEffect } from 'react'
import studentService from '../services/studentService'
import toast from 'react-hot-toast'
import '../styles/Management.css'

export default function StudentManagement() {
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState({
    name: '', email: '', department: '', registrationNumber: '', semester: 1
  })

  useEffect(() => {
    fetchStudents()
  }, [])

  const fetchStudents = async () => {
    try {
      const data = await studentService.getAll()
      setStudents(data || [])
    } catch (error) {
      toast.error('Failed to load students')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editingId) {
        await studentService.update(editingId, formData)
        toast.success('Student updated!')
      } else {
        await studentService.create(formData)
        toast.success('Student created!')
      }
      setShowForm(false)
      setEditingId(null)
      setFormData({ name: '', email: '', department: '', registrationNumber: '', semester: 1 })
      fetchStudents()
    } catch (error) {
      toast.error(error.response?.data?.message || 'Operation failed')
    }
  }

  const handleEdit = (student) => {
    setFormData(student)
    setEditingId(student.id)
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (confirm('Are you sure?')) {
      try {
        await studentService.delete(id)
        toast.success('Student deleted!')
        fetchStudents()
      } catch (error) {
        toast.error('Failed to delete student')
      }
    }
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="management-container">
      <h1>Student Management</h1>

      <button onClick={() => {
        setShowForm(true)
        setEditingId(null)
        setFormData({ name: '', email: '', department: '', registrationNumber: '', semester: 1 })
      }} className="btn-primary">
        + Add Student
      </button>

      {showForm && (
        <div className="form-modal">
          <div className="form-card">
            <h2>{editingId ? 'Edit Student' : 'Add Student'}</h2>
            <form onSubmit={handleSubmit}>
              <input
                type="text"
                name="name"
                placeholder="Name"
                value={formData.name}
                onChange={handleChange}
                required
              />
              <input
                type="email"
                name="email"
                placeholder="Email"
                value={formData.email}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="department"
                placeholder="Department"
                value={formData.department}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="registrationNumber"
                placeholder="Reg Number"
                value={formData.registrationNumber}
                onChange={handleChange}
                required
              />
              <select name="semester" value={formData.semester} onChange={handleChange}>
                {[1, 2, 3, 4, 5, 6, 7, 8].map(s => <option key={s} value={s}>{s}</option>)}
              </select>
              <div className="form-buttons">
                <button type="submit" className="btn-primary">Save</button>
                <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <table className="management-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Department</th>
            <th>Reg Number</th>
            <th>Semester</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {students.map(student => (
            <tr key={student.id}>
              <td>{student.id}</td>
              <td>{student.name}</td>
              <td>{student.email}</td>
              <td>{student.department}</td>
              <td>{student.registrationNumber}</td>
              <td>{student.semester}</td>
              <td>
                <button onClick={() => handleEdit(student)} className="btn-edit">Edit</button>
                <button onClick={() => handleDelete(student.id)} className="btn-delete">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
