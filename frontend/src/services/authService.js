const API_URL = 'http://localhost:8080/api/auth';

const normalizeRole = (role) => (role || 'USER').replace('ROLE_', '');

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
      const role = normalizeRole(data.role);
      const user = {
        username: data.username || username,
        role
      };

      localStorage.setItem('token', data.token);
      localStorage.setItem('role', role);
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('user_token', data.token);
      localStorage.setItem('user_role', role);

      data.role = role;
      data.username = user.username;
    }

    return data;
  } catch (error) {
    console.error("Error en el servicio de login:", error);
    throw error; 
  }
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  localStorage.removeItem('user');
  localStorage.removeItem('user_token');
  localStorage.removeItem('user_role');
};
