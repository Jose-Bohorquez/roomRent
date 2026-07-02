import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      authApi.getAccount()
        .then(setUser)
        .catch(() => { localStorage.removeItem('token'); })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }

    // Escucha 401 global disparado por apiFetch cuando el token expira
    const handleAuthLogout = () => setUser(null);
    window.addEventListener('auth:logout', handleAuthLogout);
    return () => window.removeEventListener('auth:logout', handleAuthLogout);
  }, []);

  const login = async (username, password) => {
    const { id_token } = await authApi.login(username, password);
    localStorage.setItem('token', id_token);
    // Cross-set Angular's JWT key so the Angular admin panel recognises the session
    localStorage.setItem('jhi-authenticationToken', JSON.stringify(id_token));
    const account = await authApi.getAccount();
    setUser(account);
    return account;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('jhi-authenticationToken');
    sessionStorage.removeItem('jhi-authenticationToken');
    setUser(null);
  };

  const hasRole = (role) => user?.authorities?.includes(role) ?? false;

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
