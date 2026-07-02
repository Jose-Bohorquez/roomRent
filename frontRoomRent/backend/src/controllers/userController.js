// src/controllers/userController.js
import User from "../models/user.js";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";

// Crear usuario
export const createUser = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res
        .status(400)
        .json({ message: "Todos los campos son obligatorios" });
    }

    const userExists = await User.findOne({ email });

    if (userExists) {
      return res.status(400).json({ message: "El usuario ya existe" });
    }

    const user = await User.create({ name, email, password });

    res.status(201).json({ message: "Usuario creado correctamente", user });
  } catch (error) {
    res.status(500).json({ message: "Error del servidor" });
  }
};

// Función para login
export const loginUser = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res
        .status(400)
        .json({ message: "Email y password son obligatorios" });
    }

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "Usuario no encontrado" });
    }

    //comparar password
    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(400).json({ message: "Contraseña incorrecta" });
    }

    const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
      expiresIn: "1d",
    });

    res.status(200).json({
      message: "Inicio de sesión con éxito",
      token,
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
      },
    });
  } catch (error) {
    res.status(500).json({ message: "Error al iniciar sesión" });
  }
};
