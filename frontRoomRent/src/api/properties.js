const API_URL = 'http://localhost:5000/api/properties';

export const getProperties = async (filters = {}) => {
  try {

    // construir query params
    const query = new URLSearchParams(filters).toString();

    const response = await fetch(`${API_URL}?${query}`);

    const data = await response.json();

    return data;
  } catch (error) {
    console.error('Error obteniendo propiedades', error);
  }
};