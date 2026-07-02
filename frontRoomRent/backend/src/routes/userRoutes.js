import express from "express";
import { createUser, loginUser } from "../controllers/userController.js";
import { protect } from "../middlewares/authMiddleware.js";

const router = express.Router();

// POST /api/users
router.post("/register", createUser);
router.post("/login", loginUser);

router.get('/profile', protect, (req, res) => {
  res.json({
    message: 'Ruta protegida',
    user: req.user,
  });
});

export default router;
