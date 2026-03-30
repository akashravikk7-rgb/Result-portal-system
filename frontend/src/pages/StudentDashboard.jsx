import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import resultService from '../services/resultService'
import toast from 'react-hot-toast'
import '../styles/Dashboard.css'

export default function StudentDashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user?.studentId) {
      fetchResults()
    }
  }, [user])

  const fetchResults = async () => {
    try {
      const data = await resultService.getByStudent(user.studentId)
      setResults(data || [])
    } catch (error) {
      toast.error('Failed to load results')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  const handleDownloadPDF = async () => {
    try {
      await resultService.downloadPdf(user.studentId)
      toast.success('Marksheet downloaded!')
    } catch (error) {
      toast.error('Failed to download marksheet')
    }
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="dashboard">
      <h1>Student Dashboard</h1>
      
      <div className="student-info-card">
        <h2>Welcome, {user?.username}!</h2>
        <p>User ID: {user?.userId}</p>
        <p>Student ID: {user?.studentId || 'N/A'}</p>
      </div>

      <div className="dashboard-actions">
        <button onClick={() => navigate('/results')} className="btn-primary">
          View Results
        </button>
        <button onClick={handleDownloadPDF} className="btn-secondary">
          Download Marksheet
        </button>
      </div>

      <div className="results-preview">
        <h3>Recent Results ({results.length})</h3>
        {results.length === 0 ? (
          <p>No results available yet.</p>
        ) : (
          <div className="results-grid">
            {results.slice(0, 3).map(result => (
              <div key={result.id} className="result-card">
                <h4>{result.subjectName}</h4>
                <p>Marks: <strong>{result.marks}</strong>/100</p>
                <p>Grade: <strong>{result.grade}</strong></p>
                <p>Semester: {result.semester}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
