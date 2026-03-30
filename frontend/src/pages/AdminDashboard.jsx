import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import studentService from '../services/studentService'
import subjectService from '../services/subjectService'
import resultService from '../services/resultService'
import toast from 'react-hot-toast'
import '../styles/Dashboard.css'
export default function AdminDashboard() {
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    students: 0,
    subjects: 0,
    results: 0
  })
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    fetchStats()
  }, [])
  const fetchStats = async () => {
    try {
      const [studentsRes, subjectsRes, resultsRes] = await Promise.all([
        studentService.getAll(),
        subjectService.getAll(),
        resultService.getAll()
      ])
      setStats({
        students: studentsRes?.length || 0,
        subjects: subjectsRes?.length || 0,
        results: resultsRes?.length || 0
      })
    } catch (error) {
      toast.error('Failed to load statistics')
    } finally {
      setLoading(false)
    }
  }
if (loading) return <div className="loading">Loading...</div>
  return (
    <div className="dashboard">
      <h1>Admin Dashboard</h1>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>{stats.students}</h3>
          <p>Total Students</p>
          <button onClick={() => navigate('/students')} className="btn-small">
            Manage
          </button>
        </div>
        <div className="stat-card">
          <h3>{stats.subjects}</h3>
          <p>Total Subjects</p>
          <button onClick={() => navigate('/subjects')} className="btn-small">
            Manage
          </button>
        </div>
        <div className="stat-card">
          <h3>{stats.results}</h3>
          <p>Total Results</p>
        </div>
      </div>
      <div className="admin-actions">
        <h2>Quick Actions</h2>
        <button onClick={() => navigate('/students')} className="btn-primary">
          👥 Manage Students
        </button>
        <button onClick={() => navigate('/subjects')} className="btn-primary">
          📚 Manage Subjects
        </button>
      </div>
    </div>
  )
}
