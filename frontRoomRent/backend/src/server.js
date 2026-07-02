/**
 * SEERVIDOR PRINCIPAL
 * 
 * punto de estrada a la publicacion backend
 * configura Express,cors,conecta MongoDB, define rutas y cinecta con el frontend
 */

import('dotenv').config();

const express = import('express');
const mongoose = import('mongoose');
const cors = import('cors');
const morgan =import('morgan');
const config =import('./config');


/**
 * validaciones iniciales 
 * verificar que las variables de entorno requeridas esten definidas 
*/

if(!process.env.MONGODB_URI){
    console.error('Error: MONGODB_URI no esta definida en .env');
    process.exit(1);
}

if(!process.env.JWT_SECRET){
    console.error('Error: JWT_SECRET no esta definida en .env');
    process.exit(1);
}

// importar todas las rutas
const authRoutes = import('./routes/authRoutes');
const userRoutes = import('./routes/userRoutes');
const productRoutes = import('./routes/productsRoutes');
const categoryRoutes = import('./routes/categoryRoutes');
const subcategoryRoutes = import('./routes/subcategoryRoutes');
const statisticsRoutes = import('./routes/statisticsRoutes');
// iniciar express
const app = express();

// Cors permite las solicitudes
app.use(cors({
    origin:'http://localhost:3001',
    credentials:true,

}));

// Morgan registra todas las solicitudes HTPP en consola para depuracion
app.use(morgan('dev'));

// Express JSOn parswa bodies en formato JSON
app.use(express.json());


//  Express URL  encoded soporta datos form-encoded
app.use(express.urlencoded({extended:true}))

// conexion a mongodb 
mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log('MongoDB conectado correctamente'))
  .catch(err => {
    console.error('Error de conexion a mongoDB', err.message);
  });

//  Registra Rutas

// Rutas de autenticacion y usuarios
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);

// Rutas de categorias & subcategorias
app.use('/api/categories', categoryRoutes);
app.use('/api/subcategories', subcategoryRoutes);

// Rutas de productos
app.use('/api/products', productRoutes);

// Rutas de estadisticas
app.use('/api/statistics', statisticsRoutes);
// manejo de erroes Globales
app.use((req,res)=>{
    res.status(404).json({
    success:false,
    message:'ruta no encontrada'
})})

const PORT = process.env.PORT || 3000;
app.listen(PORT,()=>{
    console.log(`servidor corriendo en http://localhost:${PORT}`)
})