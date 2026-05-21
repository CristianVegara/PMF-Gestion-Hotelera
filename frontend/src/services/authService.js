const API_URL = 'http://localhost:8080/api/auth';

export const login = async (username, password) => {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }) 
    });

    if (!response.ok) {
      throw new Error('Credenciales incorrectas o error en el servidor');
    }

    const data = await response.json();

    if (data.token) {
      localStorage.setItem('user_token', data.token);
      if (data.role) {
        localStorage.setItem('user_role', data.role);
      }
    }

    return data;
  } catch (error) {
    console.error("Error en el servicio de login:", error);
    throw error; 
  }
};

export const logout = () => {
  localStorage.removeItem('user_token');
  localStorage.removeItem('user_role');
};