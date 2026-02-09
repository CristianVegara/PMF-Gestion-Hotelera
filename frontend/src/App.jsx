import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [message, setMessage] = useState('Cargando...')
  const [count, setCount] = useState(0)

  useEffect(() => {
    fetch('api/test')
  .then((res) => res.text())
  .then((data) => setMessage(data))
  .catch(() => setMessage('Error al conectar con backend'))

  }, [])

  return (
    <>
      <div>
        <h1>Vite + React 4</h1>
      </div>
      <div className="backend-message">
        <strong>Backend dice:</strong> {message}
      </div>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <p>Edit <code>src/App.jsx</code> and save to test HMR</p>
      </div>
      <p className="read-the-docs">
        Click on the logos to learn more
      </p>
    </>
  )
}

export default App
