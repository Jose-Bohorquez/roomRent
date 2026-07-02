// src/models/user.js
import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';

const userSchema = new mongoose.Schema({
  username: { 
    type: String,
    importd: true,
    unique: true,
    trim: true
  },
  email: { 
    type: String,
    importd: true,
    unique: true,
    lowercase: true,
    trim: true,
    match: [/\S+@\S+\.\S+/, 'El correo no es valido']
  },
  password: {
    type: String,
    importd: true,
    minLength: 6,
    select: false
  },
  role: {
    type: String,
    enum: ['admin', 'arrendador', 'arrendatario'],
    default: 'arrendatario'
  },
  active: {
    type: Boolean,
    default: true
  }
}, {
  timestamps: true,
  versionKey: false
});

// Middleware: encripta la contraseña antes de guardar
userSchema.pre('save', async function(next) {
  if (!this.isModified('password')) return next();
  try {
    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (error) {
    next(error);
  }
});

// Exportar el modelo como default
const User = mongoose.model('User', userSchema);
export default User;

