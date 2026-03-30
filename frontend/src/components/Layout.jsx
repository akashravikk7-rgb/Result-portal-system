import { Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import '../styles/Layout.css'
export default function Layout() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
const handleLogout = () => {
    logout()
    navigate('/login')
  }
  return (
    <div className="layout">
      <nav className="navbar">
        <div className="navbar-brand">
          <h2>📊 Result Portal</h2>
        </div>
        <div className="navbar-menu">
          <a onClick={() => navigate('/dashboard')} className="nav-link">Dashboard</a>
          {!isAdmin() && (
            <a onClick={() => navigate('/results')} className="nav-link">Results</a>
          )}
          {isAdmin() && (
            <>
              <a onClick={() => navigate('/students')} className="nav-link">Students</a>
              <a onClick={() => navigate('/subjects')} className="nav-link">Subjects</a>
            </>
          )}
        </div>
        <div className="navbar-user">
          <span className="user-info">
            {user?.username} <small>({user?.role})</small>
          </span>
          <button onClick={handleLogout} className="btn-logout">Logout</button>
        </div>
      </nav>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}
