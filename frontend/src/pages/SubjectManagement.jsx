import { useState, useEffect } from 'react'
import subjectService from '../services/subjectService'
import toast from 'react-hot-toast'
import '../styles/Management.css'

export default function SubjectManagement() {
  const [subjects, setSubjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState({ name: '', code: '', credits: 3 })

  useEffect(() => {
    fetchSubjects()
  }, [])

  const fetchSubjects = async () => {
    try {
      const data = await subjectService.getAll()
      setSubjects(data || [])
    } catch (error) {
      toast.error('Failed to load subjects')
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
        await subjectService.update(editingId, formData)
        toast.success('Subject updated!')
      } else {
        await subjectService.create(formData)
        toast.success('Subject created!')
      }
      setShowForm(false)
      setEditingId(null)
      setFormData({ name: '', code: '', credits: 3 })
      fetchSubjects()
    } catch (error) {
      toast.error('Operation failed')
    }
  }

  const handleEdit = (subject) => {
    setFormData(subject)
    setEditingId(subject.id)
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (confirm('Are you sure?')) {
      try {
        await subjectService.delete(id)
        toast.success('Subject deleted!')
        fetchSubjects()
      } catch (error) {
        toast.error('Failed to delete subject')
      }
    }
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="management-container">
      <h1>Subject Management</h1>

      <button onClick={() => {
        setShowForm(true)
        setEditingId(null)
        setFormData({ name: '', code: '', credits: 3 })
      }} className="btn-primary">
        + Add Subject
      </button>

      {showForm && (
        <div className="form-modal">
          <div className="form-card">
            <h2>{editingId ? 'Edit Subject' : 'Add Subject'}</h2>
            <form onSubmit={handleSubmit}>
              <input
                type="text"
                name="name"
                placeholder="Subject Name"
                value={formData.name}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="code"
                placeholder="Subject Code"
                value={formData.code}
                onChange={handleChange}
                required
              />
              <input
                type="number"
                name="credits"
                placeholder="Credits"
                value={formData.credits}
                onChange={handleChange}
                min="1"
                max="6"
                required
              />
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
            <th>Code</th>
            <th>Credits</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {subjects.map(subject => (
            <tr key={subject.id}>
              <td>{subject.id}</td>
              <td>{subject.name}</td>
              <td>{subject.code}</td>
              <td>{subject.credits}</td>
              <td>
                <button onClick={() => handleEdit(subject)} className="btn-edit">Edit</button>
                <button onClick={() => handleDelete(subject.id)} className="btn-delete">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
