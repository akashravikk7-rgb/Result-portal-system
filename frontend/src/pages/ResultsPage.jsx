import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import resultService from '../services/resultService'
import toast from 'react-hot-toast'
import '../styles/Results.css'

export default function ResultsPage() {
  const { user } = useAuth()
  const [results, setResults] = useState([])
  const [semester, setSemester] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchResults()
  }, [semester])

  const fetchResults = async () => {
    try {
      setLoading(true)
      let data
      if (semester) {
        data = await resultService.getByStudentAndSemester(user.studentId, semester)
      } else {
        data = await resultService.getByStudent(user.studentId)
      }
      setResults(data || [])
    } catch (error) {
      toast.error('Failed to load results')
    } finally {
      setLoading(false)
    }
  }

  const calculateGPA = () => {
    if (results.length === 0) return 0
    const gradePoints = {
      'A+': 4.0, 'A': 4.0, 'B+': 3.5, 'B': 3.0,
      'C': 2.0, 'D': 1.0, 'F': 0
    }
    const total = results.reduce((sum, r) => sum + (gradePoints[r.grade] || 0), 0)
    return (total / results.length).toFixed(2)
  }

  if (loading) return <div className="loading">Loading results...</div>

  return (
    <div className="results-container">
      <h1>My Results</h1>

      <div className="filters">
        <label>Filter by Semester:</label>
        <select value={semester} onChange={(e) => setSemester(e.target.value)}>
          <option value="">All Semesters</option>
          {[1, 2, 3, 4, 5, 6, 7, 8].map(s => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </div>

      {results.length === 0 ? (
        <div className="no-results">
          <p>No results found.</p>
        </div>
      ) : (
        <>
          <div className="results-summary">
            <div className="summary-card">
              <p>Total Subjects: <strong>{results.length}</strong></p>
            </div>
            <div className="summary-card">
              <p>Average Marks: <strong>{(results.reduce((sum, r) => sum + r.marks, 0) / results.length).toFixed(2)}</strong></p>
            </div>
            <div className="summary-card">
              <p>GPA: <strong>{calculateGPA()}</strong></p>
            </div>
          </div>

          <table className="results-table">
            <thead>
              <tr>
                <th>Subject</th>
                <th>Subject Code</th>
                <th>Marks</th>
                <th>Grade</th>
                <th>Semester</th>
              </tr>
            </thead>
            <tbody>
              {results.map(result => (
                <tr key={result.id}>
                  <td>{result.subjectName}</td>
                  <td>{result.subjectCode}</td>
                  <td>{result.marks}</td>
                  <td className={`grade-${result.grade}`}>{result.grade}</td>
                  <td>{result.semester}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  )
}
