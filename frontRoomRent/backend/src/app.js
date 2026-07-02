import express from 'express';
import userRoutes from './routes/userRoutes.js';
import propertyRoutes from './routes/propertyRoutes.js';
import connectDB from "./config/db.js";

connectDB();

const app = express();

// Middleware para leer JSON
app.use(express.json());

// Usar las rutas
app.use('/api/users', userRoutes);
app.use('/api/properties', propertyRoutes);

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en puerto ${PORT}`);
});

export default app;